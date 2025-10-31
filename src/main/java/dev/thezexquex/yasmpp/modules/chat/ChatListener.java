package dev.thezexquex.yasmpp.modules.chat;

import de.unknowncity.astralib.common.message.lang.Language;
import dev.thezexquex.yasmpp.Permissions;
import dev.thezexquex.yasmpp.YasmpPlugin;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spongepowered.configurate.NodePath;

public class ChatListener implements Listener {

    private final YasmpPlugin plugin;
    private final ChatFilter chatFilter;

    public ChatListener(YasmpPlugin plugin) {
        this.plugin = plugin;
        this.chatFilter = new ChatFilter(plugin);
    }

    @EventHandler()
    public void onChat(AsyncChatEvent event) {
        // Check if the plugin should touch the chat logic or if another plugin is used for chat
        var handleChat = plugin.configuration().chat().handleChat();
        if (!handleChat) {
            return;
        }

        event.setCancelled(true);

        var message = event.message();
        var source = event.getPlayer();
        var messageAsString = PlainTextComponentSerializer.plainText().serialize(message);

        var result = chatFilter.applyFilter(source, messageAsString);

        if (result.outcome() == ChatFilter.ChatFilterResult.Outcome.BLOCK) {
            plugin.messenger().sendMessage(
                    source,
                    NodePath.path("event", "chat", "filter", "block"),
                    Placeholder.parsed("input", messageAsString)
            );
            notifyStaff(source, result);
            return;
        }

        var formattedMessage = ChatUtil.getFormattedChatMessage(
                source,
                result.message(),
                plugin.messenger().getStringOrNotAvailable(Language.GERMAN, NodePath.path("chat-format"))
        );

        plugin.getServer().getOnlinePlayers().forEach(player -> player.sendMessage(formattedMessage));
    }

    public void notifyStaff(Player violator, ChatFilter.ChatFilterResult result) {
        plugin.getServer().getOnlinePlayers()
                .stream()
                .filter(player -> player.hasPermission(Permissions.CHAT_VIOLATION_NOTIFY))
                .forEach(staff -> {
                    plugin.messenger().sendMessage(
                            staff,
                            NodePath.path("event", "chat", "filter", "notify"),
                            Placeholder.parsed("input", result.message()),
                            Placeholder.unparsed("violator", violator.getName())
                    );
        });
    }
}
