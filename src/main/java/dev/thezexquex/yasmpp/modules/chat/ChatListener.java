package dev.thezexquex.yasmpp.modules.chat;

import de.unknowncity.astralib.common.message.lang.Language;
import dev.thezexquex.yasmpp.YasmpPlugin;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spongepowered.configurate.NodePath;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class ChatListener implements Listener {

    private final YasmpPlugin plugin;
    private final List<Pattern> badWordPatterns = new ArrayList<>();

    public ChatListener(YasmpPlugin plugin) {
        this.plugin = plugin;
        plugin.configuration()
                .chatSettings()
                .forbiddenWords()
                .forEach(string -> badWordPatterns.add(Pattern.compile(string)));
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        // Check if the plugin should touch the chat logic or if another plugin is used for chat
        var handleChat = plugin.configuration().chatSettings().handleChat();
        if (!handleChat) {
            return;
        }

        event.setCancelled(true);

        var source = event.getPlayer();
        var message = event.message();

        var messageAsString = PlainTextComponentSerializer.plainText().serialize(message);

        // Check for not allowed text in chat
        if (!source.hasPermission("yasmpp.chat.filter.bypass")) {
            var result = ChatUtil.containsBadWords(messageAsString, badWordPatterns);
            if (result.key()) {
                plugin.messenger().sendMessage(
                        source,
                        NodePath.path("event", "chat", "not-allowed-string"),
                        Placeholder.parsed("match", result.value())
                );
                return;
            }
        }

        /* Send the message to every player instead of using the chat renderer to prevent the
        1.19.3+ chat message reporting, because this is the most brainless feature mojang has ever introduced
         */
        plugin.getServer().getOnlinePlayers().forEach(player -> {
            player.sendMessage(ChatUtil.getFormattedChatMessage(
                    source,
                    messageAsString,
                    plugin.messenger().getString(Language.GERMAN, NodePath.path("chat-format"))
            ));
        });
    }
}
