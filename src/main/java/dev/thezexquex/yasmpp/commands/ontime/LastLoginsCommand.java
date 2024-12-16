package dev.thezexquex.yasmpp.commands.ontime;

import de.unknowncity.astralib.common.command.sender.CommandSource;
import de.unknowncity.astralib.paper.api.command.PaperCommand;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.data.plan.PlanUser;
import dev.thezexquex.yasmpp.hooks.PlanHook;
import dev.thezexquex.yasmpp.util.DurationFormatter;
import dev.thezexquex.yasmpp.util.MapUtil;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.spongepowered.configurate.NodePath;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

import static org.incendo.cloud.parser.standard.IntegerParser.integerParser;

public class LastLoginsCommand extends PaperCommand<YasmpPlugin> {
    public LastLoginsCommand(YasmpPlugin plugin) {
        super(plugin);
    }

    @Override
    public void apply(CommandManager<CommandSender> commandManager) {
        commandManager.command(commandManager.commandBuilder("lastonline")
                .optional("page", integerParser(1))
                .permission("ucessentials.command.lastonline")
                .handler(this::handleOnTimeTop)
        );
    }

    private void handleOnTimeTop(CommandContext<CommandSender> commandSourceCommandContext) {
        var sender = (Player) commandSourceCommandContext.sender();
        var page = (int) commandSourceCommandContext.getOrDefault("page", 1);

        HashMap<String, Duration> lastLogins = new HashMap<>();
        var planQueryService = plugin.hookRegistry().getRegistered(PlanHook.class).planQueryService();

        for (PlanUser planUser : planQueryService.planUsers()) {
            if (planUser.lastSessionGlobal().isEmpty()) {
                continue;
            }
            lastLogins.put(planUser.name(), Duration.between(planUser.lastSessionGlobal().get().sessionEndTime(), LocalDateTime.now()));
        }

        var lastLoginsSorted = MapUtil.sortByValueASC(lastLogins);
        var values = lastLoginsSorted.values().stream().toList();

        var keys = lastLoginsSorted.keySet().stream().toList();

        var lowerBoundInclusive = (page - 1) * 10 + 1;
        var upperBoundInclusive = Math.min(lowerBoundInclusive + 9, values.size() - 1);

        var maxPage = values.size() / 10 + 1;

        var senderIndex = keys.indexOf(sender.getName());

        if (page > maxPage) {
            plugin.messenger().sendMessage(sender, NodePath.path("command", "lastonline", "max-pages-reached"));
            return;
        }

        plugin.messenger().sendMessage(sender, NodePath.path("command", "lastonline", "header"));

        if (senderIndex + 1 < lowerBoundInclusive) {
            plugin.messenger().sendMessage(sender, NodePath.path("command", "lastonline", "list-self"),
                    TagResolver.resolver(Placeholder.parsed("count", String.valueOf(senderIndex + 1))),
                    TagResolver.resolver(Placeholder.parsed("player", sender.getName())),
                    TagResolver.resolver(Placeholder.parsed("lastonline", DurationFormatter.formatDuration(values.get(senderIndex), "N/A"))));
        }

        for (int i = lowerBoundInclusive - 1; i <= upperBoundInclusive; i++) {
            var playerName = keys.get(i);

            if (sender.getName().equals(playerName)) {
                plugin.messenger().sendMessage(sender, NodePath.path("command", "lastonline", "list-self"),
                        TagResolver.resolver(Placeholder.parsed("count", String.valueOf(i + 1))),
                        TagResolver.resolver(Placeholder.parsed("player", playerName)),
                        TagResolver.resolver(Placeholder.parsed("lastonline", DurationFormatter.formatDuration(values.get(i), "N/A"))));
            } else {
                plugin.messenger().sendMessage(sender, NodePath.path("command", "lastonline", "list"),
                        TagResolver.resolver(Placeholder.parsed("count", String.valueOf(i + 1))),
                        TagResolver.resolver(Placeholder.parsed("player", playerName)),
                        TagResolver.resolver(Placeholder.parsed("lastonline", DurationFormatter.formatDuration(values.get(i), "N/A"))));
            }
        }

        if (senderIndex + 1 > page * 10) {
            plugin.messenger().sendMessage(sender, NodePath.path("command", "lastonline", "list-self"),
                    TagResolver.resolver(Placeholder.parsed("count", String.valueOf(senderIndex + 1))),
                    TagResolver.resolver(Placeholder.parsed("player", sender.getName())),
                    TagResolver.resolver(Placeholder.parsed("lastonline", DurationFormatter.formatDuration(values.get(senderIndex), "N/A"))));
        }

        if (page == 1) {
            if (page < maxPage) {
                plugin.messenger().sendMessage(
                        sender,
                        NodePath.path("command", "lastonline", "footer-no-prev"),
                        TagResolver.resolver(Placeholder.parsed("next", String.valueOf(page + 1))),
                        TagResolver.resolver(Placeholder.parsed("current-page", String.valueOf(page))),
                        TagResolver.resolver(Placeholder.parsed("max-page", String.valueOf(maxPage)))
                );
            } else {
                plugin.messenger().sendMessage(
                        sender,
                        NodePath.path("command", "lastonline", "footer-no-prev-no-next"),
                        TagResolver.resolver(Placeholder.parsed("next", String.valueOf(page + 1))),
                        TagResolver.resolver(Placeholder.parsed("current-page", String.valueOf(page))),
                        TagResolver.resolver(Placeholder.parsed("max-page", String.valueOf(maxPage)))
                );
            }
        } else if (page + 1 > maxPage) {
            plugin.messenger().sendMessage(
                    sender,
                    NodePath.path("command", "lastonline", "footer-no-next"),
                    TagResolver.resolver(Placeholder.parsed("previous", String.valueOf(page - 1))),
                    TagResolver.resolver(Placeholder.parsed("current-page", String.valueOf(page))),
                    TagResolver.resolver(Placeholder.parsed("max-page", String.valueOf(maxPage)))
            );
        } else {
            plugin.messenger().sendMessage(
                    sender,
                    NodePath.path("command", "lastonline", "footer"),
                    TagResolver.resolver(Placeholder.parsed("previous", String.valueOf(page - 1))),
                    TagResolver.resolver(Placeholder.parsed("next", String.valueOf(page + 1))),
                    TagResolver.resolver(Placeholder.parsed("current-page", String.valueOf(page))),
                    TagResolver.resolver(Placeholder.parsed("max-page", String.valueOf(maxPage)))
            );
        }
    }
}
