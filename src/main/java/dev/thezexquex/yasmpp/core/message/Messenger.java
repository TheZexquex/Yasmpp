package dev.thezexquex.yasmpp.core.message;

import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.core.hooks.PluginHookService;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.title.Title;
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

    public String getString(NodePath path) {
        return rootNode.node(path).getString();
    }

    public Component prefix() {
        var prefixString = rootNode.node("prefix").getString();
        return prefixString == null ? Component.text("") : miniMessage.deserialize(prefixString);
    }

    public Component component(NodePath path, Player player, TagResolver... resolvers) {
        var messageString = rootNode.node(path).getString();
        if (messageString == null) {
            return Component.text("N/A " + path);
        }
        var papiParsedString = pluginHookService.isPapiAvailable() ? PlaceholderAPI.setPlaceholders(player, messageString) : messageString;
        return miniMessage.deserialize(
                papiParsedString,
                TagResolver.resolver(resolvers),
                TagResolver.resolver(Placeholder.component("prefix", prefix()))
        );
    }

    public Component component(NodePath path, TagResolver... resolvers) {
        var messageString = rootNode.node(path).getString();
        if (messageString == null) {
            return Component.text("N/A " + path);
        }
        return miniMessage.deserialize(
                messageString,
                TagResolver.resolver(resolvers),
                TagResolver.resolver(Placeholder.component("prefix", prefix()))
        );
    }

    public void sendTitle(Player player, NodePath pathTitle, NodePath pathSubTitle, TagResolver... tagResolvers) {
        var title = Title.title(
                pathTitle == null ? Component.empty() : component(pathTitle, player, tagResolvers),
                pathSubTitle == null ? Component.empty() : component(pathSubTitle, player, tagResolvers)
        );
        player.showTitle(title);
    }

    public void broadcastTitleToServer(NodePath pathTitle, NodePath pathSubTitle, TagResolver... tagResolvers) {
        broadcastTitleToPlayers(plugin.getServer().getOnlinePlayers(), pathTitle, pathSubTitle, tagResolvers);
    }

    public void broadcastTitleToPlayers(Collection<? extends Player> players, NodePath pathTitle, NodePath pathSubTitle, TagResolver... tagResolvers) {
        players.forEach(player -> sendTitle(player, pathTitle, pathSubTitle, tagResolvers));
    }

    public void sendActionBar(Player player, NodePath path, TagResolver... tagResolvers) {
        player.sendActionBar(this.component(path, player, tagResolvers));
    }

    public void sendMessage(CommandSender sender, NodePath path, TagResolver... tagResolvers) {
        sender.sendMessage(this.component(path, tagResolvers));
    }
    public void sendMessage(Player player, NodePath path, TagResolver... tagResolvers) {
        player.sendMessage(this.component(path, player, tagResolvers));
    }

    public void broadcastActionBarToPlayers(Collection<? extends Player> players, NodePath path, TagResolver... tagResolvers) {
        players.forEach(player -> this.sendActionBar(player, path, tagResolvers));
    }

    public void broadcastActionBarToServer(NodePath path, TagResolver... tagResolvers) {
        broadcastActionBarToPlayers(plugin.getServer().getOnlinePlayers(), path, tagResolvers);
    }

    public void broadcastToPlayers(Collection<? extends Player> players, NodePath path, TagResolver... tagResolvers) {
        players.forEach(player -> sendMessage(player, path, tagResolvers));
    }

    public void broadcastToServer(NodePath path, TagResolver... tagResolvers) {
        this.broadcastToPlayers(plugin.getServer().getOnlinePlayers(), path, tagResolvers);
    }
}
