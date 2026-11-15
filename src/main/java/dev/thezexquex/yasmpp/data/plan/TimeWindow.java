package dev.thezexquex.yasmpp.data.plan;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

/**
 * Rolling time windows the playtime is aggregated over. Each window starts at {@link #start(Instant, ZoneId)}
 * and always ends now.
 */
public enum TimeWindow {
    DAY {
        @Override
        public Instant start(Instant now, ZoneId zone) {
            return now.atZone(zone).toLocalDate().atStartOfDay(zone).toInstant();
        }
    },
    WEEK {
        @Override
        public Instant start(Instant now, ZoneId zone) {
            return now.minus(Duration.ofDays(7));
        }
    },
    MONTH {
        @Override
        public Instant start(Instant now, ZoneId zone) {
            return now.minus(Duration.ofDays(30));
        }
    },
    YEAR {
        @Override
        public Instant start(Instant now, ZoneId zone) {
            return now.atZone(zone).toLocalDate().withDayOfYear(1).atStartOfDay(zone).toInstant();
        }
    },
    TOTAL {
        @Override
        public Instant start(Instant now, ZoneId zone) {
            return Instant.EPOCH;
        }
    };

    public abstract Instant start(Instant now, ZoneId zone);

    /**
     * Lowercase name used as suffix for message and PlaceholderAPI placeholders, e.g. {@code ontime_day}.
     */
    public String key() {
        return name().toLowerCase();
    }
}
