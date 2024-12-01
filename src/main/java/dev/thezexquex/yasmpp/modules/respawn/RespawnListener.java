package dev.thezexquex.yasmpp.modules.respawn;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.data.adapter.LocationAdapter;
import dev.thezexquex.yasmpp.data.database.future.BukkitFutureResult;
import dev.thezexquex.yasmpp.message.LocationPlaceholders;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.spongepowered.configurate.NodePath;

public class RespawnListener implements Listener {
    private final YasmpPlugin plugin;

    public RespawnListener(YasmpPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRespawn(PlayerPostRespawnEvent event) {
        var player = event.getPlayer();

        if (player.getPotentialBedLocation() != null) {
            plugin.messenger().sendMessage(
                    player,
                    NodePath.path("event", "death", "bed")
            );
            return;
        }

        BukkitFutureResult.of(plugin.locationService().getLocation("spawn")).whenComplete(plugin, location -> {
            location.ifPresent(worldPosition -> {
                player.teleport(LocationAdapter.adapt(worldPosition.locationContainer(), player.getServer()));
                plugin.messenger().sendMessage(
                        player,
                        NodePath.path("event", "death", "no-bed")
                );
            });
        });
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        var player = event.getEntity();
        var deathLocation = player.getLocation();

        plugin.messenger().sendMessage(
                player,
                NodePath.path("event", "death", "coords"),
                LocationPlaceholders.of(deathLocation)
        );
    }
}
