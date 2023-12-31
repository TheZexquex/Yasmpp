package dev.thezexquex.yasmpp.modules.spawnelytra.listener;

import dev.thezexquex.yasmpp.YasmpPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ElytraSavetyPlayerJoinListener implements Listener {
    private final YasmpPlugin plugin;

    public ElytraSavetyPlayerJoinListener(YasmpPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (plugin.elytraManager().isFlying(event.getPlayer())) {
            event.getPlayer().setGliding(true);
        }
    }
}
