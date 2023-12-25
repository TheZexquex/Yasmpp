package dev.thezexquex.yasmpp.configuration.settings;

public record TeleportSettings(
        int teleportCoolDownInSeconds,
        boolean cancelOnMove,
        boolean permissionBypassesCoolDown
) {}
