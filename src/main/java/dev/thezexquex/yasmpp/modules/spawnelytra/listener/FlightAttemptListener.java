package dev.thezexquex.yasmpp.modules.spawnelytra.listener;

import dev.thezexquex.yasmpp.YasmpPlugin;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class FlightAttemptListener implements Listener {
    private final YasmpPlugin plugin;

    public FlightAttemptListener(YasmpPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFlightAttempt(PlayerToggleFlightEvent event) {
        var player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }

        if (player.hasPermission("yasmpp.fly.survival")) {
            return;
        }

        event.setCancelled(true);
        plugin.elytraManager().enableElytra(player);
    }
}
