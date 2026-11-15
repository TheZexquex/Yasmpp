package dev.thezexquex.yasmpp.data.plan;

import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

/**
 * Playtime of one player on this server as persisted by Plan. Plan only writes a session once it ends,
 * so the current session of an online player is never part of this.
 *
 * @param playtime   playtime per window including AFK time
 * @param afk        AFK time per window; sessions overlapping a window start contribute proportionally
 * @param firstJoin  start of the first session on this server, {@code null} if the player never played here
 * @param lastSeen   end of the last finished session on this server, {@code null} if there is none
 * @param computedAt the instant the window boundaries were calculated from
 */
public record StoredPlaytime(
        Map<TimeWindow, Duration> playtime,
        Map<TimeWindow, Duration> afk,
        @Nullable Instant firstJoin,
        @Nullable Instant lastSeen,
        Instant computedAt
) {
}
