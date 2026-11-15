package dev.thezexquex.yasmpp.modules.lockportal.nether;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PortalRenderer {
    public static final NamespacedKey PORTAL_KEY = new NamespacedKey("yasmpp", "portal");

    private final PortalState state;
    private final NetherPortalManager netherPortalManager;
    private final Map<UUID, PortalBlock> interactionToBlock = new HashMap<>();
    private final Map<UUID, List<UUID>> interactionToDisplay = new HashMap<>();
    // Every entity spawned by this renderer. Tagged entities not in here are leftovers and get removed on load.
    private final Set<UUID> ownedEntities = new HashSet<>();

    public PortalRenderer(NetherPortalManager netherPortalManager, PortalState state) {
        this.state = state;
        this.netherPortalManager = netherPortalManager;
    }

    public void render() {
        var origin = state.origin().toLocation();
        for (var block : state.blocks()) {
            var placedBlock = block.block();
            if (placedBlock.isFrameBlock() && !block.completed()) {
                spawnBlueprintBlock(origin.getWorld(), origin, placedBlock);
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
        display.setPersistent(true);
        display.setRotation(0, 0);
        display.getPersistentDataContainer().set(PORTAL_KEY, PersistentDataType.BYTE, (byte) 1);

        BlockDisplay overlayDisplay = (BlockDisplay) world.spawnEntity(loc, EntityType.BLOCK_DISPLAY);
        overlayDisplay.setBlock(Material.RED_STAINED_GLASS.createBlockData());
        overlayDisplay.setBrightness(new Display.Brightness(15, 15));
        overlayDisplay.setPersistent(true);
        overlayDisplay.setRotation(0, 0);
        overlayDisplay.getPersistentDataContainer().set(PORTAL_KEY, PersistentDataType.BYTE, (byte) 1);

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
        interaction.getPersistentDataContainer().set(PORTAL_KEY, PersistentDataType.BYTE, (byte) 1);

        interactionToBlock.put(interaction.getUniqueId(), portalBlock);
        interactionToDisplay.put(interaction.getUniqueId(), List.of(display.getUniqueId(), overlayDisplay.getUniqueId()));
        ownedEntities.add(display.getUniqueId());
        ownedEntities.add(overlayDisplay.getUniqueId());
        ownedEntities.add(interaction.getUniqueId());
    }

    /**
     * Removes all own entities and all tagged entities in loaded chunks.
     * Tagged entities in unloaded chunks are removed by {@link PortalEntityCleanupListener} once they load.
     */
    public void despawn() {
        ownedEntities.forEach(uuid -> {
            var entity = Bukkit.getEntity(uuid);
            if (entity != null) {
                entity.remove();
            }
        });
        interactionToBlock.clear();
        interactionToDisplay.clear();
        ownedEntities.clear();

        var world = state.origin().toLocation().getWorld();
        if (world == null) {
            return;
        }
        world.getEntities().forEach(entity -> {
            if (isPortalEntity(entity)) {
                entity.remove();
            }
        });
    }

    public void completePortalBlock(Entity interaction) {
        var block = interactionToBlock.remove(interaction.getUniqueId());
        if (block == null) {
            return;
        }
        var blockState = state.getBlock(block);
        if (blockState == null || blockState.completed()) {
            return;
        }
        blockState.complete();

        var displays = interactionToDisplay.remove(interaction.getUniqueId());
        if (displays != null) {
            displays.forEach(uuid -> {
                ownedEntities.remove(uuid);
                var entity = Bukkit.getEntity(uuid);
                if (entity != null) {
                    entity.remove();
                }
            });
        }
        ownedEntities.remove(interaction.getUniqueId());
        interaction.remove();

        if (isCompleted(state)) {
            netherPortalManager.onPortalComplete();
        }

        netherPortalManager.updateBossBarProgress(state);
    }

    public boolean isPortalBlock(Entity entity) {
        return interactionToBlock.containsKey(entity.getUniqueId());
    }

    public boolean owns(Entity entity) {
        return ownedEntities.contains(entity.getUniqueId());
    }

    public PortalBlock getPortalBlock(Entity interaction) {
        return interactionToBlock.get(interaction.getUniqueId());
    }

    public boolean isCompleted(PortalState state) {
        return state.blocks().stream().filter(portalBlockState -> portalBlockState.block().isFrameBlock()).allMatch(PortalBlockState::completed);
    }

    public static boolean isPortalEntity(Entity entity) {
        return entity.getPersistentDataContainer().has(PORTAL_KEY, PersistentDataType.BYTE);
    }
}
