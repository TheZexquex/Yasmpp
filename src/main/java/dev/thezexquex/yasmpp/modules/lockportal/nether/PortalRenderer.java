package dev.thezexquex.yasmpp.modules.lockportal.nether;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import java.util.UUID;

/**
 * Shows the missing frame blocks of the portal as holograms. Holds no portal state itself, it only knows
 * which entities it spawned for which blueprint index.
 */
public class PortalRenderer {
    public static final NamespacedKey PORTAL_KEY = new NamespacedKey("yasmpp", "portal");
    public static final NamespacedKey BLOCK_INDEX_KEY = new NamespacedKey("yasmpp", "portal_block_index");

    private final Map<Integer, List<UUID>> entitiesByIndex = new HashMap<>();
    // Every entity spawned by this renderer. Tagged entities not in here are leftovers and get removed on load.
    private final Set<UUID> ownedEntities = new HashSet<>();
    private World world;

    /**
     * Replaces all holograms with one per given blueprint index.
     */
    public void render(Location origin, PortalBlueprint blueprint, Iterable<Integer> indices) {
        despawn();
        // Also clears leftovers in the target world, which may differ from the previous one
        this.world = origin.getWorld();
        despawn();
        for (int index : indices) {
            spawnBlueprintBlock(origin, index, blueprint.block(index));
        }
    }

    private void spawnBlueprintBlock(Location base, int index, PortalBlock portalBlock) {
        Location loc = base.clone().add(portalBlock.x(), portalBlock.y(), portalBlock.z()).toCenterLocation();

        BlockDisplay display = (BlockDisplay) world.spawnEntity(loc, EntityType.BLOCK_DISPLAY);
        display.setBlock(portalBlock.createBlockData());
        display.setBrightness(new Display.Brightness(15, 15));
        display.setGlowing(true);
        display.setGlowColorOverride(Color.RED);
        display.setRotation(0, 0);
        tag(display, index);

        BlockDisplay overlayDisplay = (BlockDisplay) world.spawnEntity(loc, EntityType.BLOCK_DISPLAY);
        overlayDisplay.setBlock(Material.RED_STAINED_GLASS.createBlockData());
        overlayDisplay.setBrightness(new Display.Brightness(15, 15));
        overlayDisplay.setRotation(0, 0);
        tag(overlayDisplay, index);

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
        tag(interaction, index);

        var uuids = List.of(display.getUniqueId(), overlayDisplay.getUniqueId(), interaction.getUniqueId());
        entitiesByIndex.put(index, uuids);
        ownedEntities.addAll(uuids);
    }

    private void tag(Entity entity, int index) {
        // Persistent, because non persistent entities are not spawned reliably in unloaded chunks
        entity.setPersistent(true);
        entity.getPersistentDataContainer().set(PORTAL_KEY, PersistentDataType.BYTE, (byte) 1);
        entity.getPersistentDataContainer().set(BLOCK_INDEX_KEY, PersistentDataType.INTEGER, index);
    }

    /**
     * Removes the hologram of a single blueprint index.
     */
    public void remove(int index) {
        var uuids = entitiesByIndex.remove(index);
        if (uuids == null) {
            return;
        }
        uuids.forEach(uuid -> {
            ownedEntities.remove(uuid);
            removeEntity(uuid);
        });
    }

    /**
     * Removes all own entities and all tagged entities in loaded chunks.
     * Tagged entities in unloaded chunks are removed by {@link PortalEntityCleanupListener} once they load.
     */
    public void despawn() {
        ownedEntities.forEach(this::removeEntity);
        entitiesByIndex.clear();
        ownedEntities.clear();

        if (world == null) {
            return;
        }
        world.getEntities().forEach(entity -> {
            if (isPortalEntity(entity)) {
                entity.remove();
            }
        });
    }

    private void removeEntity(UUID uuid) {
        var entity = Bukkit.getEntity(uuid);
        if (entity != null) {
            entity.remove();
        }
    }

    public boolean owns(Entity entity) {
        return ownedEntities.contains(entity.getUniqueId());
    }

    /**
     * @return the blueprint index of an interaction spawned by this renderer
     */
    public OptionalInt blockIndex(Entity entity) {
        if (!(entity instanceof Interaction) || !owns(entity)) {
            return OptionalInt.empty();
        }
        var index = entity.getPersistentDataContainer().get(BLOCK_INDEX_KEY, PersistentDataType.INTEGER);
        return index == null ? OptionalInt.empty() : OptionalInt.of(index);
    }

    public static boolean isPortalEntity(Entity entity) {
        return entity.getPersistentDataContainer().has(PORTAL_KEY, PersistentDataType.BYTE);
    }
}
