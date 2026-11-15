package dev.thezexquex.yasmpp.modules.playtime;

import de.unknowncity.astralib.common.util.DurationFormatter;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public final class PlaytimeFormat {
    public static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    private PlaytimeFormat() {
    }

    public static String duration(Duration duration) {
        var formatted = DurationFormatter.formatDuration(duration.truncatedTo(ChronoUnit.SECONDS));
        return formatted.isEmpty() ? "0s" : formatted;
    }
}
