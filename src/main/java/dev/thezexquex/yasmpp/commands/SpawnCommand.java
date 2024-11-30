package dev.thezexquex.yasmpp.commands;

import de.unknowncity.astralib.common.timer.Countdown;
import de.unknowncity.astralib.paper.api.command.PaperCommand;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.commands.util.CountDownMessenger;
import dev.thezexquex.yasmpp.configuration.settings.CountDownEntry;
import dev.thezexquex.yasmpp.data.adapter.LocationAdapter;
import dev.thezexquex.yasmpp.data.entity.SmpPlayer;
import dev.thezexquex.yasmpp.data.entity.WorldPosition;
import dev.thezexquex.yasmpp.util.timer.aborttrigger.MovementAbortTrigger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.spongepowered.configurate.NodePath;

import java.time.Duration;
import java.util.List;

public class SpawnCommand extends PaperCommand<YasmpPlugin> {
    public SpawnCommand(YasmpPlugin plugin) {
        super(plugin);
    }

    @Override
    public void apply(CommandManager<CommandSender> commandManager) {
        commandManager.command(commandManager.commandBuilder("spawn")
                .permission("yasmpp.command.spawn")
                .senderType(Player.class)
                .handler(this::handleSpawn)
        );
    }

    private void handleSpawn(CommandContext<Player> commandSenderCommandContext) {
        var player = commandSenderCommandContext.sender();

        plugin.smpPlayerService().getSmpPlayer(player).ifPresent(smpPlayer -> {
            if (smpPlayer.isCurrentlyInTeleport()) {
                plugin.messenger().sendMessage(
                        player,
                        NodePath.path("event", "teleport", "already-teleporting")
                );
                return;
            }

            var locationService = plugin.locationService();

            locationService.getLocation("spawn").whenComplete((locationOpt, throwable) -> {
                locationOpt.ifPresentOrElse(worldPosition -> {
                    plugin.getServer().getScheduler().runTask(plugin, () -> startSpawnTeleport(smpPlayer, worldPosition));
                }, () -> {
                    plugin.messenger().sendMessage(
                            player,
                            NodePath.path("command", "spawn", "no-spawn")
                    );
                });
            });
        });
    }

    private void startSpawnTeleport(SmpPlayer player, WorldPosition worldPosition) {
        var countDownInSec = plugin.configuration().teleportSettings().teleportCoolDownInSeconds();
        var countDownSettings = plugin.configuration().countDownSettings().teleportCountDown();

        if (plugin.configuration().teleportSettings().permissionBypassesCoolDown()
                && player.toBukkitPlayer().hasPermission("yasmpp.teleport.cooldown.bypass")) {
            countDownInSec = 0;
        }
        player.currentlyInTeleport(true);
        var countDown = Countdown.builder()
                .withAbortTriggers(new MovementAbortTrigger(player.toBukkitPlayer(), () -> {
                    player.currentlyInTeleport(false);
                    plugin.messenger().sendMessage(player.toBukkitPlayer(), NodePath.path("event", "teleport", "cancel"));
                }))
                .withRunOnStep(duration -> handleCountDownStep(duration, countDownSettings, player.toBukkitPlayer()))
                .withRunOnFinish(() -> handleCountDownFinish(worldPosition, player))
                .build();

        countDown.start(countDownInSec);
    }

    private void handleCountDownStep(Duration duration, List<CountDownEntry> countDownEntries, Player player) {
        var currCountDownEntry = countDownEntries.stream()
                .filter(countDownLine -> countDownLine.second() == duration.toSeconds())
                .findFirst();

        if (currCountDownEntry.isEmpty()) {
            return;
        }

        CountDownMessenger.sendCountDown(
                player,
                plugin.messenger(),
                currCountDownEntry.get(),
                NodePath.path("event", "teleport", "chat"),
                NodePath.path("event", "teleport", "title"),
                NodePath.path("event", "teleport", "subtitle")
        );
    }

    private void handleCountDownFinish(WorldPosition spawn, SmpPlayer smpPlayer) {
        var player = smpPlayer.toBukkitPlayer();
        smpPlayer.currentlyInTeleport(false);
        plugin.getServer().getScheduler().runTask(plugin, () -> player.teleportAsync(
                LocationAdapter.adapt(spawn.locationContainer(), player.getServer())
        ));

        plugin.messenger().sendMessage(player,
                NodePath.path("command", "spawn", "success")
        );
    }
}