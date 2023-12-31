package dev.thezexquex.yasmpp.modules.spawnelytra.listener;

import dev.thezexquex.yasmpp.YasmpPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;

public class ElytraToggleOnVehicleEnterListener implements Listener {
    private final YasmpPlugin plugin;

    public ElytraToggleOnVehicleEnterListener(YasmpPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player player)) {
            return;
        }
        plugin.elytraManager().disableElytra(player, false);
    }
}
