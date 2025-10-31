package dev.thezexquex.yasmpp.modules.spawnelytra.listener;

import de.unknowncity.astralib.paper.api.region.SphericalRegion;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.data.adapter.LocationAdapter;
import dev.thezexquex.yasmpp.modules.spawnelytra.integration.FJetpack2Reloaded;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    private final YasmpPlugin plugin;

    public PlayerMoveListener(YasmpPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        var player = event.getPlayer();
        var locationService = plugin.locationService();

        if (plugin.elytraManager().isFlying(player)) {
            if (player.isInLava() || player.isInWater() || player.isOnGround()) {
                plugin.elytraManager().disableElytra(player, false);
            }
            return;
        }

        var spawnLocation = locationService.getLocation("spawn");

        spawnLocation.ifPresent(worldPosition -> {
            var radius = plugin.configuration().general().spawnElytra().radius();
            var region = new SphericalRegion(LocationAdapter.adapt(worldPosition.locationContainer(), player.getServer()), radius);

            if (!region.isInRegion(player)) {
                if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
                    return;
                }

                if (player.hasPermission("yasmpp.fly.survival")) {
                    return;
                }

                if (FJetpack2Reloaded.wearsJetpack(player)) {
                    return;
                }

                player.setAllowFlight(false);
                return;
            }

            player.setAllowFlight(true);
        });
    }
}
