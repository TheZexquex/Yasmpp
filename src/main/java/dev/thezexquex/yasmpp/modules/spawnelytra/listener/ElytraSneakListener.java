package dev.thezexquex.yasmpp.modules.spawnelytra.listener;

import de.unknowncity.astralib.common.temporal.PlayerBoundCooldownAction;
import dev.thezexquex.yasmpp.YasmpPlugin;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.spongepowered.configurate.NodePath;

import java.time.Duration;

public class ElytraSneakListener implements Listener {
    private final YasmpPlugin plugin;
    private final PlayerBoundCooldownAction cooldownAction = new PlayerBoundCooldownAction(Duration.ofSeconds(3));

    public ElytraSneakListener(YasmpPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        var player = event.getPlayer();

        if (player.isSneaking()) {
            return;
        }

        if (!plugin.elytraManager().isFlying(player)) {
            return;
        }

        var boostsLeft = plugin.elytraManager().getBoostsLeft(player);
        if (boostsLeft > 0) {
            plugin.elytraManager().boostElytra(player);
            if (boostsLeft > 1) {
                boostsLeft = plugin.elytraManager().getBoostsLeft(player);
                plugin.messenger().sendMessage(
                        player,
                        NodePath.path("event", "elytra-boost", "remaining"),
                        TagResolver.resolver("boosts", Tag.preProcessParsed(String.valueOf(boostsLeft)))
                );
            } else {
                plugin.messenger().sendMessage(
                        player,
                        NodePath.path("event", "elytra-boost", "all-used")
                );
            }
        } else {
            cooldownAction.executeBoundToPlayer(player.getUniqueId(), () -> {
                plugin.messenger().sendMessage(
                        player,
                        NodePath.path("event", "elytra-boost", "all-used")
                );
            });
        }
    }
}
