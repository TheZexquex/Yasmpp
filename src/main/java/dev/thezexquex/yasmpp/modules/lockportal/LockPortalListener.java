package dev.thezexquex.yasmpp.modules.lockportal;

import de.unknowncity.astralib.common.temporal.PlayerBoundCooldownAction;
import dev.thezexquex.yasmpp.YasmpPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.spongepowered.configurate.NodePath;

import java.time.Duration;

public class LockPortalListener implements Listener {
    private final PlayerBoundCooldownAction endCoolDown = new PlayerBoundCooldownAction(Duration.ofSeconds(3));
    private final PlayerBoundCooldownAction netherCoolDown = new PlayerBoundCooldownAction(Duration.ofSeconds(3));

    private final YasmpPlugin plugin;

    public LockPortalListener(YasmpPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        var player = event.getPlayer();
        var portalSettings = plugin.configuration().general().portals();

        var cause = event.getCause();
        if (cause == PlayerTeleportEvent.TeleportCause.END_PORTAL && portalSettings.lockEnd() ) {
            event.setCancelled(true);
            endCoolDown.executeBoundToPlayer(event.getPlayer().getUniqueId(), () -> {
                plugin.messenger().sendMessage(
                        player,
                        NodePath.path("event", "end", "enter-portal-locked")
                );
            });
        }

        if (cause == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL && portalSettings.lockNether()) {
            event.setCancelled(true);
            netherCoolDown.executeBoundToPlayer(event.getPlayer().getUniqueId(), () -> {
                plugin.messenger().sendMessage(
                        player,
                        NodePath.path("event", "nether", "enter-portal-locked")
                );
            });
        }
    }
}
