package dev.thezexquex.yasmpp.modules.joinleavemessage;

import de.unknowncity.astralib.common.message.lang.Language;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.data.adapter.LocationAdapter;
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
                    Language.GERMAN,
                    NodePath.path("event", "join"),
                    Placeholder.component("player", player.name())
            );

            event.joinMessage(messageComponent);
            return;
        }

        var messageComponent = plugin.messenger().component(
                Language.GERMAN,
                NodePath.path("event", "first-join"),
                Placeholder.component("player", player.name())
        );

        event.joinMessage(messageComponent);

        var locationService = plugin.locationService();
        plugin.locationService().getLocation("spawn").whenComplete((location, throwable) -> {
           location.ifPresentOrElse(worldPosition -> {
               player.teleport(LocationAdapter.adapt(worldPosition.locationContainer(), player.getServer()));
           }, () -> plugin.messenger().sendMessage(player, NodePath.path("command", "spawn", "no-spawn")));
        });
    }
}
