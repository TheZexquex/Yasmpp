package dev.thezexquex.yasmpp.modules.joinleavemessage;

import dev.thezexquex.yasmpp.YasmpPlugin;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.spongepowered.configurate.NodePath;

public class PlayerJoinListener implements Listener {
    private final YasmpPlugin plugin;

    public PlayerJoinListener(YasmpPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();

        plugin.smpPlayerService().addSmpPlayer(player);

        if (player.hasPlayedBefore()) {
            var messageComponent = plugin.messenger().component(
                    NodePath.path("event", "join"),
                    Placeholder.component("player", player.name())
            );

            event.joinMessage(messageComponent);
            return;
        }

        var messageComponent = plugin.messenger().component(
                NodePath.path("event", "first-join"),
                Placeholder.component("player", player.name())
        );


        event.joinMessage(messageComponent);


        var locationService = plugin.locationService();
        if (!locationService.existsCachedLocation("spawn")) {
            plugin.messenger().sendMessage(player, NodePath.path("command", "spawn", "no-spawn"));
            return;
        }


        player.teleport(locationService.getCachedLocation("spawn"));
    }
}
