package dev.thezexquex.yasmpp.util;

import java.time.Duration;

public class DurationFormatter {

    public static String formatDuration(Duration duration, String empty) {
        String formattedString = String.format(
                "%dd %dh %dm %ds",
                duration.toDaysPart(),
                duration.toHoursPart(),
                duration.toMinutesPart(),
                duration.toSecondsPart()
        );

        formattedString = formattedString.replaceAll("\\b0+[dhms]\\b", "").replaceAll("\\s+", " ").trim();
        return formattedString.isEmpty() ? empty : formattedString;
    }

    public static String formatLastLogoutDuration(Duration between, String empty) {
        String formattedString = String.format(
                "%dd %dh %dm %ds",
                between.toDaysPart(),
                between.toHoursPart(),
                between.toMinutesPart(),
                between.toSecondsPart()
        );
        formattedString = formattedString.replaceAll("\\b0+[dhms]\\b", "").replaceAll("\\s+", " ").trim();
        return formattedString.isEmpty() ? empty : formattedString;

    }
}
