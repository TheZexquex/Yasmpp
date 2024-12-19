package dev.thezexquex.yasmpp.commands.admin;

import de.unknowncity.astralib.paper.api.command.PaperCommand;
import dev.thezexquex.yasmpp.YasmpPlugin;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.spongepowered.configurate.NodePath;

import static org.incendo.cloud.bukkit.parser.PlayerParser.playerParser;
import static org.incendo.cloud.parser.standard.IntegerParser.integerParser;

public class SpeedCommand extends PaperCommand<YasmpPlugin> {
    public SpeedCommand(YasmpPlugin plugin) {
        super(plugin);
    }

    @Override
    public void apply(CommandManager<CommandSender> commandManager) {
        var speedBuilder = commandManager.commandBuilder("speed")
                .required("speed", integerParser(0, 10))
                .permission("yasmpp.command.speed");

        commandManager.command(speedBuilder
                .senderType(Player.class)
                .handler(this::handleSpeedSelf)
        );

        commandManager.command(speedBuilder
                .required("player", playerParser())
                .permission("yasmpp.command.speed")
                .handler(this::handleSpeedOthers)
        );
    }

    private void handleSpeedSelf(CommandContext<Player> commandSenderCommandContext) {
        var player = commandSenderCommandContext.sender();

        var speed = (int) commandSenderCommandContext.get("speed");

        setPlayerSpeed(player, speed);

        plugin.messenger().sendMessage(
                player,
                NodePath.path("command", "speed", "self", "success"),
                Placeholder.parsed("type", player.isFlying() ? "Flight" : "Walk"),
                Placeholder.parsed("speed", String.valueOf(speed))
        );
    }

    private void handleSpeedOthers(CommandContext<CommandSender> commandSenderCommandContext) {
        var sender = commandSenderCommandContext.sender();
        var player = (Player) commandSenderCommandContext.get("player");

        var speed = (int) commandSenderCommandContext.get("speed");

        setPlayerSpeed(player, speed);

        plugin.messenger().sendMessage(
                sender,
                NodePath.path("command", "speed", "others", "success"),
                Placeholder.parsed("type", player.isFlying() ? "Flight" : "Walk"),
                Placeholder.parsed("speed", String.valueOf(speed)),
                Placeholder.parsed("player", player.getName())
        );


        if (!player.hasPermission("yasmpp.notify.speed")) {
            return;
        }

        plugin.messenger().sendMessage(
                player,
                NodePath.path("command", "speed", "notify", "success"),
                Placeholder.parsed("type", player.isFlying() ? "Flight" : "Walk"),
                Placeholder.parsed("speed", String.valueOf(speed)),
                Placeholder.parsed("player", sender.getName())
        );
    }

    public void setPlayerSpeed(Player target, int speed) {
        var realSpeed = speed * 0.1F;
        if (target.isFlying()) {
            target.setFlySpeed(realSpeed);
            return;
        }
        target.setWalkSpeed(realSpeed);
    }
}