package dev.thezexquex.yasmpp.modules.spawnelytra.listener;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import dev.thezexquex.yasmpp.YasmpPlugin;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ArmorChangeListener implements Listener {
    private final YasmpPlugin plugin;

    public ArmorChangeListener(YasmpPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAmorChange(PlayerArmorChangeEvent event) {
        var player = event.getPlayer();
        if (event.getNewItem().getType() != Material.ELYTRA) {
            return;
        }

        if (!plugin.elytraManager().isFlying(player)) {
            return;
        }

        plugin.elytraManager().disableElytra(player, true);
    }
}
