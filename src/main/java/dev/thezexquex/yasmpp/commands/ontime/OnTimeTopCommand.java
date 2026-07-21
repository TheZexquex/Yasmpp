package dev.thezexquex.yasmpp.commands.ontime;

import de.unknowncity.astralib.paper.api.command.PaperCommand;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.data.plan.PlanUser;
import dev.thezexquex.yasmpp.hooks.PlanHook;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.spongepowered.configurate.NodePath;

import java.time.Duration;
import java.util.HashMap;

import static org.incendo.cloud.parser.standard.IntegerParser.integerParser;

public class OnTimeTopCommand extends PaperCommand<YasmpPlugin> {
    public OnTimeTopCommand(YasmpPlugin plugin) {
        super(plugin);
    }

    @Override
    public void apply(CommandManager<CommandSender> commandManager) {
        commandManager.command(commandManager.commandBuilder("ontimetop")
                .senderType(Player.class)
                .optional("page", integerParser(1))
                .permission("yasmpp.command.ontimetop")
                .handler(this::handleOnTimeTop)
        );
    }

    private void handleOnTimeTop(CommandContext<Player> commandSourceCommandContext) {
        var sender = commandSourceCommandContext.sender();
        var page = (int) commandSourceCommandContext.getOrDefault("page", 1);

        HashMap<String, Duration> playTimeForPlayers = new HashMap<>();
        var planQueryService = plugin.hookRegistry().getRegistered(PlanHook.class).orElseThrow().planQueryService();

        var users = planQueryService.planUsers();
        if (users.isEmpty()) {
            plugin.messenger().sendMessage(sender, NodePath.path("command", "ontimetop", "empty"));
            return;
        }

        for (PlanUser planUser : users) {
            playTimeForPlayers.put(planUser.name(), planUser.getOnTimeTotal());
        }

        if (playTimeForPlayers.isEmpty()) {
            plugin.messenger().sendMessage(sender, NodePath.path("command", "ontimetop", "empty"));
            return;
        }

        PlayTimePrinter.printPlayTimeTop(sender, playTimeForPlayers, page, plugin.messenger());
    }
}
