package dev.thezexquex.yasmpp.modules.lockportal.nether;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Collection;

/**
 * Places and removes the real blocks of a portal in the world.
 * Physics are disabled throughout, otherwise portal fill breaks while its frame is incomplete.
 */
public final class PortalBlocks {

    private PortalBlocks() {
    }

    public static void place(Location origin, PortalBlock block) {
        worldBlock(origin, block).setBlockData(block.createBlockData(), false);
    }

    /**
     * Removes the block, but only if it is still the blueprint's material, so blocks a player put there are kept.
     */
    public static void clear(Location origin, PortalBlock block) {
        var worldBlock = worldBlock(origin, block);
        if (worldBlock.getType() == block.getMaterial()) {
            worldBlock.setType(Material.AIR, false);
        }
    }

    public static void lightPortal(Location origin, PortalBlueprint blueprint) {
        blueprint.blocks().stream()
                .filter(PortalBlock::isPortalFill)
                .forEach(block -> place(origin, block));
    }

    /**
     * Moves the given frame blocks, and the portal fill if the portal is complete, from one origin to another.
     */
    public static void move(Location from, Location to, PortalBlueprint blueprint, Collection<Integer> frameIndices, boolean portalComplete) {
        // Fill first, so no portal block is left without its frame
        if (portalComplete) {
            blueprint.blocks().stream().filter(PortalBlock::isPortalFill).forEach(block -> clear(from, block));
        }
        frameIndices.forEach(index -> clear(from, blueprint.block(index)));

        frameIndices.forEach(index -> place(to, blueprint.block(index)));
        if (portalComplete) {
            lightPortal(to, blueprint);
        }
    }

    private static Block worldBlock(Location origin, PortalBlock block) {
        return origin.clone().add(block.x(), block.y(), block.z()).getBlock();
    }
}
