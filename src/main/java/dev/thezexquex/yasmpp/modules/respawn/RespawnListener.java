package dev.thezexquex.yasmpp.modules.respawn;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import dev.thezexquex.yasmpp.YasmpPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spongepowered.configurate.NodePath;

public class RespawnListener implements Listener {
    private final YasmpPlugin plugin;

    public RespawnListener(YasmpPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRespawn(PlayerPostRespawnEvent event) {
        var player = event.getPlayer();

        if (player.getBedSpawnLocation() != null) {
            return;
        }

        if (!plugin.locationService().existsCachedLocation("spawn")) {
            return;
        }

        player.teleport(plugin.locationService().getCachedLocation("spawn"));
        plugin.messenger().sendMessage(
                player,
                NodePath.path("event", "death", "no-bed")
        );
    }
}
