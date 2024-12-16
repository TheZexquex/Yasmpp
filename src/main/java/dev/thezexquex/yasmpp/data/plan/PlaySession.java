package dev.thezexquex.yasmpp.data.plan;

import java.time.LocalDateTime;

public record PlaySession(
        String serverName,
        LocalDateTime sessionStartTime,
        LocalDateTime sessionEndTime
) {

}
