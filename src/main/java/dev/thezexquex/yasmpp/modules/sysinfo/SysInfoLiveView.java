package dev.thezexquex.yasmpp.modules.sysinfo;

import dev.thezexquex.yasmpp.YasmpPlugin;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.spongepowered.configurate.NodePath;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Shows cpu, ram and tps in the action bar of the players that enabled it, refreshed every second.
 * The cpu load is measured between two refreshes, so no blocking sample is needed.
 */
public class SysInfoLiveView {
    private static final long UPDATE_INTERVAL_TICKS = 20;

    private final YasmpPlugin plugin;
    private final Set<UUID> viewers = ConcurrentHashMap.newKeySet();
    private BukkitTask task;
    private long[] previousCpuTicks;

    public SysInfoLiveView(YasmpPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * @return whether the live view is now enabled for the player
     */
    public synchronized boolean toggle(Player player) {
        if (viewers.remove(player.getUniqueId())) {
            if (viewers.isEmpty()) {
                stop();
            }
            return false;
        }
        viewers.add(player.getUniqueId());
        if (task == null) {
            start();
        }
        return true;
    }

    private void start() {
        previousCpuTicks = SysInfo.systemInfo().getHardware().getProcessor().getSystemCpuLoadTicks();
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::update, UPDATE_INTERVAL_TICKS, UPDATE_INTERVAL_TICKS);
    }

    private void stop() {
        task.cancel();
        task = null;
    }

    private synchronized void update() {
        // Players that left are dropped instead of tracking quit events
        viewers.removeIf(uuid -> Bukkit.getPlayer(uuid) == null);
        if (viewers.isEmpty()) {
            stop();
            return;
        }

        var hardware = SysInfo.systemInfo().getHardware();
        var processor = hardware.getProcessor();
        var cpuLoad = processor.getSystemCpuLoadBetweenTicks(previousCpuTicks);
        previousCpuTicks = processor.getSystemCpuLoadTicks();

        var memory = hardware.getMemory();
        var ramLoad = SysInfo.ratio(memory.getTotal() - memory.getAvailable(), memory.getTotal());

        var tps = Bukkit.getTPS()[0];
        var tickLoad = Bukkit.getAverageTickTime() / SysInfo.TICK_BUDGET_MILLIS;

        var barSettings = plugin.configuration().main().sysInfo().bar();
        var width = barSettings.liveWidth();
        var resolver = TagResolver.resolver(
                Placeholder.unparsed("cpu-percent", SysInfo.formatPercent(cpuLoad)),
                UsageBar.resolver("cpu-bar", barSettings, width, cpuLoad, cpuLoad),
                Placeholder.unparsed("ram-percent", SysInfo.formatPercent(ramLoad)),
                UsageBar.resolver("ram-bar", barSettings, width, ramLoad, ramLoad),
                // Colored by the tick time, like the tps bar of /sysinfo server
                Placeholder.parsed("tps", UsageBar.color(barSettings, tickLoad) + SysInfo.formatTps(tps))
        );

        for (var uuid : viewers) {
            var player = Bukkit.getPlayer(uuid);
            if (player != null) {
                plugin.messenger().sendActionBar(player, NodePath.path("command", "sysinfo", "live", "actionbar"), resolver);
            }
        }
    }
}
