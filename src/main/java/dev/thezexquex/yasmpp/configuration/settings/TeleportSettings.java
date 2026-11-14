package dev.thezexquex.yasmpp.configuration.settings;

public class TeleportSettings {
    private int teleportPrice = 3;
    private int teleportCoolDownInSeconds = 3;
    private boolean cancelOnMove = true;
    private boolean permissionBypassesCoolDown = false;

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

    public int teleportPrice() {
        return teleportPrice;
    }
}
