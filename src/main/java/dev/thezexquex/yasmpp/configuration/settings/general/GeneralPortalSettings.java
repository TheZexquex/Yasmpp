package dev.thezexquex.yasmpp.configuration.settings.general;

public final class GeneralPortalSettings {
    private boolean lockEnd = true;
    private boolean lockNether = true;

    public boolean lockEnd() {
        return lockEnd;
    }

    public boolean lockNether() {
        return lockNether;
    }

    public void lockEnd(boolean lockEnd) {
        this.lockEnd = lockEnd;
    }

    public void lockNether(boolean lockNether) {
        this.lockNether = lockNether;
    }
}
