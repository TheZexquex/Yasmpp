package dev.thezexquex.yasmpp.message;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Location;

public class LocationPlaceholders {

    public static TagResolver[] of(Location location) {
        return new TagResolver[]{
                Placeholder.parsed("world", location.getWorld().getName()),
                Placeholder.parsed("x", String.format("%.2f", location.getX())),
                Placeholder.parsed("y", String.format("%.2f", location.getY())),
                Placeholder.parsed("z", String.format("%.2f", location.getZ())),
                Placeholder.parsed("yaw", String.format("%.2f", location.getYaw())),
                Placeholder.parsed("pitch", String.format("%.2f", location.getPitch()))
        };
    }
}
