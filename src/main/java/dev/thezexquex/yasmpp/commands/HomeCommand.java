package dev.thezexquex.yasmpp.commands;

import de.unknowncity.astralib.paper.api.command.PaperCommand;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.commands.util.CountDownMessenger;
import dev.thezexquex.yasmpp.configuration.settings.CountDownEntry;
import dev.thezexquex.yasmpp.data.adapter.LocationAdapter;
import dev.thezexquex.yasmpp.data.entity.Home;
import dev.thezexquex.yasmpp.data.entity.SmpPlayer;
import dev.thezexquex.yasmpp.homes.gui.HomeSlotShop;
import dev.thezexquex.yasmpp.util.timer.BukkitCountdown;
import dev.thezexquex.yasmpp.util.timer.aborttrigger.MovementAbortTrigger;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.suggestion.Suggestion;
import org.spongepowered.configurate.NodePath;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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

        commandManager.command(commandManager.commandBuilder("homeshop")
                .permission("yasmpp.command.homeshop")
                .senderType(Player.class)
                .handler(this::handleHomeShop)
        );
    }

    private void handleHomeShop(@NonNull CommandContext<Player> context) {
        var sender = context.sender();
        HomeSlotShop.open(sender, plugin);
    }

    private void handleHomes(CommandContext<Player> context) {
        var player = context.sender();
        var smpPlayerOpt = plugin.smpPlayerService().get(player);
        smpPlayerOpt.ifPresent(smpPlayer -> {
            if (smpPlayer.getHomes().isEmpty()) {
                plugin.messenger().sendMessage(player, NodePath.path("command", "homes", "empty"));
            } else {
                plugin.messenger().sendMessage(player, NodePath.path("command", "homes", "header"));
                smpPlayer.getHomes().forEach((home -> plugin.messenger().sendMessage(
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

        var smpPlayerOpt = plugin.smpPlayerService().get(player);

        smpPlayerOpt.ifPresent(smpPlayer -> {
            if (smpPlayer.hasHomeWithName(homeName)) {
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

        var smpPlayerOpt = plugin.smpPlayerService().get(player);
        if (smpPlayerOpt.isPresent()) {
            var smpPlayer = smpPlayerOpt.get();

            if (smpPlayer.hasHomeWithName(homeName) && !contex.flags().hasFlag("override")) {
                plugin.messenger().sendMessage(player,
                        NodePath.path("command", "sethome", "already-exists"),
                        Placeholder.parsed("home-name", homeName)
                );
            } else if ((smpPlayer.hasHomeWithName(homeName) && contex.flags().hasFlag("override")) ||
                    (!smpPlayer.hasHomeWithName(homeName) && !contex.flags().hasFlag("override"))) {
                plugin.homeService().getHomeSlots(player.getUniqueId()).thenAccept(homeSlots -> {
                    var currHomes = smpPlayer.getHomes().size();

                    if (homeSlots == -1 || homeSlots > currHomes || contex.flags().hasFlag("override")) {
                        smpPlayer.createOrUpdateHome(homeName, player.getLocation());
                        plugin.messenger().sendMessage(player,
                                NodePath.path("command", "sethome", "success"),
                                Placeholder.parsed("home-name", homeName),
                                Placeholder.parsed("curr-homes", String.valueOf(currHomes + 1)),
                                Placeholder.parsed("max-homes", String.valueOf(homeSlots))
                        );
                    } else {
                        plugin.messenger().sendMessage(player,
                                NodePath.path("command", "sethome", "max-homes-reached"),
                                Placeholder.parsed("home-name", homeName),
                                Placeholder.parsed("curr-homes", String.valueOf(currHomes)),
                                Placeholder.parsed("max-homes", String.valueOf(homeSlots))
                        );
                    }
                });
            } else {
                plugin.messenger().sendMessage(player,
                        NodePath.path("command", "sethome", "tztz"),
                        Placeholder.parsed("home-name", homeName)
                );
            }
        }
    }

    private void handleHome(CommandContext<Player> commandSenderCommandContext) {
        Bukkit.getServer().getScheduler().runTask(plugin, () -> {
            var player = commandSenderCommandContext.sender();
            var homeName = (String) commandSenderCommandContext.get("homeName");

            var smpPlayerOpt = plugin.smpPlayerService().get(player);

            smpPlayerOpt.ifPresent(smpPlayer -> {

                if (smpPlayer.isCurrentlyInTeleport()) {
                    plugin.messenger().sendMessage(player,
                            NodePath.path("event", "teleport", "already-teleporting")
                    );
                    return;
                }

                if (!smpPlayer.hasHomeWithName(homeName)) {
                    plugin.messenger().sendMessage(player,
                            NodePath.path("command", "home", "notfound"),
                            Placeholder.parsed("home-name", homeName)
                    );
                    return;
                }

                var countDownSettings = plugin.countdownConfiguration().countdown().teleport();

                smpPlayer.getHome(homeName).ifPresent(home -> {
                    var countDownInSec = plugin.configuration().teleport().teleportCoolDownInSeconds();
                    var countDown = BukkitCountdown.builder(plugin)
                            .withRunOnFinish(() -> handleCountDownFinish(home, smpPlayer))
                            .withRunOnStep(duration -> handleCountDownStep(duration, countDownSettings, player))
                            .withRunOnAbort(() -> smpPlayer.currentlyInTeleport(false))
                            .withAbortTriggers(new MovementAbortTrigger(player, () -> {
                                plugin.messenger().sendMessage(player, NodePath.path("event", "teleport", "cancel"));
                            }))
                            .build();

                    // Player is allowed to bypass the teleport warmup time
                    if (plugin.configuration().teleport().permissionBypassesCoolDown()
                            && player.hasPermission("yasmpp.teleport.cooldown.bypass")) {
                        countDownInSec = 0;
                    }

                    smpPlayer.currentlyInTeleport(true);
                    countDown.start(countDownInSec);
                });
            });
        });
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

    private void handleCountDownFinish(Home home, SmpPlayer smpPlayer) {
        var player = smpPlayer.toBukkitPlayer();
        smpPlayer.currentlyInTeleport(false);
        plugin.getServer().getScheduler().runTask(plugin, () -> player.teleport(
                LocationAdapter.adapt(home.locationContainer(), player.getServer())
        ));

        player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);

        plugin.messenger().sendMessage(player,
                NodePath.path("command", "home", "success"),
                home.tagResolvers()
        );
    }

    private CompletableFuture<List<Suggestion>> getHomeSuggestions(Player player) {
        return CompletableFuture.completedFuture(
                plugin.smpPlayerService()
                        .get(player)
                        .get()
                        .getHomes()
                        .stream()
                        .map(home -> Suggestion.suggestion(home.name()))
                        .toList()
        );
    }
}
