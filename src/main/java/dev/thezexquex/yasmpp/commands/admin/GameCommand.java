package dev.thezexquex.yasmpp.commands.admin;

import de.unknowncity.astralib.common.timer.Countdown;
import de.unknowncity.astralib.common.timer.Stopwatch;
import de.unknowncity.astralib.paper.api.command.PaperCommand;
import de.unknowncity.astralib.paper.api.timer.aborttrigger.MovementAbortTrigger;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.commands.util.CountDownMessenger;
import dev.thezexquex.yasmpp.configuration.settings.CountDownEntry;
import dev.thezexquex.yasmpp.configuration.settings.CountDownSettings;
import dev.thezexquex.yasmpp.data.adapter.LocationAdapter;
import dev.thezexquex.yasmpp.util.PlayerProgressUtil;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.spongepowered.configurate.NodePath;

import java.time.Duration;
import java.util.List;
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
        var countDownSettings = plugin.configuration().countDownSettings().gameStartCountDown();

        var countDown = Countdown.builder()
                .withRunOnFinish(this::handleCountDownFinish)
                .withRunOnStep(duration -> handleCountDownStep(duration, countDownSettings))
                .withTimeUnit(TimeUnit.SECONDS)
                .build();

        countDown.start(10);
    }

    private void handleCountDownStep(Duration duration, List<CountDownEntry> countDownEntries) {
        var currCountDownEntry = countDownEntries.stream()
                .filter(countDownLine -> countDownLine.second() == duration.toSeconds())
                .findFirst();

        if (currCountDownEntry.isEmpty()) {
            return;
        }

        CountDownMessenger.broadcastCountDown(
                plugin.messenger(),
                currCountDownEntry.get(),
                NodePath.path("event", "game-start", "in", "chat"),
                NodePath.path("event", "game-start", "in", "title"),
                NodePath.path("event", "game-start", "in", "subtitle")
        );
    }

    private void handleCountDownFinish() {
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
