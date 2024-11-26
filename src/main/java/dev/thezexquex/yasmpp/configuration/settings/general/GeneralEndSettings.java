package dev.thezexquex.yasmpp.configuration.settings.general;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class GeneralEndSettings {
    @JsonProperty
    private boolean lockEnd = true;

    public GeneralEndSettings() {

    }

    public boolean lockEnd() {
        return lockEnd;
    }

    public void setLockEnd(boolean lockEnd) {
        this.lockEnd = lockEnd;
    }
}
