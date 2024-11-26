package dev.thezexquex.yasmpp.modules.respawn;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.data.adapter.LocationAdapter;
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

        plugin.messenger().sendMessage(
                player,
                NodePath.path("event", "death", "coords")
        );

        if (player.getPotentialBedLocation() != null) {
            plugin.messenger().sendMessage(
                    player,
                    NodePath.path("event", "death", "bed")
            );
            return;
        }

        plugin.locationService().getLocation("spawn").whenComplete((location, throwable) -> {
            location.ifPresent(worldPosition -> {
                player.teleport(LocationAdapter.adapt(worldPosition.locationContainer(), player.getServer()));
                plugin.messenger().sendMessage(
                        player,
                        NodePath.path("event", "death", "no-bed")
                );
            });
        });
    }
}
