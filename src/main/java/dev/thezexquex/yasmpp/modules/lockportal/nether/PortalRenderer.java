package dev.thezexquex.yasmpp.modules.lockportal.nether;

import dev.thezexquex.yasmpp.YasmpPlugin;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Transformation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PortalRenderer {
    private final PortalBlueprint blueprint;
    private final PortalState state;
    private final NetherPortalManager netherPortalManager;
    private final Map<UUID, PortalBlock> interactionToBlock = new HashMap<>();
    private final Map<UUID, List<UUID>> interactionToDisplay = new HashMap<>();
    private final NamespacedKey portalKey = new NamespacedKey("yasmpp", "portal");

    public PortalRenderer(NetherPortalManager netherPortalManager, PortalBlueprint blueprint, PortalState state) {
        this.blueprint = blueprint;
        this.state = state;
        this.netherPortalManager = netherPortalManager;
    }

    public void render() {
        var origin = state.origin().toLocation();
        for (var block : state.blocks() ) {
            var placedBlock = block.block();
            if (placedBlock.isFrameBlock() && !block.completed()) {
                spawnBlueprintBlock(origin.getWorld(), origin, placedBlock);
            }
        }
        netherPortalManager.updateBossBarProgress(state);
    }

    private void spawnBlueprintBlock(World world, Location base, PortalBlock portalBlock) {
        Location loc = base.clone().add(portalBlock.x(), portalBlock.y(), portalBlock.z()).toCenterLocation();
        JavaPlugin.getPlugin(YasmpPlugin.class).getLogger().info("Spawning portal block at " + loc.toVector());

        BlockDisplay display = (BlockDisplay) world.spawnEntity(loc, EntityType.BLOCK_DISPLAY);
        display.setBlock(portalBlock.getMaterial().createBlockData());
        display.setBrightness(new Display.Brightness(15, 15));
        display.setGlowing(true);
        display.setGlowColorOverride(Color.RED);
        display.setPersistent(true);
        display.setRotation(0, 0);
        display.getPersistentDataContainer().set(portalKey, PersistentDataType.BYTE, (byte) 1);

        BlockDisplay overlayDisplay = (BlockDisplay) world.spawnEntity(loc, EntityType.BLOCK_DISPLAY);
        overlayDisplay.setBlock(Material.RED_STAINED_GLASS.createBlockData());
        overlayDisplay.setBrightness(new Display.Brightness(15, 15));
        overlayDisplay.setPersistent(true);
        overlayDisplay.setRotation(0, 0);
        overlayDisplay.getPersistentDataContainer().set(portalKey, PersistentDataType.BYTE, (byte) 1);

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
        interaction.setPersistent(true);
        interaction.getPersistentDataContainer().set(portalKey, PersistentDataType.BYTE, (byte) 1);

        JavaPlugin.getPlugin(YasmpPlugin.class).getLogger().info(interaction + "-----" + portalBlock);
        interactionToBlock.put(interaction.getUniqueId(), portalBlock);
        interactionToDisplay.put(interaction.getUniqueId(), List.of(display.getUniqueId(), overlayDisplay.getUniqueId()));
    }

    public void despawn() {
        interactionToBlock.clear();
        interactionToDisplay.clear();
        state.origin().toLocation().getWorld().getEntities().forEach(entity -> {
            if (entity.getPersistentDataContainer().has(portalKey, PersistentDataType.BYTE)) {
                entity.remove();
            }
        });
    }

    public void completePortalBlock(Entity interaction) {
        var block = interactionToBlock.get(interaction.getUniqueId());
        state.getBlock(block).complete();
        interactionToDisplay.get(interaction.getUniqueId()).forEach(uuid -> {
            var entity = Bukkit.getEntity(uuid);
            if (entity != null) {
                entity.remove();
            }
        });
        interaction.remove();

        if (isCompleted(state)) {
            netherPortalManager.onPortalComplete();
        }

        netherPortalManager.updateBossBarProgress(state);
    }

    public boolean isPortalBlock(Entity entity) {
        return interactionToBlock.keySet().stream()
                .anyMatch(interaction -> interaction.equals(entity.getUniqueId()));
    }

    public PortalBlock getPortalBlock(Entity interaction) {
        JavaPlugin.getPlugin(YasmpPlugin.class).getLogger().info("Getting portal block for interaction " + interaction);
        return interactionToBlock.get(interaction.getUniqueId());
    }

    public boolean isCompleted(PortalState state) {
        return state.blocks().stream().filter(portalBlockState -> portalBlockState.block().isFrameBlock()).allMatch(PortalBlockState::completed);
    }
}