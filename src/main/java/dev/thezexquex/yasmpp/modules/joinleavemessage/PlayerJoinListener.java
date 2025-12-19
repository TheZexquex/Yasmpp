package dev.thezexquex.yasmpp.modules.joinleavemessage;

import de.unknowncity.astralib.common.message.lang.Language;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.data.adapter.LocationAdapter;
import io.papermc.paper.event.player.AsyncPlayerSpawnLocationEvent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.spongepowered.configurate.NodePath;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerJoinListener implements Listener {
    private final YasmpPlugin plugin;
    private final Set<UUID> firstTimeJoiners = new HashSet<>();

    public PlayerJoinListener(YasmpPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            var offlinePlayer = Bukkit.getOfflinePlayer(event.getUniqueId());
            if (!offlinePlayer.hasPlayedBefore()) {
                plugin.getLogger().info("Player " + offlinePlayer.getName() + " has joined for the first time!");
                firstTimeJoiners.add(event.getUniqueId());
            }
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();

        plugin.smpPlayerService().addSmpPlayer(player);
        boolean isFirstTime = firstTimeJoiners.remove(player.getUniqueId());

        if (!isFirstTime) {
            firstTimeJoiners.remove(player.getUniqueId());
            var messageComponent = plugin.messenger().component(
                    Language.GERMAN,
                    NodePath.path("event", "join"),
                    Placeholder.component("player", player.name())
            );

            event.joinMessage(messageComponent);
            return;
        }

        var messageComponent = plugin.messenger().component(
                Language.GERMAN,
                NodePath.path("event", "first-join"),
                Placeholder.component("player", player.name())
        );

        event.joinMessage(messageComponent);

        var locationService = plugin.locationService();
        var spawnLocation = locationService.getLocation("spawn");

        spawnLocation.ifPresentOrElse(worldPosition -> {
            player.teleport(LocationAdapter.adapt(worldPosition.locationContainer(), player.getServer()));
        }, () -> plugin.messenger().sendMessage(player, NodePath.path("command", "spawn", "no-spawn")));
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        firstTimeJoiners.remove(event.getPlayer().getUniqueId());
    }
}
