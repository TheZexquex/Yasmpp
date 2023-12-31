package dev.thezexquex.yasmpp.region;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SphericalRegion {
    private final Location center;
    private final int radius;

    public SphericalRegion(Location center, int radius) {
        this.center = center;
        this.radius = radius;
    }

    public boolean isPlayerInRegion(Player player) {
        if (!player.getWorld().equals(center.getWorld())) {
            return false;
        }
        return !(player.getLocation().distanceSquared(center) > radius * radius);
    }
}
