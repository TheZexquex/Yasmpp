package dev.thezexquex.yasmpp.modules.lockportal.nether;

import dev.thezexquex.yasmpp.YasmpPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.NodePath;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NetherPortalManager {
    private static final long MAX_SCAN_VOLUME = 64 * 64 * 64;

    private final YasmpPlugin plugin;
    private PortalRenderer portalRenderer;
    private final BossBarManager bossBarManager;
    private PortalProgress progress = PortalProgress.NOT_STARTED;
    private final Map<UUID, PortalScanSession> scanSessions = new HashMap<>();

    public NetherPortalManager(YasmpPlugin plugin) {
        this.plugin = plugin;
        this.bossBarManager = new BossBarManager(plugin);
    }

    public void onPortalComplete() {
        this.progress = PortalProgress.COMPLETED;
        bossBarManager.hide();
        lightPortal(plugin.portalConfiguration().main().state());

        plugin.getServer().getOnlinePlayers().forEach(player -> {
            player.playSound(player, "minecraft:entity.wither.death", 1.0f, 0.1f);
            plugin.messenger().sendTitle(player,
                    NodePath.path("event", "nether", "unlock", "title"),
                    NodePath.path("event", "nether", "unlock", "subtitle"),
                    Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(3)));
        });

        plugin.configuration().main().general().portals().lockNether(false);
        plugin.configuration().save();
        plugin.portalConfiguration().save();
    }

    public void lightPortal(PortalState state) {
        var baseLocation = state.origin().toLocation();
        state.blocks().forEach(portalBlockState -> {
            var block = portalBlockState.block();
            if (block.isPortalFill()) {
                var loc = baseLocation.clone().add(block.x(), block.y(), block.z());
                var worldBlock = loc.getBlock();

                var blockData = Material.NETHER_PORTAL.createBlockData();
                if (block.getBlockData() != null) {
                    try {
                        blockData = plugin.getServer().createBlockData(block.getBlockData());
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Could not parse block data: " + block.getBlockData());
                    }
                }
                // Without physics, otherwise portal blocks break while their neighbours are not set yet
                worldBlock.setBlockData(blockData, false);
            }
        });
    }

    public enum PortalProgress {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED
    }

    public PortalScanSession scanSession(Player player) {
        return scanSessions.computeIfAbsent(player.getUniqueId(), uuid -> new PortalScanSession(null, null));
    }

    public void tryScanPortal(Player player) {
        var scanSession = scanSession(player);
        var pos1 = scanSession.pos1();
        var pos2 = scanSession.pos2();
        if (pos1 == null || pos2 == null) {
            return;
        }

        if (!pos1.getWorld().equals(pos2.getWorld())) {
            scanSession.pos2(null);
            player.sendMessage(Component.text("Both positions must be in the same world.").color(NamedTextColor.RED));
            return;
        }

        long volume = (long) (Math.abs(pos1.getBlockX() - pos2.getBlockX()) + 1)
                * (Math.abs(pos1.getBlockY() - pos2.getBlockY()) + 1)
                * (Math.abs(pos1.getBlockZ() - pos2.getBlockZ()) + 1);
        if (volume > MAX_SCAN_VOLUME) {
            scanSession.pos2(null);
            player.sendMessage(Component.text("Selection too large (" + volume + " > " + MAX_SCAN_VOLUME + " blocks).").color(NamedTextColor.RED));
            return;
        }

        var portal = PortalScanner.scanStructure(pos1.getWorld(), pos1, pos2);
        scanSessions.remove(player.getUniqueId());

        // The old state belongs to the old blueprint, so its holograms have to go as well
        despawnPortal();
        this.progress = PortalProgress.NOT_STARTED;

        plugin.portalConfiguration().main().blueprint(portal);
        plugin.portalConfiguration().main().state(null);
        plugin.portalConfiguration().save();
        player.sendMessage(Component.text("Portal successfully scanned! " + portal.blocks().size() + " blocks found.").color(NamedTextColor.GREEN));
    }

    /**
     * @return false if no blueprint has been scanned yet
     */
    public boolean spawnPortalAt(Location originLoc) {
        var blueprint = plugin.portalConfiguration().main().blueprint();
        if (blueprint == null || blueprint.blocks() == null || blueprint.blocks().isEmpty()) {
            return false;
        }

        var state = plugin.portalConfiguration().main().state();
        if (!isValid(state)) {
            state = new PortalState(Origin.fromLocation(originLoc), blueprint.blocks().stream().map(PortalBlockState::new).toList());
        }
        state.origin(Origin.fromLocation(originLoc));
        plugin.portalConfiguration().main().state(state);
        plugin.portalConfiguration().save();

        renderPortal(state);
        return true;
    }

    public void respawnPortal() {
        despawnPortal();

        var state = plugin.portalConfiguration().main().state();
        if (!isValid(state)) {
            plugin.getLogger().warning("No portal state found. Skipping respawn.");
            return;
        }
        if (state.origin().toLocation().getWorld() == null) {
            plugin.getLogger().warning("World '" + state.origin().world() + "' of the portal is not loaded. Skipping respawn.");
            return;
        }

        renderPortal(state);
    }

    private void renderPortal(PortalState state) {
        despawnPortal();

        portalRenderer = new PortalRenderer(this, state);
        if (portalRenderer.isCompleted(state)) {
            this.progress = PortalProgress.COMPLETED;
            return;
        }
        this.progress = PortalProgress.IN_PROGRESS;
        portalRenderer.render();
        bossBarManager.show();
    }

    private void despawnPortal() {
        if (portalRenderer != null) {
            portalRenderer.despawn();
            portalRenderer = null;
        }
        bossBarManager.hide();
    }

    private boolean isValid(PortalState state) {
        return state != null && state.origin() != null && state.blocks() != null && !state.blocks().isEmpty();
    }

    public void onDisable() {
        despawnPortal();
    }

    /**
     * The portal configuration is recreated on reload, so the renderer has to be rebuilt
     * on the new state object. Otherwise progress is written into the stale one.
     */
    public void reload() {
        respawnPortal();
    }

    public void updateBossBarProgress(PortalState portalState) {
        if (this.progress == PortalProgress.COMPLETED) {
            return;
        }
        var frameBlocks = portalState.blocks().stream()
                .filter(bs -> bs.block().isFrameBlock())
                .toList();
        var completedBlocks = frameBlocks.stream().filter(PortalBlockState::completed).count();

        bossBarManager.updateBossBar(completedBlocks, frameBlocks.size());
    }

    public PortalRenderer portalRenderer() {
        return portalRenderer;
    }

    public PortalProgress progress() {
        return progress;
    }
}
