package dev.thezexquex.yasmpp.modules.sysinfo;

import de.unknowncity.astralib.paper.api.message.PaperMessenger;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.spongepowered.configurate.NodePath;
import oshi.ffm.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.spi.SystemInfoProvider;
import oshi.util.FormatUtil;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Disk info is read from the mounted filesystems (OS view) instead of {@link oshi.hardware.HWDiskStore}.
 * HWDiskStore only knows block devices and their raw size (no usage) and inside containers it even
 * lists the disks of the host. The OS filesystems report what can actually be written to.
 */
public class SysInfo {
    private static final long CPU_SAMPLE_MILLIS = 1000;
    private static SystemInfoProvider systemInfoProvider;

    /**
     * Gathers the system info asynchronously, because sampling the cpu load blocks for {@link #CPU_SAMPLE_MILLIS}.
     */
    public static CompletableFuture<Void> sendSystemInfo(Audience audience, PaperMessenger messenger) {
        return CompletableFuture.runAsync(() -> {
            var systemInfo = systemInfo();
            var hardware = systemInfo.getHardware();
            var processor = hardware.getProcessor();
            var os = systemInfo.getOperatingSystem();

            messenger.sendMessage(audience, NodePath.path("command", "sysinfo", "header"));
            messenger.sendMessage(audience, NodePath.path("command", "sysinfo", "os"), osResolver(os));
            messenger.sendMessage(audience, NodePath.path("command", "sysinfo", "cpu"), cpuResolver(processor));
            messenger.sendMessage(audience, NodePath.path("command", "sysinfo", "memory"), memoryResolver(hardware.getMemory()));
            sendDiskInfo(audience, messenger, os);
            messenger.sendMessage(audience, NodePath.path("command", "sysinfo", "footer"));
        });
    }

    private static synchronized SystemInfoProvider systemInfo() {
        if (systemInfoProvider == null) {
            systemInfoProvider = new SystemInfo();
        }
        return systemInfoProvider;
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

    private static TagResolver cpuResolver(CentralProcessor processor) {
        var identifier = processor.getProcessorIdentifier();
        var loadAverage = processor.getSystemLoadAverage(3);
        return TagResolver.resolver(
                Placeholder.unparsed("cpu-name", identifier.getName().trim()),
                Placeholder.unparsed("cpu-vendor", identifier.getVendor()),
                Placeholder.unparsed("cpu-physical-cores", String.valueOf(processor.getPhysicalProcessorCount())),
                Placeholder.unparsed("cpu-logical-cores", String.valueOf(processor.getLogicalProcessorCount())),
                Placeholder.unparsed("cpu-max-freq", formatFrequency(processor.getMaxFreq())),
                Placeholder.unparsed("cpu-load", formatPercent(processor.getSystemCpuLoad(CPU_SAMPLE_MILLIS))),
                Placeholder.unparsed("cpu-load-average", formatLoadAverage(loadAverage))
        );
    }

    private static TagResolver memoryResolver(GlobalMemory memory) {
        var total = memory.getTotal();
        var used = total - memory.getAvailable();
        var runtime = Runtime.getRuntime();
        var heapUsed = runtime.totalMemory() - runtime.freeMemory();
        var heapMax = runtime.maxMemory();
        return TagResolver.resolver(
                Placeholder.unparsed("ram-used", FormatUtil.formatBytes(used)),
                Placeholder.unparsed("ram-total", FormatUtil.formatBytes(total)),
                Placeholder.unparsed("ram-percent", formatPercent(ratio(used, total))),
                Placeholder.unparsed("heap-used", FormatUtil.formatBytes(heapUsed)),
                Placeholder.unparsed("heap-max", FormatUtil.formatBytes(heapMax)),
                Placeholder.unparsed("heap-percent", formatPercent(ratio(heapUsed, heapMax)))
        );
    }

    private static void sendDiskInfo(Audience audience, PaperMessenger messenger, OperatingSystem os) {
        messenger.sendMessage(audience, NodePath.path("command", "sysinfo", "disk-header"));

        // The filesystem the server actually writes to. Queried via NIO because OSHI hides overlay
        // filesystems, which is the root filesystem in docker containers.
        try {
            FileStore serverStore = Files.getFileStore(Bukkit.getWorldContainer().toPath());
            messenger.sendMessage(audience, NodePath.path("command", "sysinfo", "disk-server"), diskResolver(
                    serverStore.name(),
                    serverStore.type(),
                    serverStore.getTotalSpace(),
                    serverStore.getUsableSpace()
            ));
        } catch (IOException ignored) {
            // Fall back to the OSHI listing below
        }

        // Bind mounts show up multiple times with the same volume, only show each volume once
        Set<String> seenVolumes = new HashSet<>();
        for (OSFileStore fileStore : os.getFileSystem().getFileStores(true)) {
            if (fileStore.getTotalSpace() <= 0 || !seenVolumes.add(fileStore.getVolume())) {
                continue;
            }
            messenger.sendMessage(audience, NodePath.path("command", "sysinfo", "disk-entry"), diskResolver(
                    fileStore.getMount(),
                    fileStore.getType(),
                    fileStore.getTotalSpace(),
                    fileStore.getUsableSpace()
            ));
        }
    }

    /**
     * Uses the usable space instead of the free space, because the free space includes blocks
     * reserved for root (5% on ext4 by default) which the server process can not write to.
     */
    private static TagResolver diskResolver(String name, String type, long total, long usable) {
        var used = total - usable;
        return TagResolver.resolver(
                Placeholder.unparsed("disk-name", name),
                Placeholder.unparsed("disk-type", type),
                Placeholder.unparsed("disk-used", FormatUtil.formatBytes(used)),
                Placeholder.unparsed("disk-free", FormatUtil.formatBytes(usable)),
                Placeholder.unparsed("disk-total", FormatUtil.formatBytes(total)),
                Placeholder.unparsed("disk-percent", formatPercent(ratio(used, total)))
        );
    }

    private static double ratio(long part, long total) {
        return total <= 0 ? 0 : (double) part / total;
    }

    private static String formatPercent(double ratio) {
        return String.format(Locale.GERMAN, "%.1f%%", ratio * 100);
    }

    private static String formatFrequency(long hertz) {
        return hertz <= 0 ? "N/A" : FormatUtil.formatHertz(hertz);
    }

    private static String formatLoadAverage(double[] loadAverage) {
        // Negative values mean the load average is not available (e.g. on windows)
        if (loadAverage[0] < 0) {
            return "N/A";
        }
        return String.format(Locale.GERMAN, "%.2f / %.2f / %.2f", loadAverage[0], loadAverage[1], loadAverage[2]);
    }
}
