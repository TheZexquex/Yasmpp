package dev.thezexquex.yasmpp.modules.chat;

import de.unknowncity.astralib.common.message.lang.Language;
import dev.thezexquex.yasmpp.YasmpPlugin;
import io.papermc.paper.chat.ChatRenderer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.NodePath;

public class DefaultChatRenderer implements ChatRenderer {

    private final YasmpPlugin plugin;

    public DefaultChatRenderer(YasmpPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull Component render(
            @NotNull Player source,
            @NotNull Component sourceDisplayName,
            @NotNull Component message,
            @NotNull Audience viewer) {
        return ChatUtil.getFormattedChatMessage(source, message, plugin.messenger().getString(Language.GERMAN, NodePath.path("chat-format")));
    }
}
