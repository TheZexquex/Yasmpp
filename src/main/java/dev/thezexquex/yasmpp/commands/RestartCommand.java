package dev.thezexquex.yasmpp.commands;

import de.unknowncity.astralib.common.timer.Countdown;
import de.unknowncity.astralib.paper.api.command.PaperCommand;
import dev.thezexquex.yasmpp.YasmpPlugin;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.spongepowered.configurate.NodePath;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.incendo.cloud.parser.standard.DurationParser.durationParser;
import static org.incendo.cloud.parser.standard.StringParser.quotedStringParser;

public class RestartCommand extends PaperCommand<YasmpPlugin> {
    private final Countdown countdown;

    public RestartCommand(YasmpPlugin plugin) {
        super(plugin);
        this.countdown = new Countdown();
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
        if (!countdown.isRunning()) {
            plugin.messenger().broadcastMessage(NodePath.path("command", "restart", "abort", "not-running"));
            return;
        }

        countdown.abort();
        plugin.messenger().broadcastMessage(NodePath.path("event", "restart", "abort"));
        plugin.messenger().sendMessage(sender, NodePath.path("command", "restartcountdown", "abort", "success"));
    }

    private void handleStart(CommandContext<CommandSender> commandSenderCommandContext) {
        var sender = commandSenderCommandContext.sender();
        if (countdown.isRunning()) {
            plugin.messenger().sendMessage(
                    sender,
                    NodePath.path("command", "restartcountdown", "start", "already-running")
            );
            return;
        }

        var duration = (Duration) commandSenderCommandContext.get("time");
        var reason = (String) commandSenderCommandContext.get("reason");

        plugin.messenger().sendMessage(sender, NodePath.path("command", "restartcountdown", "start", "success"));

        var countDownSettings = plugin.configuration().countDownSettings().restartCountDown();
        countdown.start(duration.toSeconds(), TimeUnit.SECONDS, (timeSpan) -> {
            var currentCDSettingOpt = countDownSettings.stream().filter(countDownLine -> countDownLine.second() == timeSpan).findFirst();

            if (currentCDSettingOpt.isEmpty()) {
                return;
            }
            var currentCountDownSetting = currentCDSettingOpt.get();

            var dur = Duration.ofSeconds(timeSpan);
            var hours = dur.toHours() == 0 ? "" : dur.toHours() + " h ";
            var minutes = dur.toMinutesPart() == 0 ? "" : dur.toMinutesPart() + " m ";
            var seconds = dur.toSecondsPart() == 0 ? "" : dur.toSecondsPart() + " s";

            if (currentCountDownSetting.useChat()) {
                plugin.messenger().broadcastMessage(
                        NodePath.path("event", "restart", "in", "chat"),
                        TagResolver.resolver("hours", Tag.preProcessParsed(hours)),
                        TagResolver.resolver("minutes", Tag.preProcessParsed(minutes)),
                        TagResolver.resolver("seconds", Tag.preProcessParsed(seconds)),
                        TagResolver.resolver("reason", Tag.preProcessParsed(reason))
                );
            }

            if (currentCountDownSetting.useTitle()) {
                plugin.messenger().broadcastTitle(
                        NodePath.path("event", "restart", "in", "title"),
                        NodePath.path("event", "restart", "in", "subtitle"),
                        TagResolver.resolver("hours", Tag.preProcessParsed(hours)),
                        TagResolver.resolver("minutes", Tag.preProcessParsed(minutes)),
                        TagResolver.resolver("seconds", Tag.preProcessParsed(seconds)),
                        TagResolver.resolver("reason", Tag.preProcessParsed(reason))
                );
            }

            if (currentCountDownSetting.useSound()) {
                plugin.getServer().getOnlinePlayers().forEach(player -> player.playSound(currentCountDownSetting.sound()));
            }
        }, () -> plugin.getServer().getScheduler().runTask(plugin, () -> {
            plugin.messenger().broadcastMessage(
                    NodePath.path("event", "restart", "now", "chat"),
                    TagResolver.resolver("reason", Tag.preProcessParsed(reason))
            );

            plugin.messenger().broadcastTitle(
                    NodePath.path("event", "restart", "now", "title"),
                    NodePath.path("event", "restart", "now", "subtitle"),
                    Title.Times.times(Duration.ZERO, Duration.ofSeconds(3), Duration.ZERO),
                    TagResolver.resolver("reason", Tag.preProcessParsed(reason))
            );
            plugin.getServer().getScheduler().runTaskLater(plugin, () ->
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "restart"), 20
            );
        }));
    }
}
