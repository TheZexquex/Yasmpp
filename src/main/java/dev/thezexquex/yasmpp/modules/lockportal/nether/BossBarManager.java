package dev.thezexquex.yasmpp.modules.lockportal.nether;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public class BossBarManager {
    private BukkitTask task;
    private final Plugin plugin;
    private final BossBar bossBar = BossBar.bossBar(Component.empty(), 0f, BossBar.Color.PURPLE, BossBar.Overlay.PROGRESS);

    public BossBarManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void updateBossBar(long completed, long total) {
        bossBar.progress((float) completed / total);
        bossBar.color(BossBar.Color.PURPLE);
        bossBar.name(getBossBarTitle(completed, total));
    }

    public void startShowingBossBar() {
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            plugin.getServer().getOnlinePlayers().forEach(player -> {
                player.showBossBar(bossBar);
            });
        }, 0, 20);
    }

    public void stopShowingBossBar() {
        if (task != null) {
            task.cancel();
        }
        plugin.getServer().getOnlinePlayers().forEach(player -> {
            player.hideBossBar(bossBar);
        });
    }

    public @NotNull ComponentLike getBossBarTitle(long completed, long total) {
        return Component.text("Netherportal (" + completed + "/" + total + ")")
                .color(NamedTextColor.LIGHT_PURPLE);
    }
}
