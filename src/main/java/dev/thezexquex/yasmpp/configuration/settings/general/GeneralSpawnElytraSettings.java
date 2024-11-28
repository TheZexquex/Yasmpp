package dev.thezexquex.yasmpp.configuration.settings.general;

import de.unknowncity.astralib.libs.com.fasterxml.jackson.annotation.JsonProperty;

public class GeneralSpawnElytraSettings {
    @JsonProperty
    private boolean useMaxBoosts = true;
    @JsonProperty
    private int maxBoosts = 1;
    @JsonProperty
    private int radius = 30;

    public GeneralSpawnElytraSettings() {

    }

    public boolean useMaxBoosts() {
        return useMaxBoosts;
    }

    public int maxBoosts() {
        return maxBoosts;
    }

    public int radius() {
        return radius;
    }

    public void setUseMaxBoosts(boolean useMaxBoosts) {
        this.useMaxBoosts = useMaxBoosts;
    }

    public void setMaxBoosts(int maxBoosts) {
        this.maxBoosts = maxBoosts;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}