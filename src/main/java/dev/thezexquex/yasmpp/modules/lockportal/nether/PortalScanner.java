package dev.thezexquex.yasmpp.modules.lockportal.nether;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PortalScanner {
    public static final long MAX_SCAN_VOLUME = 128 * 128 * 128;

    /**
     * @return an error message if the selection can not be scanned
     */
    public static Optional<String> validateSelection(Location corner1, Location corner2) {
        if (!corner1.getWorld().equals(corner2.getWorld())) {
            return Optional.of("Both positions must be in the same world.");
        }
        long volume = (long) (Math.abs(corner1.getBlockX() - corner2.getBlockX()) + 1)
                * (Math.abs(corner1.getBlockY() - corner2.getBlockY()) + 1)
                * (Math.abs(corner1.getBlockZ() - corner2.getBlockZ()) + 1);
        if (volume > MAX_SCAN_VOLUME) {
            return Optional.of("Selection too large (" + volume + " > " + MAX_SCAN_VOLUME + " blocks).");
        }
        return Optional.empty();
    }

    public static PortalBlueprint scanStructure(Location corner1, Location corner2) {
        var world = corner1.getWorld();
        List<PortalBlock> blocks = new ArrayList<>();

        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int minY = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int maxY = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    var block = world.getBlockAt(x, y, z);
                    if (!block.getType().isAir() && block.getType() != Material.STRUCTURE_BLOCK) {
                        boolean isPortalFill = block.getType() == Material.NETHER_PORTAL;

                        blocks.add(new PortalBlock(
                                x - minX,
                                y - minY,
                                z - minZ,
                                block.getType().name(),
                                isPortalFill,
                                block.getBlockData().getAsString()
                        ));
                    }
                }
            }
        }
        return new PortalBlueprint(blocks);
    }
}
