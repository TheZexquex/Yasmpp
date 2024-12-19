package dev.thezexquex.yasmpp.data.adapter;

import dev.thezexquex.yasmpp.data.entity.LocationContainer;
import org.bukkit.Location;
import org.bukkit.Server;

public class LocationAdapter  {

    public static LocationContainer asData(Location location) {
        return new LocationContainer(
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch()
        );
    }

    public static Location adapt(LocationContainer data, Server server) {
        var world = data.world();
        var x = data.x();
        var y = data.y();
        var z = data.z();
        var yaw = data.yaw();
        var pitch = data.pitch();

        return new Location(server.getWorld(world), x, y, z, yaw, pitch);
    }
}
