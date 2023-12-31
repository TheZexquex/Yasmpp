package dev.thezexquex.yasmpp.modules.joinleavemessage;

import dev.thezexquex.yasmpp.YasmpPlugin;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.spongepowered.configurate.NodePath;

public class PlayerQuitListener implements Listener {

    private final YasmpPlugin plugin;

    public PlayerQuitListener(YasmpPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        var player = event.getPlayer();

        var messageComponent = plugin.messenger().component(
                NodePath.path("event", "quit"),
                Placeholder.component("player", player.name())
        );
        event.quitMessage(messageComponent);
    }
}
