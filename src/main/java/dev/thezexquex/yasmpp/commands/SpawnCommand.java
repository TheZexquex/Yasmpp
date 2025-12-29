package dev.thezexquex.yasmpp.commands;

import de.unknowncity.astralib.common.temporal.PlayerBoundCooldownAction;
import de.unknowncity.astralib.paper.api.command.PaperCommand;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.commands.util.CountDownMessenger;
import dev.thezexquex.yasmpp.configuration.settings.CountDownEntry;
import dev.thezexquex.yasmpp.data.adapter.LocationAdapter;
import dev.thezexquex.yasmpp.data.entity.SmpPlayer;
import dev.thezexquex.yasmpp.data.entity.WorldPosition;
import dev.thezexquex.yasmpp.util.timer.BukkitCountdown;
import dev.thezexquex.yasmpp.util.timer.aborttrigger.MovementAbortTrigger;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
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

    private PlayerBoundCooldownAction playerBoundCooldownAction = new PlayerBoundCooldownAction(Duration.ofSeconds(10));

    @Override
    public void apply(CommandManager<CommandSender> commandManager) {
        commandManager.command(commandManager.commandBuilder("spawn")
                .permission("yasmpp.command.spawn")
                .senderType(Player.class)
                .handler(this::handleSpawn)
        );
    }

    private void handleSpawn(CommandContext<Player> commandSenderCommandContext) {
        Bukkit.getServer().getScheduler().runTask(plugin, () -> {
            var player = commandSenderCommandContext.sender();

            plugin.smpPlayerService().get(player).ifPresentOrElse(smpPlayer -> {
                if (smpPlayer.isCurrentlyInTeleport()) {
                    plugin.messenger().sendMessage(
                            player,
                            NodePath.path("event", "teleport", "already-teleporting")
                    );
                    return;
                }

                var locationService = plugin.locationService();

                var locationOpt = locationService.getLocation("spawn");
                locationOpt.ifPresentOrElse(worldPosition -> {
                    plugin.getLogger().info("worldPosition: " + worldPosition);
                    plugin.getServer().getScheduler().runTask(plugin, () -> startSpawnTeleport(smpPlayer, worldPosition));
                }, () -> {
                    plugin.messenger().sendMessage(
                            player,
                            NodePath.path("command", "spawn", "no-spawn")
                    );
                });
            }, () -> {
                plugin.getLogger().info("virtueller Account existiert nicht");
            });
        });
    }

    private void startSpawnTeleport(SmpPlayer player, WorldPosition worldPosition) {
        var countDownInSec = plugin.configuration().teleport().teleportCoolDownInSeconds();
        var countDownSettings = plugin.countdownConfiguration().countdown().teleport();

        if (plugin.configuration().teleport().permissionBypassesCoolDown()
                && player.toBukkitPlayer().hasPermission("yasmpp.teleport.cooldown.bypass")) {
            countDownInSec = 0;
        }
        player.currentlyInTeleport(true);
        plugin.getLogger().info("countDownInSec: " + countDownInSec);

        var countDown = BukkitCountdown.builder(plugin)
                .withAbortTriggers(new MovementAbortTrigger(player.toBukkitPlayer(), () -> {
                    player.currentlyInTeleport(false);
                    plugin.messenger().sendMessage(player.toBukkitPlayer(), NodePath.path("event", "teleport", "cancel"));
                }))
                .withRunOnAbort(() -> player.currentlyInTeleport(false))
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
            plugin.getLogger().info("Countdown Entry Empty");
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

        plugin.getServer().getScheduler().runTask(plugin, () -> player.teleport(
                LocationAdapter.adapt(spawn.locationContainer(), player.getServer())
        ));

        player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);

        plugin.messenger().sendMessage(player,
                NodePath.path("command", "spawn", "success")
        );
    }
}