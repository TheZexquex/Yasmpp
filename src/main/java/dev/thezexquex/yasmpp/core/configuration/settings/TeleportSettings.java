package dev.thezexquex.yasmpp.core.configuration.settings;

import java.util.Objects;

public class TeleportSettings {
    private int teleportCoolDownInSeconds;
    private boolean cancelOnMove;
    private boolean permissionBypassesCoolDown;

    public TeleportSettings(
            int teleportCoolDownInSeconds,
            boolean cancelOnMove,
            boolean permissionBypassesCoolDown
    ) {
        this.teleportCoolDownInSeconds = teleportCoolDownInSeconds;
        this.cancelOnMove = cancelOnMove;
        this.permissionBypassesCoolDown = permissionBypassesCoolDown;
    }

    public int teleportCoolDownInSeconds() {
        return teleportCoolDownInSeconds;
    }

    public boolean cancelOnMove() {
        return cancelOnMove;
    }

    public boolean permissionBypassesCoolDown() {
        return permissionBypassesCoolDown;
    }

    public void setTeleportCoolDownInSeconds(int teleportCoolDownInSeconds) {
        this.teleportCoolDownInSeconds = teleportCoolDownInSeconds;
    }

    public void setCancelOnMove(boolean cancelOnMove) {
        this.cancelOnMove = cancelOnMove;
    }

    public void setPermissionBypassesCoolDown(boolean permissionBypassesCoolDown) {
        this.permissionBypassesCoolDown = permissionBypassesCoolDown;
    }
}
