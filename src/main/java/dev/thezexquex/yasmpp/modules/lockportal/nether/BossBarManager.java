package dev.thezexquex.yasmpp.modules.lockportal.nether;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public class BossBarManager {
    private BukkitTask task;
    private final Plugin plugin;
    private final NetherPortalManager netherPortalManager;
    private final BossBar bossBar = BossBar.bossBar(Component.empty(), 0f, BossBar.Color.PURPLE, BossBar.Overlay.PROGRESS);

    public BossBarManager(Plugin plugin, NetherPortalManager netherPortalManager) {
        this.plugin = plugin;
        this.netherPortalManager = netherPortalManager;
    }

    public void updateBossBar(long completed, long total) {
        bossBar.progress((float) completed / total);
        bossBar.color(BossBar.Color.PURPLE);
        bossBar.name(getBossBarTitle(completed, total));
    }

    public void startShowingBossBar() {
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (netherPortalManager.progress() == NetherPortalManager.PortalProgress.COMPLETED) {
                task.cancel();
                plugin.getServer().getOnlinePlayers().forEach(player -> {
                    player.hideBossBar(bossBar);
                });
                return;
            }
            plugin.getServer().getOnlinePlayers().forEach(player -> {
                player.showBossBar(bossBar);
            });
        }, 0, 20);
    }

    public @NotNull ComponentLike getBossBarTitle(long completed, long total) {
        return Component.text("Netherportal (" + completed + "/" + total + ")")
                .color(NamedTextColor.LIGHT_PURPLE);
    }

}
