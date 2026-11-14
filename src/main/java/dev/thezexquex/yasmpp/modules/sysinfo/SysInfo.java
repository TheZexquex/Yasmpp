package dev.thezexquex.yasmpp.modules.sysinfo;

import de.unknowncity.astralib.common.message.lang.Language;
import de.unknowncity.astralib.paper.api.message.PaperMessenger;
import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.configuration.settings.sysinfo.SysInfoBarSettings;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.NodePath;
import oshi.ffm.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.software.os.OSFileStore;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import oshi.spi.SystemInfoProvider;
import oshi.util.FormatUtil;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Disk info is read from the mounted filesystems (OS view) instead of {@link oshi.hardware.HWDiskStore}.
 * HWDiskStore only knows block devices and their raw size (no usage) and inside containers it even
 * lists the disks of the host. The OS filesystems report what can actually be written to.
 */
public class SysInfo {
    private static final long CPU_SAMPLE_MILLIS = 1000;
    private static final long WORLD_STATS_TIMEOUT_SECONDS = 5;
    private static final int CORES_PER_ROW = 2;
    static final double TARGET_TPS = 20;
    static final double TICK_BUDGET_MILLIS = 50;
    private static SystemInfoProvider systemInfoProvider;

    private final YasmpPlugin plugin;

