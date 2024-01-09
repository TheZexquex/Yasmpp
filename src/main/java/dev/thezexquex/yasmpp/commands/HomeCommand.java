package dev.thezexquex.yasmpp.commands;

import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.core.command.BaseCommand;
import dev.thezexquex.yasmpp.core.timer.Countdown;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.NodePath;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class HomeCommand extends BaseCommand {
    public HomeCommand(YasmpPlugin plugin) {
        super(plugin);
    }

    @Override
    public void register(CommandManager<CommandSender> commandManager) {
        commandManager.command(
                commandManager.commandBuilder("home")
                        .argument(StringArgument.<CommandSender>builder("homeName").single().withSuggestionsProvider((objectCommandContext, s) -> {
                            if (objectCommandContext.getSender() instanceof Player player) {
                                return plugin.smpPlayerService().getSmpPlayer(player).get().getHomes().keySet().stream().toList();
                            }
                            return Collections.emptyList();
                        }).build())
                        .permission("yasmpp.command.home")
                        .senderType(Player.class)
                        .handler(this::handleHome)
        );

        commandManager.command(
                commandManager.commandBuilder("sethome")
                        .argument(StringArgument.single("homeName"))
                        .permission("yasmpp.command.sethome")
                        .senderType(Player.class)
                        .handler(this::handleSetHome)
        );

        commandManager.command(
                commandManager.commandBuilder("delhome")
                        .argument(StringArgument.<CommandSender>builder("homeName").single().withSuggestionsProvider((objectCommandContext, s) -> {
                            if (objectCommandContext.getSender() instanceof Player player) {
                                return plugin.smpPlayerService().getSmpPlayer(player).get().getHomes().keySet().stream().toList();
                            }
                            return Collections.emptyList();
                        }).build())
                        .permission("yasmpp.command.delhome")
                        .senderType(Player.class)
                        .handler(this::handleDelHome)
        );

        commandManager.command(
                commandManager.commandBuilder("homes")
                        .permission("yasmpp.command.homes")
                        .senderType(Player.class)
                        .handler(this::handleHomes)
        );
    }

    private void handleHomes(CommandContext<CommandSender> commandSenderCommandContext) {
        var player = (Player) commandSenderCommandContext.getSender();
        var smpPlayer = plugin.smpPlayerService().getSmpPlayer(player);
        smpPlayer.ifPresent(value -> {
            plugin.messenger().sendMessage(player, NodePath.path("command", "homes", "header"));
            if (value.getHomes().isEmpty()) {
                plugin.messenger().sendMessage(player, NodePath.path("command", "homes", "empty"));
            } else {
                value.getHomes().forEach(((homeName, location) -> {
                    plugin.messenger().sendMessage(
                            player,
                            NodePath.path("command", "homes", "entry"),
                            TagResolver.resolver("home-name", Tag.preProcessParsed(homeName)),
                            TagResolver.resolver("world", Tag.preProcessParsed(location.getWorld().getName())),
                            TagResolver.resolver("x", Tag.preProcessParsed(String.format("%.2f",location.getX()))),
                            TagResolver.resolver("y", Tag.preProcessParsed(String.format("%.2f",location.getY()))),
                            TagResolver.resolver("z", Tag.preProcessParsed(String.format("%.2f",location.getZ()))),
                            TagResolver.resolver("yaw", Tag.preProcessParsed(String.format("%.3f",location.getYaw()))),
                            TagResolver.resolver("pitch", Tag.preProcessParsed(String.format("%.3f",location.getPitch())))
                    );
                }));
                plugin.messenger().sendMessage(player, NodePath.path("command", "homes", "footer"));
            }
        });
    }

    private void handleDelHome(CommandContext<CommandSender> commandSenderCommandContext) {
        var player = (Player) commandSenderCommandContext.getSender();
        var homeName = (String) commandSenderCommandContext.get("homeName");

        var smpPlayerOpt = plugin.smpPlayerService().getSmpPlayer(player);;
        if (smpPlayerOpt.isPresent()) {
            var smpPlayer = smpPlayerOpt.get();

            if (smpPlayer.getHomes().containsKey(homeName)) {
                smpPlayer.deleteHome(homeName);
                plugin.homeService().deleteHome(player.getUniqueId(), homeName).thenAccept(result -> {
                    plugin.messenger().sendMessage(player,
                            NodePath.path("command", "delhome", result ? "success" : "error"),
                            TagResolver.resolver("home-name", Tag.preProcessParsed(homeName))
                    );
                });
            } else {
                plugin.messenger().sendMessage(player,
                        NodePath.path("command", "delhome", "not-exists"),
                        TagResolver.resolver("home-name", Tag.preProcessParsed(homeName))
                );
            }
        }

    }

    private void handleSetHome(CommandContext<CommandSender> commandSenderCommandContext) {
        var player = (Player) commandSenderCommandContext.getSender();
        var homeName = (String) commandSenderCommandContext.get("homeName");

        var smpPlayerOpt = plugin.smpPlayerService().getSmpPlayer(player);
        if (smpPlayerOpt.isPresent()) {
            var smpPlayer = smpPlayerOpt.get();

            if (smpPlayer.getHomes().containsKey(homeName)) {
                plugin.messenger().sendMessage(player,
                        NodePath.path("command", "sethome", "already-exists"),
                        TagResolver.resolver("home-name", Tag.preProcessParsed(homeName))
                );
            } else {
                var permissions = player.getEffectivePermissions();
                var permissionOpt = permissions.stream().filter(permissionAttachmentInfo -> permissionAttachmentInfo
                        .getPermission().startsWith("yasmpp.homes.")).findFirst();

                var maxHomesString = permissionOpt.map(permissionAttachmentInfo -> permissionAttachmentInfo.getPermission().replace("yasmpp.homes.", "")).orElse("0");
                int maxHomes;
                try {
                    maxHomes = Integer.parseInt(maxHomesString);
                } catch (NumberFormatException e) {
                    maxHomes = -1;
                }

                if (maxHomes == -1 || maxHomes > smpPlayer.getHomes().size()) {
                    plugin.homeService().saveHome(player.getUniqueId(), homeName, player.getLocation()).thenAccept(result -> {
                        smpPlayer.addHome(homeName, player.getLocation());
                        plugin.messenger().sendMessage(player,
                                NodePath.path("command", "sethome", result ? "success" : "error"),
                                TagResolver.resolver("home-name", Tag.preProcessParsed(homeName))
                        );
                    });
                } else {
                    plugin.messenger().sendMessage(player,
                            NodePath.path("command", "sethome", "max-homes-reached"),
                            TagResolver.resolver("home-name", Tag.preProcessParsed(homeName))
                    );
                }
            }
        }
    }

    private void handleHome(CommandContext<CommandSender> commandSenderCommandContext) {
        var player = (Player) commandSenderCommandContext.getSender();
        var homeName = (String) commandSenderCommandContext.get("homeName");

        var smpPlayer = plugin.smpPlayerService().getSmpPlayer(player);
        smpPlayer.ifPresent(value -> {
            value.getHome(homeName).ifPresentOrElse(location -> {

                var countDownInSec = plugin.configuration().teleportSettings().teleportCoolDownInSeconds();

                if (plugin.configuration().teleportSettings().permissionBypassesCoolDown() && player.hasPermission("yasmpp.teleport.cooldown.bypass")) {
                    countDownInSec = 0;
                }

                var countDown = new Countdown();
                var countDownSettings = plugin.configuration().countDownSettings().teleportCountDownSettings();

                plugin.teleportQueue().addToTeleportQueue(player, countDown);

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
                    plugin.messenger().sendMessage(player,
                            NodePath.path("command", "home", "success"),
                            TagResolver.resolver("home-name", Tag.preProcessParsed(homeName))
                    );
                    plugin.teleportQueue().clearQueue(player);
                });
            }, () -> {
                plugin.messenger().sendMessage(player,
                        NodePath.path("command", "home", "notfound"),
                        TagResolver.resolver("home-name", Tag.preProcessParsed(homeName))
                );
            });
        });
    }
}
