package dev.thezexquex.yasmpp.commands.ontime;

import de.unknowncity.astralib.paper.api.command.PaperCommand;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.data.plan.LeaderboardEntry;
import dev.thezexquex.yasmpp.data.plan.PlaytimeService;
import dev.thezexquex.yasmpp.modules.playtime.PlaytimeFormat;
import dev.thezexquex.yasmpp.util.Page;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.spongepowered.configurate.NodePath;

import java.util.Comparator;
import java.util.logging.Level;

import static org.incendo.cloud.parser.standard.IntegerParser.integerParser;

public class OnTimeTopCommand extends PaperCommand<YasmpPlugin> {
    private final PlaytimeService playtimeService;
    private final LeaderboardPrinter printer;

    public OnTimeTopCommand(YasmpPlugin plugin, PlaytimeService playtimeService) {
        super(plugin);
        this.playtimeService = playtimeService;
        this.printer = new LeaderboardPrinter(plugin);
    }

    @Override
    public void apply(CommandManager<CommandSender> commandManager) {
        commandManager.command(commandManager.commandBuilder("ontimetop")
                .optional("page", integerParser(1))
                .flag(commandManager.flagBuilder("no-afk"))
                .permission("yasmpp.command.ontimetop")
                .handler(this::handleOnTimeTop)
        );
    }

    private void handleOnTimeTop(CommandContext<CommandSender> context) {
        var sender = context.sender();
        int pageNumber = context.getOrDefault("page", 1);
        var excludeAfk = context.flags().isPresent("no-afk");

        playtimeService.leaderboard()
                .thenAccept(leaderboard -> {
                    if (leaderboard.isEmpty()) {
                        plugin.messenger().sendMessage(sender, NodePath.path("command", "ontimetop", "empty"));
                        return;
                    }

                    var sorted = leaderboard.stream()
                            .sorted(Comparator.comparing((LeaderboardEntry entry) -> entry.playtime(excludeAfk)).reversed())
                            .toList();
                    var page = Page.of(sorted, pageNumber, entry -> isSelf(sender, entry));
                    if (page.isEmpty()) {
                        plugin.messenger().sendMessage(sender, NodePath.path("pagination", "page-not-found"));
                        return;
                    }

                    var afkHint = excludeAfk
                            ? plugin.messenger().component(plugin.language(sender), NodePath.path("command", "ontime", "afk-excluded"))
                            : Component.empty();
                    printer.send(sender, "ontimetop", page.get(),
                            number -> "/ontimetop " + number + (excludeAfk ? " --no-afk" : ""),
                            entry -> TagResolver.resolver(
                                    Placeholder.parsed("player", entry.user().name()),
                                    Placeholder.parsed("ontime", PlaytimeFormat.duration(entry.playtime(excludeAfk)))
                            ),
                            Placeholder.component("afk_hint", afkHint)
                    );
                })
                .exceptionally(throwable -> {
                    plugin.getLogger().log(Level.SEVERE, "Failed to load playtime leaderboard", throwable);
                    plugin.messenger().sendMessage(sender, NodePath.path("exception", "plan-unavailable"));
                    return null;
                });
    }

    static boolean isSelf(CommandSender sender, LeaderboardEntry entry) {
        return sender instanceof Player player && player.getUniqueId().equals(entry.user().uniqueId());
    }
}
