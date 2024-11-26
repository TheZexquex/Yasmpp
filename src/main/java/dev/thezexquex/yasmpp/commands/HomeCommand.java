package dev.thezexquex.yasmpp.commands;

import de.unknowncity.astralib.common.timer.Countdown;
import de.unknowncity.astralib.paper.api.command.PaperCommand;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.data.adapter.LocationAdapter;
import dev.thezexquex.yasmpp.modules.shop.HomeShop;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.suggestion.Suggestion;
import org.spongepowered.configurate.NodePath;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.incendo.cloud.parser.standard.StringParser.stringParser;

public class HomeCommand extends PaperCommand<YasmpPlugin> {
    public HomeCommand(YasmpPlugin plugin) {
        super(plugin);
    }

    @Override
    public void apply(CommandManager<CommandSender> commandManager) {
        commandManager.command(commandManager.commandBuilder("home")
                .senderType(Player.class)
                .required("homeName", stringParser(), (context, input) -> getHomeSuggestions(context.sender()))
                .permission("yasmpp.command.home")
                .handler(this::handleHome)
        );

        commandManager.command(commandManager.commandBuilder("sethome")
                .required("homeName", stringParser())
                .permission("yasmpp.command.sethome")
                .flag(commandManager.flagBuilder("override").withAliases("o"))
                .senderType(Player.class)
                .handler(this::handleSetHome)
        );

        commandManager.command(commandManager.commandBuilder("delhome")
                .senderType(Player.class)
                .required("homeName", stringParser(), (context, input) -> getHomeSuggestions(context.sender()))
                .permission("yasmpp.command.delhome")
                .handler(this::handleDelHome)
        );

        commandManager.command(commandManager.commandBuilder("homes")
                .permission("yasmpp.command.homes")
                .senderType(Player.class)
                .handler(this::handleHomes)
        );
    }

    private void handleHomes(CommandContext<Player> context) {
        var player = context.sender();
        var smpPlayerOpt = plugin.smpPlayerService().getSmpPlayer(player);
        smpPlayerOpt.ifPresent(smpPlayer -> {
            if (smpPlayer.getHomeCache().isEmpty()) {
                plugin.messenger().sendMessage(player, NodePath.path("command", "homes", "empty"));
            } else {
                plugin.messenger().sendMessage(player, NodePath.path("command", "homes", "header"));
                smpPlayer.getHomeCache().forEach((home -> plugin.messenger().sendMessage(
                        player,
                        NodePath.path("command", "homes", "entry"),
                        home.tagResolvers()
                )));
                plugin.messenger().sendMessage(player, NodePath.path("command", "homes", "footer"));
            }
        });
    }


    private void handleDelHome(CommandContext<Player> context) {
        var player = context.sender();
        var homeName = (String) context.get("homeName");

        var smpPlayerOpt = plugin.smpPlayerService().getSmpPlayer(player);

        smpPlayerOpt.ifPresent(smpPlayer -> {
            if (smpPlayer.hasHome(homeName)) {
                smpPlayer.deleteHome(homeName);
                plugin.messenger().sendMessage(player,
                        NodePath.path("command", "delhome", "success"),
                        Placeholder.parsed("home-name", homeName)
                );
            } else {
                plugin.messenger().sendMessage(player,
                        NodePath.path("command", "delhome", "not-exists"),
                        Placeholder.parsed("home-name", homeName)
                );
            }
        });
    }