    public SysInfo(YasmpPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Gathers the system info asynchronously, because sampling the cpu load blocks for {@link #CPU_SAMPLE_MILLIS}.
     *
     * @param bars     show usage values as colored bars
     * @param detailed show additional values (per core load, swap, jvm memory, worlds, process)
     */
    public CompletableFuture<Void> sendSystemInfo(Audience audience, Set<SysInfoSection> sections, boolean bars, boolean detailed) {
        var showWorlds = sections.contains(SysInfoSection.SERVER) && detailed;
        // Chunk and entity counts may only be read on the main thread
        var worldStats = showWorlds
                ? CompletableFuture.supplyAsync(SysInfo::worldStats, task -> Bukkit.getScheduler().runTask(plugin, task))
                : CompletableFuture.completedFuture(List.<WorldStats>of());

        return CompletableFuture.runAsync(() -> {
            var systemInfo = systemInfo();
            var hardware = systemInfo.getHardware();
            var processor = hardware.getProcessor();
            var os = systemInfo.getOperatingSystem();
            var view = new View(plugin.messenger(), audience, bars, detailed, plugin.configuration().main().sysInfo().bar());

            var needsCpuSample = sections.contains(SysInfoSection.CPU) || showWorlds;
            var cpuSample = needsCpuSample ? CpuSample.take(processor, os) : null;

            view.send(path("header"));
            for (var section : sections) {
                switch (section) {
                    case OS -> view.send(path("os"), osResolver(os));
                    case CPU -> sendCpuInfo(view, processor, cpuSample);
                    case MEMORY -> sendMemoryInfo(view, hardware.getMemory());
                    case DISK -> sendDiskInfo(view, os);
                    case SERVER -> sendServerInfo(view, os, cpuSample,
                            worldStats.orTimeout(WORLD_STATS_TIMEOUT_SECONDS, TimeUnit.SECONDS).join());
                }
            }
            view.send(path("footer"));
        });
    }

    static synchronized SystemInfoProvider systemInfo() {
        if (systemInfoProvider == null) {
            systemInfoProvider = new SystemInfo();
        }
        return systemInfoProvider;
    }

    private static NodePath path(Object... keys) {
        var fullPath = new Object[keys.length + 2];
        fullPath[0] = "command";
        fullPath[1] = "sysinfo";
        System.arraycopy(keys, 0, fullPath, 2, keys.length);
        return NodePath.path(fullPath);
    }

    private static TagResolver osResolver(OperatingSystem os) {
        var versionInfo = os.getVersionInfo();
        return TagResolver.resolver(
                Placeholder.unparsed("os-family", os.getFamily()),
                Placeholder.unparsed("os-manufacturer", os.getManufacturer()),
                Placeholder.unparsed("os-version", versionInfo.getVersion()),
                Placeholder.unparsed("os-codename", versionInfo.getCodeName()),
                Placeholder.unparsed("os-build", versionInfo.getBuildNumber()),
                Placeholder.unparsed("os-bitness", String.valueOf(os.getBitness())),
                Placeholder.unparsed("os-uptime", FormatUtil.formatElapsedSecs(os.getSystemUptime()))
        );
    }

    private static void sendCpuInfo(View view, CentralProcessor processor, CpuSample cpuSample) {
        var identifier = processor.getProcessorIdentifier();
        view.send(path("cpu", "info"), TagResolver.resolver(
                Placeholder.unparsed("cpu-name", identifier.getName().trim()),
                Placeholder.unparsed("cpu-vendor", identifier.getVendor()),
                Placeholder.unparsed("cpu-physical-cores", String.valueOf(processor.getPhysicalProcessorCount())),
                Placeholder.unparsed("cpu-logical-cores", String.valueOf(processor.getLogicalProcessorCount())),
                Placeholder.unparsed("cpu-max-freq", formatFrequency(processor.getMaxFreq()))
        ));
        view.sendRatio("cpu", "load", cpuSample.systemLoad());
        view.send(path("cpu", "load-average"),
                Placeholder.unparsed("cpu-load-average", formatLoadAverage(processor.getSystemLoadAverage(3))));

        if (!view.detailed()) {
            return;
        }
        view.send(path("cpu", "current-freq"),
                Placeholder.unparsed("cpu-current-freq", formatFrequency(averageFrequency(processor.getCurrentFreq()))));
        view.send(path("cpu", "core-header"));
        sendCoreLoads(view, cpuSample.coreLoads());
    }

    private static void sendCoreLoads(View view, double[] coreLoads) {
        var separator = view.component(path("cpu", "core-separator"));
        for (int rowStart = 0; rowStart < coreLoads.length; rowStart += CORES_PER_ROW) {
            var row = new ArrayList<Component>();
            for (int core = rowStart; core < Math.min(rowStart + CORES_PER_ROW, coreLoads.length); core++) {
                var load = coreLoads[core];
                row.add(view.component(path("cpu", "core"),
                        Placeholder.unparsed("core-index", String.valueOf(core)),
                        Placeholder.unparsed("percent", formatPercent(load)),
                        UsageBar.resolver("bar", view.barSettings(), view.barSettings().coreWidth(), load, load)
                ));
            }
            view.audience().sendMessage(Component.join(JoinConfiguration.separator(separator), row));
        }
    }

    private static void sendMemoryInfo(View view, GlobalMemory memory) {
        var runtime = Runtime.getRuntime();
        var heapUsed = runtime.totalMemory() - runtime.freeMemory();
        var heapMax = runtime.maxMemory();
        var ramTotal = memory.getTotal();
        var ramUsed = ramTotal - memory.getAvailable();

        view.send(path("memory", "header"));
        view.sendUsage("memory", "ram", ramUsed, ramTotal);
        view.sendUsage("memory", "heap", heapUsed, heapMax);

        if (!view.detailed()) {
            return;
        }
        var virtualMemory = memory.getVirtualMemory();
        view.sendUsage("memory", "swap", virtualMemory.getSwapUsed(), virtualMemory.getSwapTotal());

        var memoryBean = ManagementFactory.getMemoryMXBean();
        var metaspaceUsed = ManagementFactory.getMemoryPoolMXBeans().stream()
                .filter(pool -> pool.getName().equals("Metaspace"))
                .findFirst()
                .map(MemoryPoolMXBean::getUsage)
                .map(usage -> FormatUtil.formatBytes(usage.getUsed()))
                .orElse("N/A");
        view.send(path("memory", "jvm"), TagResolver.resolver(
                Placeholder.unparsed("heap-committed", FormatUtil.formatBytes(memoryBean.getHeapMemoryUsage().getCommitted())),
                Placeholder.unparsed("non-heap-used", FormatUtil.formatBytes(memoryBean.getNonHeapMemoryUsage().getUsed())),
                Placeholder.unparsed("metaspace-used", metaspaceUsed)
        ));

        for (var collector : ManagementFactory.getGarbageCollectorMXBeans()) {
            view.send(path("memory", "gc"), TagResolver.resolver(
                    Placeholder.unparsed("gc-name", collector.getName()),
                    Placeholder.unparsed("gc-count", formatNumber(Math.max(0, collector.getCollectionCount()))),
                    Placeholder.unparsed("gc-time", formatMillis(Math.max(0, collector.getCollectionTime())))
            ));
        }
    }

    private static void sendDiskInfo(View view, OperatingSystem os) {
        view.send(path("disk", "header"));

        // The filesystem the server actually writes to. Queried via NIO because OSHI hides overlay
        // filesystems, which is the root filesystem in docker containers.
        try {
            FileStore serverStore = Files.getFileStore(Bukkit.getWorldContainer().toPath());
            sendDisk(view, "server", serverStore.name(), serverStore.type(), serverStore.getTotalSpace(), serverStore.getUsableSpace());
        } catch (IOException ignored) {
            // Fall back to the OSHI listing below
        }

        // Bind mounts show up multiple times with the same volume, only show each volume once
        Set<String> seenVolumes = new HashSet<>();
        for (OSFileStore fileStore : os.getFileSystem().getFileStores(true)) {
            if (fileStore.getTotalSpace() <= 0 || !seenVolumes.add(fileStore.getVolume())) {
                continue;
            }
            sendDisk(view, "entry", fileStore.getMount(), fileStore.getType(), fileStore.getTotalSpace(), fileStore.getUsableSpace());
        }
    }

    /**
     * Uses the usable space instead of the free space, because the free space includes blocks
     * reserved for root (5% on ext4 by default) which the server process can not write to.
     */
    private static void sendDisk(View view, String key, String name, String type, long total, long usable) {
        view.sendUsage("disk", key, total - usable, total,
                Placeholder.unparsed("disk-name", name),
                Placeholder.unparsed("disk-type", type)
        );
    }

    private static void sendServerInfo(View view, OperatingSystem os, CpuSample cpuSample, List<WorldStats> worldStats) {
        var tps = Bukkit.getTPS();
        var mspt = Bukkit.getAverageTickTime();
        var tickLoad = mspt / TICK_BUDGET_MILLIS;

        view.send(path("server", "header"));
        // A full TPS bar is good, so it is colored by the tick time instead of its fill
        view.sendBar("server", "tps", tps[0] / TARGET_TPS, tickLoad, TagResolver.resolver(
                Placeholder.unparsed("tps-1m", formatTps(tps[0])),
                Placeholder.unparsed("tps-5m", formatTps(tps[1])),
                Placeholder.unparsed("tps-15m", formatTps(tps[2]))
        ));
        view.sendBar("server", "mspt", tickLoad, tickLoad, TagResolver.resolver(
                Placeholder.unparsed("mspt", String.format(Locale.GERMAN, "%.1f", mspt)),
                Placeholder.unparsed("percent", formatPercent(tickLoad))
        ));
        view.send(path("server", "players"), TagResolver.resolver(
                Placeholder.unparsed("players-online", String.valueOf(Bukkit.getOnlinePlayers().size())),
                Placeholder.unparsed("players-max", String.valueOf(Bukkit.getMaxPlayers()))
        ));

        if (!view.detailed()) {
            return;
        }
        view.send(path("server", "world-header"));
        for (var world : worldStats) {
            view.send(path("server", "world"), TagResolver.resolver(
                    Placeholder.unparsed("world-name", world.name()),
                    Placeholder.unparsed("world-chunks", formatNumber(world.chunks())),
                    Placeholder.unparsed("world-entities", formatNumber(world.entities()))
            ));
        }

        var process = cpuSample.process();
        var runtimeBean = ManagementFactory.getRuntimeMXBean();
        view.send(path("server", "process"), TagResolver.resolver(
                Placeholder.unparsed("process-pid", String.valueOf(os.getProcessId())),
                Placeholder.unparsed("process-threads", process == null ? "N/A" : String.valueOf(process.getThreadCount())),
                Placeholder.unparsed("process-memory", process == null ? "N/A" : FormatUtil.formatBytes(process.getResidentMemory())),
                Placeholder.unparsed("process-cpu", cpuSample.processLoad() < 0 ? "N/A" : formatPercent(cpuSample.processLoad())),
                Placeholder.unparsed("java-version", System.getProperty("java.version")),
                Placeholder.unparsed("java-vendor", System.getProperty("java.vendor")),
                Placeholder.unparsed("jvm-uptime", FormatUtil.formatElapsedSecs(runtimeBean.getUptime() / 1000))
        ));
    }

    private static List<WorldStats> worldStats() {
        return Bukkit.getWorlds().stream()
                .map(world -> new WorldStats(world.getName(), world.getChunkCount(), world.getEntityCount()))
                .toList();
    }

    static double ratio(long part, long total) {
        return total <= 0 ? 0 : (double) part / total;
    }

    static String formatPercent(double ratio) {
        return String.format(Locale.GERMAN, "%.1f%%", ratio * 100);
    }

    static String formatTps(double tps) {
        // Paper reports slightly more than 20 TPS right after catching up
        return String.format(Locale.GERMAN, "%.2f", Math.min(tps, TARGET_TPS));
    }

    private static String formatNumber(long number) {
        return String.format(Locale.GERMAN, "%,d", number);
    }

    private static String formatMillis(long millis) {
        return millis < 1000 ? millis + " ms" : String.format(Locale.GERMAN, "%.1f s", millis / 1000.0);
    }

    private static String formatFrequency(long hertz) {
        return hertz <= 0 ? "N/A" : FormatUtil.formatHertz(hertz);
    }

    private static long averageFrequency(long[] frequencies) {
        // Cores without a reported frequency are -1
        return (long) Arrays.stream(frequencies).filter(frequency -> frequency > 0).average().orElse(-1);
    }

    private static String formatLoadAverage(double[] loadAverage) {
        // Negative values mean the load average is not available (e.g. on windows)
        if (loadAverage[0] < 0) {
            return "N/A";
        }
        return String.format(Locale.GERMAN, "%.2f / %.2f / %.2f", loadAverage[0], loadAverage[1], loadAverage[2]);
    }

    private record WorldStats(String name, int chunks, int entities) {
    }

    /**
     * System, per core and process cpu load sampled over the same {@link #CPU_SAMPLE_MILLIS} window.
     *
     * @param processLoad load of the server process relative to all logical cores, negative if unavailable
     */
    private record CpuSample(double systemLoad, double[] coreLoads, OSProcess process, double processLoad) {

        static CpuSample take(CentralProcessor processor, OperatingSystem os) {
            var systemTicks = processor.getSystemCpuLoadTicks();
            var coreTicks = processor.getProcessorCpuLoadTicks();
            var processBefore = os.getCurrentProcess();
            try {
                Thread.sleep(CPU_SAMPLE_MILLIS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            var processAfter = os.getCurrentProcess();
            var processLoad = processBefore == null || processAfter == null
                    ? -1
                    : processAfter.getProcessCpuLoadBetweenTicks(processBefore) / processor.getLogicalProcessorCount();
            return new CpuSample(
                    processor.getSystemCpuLoadBetweenTicks(systemTicks),
                    processor.getProcessorCpuLoadBetweenTicks(coreTicks),
                    processAfter,
                    processLoad
            );
        }
    }

    private record View(PaperMessenger messenger, Audience audience, boolean bars, boolean detailed, SysInfoBarSettings barSettings) {

        void send(NodePath path, TagResolver... resolvers) {
            messenger.sendMessage(audience, path, resolvers);
        }

        Component component(NodePath path, TagResolver... resolvers) {
            return audience instanceof Player player
                    ? messenger.component(player, path, resolvers)
                    : messenger.component(Language.GERMAN, path, resolvers);
        }

        /**
         * Sends {@code <section>.<key>} or, with bars enabled, {@code <section>.<key>-bar} with the usual
         * {@code <used>}, {@code <free>}, {@code <total>}, {@code <percent>} and {@code <bar>} placeholders.
         */
        void sendUsage(String section, String key, long used, long total, TagResolver... extra) {
            var usage = ratio(used, total);
            sendBar(section, key, usage, usage, TagResolver.resolver(
                    Placeholder.unparsed("used", FormatUtil.formatBytes(used)),
                    Placeholder.unparsed("free", FormatUtil.formatBytes(total - used)),
                    Placeholder.unparsed("total", FormatUtil.formatBytes(total)),
                    Placeholder.unparsed("percent", formatPercent(usage)),
                    TagResolver.resolver(extra)
            ));
        }

        void sendRatio(String section, String key, double ratio) {
            sendBar(section, key, ratio, ratio, Placeholder.unparsed("percent", formatPercent(ratio)));
        }

        void sendBar(String section, String key, double fill, double load, TagResolver resolver) {
            if (!bars) {
                send(path(section, key), resolver);
                return;
            }
            send(path(section, key + "-bar"), resolver,
                    UsageBar.resolver("bar", barSettings, barSettings.width(), fill, load));
        }
    }
}
