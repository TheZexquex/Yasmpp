package dev.thezexquex.yasmpp.modules.spawnelytra.listener;

import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent;
import de.unknowncity.astralib.common.temporal.PlayerBoundCooldownAction;
import dev.thezexquex.yasmpp.YasmpPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spongepowered.configurate.NodePath;

import java.time.Duration;

public class ElytraBoostListener implements Listener {
    private final YasmpPlugin plugin;
    private final PlayerBoundCooldownAction cooldownAction = new PlayerBoundCooldownAction(Duration.ofSeconds(3));

    public ElytraBoostListener(YasmpPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onElytraBoost(PlayerElytraBoostEvent event) {
        var player = event.getPlayer();
        if (!player.isGliding()) {
            return;
        }
        if (!plugin.elytraManager().isFlying(player)) {
            return;
        }

        cooldownAction.executeBoundToPlayer(player.getUniqueId(), () -> {
            plugin.messenger().sendMessage(
                    player,
                    NodePath.path("event", "elytra-boost", "firework")
            );
        });

        event.setCancelled(true);
    }
}
