package dev.thezexquex.yasmpp.modules.chat;

import de.unknowncity.astralib.paper.api.message.color.LegacyChatColor;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.regex.Pattern;

public class ChatUtil {

    public static Component getFormattedChatMessage(Player sender, Component message, String messageFormat) {

        var withPlaceholders = PlaceholderAPI.setPlaceholders(sender, messageFormat);
        var withLegacyTextAsString = LegacyChatColor.translateToMiniMessage('&', withPlaceholders);

        var messageComponent = getMessageFormat(sender, message);

        return MiniMessage.miniMessage().deserialize(withLegacyTextAsString, TagResolver.resolver(
                Placeholder.component("player", sender.name()),
                Placeholder.component("message", messageComponent)
        ));
    }

    public static Component getMessageFormat(Player sender, Component message) {
        var messageString = PlainTextComponentSerializer.plainText().serialize(message);

        Component toReturn;
        if (sender.hasPermission("svp.chat.format")) {
            toReturn = MiniMessage.miniMessage().deserialize(messageString);
        } else {
            toReturn = MiniMessage.builder().tags(TagResolver.empty()).build().deserialize(messageString);
        }
        return toReturn;
    }

    public static boolean containsBadWords(Player sender, String messageString, List<String> forbiddenWords) {
        if (forbiddenWords == null) {
            return false;
        }
        /*
        if (sender.hasPermission("svp.chat.badwords.bypass")) {
            return false;
        }

         */
        for (String string : forbiddenWords) {
            if (Pattern.compile(string).matcher(messageString).find()) {
               return true;
            }
        }
        return false;
    }
}
