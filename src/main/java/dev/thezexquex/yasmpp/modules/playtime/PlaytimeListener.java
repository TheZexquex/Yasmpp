package dev.thezexquex.yasmpp.modules.playtime;

import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.data.plan.PlaytimeService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlaytimeListener implements Listener {
    // Plan persists the finished session asynchronously after the quit event
    private static final long SESSION_PERSIST_DELAY_TICKS = 40;

    private final YasmpPlugin plugin;
    private final PlaytimeService playtimeService;

    public PlaytimeListener(YasmpPlugin plugin, PlaytimeService playtimeService) {
        this.plugin = plugin;
        this.playtimeService = playtimeService;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        playtimeService.rememberName(event.getPlayer().getName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        plugin.getServer().getScheduler().runTaskLater(plugin, playtimeService::invalidateTotals, SESSION_PERSIST_DELAY_TICKS);
    }
}
