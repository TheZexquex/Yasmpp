package dev.thezexquex.yasmpp.modules.lockportal.nether;

import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.configuration.PortalConfiguration;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Owns the portal flow: spawning, placing frame blocks and completing the portal.
 * The blueprint and state are always read from the current portal configuration and never cached,
 * so a reload of the configuration can not leave stale references behind.
 */
public class NetherPortalManager {
    private final YasmpPlugin plugin;
    private final PortalRenderer renderer = new PortalRenderer();
    private final List<PortalProgressListener> listeners = new ArrayList<>();

    public NetherPortalManager(YasmpPlugin plugin) {
        this.plugin = plugin;
    }

    public void addListener(PortalProgressListener listener) {
        listeners.add(listener);
    }

    public enum PlaceResult {
        PLACED,
        PORTAL_COMPLETED,
        WRONG_ITEM,
        NOT_PLACEABLE
    }

    public PlaceResult placeBlock(Player player, int index) {
        var blueprint = config().blueprint();
        var state = config().state();
        if (!isValid(blueprint, state) || !blueprint.isFrameIndex(index) || state.isCompleted(index)) {
            return PlaceResult.NOT_PLACEABLE;
        }
        var origin = state.origin().toLocation();
        if (origin.isEmpty()) {
            return PlaceResult.NOT_PLACEABLE;
        }

        var portalBlock = blueprint.block(index);
        var handItem = player.getInventory().getItemInMainHand();
        if (handItem.getType() != portalBlock.getMaterial()) {
            return PlaceResult.WRONG_ITEM;
        }
        if (player.getGameMode() != GameMode.CREATIVE) {
            handItem.setAmount(handItem.getAmount() - 1);
        }

        PortalBlocks.place(origin.get(), portalBlock);
        state.complete(index);
        renderer.remove(index);
        plugin.portalConfiguration().save();

        if (state.isPortalComplete(blueprint)) {
            listeners.forEach(listener -> listener.onCompleted(origin.get(), blueprint));
            return PlaceResult.PORTAL_COMPLETED;
        }
        notifyProgress(blueprint, state);
        return PlaceResult.PLACED;
    }

    /**
     * Replaces the blueprint. The current state refers to the old blueprint, so it is discarded.
     */
    public void replaceBlueprint(PortalBlueprint blueprint) {
        despawnPortal();
        config().blueprint(blueprint);
        config().state(null);
        plugin.portalConfiguration().save();
    }

    /**
     * Moves the portal to the given location, keeping its progress. Already placed blocks are moved along.
     *
     * @return false if no blueprint has been scanned yet
     */
    public boolean spawnPortalAt(Location originLoc) {
        var blueprint = config().blueprint();
        if (blueprint == null || !blueprint.hasBlocks()) {
            return false;
        }

        var state = config().state();
        if (state == null) {
            state = new PortalState();
            config().state(state);
        }

        var newOrigin = Origin.fromLocation(originLoc);
        var oldOrigin = state.origin() == null ? null : state.origin().toLocation().orElse(null);
        if (oldOrigin != null && !state.origin().equals(newOrigin) && blueprint.invalidBlockIndices().isEmpty()) {
            state.retainValid(blueprint);
            // Block aligned, like the origin the holograms are rendered at
            PortalBlocks.move(oldOrigin, newOrigin.toLocation().orElseThrow(), blueprint, state.completed(), state.isPortalComplete(blueprint));
        } else if (oldOrigin == null && state.origin() != null && !state.completed().isEmpty()) {
            plugin.getLogger().warning("World '" + state.origin().world() + "' of the portal is not loaded. Placed blocks were not moved.");
        }
        state.origin(newOrigin);
        plugin.portalConfiguration().save();

        respawnPortal();
        return true;
    }

    /**
     * Rebuilds the holograms from the stored state.
     */
    public void respawnPortal() {
        despawnPortal();

        var blueprint = config().blueprint();
        var state = config().state();
        if (!isValid(blueprint, state)) {
            plugin.getLogger().warning("No portal state found. Skipping respawn.");
            return;
        }

        var invalidBlocks = blueprint.invalidBlockIndices();
        if (!invalidBlocks.isEmpty()) {
            plugin.getLogger().severe("The portal blueprint contains unknown materials at block indices " + invalidBlocks + ". Rescan the portal.");
            return;
        }

        var removed = state.retainValid(blueprint);
        if (removed > 0) {
            plugin.getLogger().warning("Removed " + removed + " completed portal blocks that do not belong to the blueprint.");
            plugin.portalConfiguration().save();
        }

        var origin = state.origin().toLocation();
        if (origin.isEmpty()) {
            plugin.getLogger().warning("World '" + state.origin().world() + "' of the portal is not loaded. Skipping respawn.");
            return;
        }

        if (state.isPortalComplete(blueprint)) {
            return;
        }

        var missing = blueprint.frameIndices().filter(index -> !state.isCompleted(index)).boxed().toList();
        renderer.render(origin.get(), blueprint, missing);
        notifyProgress(blueprint, state);
    }

    private void despawnPortal() {
        renderer.despawn();
        listeners.forEach(PortalProgressListener::onHidden);
    }

    private void notifyProgress(PortalBlueprint blueprint, PortalState state) {
        var completed = state.completedFrameCount(blueprint);
        var total = blueprint.frameBlockCount();
        listeners.forEach(listener -> listener.onProgress(completed, total));
    }

    private boolean isValid(PortalBlueprint blueprint, PortalState state) {
        return blueprint != null && blueprint.hasBlocks() && state != null && state.origin() != null;
    }

    private PortalConfiguration config() {
        return plugin.portalConfiguration().main();
    }

    public void onDisable() {
        despawnPortal();
    }

    /**
     * The portal configuration is recreated on reload, so the holograms are rebuilt from the new state.
     */
    public void reload() {
        respawnPortal();
    }

    public PortalRenderer portalRenderer() {
        return renderer;
    }
}
