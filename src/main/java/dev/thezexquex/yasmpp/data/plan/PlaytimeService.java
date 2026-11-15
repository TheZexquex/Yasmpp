package dev.thezexquex.yasmpp.data.plan;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Entry point for playtime data. Combines the sessions stored by Plan with the running sessions
 * held in Plan's memory, so values are accurate to the second, also directly after a join.
 * <p>
 * All returned futures complete on the main thread.
 */
public class PlaytimeService {
    private static final Duration TOTALS_TTL = Duration.ofSeconds(30);

    private final PlanSessionRepository repository;
    private final LiveSessions liveSessions;
    private final Server server;
    private final Executor mainThread;
    private final ZoneId zone;
    private final Logger logger;
    private final Set<String> knownNames = ConcurrentHashMap.newKeySet();

    private CompletableFuture<List<StoredTotal>> totals;
    private Instant totalsLoadedAt = Instant.MIN;

    public PlaytimeService(PlanSessionRepository repository, LiveSessions liveSessions, Server server, Executor mainThread, ZoneId zone, Logger logger) {
        this.repository = repository;
        this.liveSessions = liveSessions;
        this.server = server;
        this.mainThread = mainThread;
        this.zone = zone;
        this.logger = logger;
    }

    /**
     * Resolves a player by name, preferring players that are currently online.
     */
    public CompletableFuture<Optional<PlanUserRef>> resolve(String name) {
        var player = server.getPlayerExact(name);
        if (player != null) {
            return CompletableFuture.completedFuture(Optional.of(new PlanUserRef(player.getUniqueId(), player.getName())));
        }
        return repository.findUser(name).thenApplyAsync(user -> user, mainThread);
    }

    public CompletableFuture<PlaytimeStats> stats(PlanUserRef user) {
        return storedPlaytime(user.uniqueId()).thenApplyAsync(stored -> combine(user, stored), mainThread);
    }

    public CompletableFuture<StoredPlaytime> storedPlaytime(UUID uniqueId) {
        return repository.storedPlaytime(uniqueId, Instant.now());
    }

    /**
     * Adds the running session of the player to previously loaded stored playtime. Must be called on the main thread.
     */
    public PlaytimeStats combine(PlanUserRef user, StoredPlaytime stored) {
        var now = Instant.now();
        var online = server.getPlayer(user.uniqueId()) != null;
        var live = online ? liveSessions.session(user.uniqueId(), now) : Optional.<LiveSessions.LiveSession>empty();
        return PlaytimeStats.of(user, stored, live, online, now, zone);
    }

    /**
     * Total playtime of every player on this server, unsorted. Stored totals are cached for a short time,
     * running sessions are added fresh on every call.
     */
    public CompletableFuture<List<LeaderboardEntry>> leaderboard() {
        return cachedTotals().thenApplyAsync(this::withLiveSessions, mainThread);
    }

    /**
     * Leaderboard from the cache without waiting for the database, for synchronous callers like placeholders.
     * Triggers a reload in the background if the cache is stale. Must be called on the main thread.
     */
    public Optional<List<LeaderboardEntry>> cachedLeaderboard() {
        var current = cachedTotals();
        if (!current.isDone() || current.isCompletedExceptionally()) {
            return Optional.empty();
        }
        return Optional.of(withLiveSessions(current.join()));
    }

    /**
     * Forces the next leaderboard request to reload the stored totals, e.g. after a session was persisted.
     */
    public synchronized void invalidateTotals() {
        totalsLoadedAt = Instant.MIN;
    }

    public Collection<String> knownNames() {
        var names = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        names.addAll(knownNames);
        server.getOnlinePlayers().forEach(player -> names.add(player.getName()));
        return names;
    }

    public void rememberName(String name) {
        knownNames.add(name);
    }

    private synchronized CompletableFuture<List<StoredTotal>> cachedTotals() {
        var stale = totalsLoadedAt.plus(TOTALS_TTL).isBefore(Instant.now());
        var failed = totals != null && totals.isCompletedExceptionally();
        if (totals == null || failed || (stale && totals.isDone())) {
            totalsLoadedAt = Instant.now();
            totals = repository.storedTotals().whenComplete((loaded, throwable) -> {
                if (throwable != null) {
                    logger.log(Level.SEVERE, "Failed to load playtime totals from Plan", throwable);
                    return;
                }
                loaded.forEach(total -> knownNames.add(total.user().name()));
            });
        }
        return totals;
    }

    private List<LeaderboardEntry> withLiveSessions(List<StoredTotal> totals) {
        var now = Instant.now();
        var entries = new LinkedHashMap<UUID, LeaderboardEntry>();
        for (var total : totals) {
            entries.put(total.user().uniqueId(), new LeaderboardEntry(total.user(), total.playtime(), total.afk(), total.lastSeen(), false));
        }

        for (Player player : server.getOnlinePlayers()) {
            var live = liveSessions.session(player.getUniqueId(), now);
            var stored = entries.get(player.getUniqueId());
            var user = stored != null ? stored.user() : new PlanUserRef(player.getUniqueId(), player.getName());
            var playtime = stored != null ? stored.playtime() : Duration.ZERO;
            var afk = stored != null ? stored.afk() : Duration.ZERO;
            var lastSeen = stored != null ? stored.lastSeen() : null;
            entries.put(player.getUniqueId(), new LeaderboardEntry(
                    user,
                    playtime.plus(live.map(LiveSessions.LiveSession::length).orElse(Duration.ZERO)),
                    afk.plus(live.map(LiveSessions.LiveSession::afk).orElse(Duration.ZERO)),
                    lastSeen,
                    true
            ));
        }
        return List.copyOf(entries.values());
    }
}
