package dev.thezexquex.yasmpp.data.plan;

import com.djrapitops.plan.query.QueryService;
import org.intellij.lang.annotations.Language;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Reads session data of this server straight from the Plan database (MariaDB/MySQL).
 * All methods run the query asynchronously and never touch the Bukkit API.
 */
public class PlanSessionRepository {
    /**
     * Playtime and AFK time of one window; both placeholders take the window start in epoch millis.
     * afk_time is not timestamped, so a session cut by the window start contributes it proportionally.
     */
    @Language(value = "mariadb", prefix = "SELECT ", suffix = " FROM plan_sessions AS s")
    private static final String WINDOW_COLUMNS = """
            SUM(GREATEST(0, s.session_end - GREATEST(s.session_start, ?))),
            SUM(s.afk_time * GREATEST(0, s.session_end - GREATEST(s.session_start, ?)) DIV NULLIF(s.session_end - s.session_start, 0))""";

    private static final String STORED_PLAYTIME_QUERY = """
            SELECT
            %s,
            MIN(s.session_start),
            MAX(s.session_end)
            FROM plan_sessions AS s
            JOIN plan_users AS u ON u.id = s.user_id
            JOIN plan_servers AS sv ON sv.id = s.server_id
            WHERE u.uuid = ? AND sv.uuid = ?
            """.formatted(String.join(",\n", Collections.nCopies(TimeWindow.values().length, WINDOW_COLUMNS)));

    @Language("mariadb")
    private static final String TOTALS_QUERY = """
            SELECT
                u.uuid,
                u.name,
                SUM(s.session_end - s.session_start),
                SUM(s.afk_time),
                MAX(s.session_end)
            FROM plan_sessions AS s
            JOIN plan_users AS u ON u.id = s.user_id
            JOIN plan_servers AS sv ON sv.id = s.server_id
            WHERE sv.uuid = ?
            GROUP BY u.id, u.uuid, u.name
            """;

    @Language("mariadb")
    private static final String FIND_USER_QUERY = """
            SELECT uuid, name FROM plan_users WHERE name = ? LIMIT 1
            """;

    private final ZoneId zone;

    public PlanSessionRepository(ZoneId zone) {
        this.zone = zone;
    }

    public CompletableFuture<StoredPlaytime> storedPlaytime(UUID uniqueId, Instant now) {
        return CompletableFuture.supplyAsync(() -> {
            var serverUuid = serverUuid();
            return queryService().query(STORED_PLAYTIME_QUERY, statement -> {
                var index = 1;
                for (var window : TimeWindow.values()) {
                    var start = window.start(now, zone).toEpochMilli();
                    statement.setLong(index++, start);
                    statement.setLong(index++, start);
                }
                statement.setString(index++, uniqueId.toString());
                statement.setString(index, serverUuid.toString());

                try (var resultSet = statement.executeQuery()) {
                    resultSet.next();
                    var playtime = new EnumMap<TimeWindow, Duration>(TimeWindow.class);
                    var afk = new EnumMap<TimeWindow, Duration>(TimeWindow.class);
                    var column = 1;
                    for (var window : TimeWindow.values()) {
                        playtime.put(window, Duration.ofMillis(resultSet.getLong(column++)));
                        afk.put(window, Duration.ofMillis(resultSet.getLong(column++)));
                    }
                    var firstJoin = nullableInstant(resultSet, column++);
                    var lastSeen = nullableInstant(resultSet, column);
                    return new StoredPlaytime(playtime, afk, firstJoin, lastSeen, now);
                }
            });
        });
    }

    public CompletableFuture<List<StoredTotal>> storedTotals() {
        return CompletableFuture.supplyAsync(() -> {
            var serverUuid = serverUuid();
            return queryService().query(TOTALS_QUERY, statement -> {
                statement.setString(1, serverUuid.toString());
                var totals = new ArrayList<StoredTotal>();
                try (var resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        var user = new PlanUserRef(UUID.fromString(resultSet.getString(1)), resultSet.getString(2));
                        totals.add(new StoredTotal(
                                user,
                                Duration.ofMillis(resultSet.getLong(3)),
                                Duration.ofMillis(resultSet.getLong(4)),
                                Instant.ofEpochMilli(resultSet.getLong(5))
                        ));
                    }
                }
                return totals;
            });
        });
    }

    /**
     * Looks up a player by their last known name. The comparison follows the column collation,
     * which is case-insensitive in a default Plan setup.
     */
    public CompletableFuture<Optional<PlanUserRef>> findUser(String name) {
        return CompletableFuture.supplyAsync(() -> queryService().query(FIND_USER_QUERY, statement -> {
            statement.setString(1, name);
            try (var resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(new PlanUserRef(UUID.fromString(resultSet.getString(1)), resultSet.getString(2)));
            }
        }));
    }

    private static Instant nullableInstant(ResultSet resultSet, int column) throws SQLException {
        var millis = resultSet.getLong(column);
        return resultSet.wasNull() ? null : Instant.ofEpochMilli(millis);
    }

    private static QueryService queryService() {
        return QueryService.getInstance();
    }

    private static UUID serverUuid() {
        return queryService().getServerUUID()
                .orElseThrow(() -> new IllegalStateException("Plan has not registered this server yet"));
    }
}
