package dev.thezexquex.yasmpp.core.data.database.dao.location;

import org.bukkit.Location;
import org.bukkit.Server;

public record LocationContainer(
     String world,
     double x,
     double y,
     double z,
     float yaw,
     float pitch
) {

    public static LocationContainer of(Location location) {
        return new LocationContainer(
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch()
        );
    }
    public static LocationContainer of(String world, double x, double y, double z, float yaw, float pitch) {
        return new LocationContainer(
                world, x, y, z, yaw, pitch
        );
    }

    public Location toLocation(Server server) {
        return new Location(server.getWorld(world), x, y, z, yaw, pitch);
    }
}
