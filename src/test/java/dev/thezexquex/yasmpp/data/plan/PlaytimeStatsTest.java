package dev.thezexquex.yasmpp.data.plan;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.EnumMap;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PlaytimeStatsTest {
    private static final ZoneOffset ZONE = ZoneOffset.UTC;
    private static final PlanUserRef USER = new PlanUserRef(UUID.randomUUID(), "Alice");
    // 2026-11-15T01:00:00Z
    private static final Instant NOW = Instant.parse("2026-11-15T01:00:00Z");

    private static StoredPlaytime stored(Duration playtime, Duration afk, Instant firstJoin) {
        var playtimes = new EnumMap<TimeWindow, Duration>(TimeWindow.class);
        var afks = new EnumMap<TimeWindow, Duration>(TimeWindow.class);
        for (var window : TimeWindow.values()) {
            playtimes.put(window, playtime);
            afks.put(window, afk);
        }
        return new StoredPlaytime(playtimes, afks, firstJoin, firstJoin, NOW);
    }

    @Test
    void liveSessionIsAddedToAllWindows() {
        var live = new LiveSessions.LiveSession(NOW.minus(Duration.ofMinutes(30)), Duration.ofMinutes(30), Duration.ZERO);
        var stats = PlaytimeStats.of(USER, stored(Duration.ofHours(1), Duration.ZERO, NOW.minus(Duration.ofDays(3))), Optional.of(live), true, NOW, ZONE);

        assertEquals(Duration.ofMinutes(30), stats.session(false));
        for (var window : TimeWindow.values()) {
            assertEquals(Duration.ofMinutes(90), stats.playtime(window, false), window.name());
        }
    }

    @Test
    void liveSessionOverMidnightOnlyCountsTodaysPartForDay() {
        // Started 23:00 yesterday, now is 01:00
        var live = new LiveSessions.LiveSession(NOW.minus(Duration.ofHours(2)), Duration.ofHours(2), Duration.ofMinutes(40));
        var stats = PlaytimeStats.of(USER, stored(Duration.ZERO, Duration.ZERO, NOW.minus(Duration.ofDays(3))), Optional.of(live), true, NOW, ZONE);

        assertEquals(Duration.ofHours(1), stats.playtime(TimeWindow.DAY, false));
        assertEquals(Duration.ofMinutes(40), stats.playtime(TimeWindow.DAY, true));
        assertEquals(Duration.ofHours(2), stats.playtime(TimeWindow.WEEK, false));
        assertEquals(Duration.ofMinutes(80), stats.session(true));
    }

    @Test
    void afkIsOnlyExcludedOnRequest() {
        var stats = PlaytimeStats.of(USER, stored(Duration.ofHours(2), Duration.ofMinutes(30), NOW), Optional.empty(), false, NOW, ZONE);

        assertEquals(Duration.ofHours(2), stats.playtime(TimeWindow.TOTAL, false));
        assertEquals(Duration.ofMinutes(90), stats.playtime(TimeWindow.TOTAL, true));
    }

    @Test
    void firstSessionCountsAsPlayed() {
        var live = new LiveSessions.LiveSession(NOW.minus(Duration.ofMinutes(5)), Duration.ofMinutes(5), Duration.ZERO);
        var stats = PlaytimeStats.of(USER, stored(Duration.ZERO, Duration.ZERO, null), Optional.of(live), true, NOW, ZONE);

        assertTrue(stats.hasPlayed());
        assertEquals(live.start(), stats.firstJoin());
    }

    @Test
    void neverPlayedWithoutSessions() {
        var stats = PlaytimeStats.of(USER, stored(Duration.ZERO, Duration.ZERO, null), Optional.empty(), false, NOW, ZONE);

        assertFalse(stats.hasPlayed());
    }
}
