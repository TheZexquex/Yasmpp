package dev.thezexquex.yasmpp.data.plan;

import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;

/**
 * Total playtime of one player on this server including a running session.
 *
 * @param lastSeen end of the last finished session, {@code null} if the player is in their first session
 */
public record LeaderboardEntry(PlanUserRef user, Duration playtime, Duration afk, @Nullable Instant lastSeen, boolean online) {

    public Duration playtime(boolean excludeAfk) {
        return excludeAfk ? PlaytimeStats.nonNegative(playtime.minus(afk)) : playtime;
    }
}
