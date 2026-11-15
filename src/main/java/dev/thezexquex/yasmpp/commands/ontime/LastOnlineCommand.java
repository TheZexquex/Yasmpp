package dev.thezexquex.yasmpp.commands.ontime;

import de.unknowncity.astralib.paper.api.command.PaperCommand;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.data.plan.LeaderboardEntry;
import dev.thezexquex.yasmpp.data.plan.PlaytimeService;
import dev.thezexquex.yasmpp.modules.playtime.PlaytimeFormat;
import dev.thezexquex.yasmpp.util.Page;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.spongepowered.configurate.NodePath;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.logging.Level;

import static org.incendo.cloud.parser.standard.IntegerParser.integerParser;

public class LastOnlineCommand extends PaperCommand<YasmpPlugin> {
    // Online players first, then the most recently seen
    private static final Comparator<LeaderboardEntry> MOST_RECENT_FIRST = Comparator
            .comparing(LeaderboardEntry::online).reversed()
            .thenComparing(LeaderboardEntry::lastSeen, Comparator.nullsFirst(Comparator.<Instant>reverseOrder()));

    private final PlaytimeService playtimeService;
    private final LeaderboardPrinter printer;

    public LastOnlineCommand(YasmpPlugin plugin, PlaytimeService playtimeService) {
        super(plugin);
        this.playtimeService = playtimeService;
        this.printer = new LeaderboardPrinter(plugin);
    }

    @Override
    public void apply(CommandManager<CommandSender> commandManager) {
        commandManager.command(commandManager.commandBuilder("lastonline")
                .optional("page", integerParser(1))
                .permission("yasmpp.command.lastonline")
                .handler(this::handleLastOnline)
        );
    }

    private void handleLastOnline(CommandContext<CommandSender> context) {
        var sender = context.sender();
        int pageNumber = context.getOrDefault("page", 1);

        playtimeService.leaderboard()
                .thenAccept(leaderboard -> {
                    if (leaderboard.isEmpty()) {
                        plugin.messenger().sendMessage(sender, NodePath.path("command", "lastonline", "empty"));
                        return;
                    }

                    var sorted = leaderboard.stream().sorted(MOST_RECENT_FIRST).toList();
                    var page = Page.of(sorted, pageNumber, entry -> OnTimeTopCommand.isSelf(sender, entry));
                    if (page.isEmpty()) {
                        plugin.messenger().sendMessage(sender, NodePath.path("pagination", "page-not-found"));
                        return;
                    }

                    var now = Instant.now();
                    var onlineText = plugin.messenger().getStringOrNotAvailable(
                            plugin.language(sender), NodePath.path("command", "lastonline", "online"));
                    printer.send(sender, "lastonline", page.get(),
                            number -> "/lastonline " + number,
                            entry -> TagResolver.resolver(
                                    Placeholder.parsed("player", entry.user().name()),
                                    Placeholder.parsed("lastonline", entry.online() || entry.lastSeen() == null
                                            ? onlineText
                                            : PlaytimeFormat.duration(Duration.between(entry.lastSeen(), now)))
                            ),
                            TagResolver.empty()
                    );
                })
                .exceptionally(throwable -> {
                    plugin.getLogger().log(Level.SEVERE, "Failed to load last online players", throwable);
                    plugin.messenger().sendMessage(sender, NodePath.path("exception", "plan-unavailable"));
                    return null;
                });
    }
}
