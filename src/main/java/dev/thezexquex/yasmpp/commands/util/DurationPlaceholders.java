package dev.thezexquex.yasmpp.commands.util;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.time.Duration;

public class DurationPlaceholders {
    
    public static TagResolver[] forDuration(Duration duration) {
        var days = duration.toHours() == 0 ? "" : String.valueOf(duration.toDaysPart());
        var hours = duration.toHours() == 0 ? "" : String.valueOf(duration.toHoursPart());
        var minutes = duration.toMinutesPart() == 0 ? "" : String.valueOf(duration.toMinutesPart());
        var seconds = duration.toSecondsPart() == 0 ? "" : String.valueOf(duration.toSecondsPart());
        var millis = duration.toSecondsPart() == 0 ? "" : String.valueOf(duration.toMillisPart());

        return new TagResolver[]{
                Placeholder.parsed("days", days),
                Placeholder.parsed("hours", hours),
                Placeholder.parsed("minutes", minutes),
                Placeholder.parsed("seconds", seconds),
                Placeholder.parsed("millis", millis),
        };
    }
}
