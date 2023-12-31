package dev.thezexquex.yasmpp.commands;

import cloud.commandframework.CommandManager;
import cloud.commandframework.context.CommandContext;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.core.command.BaseCommand;
import dev.thezexquex.yasmpp.core.timer.Countdown;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.NodePath;

import java.util.concurrent.TimeUnit;

public class SpawnCommand extends BaseCommand {
    public SpawnCommand(YasmpPlugin plugin) {
        super(plugin);
    }

    @Override
    public void register(CommandManager<CommandSender> commandManager) {
        commandManager.command(
                commandManager.commandBuilder("spawn")
                        .permission("yasmpp.command.spawn")
                        .senderType(Player.class)
                        .handler(this::handleSpawn)
        );
    }

    private void handleSpawn(CommandContext<CommandSender> commandSenderCommandContext) {
        var player = (Player) commandSenderCommandContext.getSender();

        if (plugin.getTeleportQueue().isTeleporting(player)) {
            plugin.messenger().sendMessage(
                    player,
                    NodePath.path("event", "teleport", "already-teleporting")
            );
            return;
        }

        var locationService = plugin.locationService();

        if (!locationService.existsCachedLocation("spawn")) {
            plugin.messenger().sendMessage(
                    player,
                    NodePath.path("command", "spawn", "no-spawn")
            );
            return;
        }

        var location = locationService.getCachedLocation("spawn");
        var countDownInSec = plugin.configuration().teleportSettings().teleportCoolDownInSeconds();

        if (plugin.configuration().teleportSettings().permissionBypassesCoolDown() && player.hasPermission("yasmpp.teleport.cooldown.bypass")) {
            countDownInSec = 0;
        }

        var countDown = new Countdown();
        var countDownSettings = plugin.configuration().countDownSettings().teleportCountDownSettings();

        plugin.getTeleportQueue().addToTeleportQueue(player, countDown);

        countDown.start(countDownInSec, TimeUnit.SECONDS, (timeSpan) -> {
            var currentCDSettingOpt = countDownSettings.stream().filter(countDownLine -> countDownLine.second() == timeSpan).findFirst();

            if (currentCDSettingOpt.isEmpty()) {
                return;
            }
            var currentCountDownSetting = currentCDSettingOpt.get();

            if (currentCountDownSetting.useChat()) {
                plugin.messenger().sendMessage(
                        player,
                        NodePath.path("event", "teleport", "countdown"),
                        TagResolver.resolver("time", Tag.preProcessParsed(String.valueOf(timeSpan)))
                );
            }

            if (currentCountDownSetting.useTitle()) {
                plugin.messenger().sendTitle(
                        player,
                        NodePath.path("event", "teleport", "countdown"),
                        null,
                        TagResolver.resolver("time", Tag.preProcessParsed(String.valueOf(timeSpan)))
                );
            }

            if (currentCountDownSetting.useSound()) {
                player.playSound(currentCountDownSetting.soundName());
            }
        }, () -> {
            plugin.getServer().getScheduler().runTask(plugin, () -> player.teleport(location));
            plugin.getTeleportQueue().clearQueue(player);
        });
    }
}