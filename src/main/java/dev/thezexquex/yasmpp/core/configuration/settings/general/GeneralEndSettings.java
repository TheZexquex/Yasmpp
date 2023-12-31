package dev.thezexquex.yasmpp.core.configuration.settings.general;

public final class GeneralEndSettings {
    private boolean lockEnd;

    public GeneralEndSettings(
            boolean lockEnd
    ) {
        this.lockEnd = lockEnd;
    }

    public boolean lockEnd() {
        return lockEnd;
    }

    public void setLockEnd(boolean lockEnd) {
        this.lockEnd = lockEnd;
    }
}
