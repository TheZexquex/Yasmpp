package dev.thezexquex.yasmpp.modules.teleport;

import dev.thezexquex.yasmpp.YasmpPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.spongepowered.configurate.NodePath;

public class CancelTeleportListener implements Listener {
    private final YasmpPlugin plugin;

    public CancelTeleportListener(YasmpPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        var player = event.getPlayer();

        if (!plugin.teleportQueue().isTeleporting(player)) {
            return;
        }

        if (!event.getFrom().getBlock().equals(event.getTo().getBlock())) {
            plugin.teleportQueue().cancelTeleport(player);
            plugin.messenger().sendMessage(
                    player,
                    NodePath.path("event", "teleport", "cancel")
            );
        }
    }
}
