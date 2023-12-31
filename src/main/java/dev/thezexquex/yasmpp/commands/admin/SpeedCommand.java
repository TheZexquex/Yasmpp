package dev.thezexquex.yasmpp.commands.admin;

import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import cloud.commandframework.context.CommandContext;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.core.command.BaseCommand;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.NodePath;

public class SpeedCommand extends BaseCommand {
    public SpeedCommand(YasmpPlugin plugin) {
        super(plugin);
    }

    @Override
    public void register(CommandManager<CommandSender> commandManager) {
        commandManager.command(commandManager.commandBuilder("speed")
                .argument(IntegerArgument.<CommandSender>builder("speed1").withMin(0).withMax(10))
                .argument(PlayerArgument.optional("player"))
                .senderType(Player.class)
                .permission("yasmpp.command.speed")
                .handler(this::handleSpeedSelf)
        );

        commandManager.command(commandManager.commandBuilder("speed")
                .argument(IntegerArgument.<CommandSender>builder("speed2").withMin(0).withMax(10))
                .argument(PlayerArgument.of("player"))
                .permission("yasmpp.command.speed")
                .handler(this::handleSpeedOthers)
        );
    }

    private void handleSpeedSelf(CommandContext<CommandSender> commandSenderCommandContext) {
        var player = (Player) commandSenderCommandContext.getSender();

        var speed = (int) commandSenderCommandContext.get("speed1");

        if (player.isFlying()) {
            player.setFlySpeed(speed);
            plugin.messenger().sendMessage(
                    player,
                    NodePath.path("command", "speed", "self", "success"),
                    TagResolver.resolver("type", Tag.preProcessParsed("Flight")),
                    TagResolver.resolver("speed", Tag.preProcessParsed(String.valueOf(speed)))
            );
        } else {
            player.setWalkSpeed(speed);
            plugin.messenger().sendMessage(
                    player,
                    NodePath.path("command", "speed", "self", "success"),
                    TagResolver.resolver("type", Tag.preProcessParsed("Walk")),
                    TagResolver.resolver("speed", Tag.preProcessParsed(String.valueOf(speed)))
            );
        }
    }

    private void handleSpeedOthers(CommandContext<CommandSender> commandSenderCommandContext) {
        var sender = commandSenderCommandContext.getSender();
        var player = (Player) commandSenderCommandContext.get("player");

        var speed = (int) commandSenderCommandContext.get("speed2");

        if (player.isFlying()) {
            player.setFlySpeed(speed);
            plugin.messenger().sendMessage(
                    player,
                    NodePath.path("command", "speed", "self", "success"),
                    TagResolver.resolver("type", Tag.preProcessParsed("Flight")),
                    TagResolver.resolver("speed", Tag.preProcessParsed(String.valueOf(speed))),
                    TagResolver.resolver("player", Tag.preProcessParsed(sender.getName()))
            );
            plugin.messenger().sendMessage(
                    sender,
                    NodePath.path("command", "speed", "others", "success"),
                    TagResolver.resolver("type", Tag.preProcessParsed("Flight")),
                    TagResolver.resolver("speed", Tag.preProcessParsed(String.valueOf(speed))),
                    TagResolver.resolver("player", Tag.preProcessParsed(player.getName()))
            );
        } else {
            player.setWalkSpeed(speed);
            plugin.messenger().sendMessage(
                    player,
                    NodePath.path("command", "speed", "self", "success"),
                    TagResolver.resolver("type", Tag.preProcessParsed("Walk")),
                    TagResolver.resolver("speed", Tag.preProcessParsed(String.valueOf(speed))),
                    TagResolver.resolver("player", Tag.preProcessParsed(sender.getName()))
            );
            plugin.messenger().sendMessage(
                    sender,
                    NodePath.path("command", "speed", "others", "success"),
                    TagResolver.resolver("type", Tag.preProcessParsed("Walk")),
                    TagResolver.resolver("speed", Tag.preProcessParsed(String.valueOf(speed))),
                    TagResolver.resolver("player", Tag.preProcessParsed(player.getName()))
            );
        }
    }
}
