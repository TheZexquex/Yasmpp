package dev.thezexquex.yasmpp.commands.ontime;

import de.unknowncity.astralib.paper.api.command.PaperCommand;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.data.plan.PlanQueryService;
import dev.thezexquex.yasmpp.hooks.PlanHook;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.suggestion.Suggestion;
import org.spongepowered.configurate.NodePath;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.incendo.cloud.parser.standard.StringParser.stringParser;

public class OnTimeCommand extends PaperCommand<YasmpPlugin> {
    private List<Suggestion> suggestions;
    private PlanQueryService planQueryService;
    public OnTimeCommand(YasmpPlugin plugin) {
        super(plugin);
        planQueryService = plugin.hookRegistry().getRegistered(PlanHook.class).planQueryService();
        suggestions = planQueryService.planUsers().stream().map(planUser -> Suggestion.suggestion(planUser.name())).toList();
    }

    @Override
    public void apply(CommandManager<CommandSender> commandManager) {
        commandManager.command(commandManager.commandBuilder("ontime")
                .senderType(Player.class)
                .permission("ucessentials.command.ontime.self")
                .handler(this::handlePlayTime)
        );

        commandManager.command(commandManager.commandBuilder("ontime")
                .senderType(Player.class)
                .permission("ucessentials.command.ontime.self")
                .required("player_name", stringParser(), (context, input) -> CompletableFuture.completedFuture(suggestions))
                .handler(this::handlePlayTime)
        );
    }

    private void handlePlayTime(CommandContext<Player> commandSourceCommandContext) {
        var sender = commandSourceCommandContext.sender();

        var targetName = sender.getName();
        if (commandSourceCommandContext.contains("player_name")) {
            targetName = commandSourceCommandContext.get("player_name");
        }

        var planUserOpt = planQueryService.getPlanUser(targetName);

        if (planUserOpt.isEmpty()) {
            plugin.messenger().sendMessage(
                    sender,
                    NodePath.path("exception", "player-unknown")
            );
            return;
        }

        var planUser = planUserOpt.get();

        var online = plugin.getServer()
                .getOnlinePlayers()
                .stream()
                .anyMatch(player -> player.getUniqueId().equals(planUser.uniqueId()));

        PlayTimePrinter.printPlayTime(
                sender, planUser, plugin.configuration().generalSettings().planServerName(), online, plugin.messenger()
        );
    }
}
