package dev.thezexquex.yasmpp.modules.chat;

import dev.thezexquex.yasmpp.YasmpPlugin;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {

    private final YasmpPlugin plugin;

    public ChatListener(YasmpPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        var player = event.getPlayer();
        var handleChat = plugin.configuration().chatSettings().handleChat();
        if (!handleChat) {
            return;
        }

        event.renderer(new DefaultChatRenderer(plugin));
    }
}
