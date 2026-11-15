package dev.thezexquex.yasmpp.data.plan;

import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 * Playtime of one player on this server, combining the sessions stored by Plan with the currently running one.
 *
 * @param session    length of the running session, {@link Duration#ZERO} if the player is offline
 * @param sessionAfk AFK time of the running session
 * @param playtime   playtime per window including AFK time
 * @param afk        AFK time per window
 */
public record PlaytimeStats(
        PlanUserRef user,
        boolean online,
        @Nullable Instant firstJoin,
        @Nullable Instant lastSeen,
        Duration session,
        Duration sessionAfk,
        Map<TimeWindow, Duration> playtime,
        Map<TimeWindow, Duration> afk
) {

    public static PlaytimeStats of(PlanUserRef user, StoredPlaytime stored, Optional<LiveSessions.LiveSession> live, boolean online, Instant now, ZoneId zone) {
        var playtime = new EnumMap<>(stored.playtime());
        var afk = new EnumMap<>(stored.afk());
        var firstJoin = stored.firstJoin();

        if (live.isPresent()) {
            var session = live.get();
            for (var window : TimeWindow.values()) {
                var overlap = overlap(session.start(), now, window.start(now, zone));
                playtime.merge(window, overlap, Duration::plus);
                afk.merge(window, proportionalAfk(session, overlap), Duration::plus);
            }
            if (firstJoin == null) {
                firstJoin = session.start();
            }
        }

        return new PlaytimeStats(
                user,
                online,
                firstJoin,
                stored.lastSeen(),
                live.map(LiveSessions.LiveSession::length).orElse(Duration.ZERO),
                live.map(LiveSessions.LiveSession::afk).orElse(Duration.ZERO),
                playtime,
                afk
        );
    }

    public boolean hasPlayed() {
        return firstJoin != null;
    }

    public Duration session(boolean excludeAfk) {
        return excludeAfk ? nonNegative(session.minus(sessionAfk)) : session;
    }

    public Duration playtime(TimeWindow window, boolean excludeAfk) {
        var total = playtime.getOrDefault(window, Duration.ZERO);
        return excludeAfk ? nonNegative(total.minus(afk.getOrDefault(window, Duration.ZERO))) : total;
    }

    private static Duration overlap(Instant start, Instant end, Instant windowStart) {
        var from = start.isAfter(windowStart) ? start : windowStart;
        return from.isBefore(end) ? Duration.between(from, end) : Duration.ZERO;
    }

    private static Duration proportionalAfk(LiveSessions.LiveSession session, Duration overlap) {
        if (session.length().isZero()) {
            return Duration.ZERO;
        }
        return Duration.ofMillis(session.afk().toMillis() * overlap.toMillis() / session.length().toMillis());
    }

    static Duration nonNegative(Duration duration) {
        return duration.isNegative() ? Duration.ZERO : duration;
    }
}
