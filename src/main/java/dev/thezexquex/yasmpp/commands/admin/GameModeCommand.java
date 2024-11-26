package dev.thezexquex.yasmpp.commands.admin;

import de.unknowncity.astralib.paper.api.command.PaperCommand;
import dev.thezexquex.yasmpp.YasmpPlugin;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.suggestion.Suggestion;
import org.spongepowered.configurate.NodePath;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.incendo.cloud.bukkit.parser.PlayerParser.playerParser;
import static org.incendo.cloud.parser.standard.IntegerParser.integerParser;
import static org.incendo.cloud.parser.standard.StringParser.stringParser;

public class GameModeCommand extends PaperCommand<YasmpPlugin> {
    public GameModeCommand(YasmpPlugin plugin) {
        super(plugin);
    }

    public Map<String, GameMode> gameModes = Map.of(
            "0", GameMode.SURVIVAL,
            "1", GameMode.CREATIVE,
            "2", GameMode.ADVENTURE,
            "3", GameMode.SPECTATOR,
            "survival", GameMode.SURVIVAL,
            "creative", GameMode.CREATIVE,
            "adventure", GameMode.ADVENTURE,
            "spectator", GameMode.SPECTATOR
    );


    @Override
    public void apply(CommandManager<CommandSender> commandManager) {
        var gameModeBuilder = commandManager.commandBuilder("gamemode", "gm")
                .required("gamemode", stringParser(), (context, input) ->
                        CompletableFuture.completedFuture(gameModes.keySet().stream().map(Suggestion::suggestion).toList())
                )
                .permission("yasmpp.command.gamemode");
        commandManager.command(gameModeBuilder
                .senderType(Player.class)
                .handler(this::handelGameModeSelf)
        );

        commandManager.command(gameModeBuilder
                .required("player", playerParser())
                .handler(this::handelGameModeOthers)
        );
    }

    private void handelGameModeSelf(CommandContext<Player> commandSenderCommandContext) {
        var player = commandSenderCommandContext.sender();
        var gameModeInput = (String) commandSenderCommandContext.get("gamemode");

        validateAndSetGameMode(player, player, gameModeInput);
    }

    private void handelGameModeOthers(CommandContext<CommandSender> commandSenderCommandContext) {
        var sender = commandSenderCommandContext.sender();
        var player = (Player) commandSenderCommandContext.get("player");
        var gameModeInput = (String) commandSenderCommandContext.get("gamemode");

        validateAndSetGameMode(sender, player, gameModeInput);
    }

    public void validateAndSetGameMode(CommandSender sender, Player target, String gameModeInput) {
        if (!gameModes.containsKey(gameModeInput)) {
            plugin.messenger().sendMessage(
                    sender,
                    NodePath.path("command", "gamemode", "invalid"),
                    Placeholder.parsed("gamemode", gameModeInput)
            );
            return;
        }

        var gameMode = gameModes.get(gameModeInput);

        if (sender instanceof Player senderPlayer && !senderPlayer.equals(target)) {
            plugin.messenger().sendMessage(
                    sender,
                    NodePath.path("command", "gamemode", "others", "success"),
                    Placeholder.parsed("player", target.getName()),
                    Placeholder.parsed("gamemode", gameMode.name())
            );

            if (target.hasPermission("yasmpp.notify.gamemode")) {
                plugin.messenger().sendMessage(
                        target,
                        NodePath.path("command", "gamemode", "notify", "success"),
                        Placeholder.parsed("player", sender.getName()),
                        Placeholder.parsed("gamemode", gameMode.name())
                );
            }
            return;
        }

        plugin.messenger().sendMessage(
                sender,
                NodePath.path("command", "gamemode", "self", "success"),
                Placeholder.parsed("gamemode", gameMode.name())
        );
    }
}