package dev.thezexquex.yasmpp.modules.lockportal.nether;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Optional;

public record Origin(String world, int x, int y, int z) {
    public static Origin fromLocation(org.bukkit.Location location) {
        return new Origin(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    /**
     * @return the location, or empty if the world is not loaded
     */
    public Optional<Location> toLocation() {
        var bukkitWorld = Bukkit.getWorld(world);
        if (bukkitWorld == null) {
            return Optional.empty();
        }
        return Optional.of(new Location(bukkitWorld, x, y, z));
    }
}
