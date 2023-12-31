package dev.thezexquex.yasmpp.commands;

import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.core.command.BaseCommand;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.NodePath;

public class HomeCommand extends BaseCommand {
    public HomeCommand(YasmpPlugin plugin) {
        super(plugin);
    }

    @Override
    public void register(CommandManager<CommandSender> commandManager) {
        commandManager.command(
                commandManager.commandBuilder("home")
                        .argument(StringArgument.single("homeName"))
                        .permission("yasmpp.command.home")
                        .senderType(Player.class)
                        .handler(this::handleHome)
        );

        commandManager.command(
                commandManager.commandBuilder("sethome")
                        .argument(StringArgument.single("homeName"))
                        .permission("yasmpp.command.sethome")
                        .senderType(Player.class)
                        .handler(this::handleSetHome)
        );

        commandManager.command(
                commandManager.commandBuilder("delhome")
                        .argument(StringArgument.single("homeName"))
                        .permission("yasmpp.command.delhome")
                        .senderType(Player.class)
                        .handler(this::handleDelHome)
        );
    }

    private void handleDelHome(CommandContext<CommandSender> commandSenderCommandContext) {
        var player = (Player) commandSenderCommandContext.getSender();
        var homeName = (String) commandSenderCommandContext.get("homeName");

        plugin.homeService().deleteHome(player.getUniqueId(), homeName).thenAccept(result -> {
            plugin.messenger().sendMessage(player,
                    NodePath.path("command", "delhome", result ? "success" : "error"),
                    TagResolver.resolver("home-name", Tag.preProcessParsed(homeName))
            );
        });
    }

    private void handleSetHome(CommandContext<CommandSender> commandSenderCommandContext) {
        var player = (Player) commandSenderCommandContext.getSender();
        var homeName = (String) commandSenderCommandContext.get("homeName");

        plugin.homeService().saveHome(player.getUniqueId(), homeName, player.getLocation()).thenAccept(result -> {
            plugin.messenger().sendMessage(player,
                    NodePath.path("command", "sethome", result ? "success" : "error"),
                    TagResolver.resolver("home-name", Tag.preProcessParsed(homeName))
            );
        });
    }

    private void handleHome(CommandContext<CommandSender> commandSenderCommandContext) {
        var player = (Player) commandSenderCommandContext.getSender();
        var homeName = (String) commandSenderCommandContext.get("homeName");

        plugin.homeService().getHome(player.getUniqueId(), homeName).thenAccept(locationContainerOpt -> {
            if (locationContainerOpt.isEmpty()) {
                plugin.messenger().sendMessage(player,
                        NodePath.path("command", "home", "notfound"),
                        TagResolver.resolver("home-name", Tag.preProcessParsed(homeName))
                );
            } else {
                plugin.messenger().sendMessage(player,
                        NodePath.path("command", "home", "success"),
                        TagResolver.resolver("home-name", Tag.preProcessParsed(homeName))
                );
                plugin.getServer().getScheduler().runTask(plugin, () -> player.teleport(locationContainerOpt.get().toLocation(plugin.getServer())));
            }
        });
    }
}
