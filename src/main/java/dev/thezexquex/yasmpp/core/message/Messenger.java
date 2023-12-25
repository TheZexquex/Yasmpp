package dev.thezexquex.yasmpp.core.message;

import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.core.hooks.PluginHookService;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.NodePath;

import java.util.Collection;

public class Messenger {
    private final ConfigurationNode rootNode;
    private final MiniMessage miniMessage;
    private final YasmpPlugin plugin;
    private final PluginHookService pluginHookService;

    public Messenger(YasmpPlugin plugin, ConfigurationNode rootNode) {
        this.plugin = plugin;
        this.rootNode = rootNode;
        this.miniMessage = MiniMessage.miniMessage();
        this.pluginHookService = plugin.pluginHookService();
    }

    public Component prefix() {
        var prefixString = rootNode.node("prefix").getString();
        return prefixString == null ? Component.text("") : miniMessage.deserialize(prefixString);
    }

    public Component message(NodePath path, Player player, TagResolver... resolvers) {
        var messageString = rootNode.node(path).getString();
        if (messageString == null) {
            return Component.text("No message found");
        }
        var papiParsedString = pluginHookService.isPapiAvailable() ? PlaceholderAPI.setPlaceholders(player, messageString) : messageString;
        return miniMessage.deserialize(
                papiParsedString,
                TagResolver.resolver(resolvers),
                TagResolver.resolver(Placeholder.component("prefix", prefix()))
        );
    }

    public Component message(NodePath path, TagResolver... resolvers) {
        var messageString = rootNode.node(path).getString();
        if (messageString == null) {
            return Component.text("No message found");
        }
        return miniMessage.deserialize(
                messageString,
                TagResolver.resolver(resolvers),
                TagResolver.resolver(Placeholder.component("prefix", prefix()))
        );
    }

    public void sendActionBar(Player player, NodePath path, TagResolver... tagResolvers) {
        player.sendActionBar(this.message(path, player, tagResolvers));
    }

    public void broadcastActionBarToPlayers(Collection<? extends Player> players, NodePath path, TagResolver... tagResolvers) {
        players.forEach(player -> this.sendActionBar(player, path, tagResolvers));
    }

    public void broadcastActionBarToServer(NodePath path, TagResolver... tagResolvers) {
        broadcastActionBarToPlayers(plugin.getServer().getOnlinePlayers(), path, tagResolvers);
    }

    public void messageToCommandSender(CommandSender sender, NodePath path, TagResolver... tagResolvers) {
        sender.sendMessage(this.message(path, tagResolvers));
    }
    public void messageToPlayer(Player player, NodePath path, TagResolver... tagResolvers) {
        player.sendMessage(this.message(path, player, tagResolvers));
    }

    public void broadcastToPlayers(Collection<? extends Player> players, NodePath path, TagResolver... tagResolvers) {
        players.forEach(player -> messageToPlayer(player, path, tagResolvers));
    }

    public void broadcastToServer(NodePath path, TagResolver... tagResolvers) {
        this.broadcastToPlayers(plugin.getServer().getOnlinePlayers(), path, tagResolvers);
    }
}
