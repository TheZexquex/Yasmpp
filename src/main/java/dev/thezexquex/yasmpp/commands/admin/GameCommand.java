package dev.thezexquex.yasmpp.commands.admin;

import cloud.commandframework.CommandManager;
import cloud.commandframework.context.CommandContext;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.core.command.BaseCommand;
import dev.thezexquex.yasmpp.core.timer.Countdown;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.spongepowered.configurate.NodePath;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class GameCommand extends BaseCommand {
    public GameCommand(YasmpPlugin plugin) {
        super(plugin);
    }

    @Override
    public void register(CommandManager<CommandSender> commandManager) {
        commandManager.command(commandManager.commandBuilder("game")
                .literal("start")
                .permission("yasmpp.command.game.start")
                .handler(this::handelStart)
        );

        commandManager.command(commandManager.commandBuilder("game")
                .literal("reset")
                .permission("yasmpp.command.game.reset")
                .handler(this::handelReset)
        );
    }

    private void handelStart(CommandContext<CommandSender> commandSenderCommandContext) {
        var sender = commandSenderCommandContext.getSender();

        var countDown = new Countdown();
        var countDownSettings = plugin.configuration().countDownSettings().gameStartCountDownSettings();

        countDown.start(10, TimeUnit.SECONDS, (timeSpan) -> {
            var currentCDSettingOpt = countDownSettings.stream().filter(countDownLine -> countDownLine.second() == timeSpan).findFirst();

            if (currentCDSettingOpt.isEmpty()) {
                return;
            }

            var currentCountDownSetting = currentCDSettingOpt.get();

            var dur = Duration.ofSeconds(timeSpan);
            var hours = dur.toHours() == 0 ? "" : dur.toHours() + " h ";
            var minutes = dur.toMinutesPart() == 0 ? "" : dur.toMinutesPart() + " m ";
            var seconds = dur.toSecondsPart() == 0 ? "" : dur.toSecondsPart() + " s";

            if (timeSpan != 0)  {
                if (currentCountDownSetting.useChat()) {
                    plugin.messenger().broadcastToServer(
                            NodePath.path("event", "game-start", "in", "chat"),
                            TagResolver.resolver("hours", Tag.preProcessParsed(hours)),
                            TagResolver.resolver("minutes", Tag.preProcessParsed(minutes)),
                            TagResolver.resolver("seconds", Tag.preProcessParsed(seconds))
                    );
                }

                if (currentCountDownSetting.useTitle()) {
                    plugin.messenger().broadcastTitleToServer(
                            NodePath.path("event", "game-start", "in", "title"),
                            NodePath.path("event", "game-start", "in", "subtitle"),
                            TagResolver.resolver("hours", Tag.preProcessParsed(hours)),
                            TagResolver.resolver("minutes", Tag.preProcessParsed(minutes)),
                            TagResolver.resolver("seconds", Tag.preProcessParsed(seconds))
                    );
                }
            }

            if (currentCountDownSetting.useSound()) {
                plugin.getServer().getOnlinePlayers().forEach(player -> player.playSound(currentCountDownSetting.soundName()));
            }

        }, () -> {
            plugin.messenger().broadcastToServer(
                    NodePath.path("event", "game-start", "now", "chat")
            );

            plugin.messenger().broadcastTitleToServer(
                    NodePath.path("event", "game-start", "now", "title"),
                    NodePath.path("event", "game-start", "now", "subtitle")
            );
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                Location location;

                if (!plugin.locationService().existsCachedLocation("spawn")) {
                    location = plugin.getServer().getWorld("world").getSpawnLocation();
                } else {
                    location = plugin.locationService().getCachedLocation("spawn");
                }

                var worldBorder = location.getWorld().getWorldBorder();

                worldBorder.setCenter(location);
                worldBorder.setSize(plugin.configuration().generalSettings().generalBorderSettings().borderDiameterGamePhase());
            });
        });
    }

    private void handelReset(CommandContext<CommandSender> commandSenderCommandContext) {
        var sender = commandSenderCommandContext.getSender();

        Location location;
        World world;

        if (!plugin.locationService().existsCachedLocation("spawn")) {
            location = plugin.getServer().getWorld("world").getSpawnLocation();
        } else {
            location = plugin.locationService().getCachedLocation("spawn");
        }

        plugin.getServer().getOnlinePlayers().forEach(player -> player.teleport(location));
        var worldBorder = location.getWorld().getWorldBorder();

        worldBorder.setCenter(location);
        worldBorder.setSize(plugin.configuration().generalSettings().generalBorderSettings().borderDiameterLobbyPhase());
    }
}
