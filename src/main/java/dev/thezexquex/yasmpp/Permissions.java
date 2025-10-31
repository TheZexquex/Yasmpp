package dev.thezexquex.yasmpp;

import java.util.List;

public class Permissions {
    public static final String CHAT_VIOLATION_NOTIFY = "yasmpp.chat.filter.notify";
    public static final String CHAT_VIOLATION_BYPASS = "yasmpp.chat.filter.bypass";

    public static final List<String> ALL_PERMISSIONS = List.of(
            CHAT_VIOLATION_NOTIFY,
            CHAT_VIOLATION_BYPASS
    );
}
