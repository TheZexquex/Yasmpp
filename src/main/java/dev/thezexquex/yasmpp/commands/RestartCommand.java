package dev.thezexquex.yasmpp.commands;

import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.DurationArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.core.command.BaseCommand;
import dev.thezexquex.yasmpp.core.timer.Countdown;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.spongepowered.configurate.NodePath;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class RestartCommand extends BaseCommand {
    private Countdown countdown;

    public RestartCommand(YasmpPlugin plugin) {
        super(plugin);
        this.countdown = new Countdown();
    }

    @Override
    public void register(CommandManager<CommandSender> commandManager) {
        commandManager.command(commandManager.commandBuilder("restartcountdown")
                .permission("yasmpp.command.restartcountdown")
                .literal("start")
                .argument(DurationArgument.of("time"))
                .argument(StringArgument.of("reason", StringArgument.StringMode.QUOTED))
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
        var sender = commandSenderCommandContext.getSender();
        plugin.messenger().sendMessage(sender, NodePath.path("command", "restartcountdown", "help"));
    }

    private void handleAbort(CommandContext<CommandSender> commandSenderCommandContext) {
        var sender = commandSenderCommandContext.getSender();
        if (!countdown.isRunning()) {
            plugin.messenger().broadcastToServer(NodePath.path("command", "restart", "abort", "not-running"));
            return;
        }

        countdown.abort();
        plugin.messenger().broadcastToServer(NodePath.path("event", "restart", "abort"));
        plugin.messenger().sendMessage(sender, NodePath.path("command", "restartcountdown", "abort", "success"));
    }

    private void handleStart(CommandContext<CommandSender> commandSenderCommandContext) {
        var sender = commandSenderCommandContext.getSender();
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

        var countDownSettings = plugin.configuration().countDownSettings().restartCountDownSettings();
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
                plugin.messenger().broadcastToServer(
                        NodePath.path("event", "restart", "in", "chat"),
                        TagResolver.resolver("hours", Tag.preProcessParsed(hours)),
                        TagResolver.resolver("minutes", Tag.preProcessParsed(minutes)),
                        TagResolver.resolver("seconds", Tag.preProcessParsed(seconds)),
                        TagResolver.resolver("reason", Tag.preProcessParsed(reason))
                );
            }

            if (currentCountDownSetting.useTitle()) {
                plugin.messenger().broadcastTitleToServer(
                        NodePath.path("event", "restart", "in", "title"),
                        NodePath.path("event", "restart", "in", "subtitle"),
                        TagResolver.resolver("hours", Tag.preProcessParsed(hours)),
                        TagResolver.resolver("minutes", Tag.preProcessParsed(minutes)),
                        TagResolver.resolver("seconds", Tag.preProcessParsed(seconds)),
                        TagResolver.resolver("reason", Tag.preProcessParsed(reason))
                );
            }

            if (currentCountDownSetting.useSound()) {
                plugin.getServer().getOnlinePlayers().forEach(player -> player.playSound(currentCountDownSetting.soundName()));
            }
        }, () -> plugin.getServer().getScheduler().runTask(plugin, () -> {
            plugin.messenger().broadcastToServer(
                    NodePath.path("event", "restart", "now", "chat"),
                    TagResolver.resolver("reason", Tag.preProcessParsed(reason))
            );

            plugin.messenger().broadcastTitleToServer(
                    NodePath.path("event", "restart", "now", "title"),
                    NodePath.path("event", "restart", "now", "subtitle"),
                    TagResolver.resolver("reason", Tag.preProcessParsed(reason))
            );
            plugin.getServer().getScheduler().runTaskLater(plugin, () ->
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "restart"), 20
            );
        }));
    }
}
