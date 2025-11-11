package dev.thezexquex.yasmpp.modules.lockportal.nether;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class PortalScanner {

    public static PortalBlueprint scanStructure(World world, Location corner1, Location corner2) {
        var blueprint = new PortalBlueprint();
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
                    if (block.getType() != Material.AIR && block.getType() != Material.STRUCTURE_BLOCK) {
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
        blueprint.blocks(blocks);
        return blueprint;
    }
}