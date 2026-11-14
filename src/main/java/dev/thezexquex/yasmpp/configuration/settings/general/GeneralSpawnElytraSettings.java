package dev.thezexquex.yasmpp.configuration.settings.general;

public class GeneralSpawnElytraSettings {
    private boolean useMaxBoosts = true;
    private int maxBoosts = 1;
    private int radius = 30;

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