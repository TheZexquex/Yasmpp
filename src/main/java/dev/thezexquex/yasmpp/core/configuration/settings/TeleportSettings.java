package dev.thezexquex.yasmpp.core.configuration.settings;

public record TeleportSettings(
        int teleportCoolDownInSeconds,
        boolean cancelOnMove,
        boolean permissionBypassesCoolDown
) {}
