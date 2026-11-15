package dev.thezexquex.yasmpp.commands.ontime;

import de.unknowncity.astralib.paper.api.command.PaperCommand;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.data.plan.PlaytimeService;
import dev.thezexquex.yasmpp.data.plan.PlaytimeStats;
import dev.thezexquex.yasmpp.data.plan.TimeWindow;
import dev.thezexquex.yasmpp.modules.playtime.PlaytimeFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.suggestion.Suggestion;
import org.spongepowered.configurate.NodePath;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import static org.incendo.cloud.parser.standard.StringParser.stringParser;

public class OnTimeCommand extends PaperCommand<YasmpPlugin> {
    private final PlaytimeService playtimeService;

    public OnTimeCommand(YasmpPlugin plugin, PlaytimeService playtimeService) {
        super(plugin);
        this.playtimeService = playtimeService;
    }

    @Override
    public void apply(CommandManager<CommandSender> commandManager) {
        // A single command with an optional name, as cloud rejects a flag node next to an argument node
        commandManager.command(commandManager.commandBuilder("ontime")
                .permission("yasmpp.command.ontime.self")
                .optional("player_name", stringParser(), (context, input) -> CompletableFuture.completedFuture(
                        playtimeService.knownNames().stream().map(Suggestion::suggestion).toList()))
                .flag(commandManager.flagBuilder("no-afk"))
                .handler(this::handleOnTime)
        );
    }

    private void handleOnTime(CommandContext<CommandSender> context) {
        var sender = context.sender();
        var excludeAfk = context.flags().isPresent("no-afk");

        var targetName = context.<String>optional("player_name").orElse(null);
        if (targetName == null) {
            if (!(sender instanceof Player player)) {
                plugin.messenger().sendMessage(sender, NodePath.path("exception", "command", "invalidsender"));
                return;
            }
            targetName = player.getName();
        }
        var target = targetName;

        playtimeService.resolve(target)
                .thenCompose(user -> user
                        .map(planUser -> playtimeService.stats(planUser).thenApply(Optional::of))
                        .orElseGet(() -> CompletableFuture.completedFuture(Optional.empty())))
                .thenAccept(stats -> {
                    if (stats.isEmpty()) {
                        plugin.messenger().sendMessage(sender, NodePath.path("exception", "player-unknown"));
                        return;
                    }
                    if (!stats.get().hasPlayed()) {
                        plugin.messenger().sendMessage(sender, NodePath.path("command", "ontime", "never-joined"));
                        return;
                    }
                    sendStats(sender, stats.get(), excludeAfk);
                })
                .exceptionally(throwable -> {
                    plugin.getLogger().log(Level.SEVERE, "Failed to load playtime of " + target, throwable);
                    plugin.messenger().sendMessage(sender, NodePath.path("exception", "plan-unavailable"));
                    return null;
                });
    }

    private void sendStats(CommandSender receiver, PlaytimeStats stats, boolean excludeAfk) {
        var now = Instant.now();
        var zone = ZoneId.systemDefault();
        var firstJoin = stats.firstJoin();
        var tags = new ArrayList<TagResolver>();

        tags.add(Placeholder.parsed("player", stats.user().name()));
        tags.add(Placeholder.parsed("first_joined", PlaytimeFormat.DATE_TIME.format(firstJoin.atZone(zone))));
        tags.add(Placeholder.parsed("first_joined_ago", String.valueOf(Duration.between(firstJoin, now).toDays())));
        tags.add(Placeholder.parsed("year", String.valueOf(LocalDate.now(zone).getYear())));
        tags.add(Placeholder.component("afk_hint", excludeAfk
                ? plugin.messenger().component(plugin.language(receiver), NodePath.path("command", "ontime", "afk-excluded"))
                : Component.empty()));

        tags.add(Placeholder.parsed("ontime_session", PlaytimeFormat.duration(stats.session(excludeAfk))));
        var lastSeen = stats.lastSeen();
        tags.add(Placeholder.parsed("offline_since", lastSeen == null ? "-" : PlaytimeFormat.duration(Duration.between(lastSeen, now))));
        for (var window : TimeWindow.values()) {
            tags.add(Placeholder.parsed("ontime_" + window.key(), PlaytimeFormat.duration(stats.playtime(window, excludeAfk))));
        }

        plugin.messenger().sendMessage(receiver,
                NodePath.path("command", "ontime", stats.online() ? "online" : "offline"),
                tags.toArray(TagResolver[]::new)
        );
    }
}
