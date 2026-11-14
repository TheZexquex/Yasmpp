package dev.thezexquex.yasmpp.modules.sysinfo;

import dev.thezexquex.yasmpp.configuration.settings.sysinfo.SysInfoBarSettings;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

/**
 * Renders a usage bar like {@code ||||||||    } where the used part is colored by the load.
 */
public class UsageBar {

    /**
     * @param fill how much of the bar is filled (0..1)
     * @param load decides the color (0..1), usually the same as fill. Differs e.g. for TPS where a full bar is good.
     */
    public static TagResolver resolver(String name, SysInfoBarSettings settings, int width, double fill, double load) {
        return Placeholder.parsed(name, render(settings, width, fill, load));
    }

    public static String render(SysInfoBarSettings settings, int width, double fill, double load) {
        var filled = (int) Math.round(clamp(fill) * width);
        var character = MiniMessage.miniMessage().escapeTags(settings.character());
        return color(settings, load) + character.repeat(filled)
                + settings.emptyColor() + character.repeat(width - filled);
    }

    public static String color(SysInfoBarSettings settings, double load) {
        var percent = load * 100;
        if (percent >= settings.criticalThreshold()) {
            return settings.criticalColor();
        }
        if (percent >= settings.warningThreshold()) {
            return settings.warningColor();
        }
        return settings.goodColor();
    }

    private static double clamp(double ratio) {
        return Math.max(0, Math.min(1, ratio));
    }
}
