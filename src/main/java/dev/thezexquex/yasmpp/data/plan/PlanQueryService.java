package dev.thezexquex.yasmpp.data.plan;

import com.djrapitops.plan.query.QueryService;
import org.intellij.lang.annotations.Language;

import java.sql.ResultSet;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlanQueryService {
    private final Set<PlanUser> planUsers;
    private final Logger logger;

    public PlanQueryService(Logger logger) {
        this.planUsers = new HashSet<>();
        this.logger = logger;
    }

    public void refreshPlanCache(QueryService queryService) {
        planUsers.clear();

        cachePlanUsers(queryService);
    }

    public Set<PlanUser> planUsers() {
        return planUsers;
    }

    public Optional<PlanUser> getPlanUser(String lastKnownName) {
        return planUsers.stream().filter(planUser -> planUser.name().equals(lastKnownName)).findFirst();
    }

    public Optional<PlanUser> getPlanUser(UUID uniqueId) {
        return planUsers.stream().filter(planUser -> planUser.uniqueId().equals(uniqueId)).findFirst();
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
                            var uuid = UUID.fromString(resultSet.getString(1));
                            var id = resultSet.getInt(2);
                            var registered = resultSet.getLong(3);
                            var name = resultSet.getString(4);

                            var planUser = new PlanUser(
                                    id,
                                    uuid,
                                    name,
                                    Instant.ofEpochMilli(registered).atZone(ZoneId.systemDefault()).toLocalDateTime()
                            );

                            CompletableFuture.runAsync(() -> {
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
                                try {
                                    queryService.query(userDataQuery, userDataStatement -> {
                                        userDataStatement.setInt(1, planUser.id());
                                        try (ResultSet userDataResultSet = userDataStatement.executeQuery()) {
                                            while (userDataResultSet.next()) {
                                                var serverName = userDataResultSet.getString(1);
                                                var sessionStart = userDataResultSet.getLong(2);
                                                var sessionEnd = userDataResultSet.getLong(3);

                                                var session = new PlaySession(
                                                        serverName,
                                                        Instant.ofEpochMilli(sessionStart).atZone(ZoneId.systemDefault()).toLocalDateTime(),
                                                        Instant.ofEpochMilli(sessionEnd).atZone(ZoneId.systemDefault()).toLocalDateTime()
                                                );

                                                planUser.addSession(session);
                                            }
                                        } catch (Throwable e) {
                                            logger.log(Level.SEVERE, "Failed to cache plan user data", e);
                                        }
                                        return null;
                                    });
                                } catch (Throwable e) {
                                    logger.log(Level.SEVERE, "Failed to cache plan users", e);
                                }
                            }).whenComplete((unused, throwable) -> {
                                planUsers.add(planUser);
                            });
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
}
