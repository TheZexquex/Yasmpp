package dev.thezexquex.yasmpp.commands.admin;

import de.unknowncity.astralib.common.timer.Countdown;
import de.unknowncity.astralib.paper.api.command.PaperCommand;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.data.adapter.LocationAdapter;
import dev.thezexquex.yasmpp.util.PlayerProgressUtil;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.spongepowered.configurate.NodePath;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class GameCommand extends PaperCommand<YasmpPlugin> {
    public GameCommand(YasmpPlugin plugin) {
        super(plugin);
    }

    @Override
    public void apply(CommandManager<CommandSender> commandManager) {
        commandManager.command(commandManager.commandBuilder("game")
                .literal("start")
                .permission("yasmpp.command.game.start")
                .handler(this::handelStart)
        );

        commandManager.command(commandManager.commandBuilder("game")
                .literal("reset")
                .permission("yasmpp.command.game.reset")
                .flag(commandManager.flagBuilder("hard"))
                .handler(this::handelReset)
        );
    }

    private void handelStart(CommandContext<CommandSender> commandSenderCommandContext) {
        var sender = commandSenderCommandContext.sender();

        var countDown = new Countdown();
        var countDownSettings = plugin.configuration().countDownSettings().gameStartCountDown();

        countDown.start(10, TimeUnit.SECONDS, (timeSpan) -> {
            var currCountDownEntry = countDownSettings.stream()
                    .filter(countDownLine -> countDownLine.second() == timeSpan)
                    .findFirst();

            if (currCountDownEntry.isEmpty()) {
                return;
            }

            var currentCountDownSetting = currCountDownEntry.get();

            var dur = Duration.ofSeconds(timeSpan);
            var hours = dur.toHours() == 0 ? "" : dur.toHours() + " h ";
            var minutes = dur.toMinutesPart() == 0 ? "" : dur.toMinutesPart() + " m ";
            var seconds = dur.toSecondsPart() == 0 ? "" : dur.toSecondsPart() + " s";

            if (timeSpan != 0)  {
                if (currentCountDownSetting.useChat()) {
                    plugin.messenger().broadcastMessage(
                            NodePath.path("event", "game-start", "in", "chat"),
                            Placeholder.parsed("hours", hours),
                            Placeholder.parsed("minutes", minutes),
                            Placeholder.parsed("seconds", seconds)
                    );
                }

                if (currentCountDownSetting.useTitle()) {
                    plugin.messenger().broadcastTitle(
                            NodePath.path("event", "game-start", "in", "title"),
                            NodePath.path("event", "game-start", "in", "subtitle"),
                            Title.Times.times(Duration.ZERO, Duration.ofSeconds(1), Duration.ZERO),
                            Placeholder.parsed("hours", hours),
                            Placeholder.parsed("minutes", minutes),
                            Placeholder.parsed("seconds", seconds)
                    );
                }
            }

            if (currentCountDownSetting.useSound()) {
                for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                    onlinePlayer.playSound(currentCountDownSetting.sound());
                }
            }

        }, () -> {
            plugin.messenger().broadcastMessage(
                    NodePath.path("event", "game-start", "now", "chat")
            );

            plugin.messenger().broadcastTitle(
                    NodePath.path("event", "game-start", "now", "title"),
                    NodePath.path("event", "game-start", "now", "subtitle"),
                    Title.Times.times(Duration.ZERO, Duration.ofSeconds(1), Duration.ZERO)
            );
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                plugin.locationService().getLocation("spawn").whenComplete((location, throwable) -> {
                    if (location.isEmpty()) {
                        return;
                    }

                    var loc = LocationAdapter.adapt(location.get().locationContainer(), plugin.getServer());

                    plugin.getServer().getOnlinePlayers().forEach(player -> {
                        player.teleportAsync(loc);
                    });

                    var worldBorder = loc.getWorld().getWorldBorder();

                    worldBorder.setCenter(loc);
                    worldBorder.setSize(plugin.configuration().generalSettings().generalBorderSettings().borderDiameterGamePhase());
                });
            });
        });
    }

    private void handelReset(CommandContext<CommandSender> context) {
        var isHardReset = context.flags().hasFlag("hard");

        plugin.locationService().getLocation("spawn").whenComplete((location, throwable) -> {
            if (location.isEmpty()) {
                return;
            }

            var loc = LocationAdapter.adapt(location.get().locationContainer(), plugin.getServer());

            plugin.getServer().getOnlinePlayers().forEach(player -> {
                player.teleportAsync(loc);
                if (isHardReset) {
                    PlayerProgressUtil.revokeAllAdvancements(player);
                    PlayerProgressUtil.revokeAllRecipes(player);
                    PlayerProgressUtil.clearEnderChest(player);
                    PlayerProgressUtil.clearInventory(player);
                }
            });

            var worldBorder = loc.getWorld().getWorldBorder();

            worldBorder.setCenter(loc);
            worldBorder.setSize(plugin.configuration().generalSettings().generalBorderSettings().borderDiameterLobbyPhase());

            plugin.messenger().broadcastMessage(
                    NodePath.path("event", "game-reset", "complete")
            );
        });
    }
}
