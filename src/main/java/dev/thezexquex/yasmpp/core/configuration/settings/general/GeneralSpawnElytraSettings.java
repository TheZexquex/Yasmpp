package dev.thezexquex.yasmpp.core.configuration.settings.general;

import java.util.Objects;

public class GeneralSpawnElytraSettings {
    private boolean useMaxBoosts;
    private int maxBoosts;
    private int radius;

    public GeneralSpawnElytraSettings(
            boolean useMaxBoosts,
            int maxBoosts,
            int radius
    ) {
        this.useMaxBoosts = useMaxBoosts;
        this.maxBoosts = maxBoosts;
        this.radius = radius;
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