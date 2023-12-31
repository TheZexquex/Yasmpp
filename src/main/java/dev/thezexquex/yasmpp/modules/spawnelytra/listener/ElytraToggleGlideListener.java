package dev.thezexquex.yasmpp.modules.spawnelytra.listener;

import dev.thezexquex.yasmpp.YasmpPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;

@SuppressWarnings("deprecation")
public class ElytraToggleGlideListener implements Listener {
    private final YasmpPlugin plugin;

    public ElytraToggleGlideListener(YasmpPlugin plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onToggleGlide(EntityToggleGlideEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (!player.isGliding()) {
            return;
        }
        if (!plugin.elytraManager().isFlying(player)) {
            return;
        }

        event.setCancelled(true);
    }
}
