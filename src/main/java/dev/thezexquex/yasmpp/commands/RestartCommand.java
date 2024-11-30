package dev.thezexquex.yasmpp.commands;

import de.unknowncity.astralib.common.timer.Countdown;
import de.unknowncity.astralib.paper.api.command.PaperCommand;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.commands.util.CountDownMessenger;
import dev.thezexquex.yasmpp.configuration.settings.CountDownEntry;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.spongepowered.configurate.NodePath;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.incendo.cloud.parser.standard.DurationParser.durationParser;
import static org.incendo.cloud.parser.standard.StringParser.quotedStringParser;

public class RestartCommand extends PaperCommand<YasmpPlugin> {
    private Countdown countdown;

    public RestartCommand(YasmpPlugin plugin) {
        super(plugin);
        this.countdown = null;
    }

    @Override
    public void apply(CommandManager<CommandSender> commandManager) {
        commandManager.command(commandManager.commandBuilder("restartcountdown")
                .permission("yasmpp.command.restartcountdown")
                .literal("start")
                .required("time", durationParser())
                .required("reason", quotedStringParser())
                .handler(this::handleStart)
                .build()
        );

        commandManager.command(commandManager.commandBuilder("restartcountdown")
                .permission("yasmpp.command.restartcountdown")
                .literal("abort")
                .handler(this::handleAbort)
                .build()
        );

        commandManager.command(commandManager.commandBuilder("restartcountdown")
                .permission("yasmpp.command.restartcountdown")
                .handler(this::handleHelp)
                .build()
        );
    }

    private void handleHelp(CommandContext<CommandSender> commandSenderCommandContext) {
        var sender = commandSenderCommandContext.sender();
        plugin.messenger().sendMessage(sender, NodePath.path("command", "restartcountdown", "help"));
    }

    private void handleAbort(CommandContext<CommandSender> commandSenderCommandContext) {
        var sender = commandSenderCommandContext.sender();
        if (countdown == null || !countdown.isRunning()) {
            plugin.messenger().broadcastMessage(NodePath.path("command", "restart", "abort", "not-running"));
            return;
        }

        countdown.abort();
        plugin.messenger().broadcastMessage(NodePath.path("event", "restart", "abort"));
        plugin.messenger().sendMessage(sender, NodePath.path("command", "restartcountdown", "abort", "success"));
    }

    private void handleStart(CommandContext<CommandSender> commandSenderCommandContext) {
        var sender = commandSenderCommandContext.sender();
        var duration = (Duration) commandSenderCommandContext.get("time");
        var reason = (String) commandSenderCommandContext.get("reason");
        var countDownSettings = plugin.configuration().countDownSettings().restartCountDown();
        if (countdown == null || !countdown.isRunning()) {
            countdown = Countdown.builder()
                    .withRunOnFinish(() -> handleCountDownFinish(reason))
                    .withRunOnStep(durationLeft -> handleCountDownStep(durationLeft, countDownSettings, reason))
                    .build();

            countdown.start(duration.toSeconds());
            plugin.messenger().sendMessage(sender, NodePath.path("command", "restartcountdown", "start", "success"));
            return;
        }

        plugin.messenger().sendMessage(
                sender,
                NodePath.path("command", "restartcountdown", "start", "already-running")
        );
    }

    private void handleCountDownStep(Duration duration, List<CountDownEntry> countDownEntries, String reason) {
        var currCountDownEntry = countDownEntries.stream()
                .filter(countDownLine -> countDownLine.second() == duration.toSeconds())
                .findFirst();

        if (currCountDownEntry.isEmpty()) {
            return;
        }

        CountDownMessenger.broadcastCountDown(
                plugin.messenger(),
                currCountDownEntry.get(),
                NodePath.path("event", "restart", "in", "chat"),
                NodePath.path("event", "restart", "in", "title"),
                NodePath.path("event", "restart", "in", "subtitle"),
                Placeholder.parsed("reason", reason)
        );
    }

    private void handleCountDownFinish(String reason) {
        plugin.messenger().broadcastMessage(
                NodePath.path("event", "restart", "now", "chat"),
                Placeholder.parsed("reason", reason)
        );

        plugin.messenger().broadcastTitle(
                NodePath.path("event", "restart", "now", "title"),
                NodePath.path("event", "restart", "now", "subtitle"),
                Title.Times.times(Duration.ZERO, Duration.ofSeconds(3), Duration.ZERO),
                Placeholder.parsed("reason", reason)
        );
        plugin.getServer().getScheduler().runTaskLater(plugin, () ->
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "restart"), 20
        );
    }
}