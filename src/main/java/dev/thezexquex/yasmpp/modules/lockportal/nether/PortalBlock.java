package dev.thezexquex.yasmpp.modules.lockportal.nether;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.Nullable;

public record PortalBlock(
        int x,
        int y,
        int z,
        String material,
        boolean isPortalFill,
        String blockData
) {

    /**
     * @return the material, or null if the server does not know it
     */
    public @Nullable Material getMaterial() {
        return Material.matchMaterial(material);
    }

    /**
     * @return the stored block data, falling back to the material's default if it can not be parsed
     */
    public BlockData createBlockData() {
        if (blockData != null) {
            try {
                return Bukkit.createBlockData(blockData);
            } catch (IllegalArgumentException ignored) {
                // Fall back to the material's default block data
            }
        }
        return getMaterial().createBlockData();
    }

    public boolean isFrameBlock() {
        return !isPortalFill;
    }
}
