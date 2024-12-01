package dev.thezexquex.yasmpp.modules.chat;

import de.unknowncity.astralib.common.structure.KeyValue;
import de.unknowncity.astralib.paper.api.message.color.LegacyChatColor;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.regex.Pattern;

public class ChatUtil {

    public static Component getFormattedChatMessage(Player sender, String message, String messageFormat) {

        var withPlaceholders = PlaceholderAPI.setPlaceholders(sender, messageFormat);

        // Make placeholders from other plugins that are still using legacy color codes compatible with minimessage
        var withLegacyTextAsString = LegacyChatColor.translateToMiniMessage('&', withPlaceholders);

        // Apply color to messages if the player has permission to use color  in chat
        var messageComponent = getMessageWithColorIfAllowed(sender, message);

        return MiniMessage.miniMessage().deserialize(withLegacyTextAsString, TagResolver.resolver(
                Placeholder.component("player", sender.name()),
                Placeholder.component("message", messageComponent)
        ));
    }

    private static Component getMessageWithColorIfAllowed(Player sender, String message) {
        Component toReturn;
        if (sender.hasPermission("yasmpp.chat.format")) {
            toReturn = MiniMessage.miniMessage().deserialize(message);
        } else {
            toReturn = MiniMessage.builder().tags(TagResolver.empty()).build().deserialize(message);
        }
        return toReturn;
    }

    public static KeyValue<Boolean, String> containsBadWords(String messageString, List<Pattern> forbiddenWords) {
        for (Pattern forbiddenWord : forbiddenWords) {
            var matcher = forbiddenWord.matcher(messageString);

            if (matcher.find()) {
                return KeyValue.of(true, matcher.group());
            }
        }
        return KeyValue.of(false, "");
    }
}
