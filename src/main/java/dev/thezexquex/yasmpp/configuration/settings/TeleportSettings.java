package dev.thezexquex.yasmpp.configuration.settings;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TeleportSettings {
    @JsonProperty
    private int teleportCoolDownInSeconds = 3;
    @JsonProperty
    private boolean cancelOnMove = true;
    @JsonProperty
    private boolean permissionBypassesCoolDown = false;

    public TeleportSettings() {

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
