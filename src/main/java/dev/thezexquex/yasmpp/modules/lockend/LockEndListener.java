package dev.thezexquex.yasmpp.modules.lockend;

import dev.thezexquex.yasmpp.YasmpPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.spongepowered.configurate.NodePath;

public class LockEndListener implements Listener {

    private final YasmpPlugin plugin;

    public LockEndListener(YasmpPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            return;
        }
        if (!plugin.configuration().generalSettings().generalEndSettings().lockEnd()) {
            return;
        }

        event.setCancelled(true);

        var player = event.getPlayer();
        if (!player.getScoreboardTags().contains("svp-end-lock-message")) {
            plugin.messenger().sendMessage(
                    player,
                    NodePath.path("event", "end", "enter-portal-locked")
            );
            player.addScoreboardTag("svp-end-lock-message");
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                player.removeScoreboardTag("svp-end-lock-message");
            }, 20*3);
        }
    }
}
