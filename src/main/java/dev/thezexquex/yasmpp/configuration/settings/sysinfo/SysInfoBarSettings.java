package dev.thezexquex.yasmpp.configuration.settings.sysinfo;

/**
 * Look of the usage bars. Thresholds are in percent, colors are MiniMessage tags.
 */
public class SysInfoBarSettings {
    private String character = "|";
    private int width = 64;
    private int coreWidth = 12;
    private int liveWidth = 10;
    private int warningThreshold = 60;
    private int criticalThreshold = 85;
    private String goodColor = "<green>";
    private String warningColor = "<yellow>";
    private String criticalColor = "<red>";
    private String emptyColor = "<dark_gray>";

    public String character() {
        return character;
    }

    public int width() {
        return width;
    }

    public int coreWidth() {
        return coreWidth;
    }

    public int liveWidth() {
        return liveWidth;
    }

    public int warningThreshold() {
        return warningThreshold;
    }

    public int criticalThreshold() {
        return criticalThreshold;
    }

    public String goodColor() {
        return goodColor;
    }

    public String warningColor() {
        return warningColor;
    }

    public String criticalColor() {
        return criticalColor;
    }

    public String emptyColor() {
        return emptyColor;
    }
}
