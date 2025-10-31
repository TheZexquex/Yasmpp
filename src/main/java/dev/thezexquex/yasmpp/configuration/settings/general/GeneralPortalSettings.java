package dev.thezexquex.yasmpp.configuration.settings.general;

import de.unknowncity.astralib.libs.com.fasterxml.jackson.annotation.JsonProperty;

public final class GeneralPortalSettings {
    @JsonProperty
    private boolean lockEnd = true;
    @JsonProperty
    private boolean lockNether = true;

    public GeneralPortalSettings() {

    }

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
