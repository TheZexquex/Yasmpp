package dev.thezexquex.yasmpp.data.plan;

import java.time.Duration;
import java.time.Instant;

/**
 * Total playtime of one player on this server as persisted by Plan, used for leaderboards.
 */
public record StoredTotal(PlanUserRef user, Duration playtime, Duration afk, Instant lastSeen) {
}
