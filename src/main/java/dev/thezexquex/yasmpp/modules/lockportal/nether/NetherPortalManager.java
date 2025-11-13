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

public class NetherPortalManager {
    private final YasmpPlugin plugin;
    private PortalRenderer portalRenderer;
    private BossBarManager bossBarManager;
    private PortalProgress progress = PortalProgress.NOT_STARTED;
    private final PortalScanSession scanSession = new PortalScanSession(null, null);

    public NetherPortalManager(YasmpPlugin plugin) {
        this.plugin = plugin;
        this.bossBarManager = new BossBarManager(plugin, this);
    }

    public void onPortalComplete() {
        this.progress = PortalProgress.COMPLETED;
        lightPortal(plugin.portalConfiguration().state());

        plugin.getServer().getOnlinePlayers().forEach(player -> {
            player.playSound(player, "minecraft:entity.wither.death", 1.0f, 0.1f);
            plugin.messenger().sendTitle(player,
                    NodePath.path("event", "nether", "unlock", "title"),
                    NodePath.path("event", "nether", "unlock", "subtitlle"),
                    Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(3)));
        });

        plugin.configuration().general().portals().lockNether(false);
        plugin.portalConfiguration().save();
    }

    public void lightPortal(PortalState state) {
        var baseLocation = state.origin().toLocation();
        state.blocks().forEach(portalBlockState -> {
            var block = portalBlockState.block();
            if (block.isPortalFill()) {
                var loc = baseLocation.clone().add(block.x(), block.y(), block.z());
                var worldBlock = loc.getBlock();
                worldBlock.setType(Material.NETHER_PORTAL);

                if (block.getBlockData() != null) {
                    try {
                        var blockData = plugin.getServer().createBlockData(block.getBlockData());
                        worldBlock.setBlockData(blockData);
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Could not parse block data: " + block.getBlockData());
                    }
                }
            }
        });
    }

    public enum PortalProgress {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED
    }

    public PortalScanSession scanSession() {
        return scanSession;
    }


    public void tryScanPortal(Player player) {
        if (scanSession.pos1() == null || scanSession.pos2() == null) {
            return;
        }

        var portal = PortalScanner.scanStructure(
                player.getWorld() ,
                scanSession.pos1(),
                scanSession.pos2()
        );

        scanSession.pos1(null);
        scanSession.pos2(null);

        plugin.portalConfiguration().blueprint(portal);
        plugin.portalConfiguration().state(null);
        plugin.portalConfiguration().save();
        player.sendMessage(Component.text("Portal successfully scanned! " + portal.blocks().size() + " blocks found.").color(NamedTextColor.GREEN));
    }

    public void spawnPortalAt(Location originLoc) {
        var blueprint = plugin.portalConfiguration().blueprint();
        var state = plugin.portalConfiguration().state();
        if (state == null || state.origin() == null || state.blocks() == null || state.blocks().isEmpty()) {
            state = new PortalState(Origin.fromLocation(originLoc), blueprint.blocks().stream().map(PortalBlockState::new).toList());
        }
        state.origin(Origin.fromLocation(originLoc));
        plugin.portalConfiguration().state(state);
        plugin.portalConfiguration().save();
        if (portalRenderer != null) {
            portalRenderer.despawn();
        }
        portalRenderer = new PortalRenderer(this, blueprint, state);
        if (portalRenderer.isCompleted(state)) {
            return;
        }
        this.progress = PortalProgress.NOT_STARTED;
        portalRenderer.render();
        this.bossBarManager.startShowingBossBar();
    }

    public void respawnPortalOnServerStart() {
        var state = plugin.portalConfiguration().state();
        if (state == null || state.origin() == null || state.blocks() == null || state.blocks().isEmpty()) {
            plugin.getLogger().warning("No portal state found. Skipping respawn.");
            return;
        }

        if (portalRenderer != null) {
            portalRenderer.despawn();
        }

        portalRenderer = new PortalRenderer(this, plugin.portalConfiguration().blueprint(), state);
        if (portalRenderer.isCompleted(state)) {
            return;
        }
        portalRenderer.render();
        this.bossBarManager.startShowingBossBar();
    }

    public void onDisable() {
        portalRenderer.despawn();
    }

    public void updateBossBarProgress(PortalState portalState) {
        if (this.progress == PortalProgress.COMPLETED) {
            return;
        }
        var completedBlocks = portalState.blocks().stream()
                .filter(bs -> bs.block().isFrameBlock() && bs.completed())
                .count();
        var totalBlocks = plugin.portalConfiguration().blueprint().blocks().stream()
                .filter(PortalBlock::isFrameBlock)
                .count();

        bossBarManager.updateBossBar(completedBlocks, totalBlocks);
    }

    public PortalRenderer portalRenderer() {
        return portalRenderer;
    }

    public PortalProgress progress() {
        return progress;
    }
}
