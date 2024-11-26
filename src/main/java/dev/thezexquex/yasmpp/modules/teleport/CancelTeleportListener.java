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

        plugin.smpPlayerService().getSmpPlayer(player).ifPresent(smpPlayer -> {
            if (!smpPlayer.isInTeleportWarmup()) {
                return;
            }

            if (!event.getFrom().getBlock().equals(event.getTo().getBlock())) {
                smpPlayer.cancelCurrentTeleport();
                plugin.messenger().sendMessage(
                        player,
                        NodePath.path("event", "teleport", "cancel")
                );
            }
        });


    }
}
