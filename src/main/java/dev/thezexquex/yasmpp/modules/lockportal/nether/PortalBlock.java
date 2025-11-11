package dev.thezexquex.yasmpp.modules.lockportal.nether;

import de.unknowncity.astralib.libs.com.fasterxml.jackson.annotation.JsonProperty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public record PortalBlock(
        @JsonProperty("x") int x,
        @JsonProperty("y") int y,
        @JsonProperty("z") int z,
        @JsonProperty("material") String material,
        @JsonProperty("isPortalFill") boolean isPortalFill,
        @JsonProperty("blockData") String blockData
) {

    public PortalBlock(int x, int y, int z, String material, String blockData) {
        this(x, y, z, material, false, blockData);
    }

    public Location toLocation(World world) {
        return new Location(world, x, y, z);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PortalBlock other) {
            return other.x == x && other.y == y && other.z == z && other.material.equals(material);
        }
        return false;
    }

    public Material getMaterial() {
        return Material.valueOf(material);
    }

    public String getBlockData() {
        return blockData;
    }

    public boolean isPortalFill() {
        return isPortalFill;
    }


    public boolean isFrameBlock() {
        return !isPortalFill;
    }
}