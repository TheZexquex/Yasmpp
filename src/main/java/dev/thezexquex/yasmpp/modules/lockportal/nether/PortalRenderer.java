package dev.thezexquex.yasmpp.modules.lockportal.nether;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.util.Transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PortalRenderer {
    private final PortalBlueprint blueprint;
    private final PortalState state;
    private final NetherPortalManager netherPortalManager;
    private final Map<Interaction, PortalBlock> interactionToBlock = new HashMap<>();
    private final Map<Interaction, List<BlockDisplay>> interactionToDisplay = new HashMap<>();

    public PortalRenderer(NetherPortalManager netherPortalManager, PortalBlueprint blueprint, PortalState state) {
        this.blueprint = blueprint;
        this.state = state;
        this.netherPortalManager = netherPortalManager;
    }

    public void render(World world, Location base) {
        for (var block : blueprint.blocks()) {
            if (block.isFrameBlock()) {
                spawnBlueprintBlock(world, base, block);
            }
        }
        netherPortalManager.updateBossBarProgress(state);
    }

    private void spawnBlueprintBlock(World world, Location base, PortalBlock portalBlock) {
        Location loc = base.clone().add(portalBlock.x(), portalBlock.y(), portalBlock.z()).toCenterLocation();

        BlockDisplay display = (BlockDisplay) world.spawnEntity(loc, EntityType.BLOCK_DISPLAY);
        display.setBlock(portalBlock.getMaterial().createBlockData());
        display.setBrightness(new Display.Brightness(15, 15));
        display.setGlowing(true);
        display.setGlowColorOverride(Color.RED);
        display.setPersistent(false);
        display.setRotation(0, 0);

        BlockDisplay overlayDisplay = (BlockDisplay) world.spawnEntity(loc, EntityType.BLOCK_DISPLAY);
        overlayDisplay.setBlock(Material.RED_STAINED_GLASS.createBlockData());
        overlayDisplay.setBrightness(new Display.Brightness(15, 15));
        overlayDisplay.setPersistent(false);
        overlayDisplay.setRotation(0, 0);

        Transformation transform = display.getTransformation();
        transform.getTranslation().set(-0.5, -0.5, -0.5);
        transform.getLeftRotation().set(0, 0, 0, 1);
        display.setTransformation(transform);
        overlayDisplay.setTransformation(transform);
        overlayDisplay.getTransformation().getScale().set(1.0005, 1.0005, 1.0005);

        Interaction interaction = (Interaction) world.spawnEntity(loc.subtract(0, 0.5, 0), EntityType.INTERACTION);
        interaction.setInteractionWidth(1.001f);
        interaction.setInteractionHeight(1.001f);
        interaction.setResponsive(true);
        interaction.setPersistent(false);

        interactionToBlock.put(interaction, portalBlock);
        interactionToDisplay.put(interaction, List.of(display, overlayDisplay));
    }

    public void despawn() {
        interactionToDisplay.forEach((interaction, displays) -> displays.forEach(BlockDisplay::remove));
        interactionToBlock.forEach((interaction, block) -> interaction.remove());
        interactionToBlock.clear();
        interactionToDisplay.clear();
    }

    public void completePortalBlock(Interaction interaction) {
        var block = interactionToBlock.get(interaction);
        state.getBlock(block).complete();
        interactionToDisplay.get(interaction).forEach(BlockDisplay::remove);
        interaction.remove();

        if (isCompleted(state)) {
            netherPortalManager.onPortalComplete();
        }

        netherPortalManager.updateBossBarProgress(state);
    }

    public boolean isPortalBlock(Entity entity) {
        return interactionToBlock.keySet().stream()
                .anyMatch(interaction -> interaction.getUniqueId().equals(entity.getUniqueId()));
    }

    public PortalBlock getPortalBlock(Interaction interaction) {
        return interactionToBlock.get(interaction);
    }

    public boolean isCompleted(PortalState state) {
        return state.blocks().stream().filter(portalBlockState -> portalBlockState.block().isFrameBlock()).allMatch(PortalBlockState::completed);
    }
}