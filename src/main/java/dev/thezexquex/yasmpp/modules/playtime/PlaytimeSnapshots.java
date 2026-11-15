package dev.thezexquex.yasmpp.modules.playtime;

import dev.thezexquex.yasmpp.data.plan.PlaytimeService;
import dev.thezexquex.yasmpp.data.plan.StoredPlaytime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stored playtime of online players for synchronous callers like placeholders. Plan does not write
 * sessions of this server while the player is online, so a snapshot only goes stale because the rolling
 * window boundaries move. It is therefore reloaded in the background on a new day and every few minutes.
 */
public class PlaytimeSnapshots implements Listener {
    private static final Duration MAX_AGE = Duration.ofMinutes(5);

    private final PlaytimeService playtimeService;
    private final ZoneId zone;
    private final Logger logger;
    private final Map<UUID, StoredPlaytime> snapshots = new ConcurrentHashMap<>();
    private final Set<UUID> loading = ConcurrentHashMap.newKeySet();

    public PlaytimeSnapshots(PlaytimeService playtimeService, ZoneId zone, Logger logger) {
        this.playtimeService = playtimeService;
        this.zone = zone;
        this.logger = logger;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        load(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        remove(event.getPlayer().getUniqueId());
    }

    public Optional<StoredPlaytime> get(UUID uniqueId) {
        var snapshot = snapshots.get(uniqueId);
        if (snapshot == null || isStale(snapshot)) {
            load(uniqueId);
        }
        return Optional.ofNullable(snapshot);
    }

    public void load(UUID uniqueId) {
        if (!loading.add(uniqueId)) {
            return;
        }
        playtimeService.storedPlaytime(uniqueId).whenComplete((stored, throwable) -> {
            if (!loading.remove(uniqueId)) {
                // Player quit while loading
                return;
            }
            if (throwable != null) {
                logger.log(Level.WARNING, "Failed to load playtime snapshot of " + uniqueId, throwable);
                return;
            }
            snapshots.put(uniqueId, stored);
        });
    }

    public void remove(UUID uniqueId) {
        loading.remove(uniqueId);
        snapshots.remove(uniqueId);
    }

    private boolean isStale(StoredPlaytime snapshot) {
        var now = Instant.now();
        var otherDay = !snapshot.computedAt().atZone(zone).toLocalDate().equals(now.atZone(zone).toLocalDate());
        return otherDay || snapshot.computedAt().plus(MAX_AGE).isBefore(now);
    }
}