    private void handleSetHome(CommandContext<Player> contex) {
        var player = contex.sender();
        var homeName = (String) contex.get("homeName");

        var smpPlayerOpt = plugin.smpPlayerService().getSmpPlayer(player);
        if (smpPlayerOpt.isPresent()) {
            var smpPlayer = smpPlayerOpt.get();

            if (smpPlayer.hasHome(homeName) && !contex.flags().hasFlag("override")) {
                plugin.messenger().sendMessage(player,
                        NodePath.path("command", "sethome", "already-exists"),
                        Placeholder.parsed("home-name", homeName)
                );
            } else {
                var maxHomes = HomeShop.getCurrentMaxHomes(player);
                var currHomes = smpPlayer.getHomeCache().size();

                if (maxHomes == -1 || maxHomes > currHomes) {
                    smpPlayer.createOrUpdateHome(homeName, player.getLocation());
                    plugin.messenger().sendMessage(player,
                            NodePath.path("command", "sethome", "success"),
                            Placeholder.parsed("home-name", homeName),
                            Placeholder.parsed("curr-homes", String.valueOf(currHomes + 1)),
                            Placeholder.parsed("max-homes", String.valueOf(maxHomes))
                    );
                } else {
                    plugin.messenger().sendMessage(player,
                            NodePath.path("command", "sethome", "max-homes-reached"),
                            Placeholder.parsed("home-name", homeName),
                            Placeholder.parsed("curr-homes", String.valueOf(currHomes)),
                            Placeholder.parsed("max-homes", String.valueOf(maxHomes))
                    );
                }
            }
        }
    }

    private void handleHome(CommandContext<Player> commandSenderCommandContext) {
        var player = commandSenderCommandContext.sender();
        var homeName = (String) commandSenderCommandContext.get("homeName");

        var smpPlayerOpt = plugin.smpPlayerService().getSmpPlayer(player);

        smpPlayerOpt.ifPresent(smpPlayer -> {

            if (smpPlayer.isInTeleportWarmup()) {
                plugin.messenger().sendMessage(player,
                        NodePath.path("event", "teleport", "already-teleporting")
                );
                return;
            }

            if (!smpPlayer.hasHome(homeName)) {
                plugin.messenger().sendMessage(player,
                        NodePath.path("command", "home", "notfound"),
                        Placeholder.parsed("home-name", homeName)
                );
                return;
            }

            var countDownSettings = plugin.configuration().countDownSettings().teleportCountDown();

            smpPlayer.getHome(homeName).ifPresent(home -> {

                var countDown = new Countdown();
                var countDownInSec = plugin.configuration().teleportSettings().teleportCoolDownInSeconds();

                // Player is allowed to bypass the teleport warmup time
                if (plugin.configuration().teleportSettings().permissionBypassesCoolDown()
                        && player.hasPermission("yasmpp.teleport.cooldown.bypass")) {
                    countDownInSec = 0;
                }

                countDown.start(countDownInSec, TimeUnit.SECONDS, (timeSpan) -> {
                    smpPlayer.setCurrentTeleportCountDown(countDown);

                    var currCountDownEntryOpt = countDownSettings.stream()
                            .filter(countDownLine -> countDownLine.second() == timeSpan)
                            .findFirst();

                    if (currCountDownEntryOpt.isEmpty()) {
                        return;
                    }

                    var currCountDownEntry = currCountDownEntryOpt.get();

                    if (currCountDownEntry.useChat()) {
                        plugin.messenger().sendMessage(
                                player,
                                NodePath.path("event", "teleport", "countdown"),
                                Placeholder.parsed("time", String.valueOf(timeSpan))
                        );
                    }

                    if (currCountDownEntry.useTitle()) {
                        plugin.messenger().sendTitle(
                                player,
                                NodePath.path("event", "teleport", "countdown"),
                                null,
                                Title.Times.times(Duration.ZERO, Duration.ofSeconds(1), Duration.ZERO),
                                Placeholder.parsed("time", String.valueOf(timeSpan))
                        );
                    }

                    if (currCountDownEntry.useSound()) {
                        player.playSound(currCountDownEntry.sound());

                    }
                }, () -> {
                    plugin.getServer().getScheduler().runTask(plugin, () -> player.teleportAsync(
                            LocationAdapter.adapt(home.locationContainer(), player.getServer())
                    ));
                    plugin.messenger().sendMessage(player,
                            NodePath.path("command", "home", "success"),
                            Placeholder.parsed("home-name", homeName)
                    );
                    smpPlayer.cancelCurrentTeleport();
                });
            });
        });
    }

    private CompletableFuture<List<Suggestion>> getHomeSuggestions(Player player) {
        return CompletableFuture.completedFuture(
                plugin.smpPlayerService()
                        .getSmpPlayer(player)
                        .get()
                        .getHomeCache()
                        .stream()
                        .map(home -> Suggestion.suggestion(home.name()))
                        .toList()
        );
    }
}
