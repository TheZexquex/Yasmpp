package dev.thezexquex.yasmpp.commands.admin;

import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.EnumArgument;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import cloud.commandframework.context.CommandContext;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.core.command.BaseCommand;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.NodePath;
public class GameModeCommand extends BaseCommand {
    public GameModeCommand(YasmpPlugin plugin) {
        super(plugin);
    }

    @Override
    public void register(CommandManager<CommandSender> commandManager) {
        commandManager.command(commandManager.commandBuilder("gamemode", "gm")
                .argument(EnumArgument.of(GameMode.class, "gamemodeSE"))
                .permission("yasmpp.command.gamemode")
                .senderType(Player.class)
                .handler(this::handelGameModeSelfEnum)
        );

        commandManager.command(commandManager.commandBuilder("gamemode", "gm")
                .argument(EnumArgument.of(GameMode.class, "gamemodeOE"))
                .argument(PlayerArgument.of("playerE"))
                .permission("yasmpp.command.gamemode.others")
                .handler(this::handelGameModeOthersEnum)
        );

        commandManager.command(commandManager.commandBuilder("gamemode", "gm")
                .argument(IntegerArgument.<CommandSender>builder("gamemodeSI").withMin(0).withMax(3))
                .permission("yasmpp.command.gamemode")
                .senderType(Player.class)
                .handler(this::handelGameModeSelfInt)
        );

        commandManager.command(commandManager.commandBuilder("gamemode", "gm")
                .argument(EnumArgument.of(GameMode.class, "gamemodeOI"))
                .argument(PlayerArgument.of("playerI"))
                .permission("yasmpp.command.gamemode.others")
                .handler(this::handelGameModeOthersInt)
        );
    }

    private void handelGameModeSelfInt(CommandContext<CommandSender> commandSenderCommandContext) {
        var player = (Player) commandSenderCommandContext.getSender();

        var gameMode = GameMode.getByValue(commandSenderCommandContext.get("gamemodeSI"));
        player.setGameMode(gameMode);
        plugin.messenger().sendMessage(
                player,
                NodePath.path("command", "gamemode", "self", "success"),
                TagResolver.resolver("gamemode", Tag.preProcessParsed(gameMode.name()))
        );
    }

    private void handelGameModeOthersInt(CommandContext<CommandSender> commandSenderCommandContext) {
        var sender = commandSenderCommandContext.getSender();
        var player = (Player) commandSenderCommandContext.get("playerI");

        var gameMode = GameMode.getByValue(commandSenderCommandContext.get("gamemodeOI"));
        player.setGameMode(gameMode);
        plugin.messenger().sendMessage(
                player,
                NodePath.path("command", "gamemode", "self", "success"),
                TagResolver.resolver("player", Tag.preProcessParsed(sender.getName()))
        );
        plugin.messenger().sendMessage(
                sender,
                NodePath.path("command", "gamemode", "others", "success"),
                TagResolver.resolver("player", Tag.preProcessParsed(sender.getName())),
                TagResolver.resolver("gamemode", Tag.preProcessParsed(gameMode.name()))
        );

    }

    private void handelGameModeSelfEnum(CommandContext<CommandSender> commandSenderCommandContext) {
        var player = (Player) commandSenderCommandContext.getSender();

        var gameMode = (GameMode) commandSenderCommandContext.get("gamemodeSE");
        player.setGameMode(gameMode);
        plugin.messenger().sendMessage(
                player,
                NodePath.path("command", "gamemode", "self", "success"),
                TagResolver.resolver("gamemode", Tag.preProcessParsed(gameMode.name()))
        );
    }

    private void handelGameModeOthersEnum(CommandContext<CommandSender> commandSenderCommandContext) {
        var sender = commandSenderCommandContext.getSender();
        var player = (Player) commandSenderCommandContext.get("playerE");

        var gameMode = (GameMode) commandSenderCommandContext.get("gamemodeOE");
        player.setGameMode(gameMode);
        plugin.messenger().sendMessage(
                player,
                NodePath.path("command", "gamemode", "self", "success"),
                TagResolver.resolver("player", Tag.preProcessParsed(sender.getName())),
                TagResolver.resolver("gamemode", Tag.preProcessParsed(gameMode.name()))
        );
        plugin.messenger().sendMessage(
                sender,
                NodePath.path("command", "gamemode", "others", "success"),
                TagResolver.resolver("player", Tag.preProcessParsed(sender.getName())),
                TagResolver.resolver("gamemode", Tag.preProcessParsed(gameMode.name()))
        );
    }
}
