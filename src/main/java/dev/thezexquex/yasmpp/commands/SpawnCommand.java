package dev.thezexquex.yasmpp.commands;

import de.unknowncity.astralib.common.timer.Countdown;
import de.unknowncity.astralib.paper.api.command.PaperCommand;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.data.adapter.LocationAdapter;
import dev.thezexquex.yasmpp.data.entity.WorldPosition;
import dev.thezexquex.yasmpp.data.entity.SmpPlayer;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.spongepowered.configurate.NodePath;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

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
            if (smpPlayer.isInTeleportWarmup()) {
                plugin.messenger().sendMessage(
                        player,
                        NodePath.path("event", "teleport", "already-teleporting")
                );
                return;
            }

            var locationService = plugin.locationService();

            locationService.getLocation("spawn").whenComplete((locationOpt, throwable) -> {
                locationOpt.ifPresentOrElse(worldPosition -> {
                    startSpawnTeleport(smpPlayer, worldPosition);
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

        if (plugin.configuration().teleportSettings().permissionBypassesCoolDown()
                && player.toBukkitPlayer().hasPermission("yasmpp.teleport.cooldown.bypass")) {
            countDownInSec = 0;
        }

        var countDown = new Countdown();
        var countDownSettings = plugin.configuration().countDownSettings().teleportCountDown();

        countDown.start(countDownInSec, TimeUnit.SECONDS, (timeSpan) -> {
            player.setCurrentTeleportCountDown(countDown);

            var currentCDSettingOpt = countDownSettings.stream().filter(countDownLine -> countDownLine.second() == timeSpan).findFirst();

            if (currentCDSettingOpt.isEmpty()) {
                return;
            }
            var currentCountDownSetting = currentCDSettingOpt.get();

            if (currentCountDownSetting.useChat()) {
                plugin.messenger().sendMessage(
                        player.toBukkitPlayer(),
                        NodePath.path("event", "teleport", "countdown"),
                        TagResolver.resolver("time", Tag.preProcessParsed(String.valueOf(timeSpan)))
                );
            }

            if (currentCountDownSetting.useTitle()) {
                plugin.messenger().sendTitle(
                        player.toBukkitPlayer(),
                        NodePath.path("event", "teleport", "countdown"),
                        null,
                        Title.Times.times(Duration.ZERO, Duration.ofSeconds(1), Duration.ZERO),
                        Placeholder.parsed("time", String.valueOf(timeSpan))
                );
            }

            if (currentCountDownSetting.useSound()) {
                player.toBukkitPlayer().playSound(currentCountDownSetting.sound());
            }
        }, () -> plugin.getServer().getScheduler().runTask(plugin, () -> {
            player.toBukkitPlayer().teleportAsync(LocationAdapter.adapt(worldPosition.locationContainer(), plugin.getServer()));
            player.cancelCurrentTeleport();
        }));
    }
}