package dev.thezexquex.yasmpp.modules.playtime;

import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.data.plan.LeaderboardEntry;
import dev.thezexquex.yasmpp.data.plan.PlanUserRef;
import dev.thezexquex.yasmpp.data.plan.PlaytimeService;
import dev.thezexquex.yasmpp.data.plan.TimeWindow;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Playtime placeholders of online players on this server:
 * {@code %yasmpp_ontime_session%}, {@code %yasmpp_ontime_<day|week|month|year|total>%} and {@code %yasmpp_ontime_top_rank%}.
 * Values are empty while the player's data is still loading.
 */
public class PlaytimeExpansion extends PlaceholderExpansion {
    private static final String PREFIX = "ontime_";

    private final YasmpPlugin plugin;
    private final PlaytimeService playtimeService;
    private final PlaytimeSnapshots snapshots;

    public PlaytimeExpansion(YasmpPlugin plugin, PlaytimeService playtimeService, PlaytimeSnapshots snapshots) {
        this.plugin = plugin;
        this.playtimeService = playtimeService;
        this.snapshots = snapshots;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "yasmpp";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getPluginMeta().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null || !player.isOnline() || !params.startsWith(PREFIX)) {
            return null;
        }
        var key = params.substring(PREFIX.length()).toLowerCase(Locale.ROOT);

        if (key.equals("top_rank")) {
            return topRank(player);
        }

        var stored = snapshots.get(player.getUniqueId());
        if (stored.isEmpty()) {
            return "";
        }
        var stats = playtimeService.combine(new PlanUserRef(player.getUniqueId(), player.getName()), stored.get());

        if (key.equals("session")) {
            return PlaytimeFormat.duration(stats.session(false));
        }
        for (var window : TimeWindow.values()) {
            if (window.key().equals(key)) {
                return PlaytimeFormat.duration(stats.playtime(window, false));
            }
        }
        return null;
    }

    private String topRank(OfflinePlayer player) {
        var leaderboard = playtimeService.cachedLeaderboard();
        if (leaderboard.isEmpty()) {
            return "";
        }
        var sorted = leaderboard.get().stream().sorted(LeaderboardEntry.mostPlaytimeFirst(false)).toList();
        for (var i = 0; i < sorted.size(); i++) {
            if (sorted.get(i).user().uniqueId().equals(player.getUniqueId())) {
                return String.valueOf(i + 1);
            }
        }
        return "";
    }
}
