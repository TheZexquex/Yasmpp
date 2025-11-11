package dev.thezexquex.yasmpp.modules.lockportal.nether;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public record Origin(String world, int x, int y, int z) {
    public static Origin fromLocation(org.bukkit.Location location) {
        return new Origin(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public Location toLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z);
    }
}
