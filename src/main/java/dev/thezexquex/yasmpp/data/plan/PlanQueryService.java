package dev.thezexquex.yasmpp.data.plan;

import com.djrapitops.plan.query.QueryService;
import org.intellij.lang.annotations.Language;

import java.sql.ResultSet;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlanQueryService {
    private final Map<UUID, PlanUser> planUsers;
    private final Logger logger;

    public PlanQueryService(Logger logger) {
        this.planUsers = new ConcurrentHashMap<>();
        this.logger = logger;
    }

    public void refreshPlanCache(QueryService queryService) {
        cachePlanUsers(queryService);
    }

    public Collection<PlanUser> planUsers() {
        return planUsers.values();
    }

    public Optional<PlanUser> getPlanUser(String lastKnownName) {
        return planUsers.values().stream().filter(planUser -> planUser.name().equalsIgnoreCase(lastKnownName)).findFirst();
    }

    public Optional<PlanUser> getPlanUser(UUID uniqueId) {
        return Optional.ofNullable(planUsers.get(uniqueId));
    }

    private void cachePlanUsers(QueryService queryService) {
        CompletableFuture.runAsync(() -> {
            @Language("mariadb")
            var query = """
                    SELECT uuid, id, registered, name FROM plan_users;
                    """;
            try {
                queryService.query(query, preparedStatement -> {
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            var uuidString = resultSet.getString(1);
                            if (uuidString == null) continue;
                            var uuid = UUID.fromString(uuidString);
                            var id = resultSet.getInt(2);
                            var registered = resultSet.getLong(3);
                            var name = resultSet.getString(4);

                            LocalDateTime registeredDate;
                            if (registered <= 0) {
                                registeredDate = LocalDateTime.now();
                            } else {
                                registeredDate = Instant.ofEpochMilli(registered).atZone(ZoneId.systemDefault()).toLocalDateTime();
                            }

                            var planUser = new PlanUser(
                                    id,
                                    uuid,
                                    name,
                                    registeredDate
                            );

                            fetchUserData(queryService, planUser);
                        }
                    } catch (Throwable e) {
                        logger.log(Level.SEVERE, "Failed to cache plan users", e);
                    }
                    return null;
                });
            } catch (Throwable e) {
                logger.log(Level.SEVERE, "Failed to cache plan users", e);
            }
        });
    }

    private void fetchUserData(QueryService queryService, PlanUser planUser) {
        CompletableFuture.runAsync(() -> {
            try {
                var sessionOnTimeMillis = queryService.getCommonQueries()
                        .fetchCurrentSessionPlaytime(planUser.uniqueId());

                var sessionOnTime = Duration.ofMillis(sessionOnTimeMillis);
                planUser.sessionPlayTime(sessionOnTime);
                @Language("mariadb")
                var userDataQuery = """
                        SELECT
                            servers.name AS server_name,
                            sessions.session_start,
                            sessions.session_end
                        FROM
                            plan_sessions AS sessions
                        JOIN
                            plan_servers AS servers ON sessions.server_id = servers.id
                        WHERE
                            sessions.user_id = ?;
                        """;
                queryService.query(userDataQuery, userDataStatement -> {
                    userDataStatement.setInt(1, planUser.id());
                    try (ResultSet userDataResultSet = userDataStatement.executeQuery()) {
                        while (userDataResultSet.next()) {
                            var serverName = userDataResultSet.getString(1);
                            var sessionStart = userDataResultSet.getLong(2);
                            var sessionEnd = userDataResultSet.getLong(3);

                            if (sessionStart <= 0) continue;

                            var start = Instant.ofEpochMilli(sessionStart).atZone(ZoneId.systemDefault()).toLocalDateTime();
                            var end = sessionEnd <= 0 ? LocalDateTime.now() : Instant.ofEpochMilli(sessionEnd).atZone(ZoneId.systemDefault()).toLocalDateTime();

                            var session = new PlaySession(
                                    serverName,
                                    start,
                                    end
                            );

                            planUser.addSession(session);
                        }
                    } catch (Throwable e) {
                        logger.log(Level.SEVERE, "Failed to cache plan user data for " + planUser.name(), e);
                    }
                    return null;
                });
            } catch (Throwable e) {
                logger.log(Level.SEVERE, "Failed to fetch plan user data for " + planUser.name(), e);
            }
        }).whenComplete((unused, throwable) -> {
            if (throwable != null) {
                logger.log(Level.SEVERE, "Error fetching data for " + planUser.name(), throwable);
            }
            planUsers.put(planUser.uniqueId(), planUser);
        });
    }
}
