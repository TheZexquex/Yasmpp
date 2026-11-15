package dev.thezexquex.yasmpp.modules.lockportal.nether;

import de.unknowncity.astralib.common.message.lang.Language;
import dev.thezexquex.yasmpp.YasmpPlugin;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.NodePath;

public class BossBarManager implements PortalProgressListener {
    private BukkitTask task;
    private final YasmpPlugin plugin;
    private final BossBar bossBar = BossBar.bossBar(Component.empty(), 0f, BossBar.Color.PURPLE, BossBar.Overlay.PROGRESS);

    public BossBarManager(YasmpPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onProgress(long completed, long total) {
        bossBar.progress(total == 0 ? 0f : Math.clamp((float) completed / total, 0f, 1f));
        bossBar.name(plugin.messenger().component(
                Language.GERMAN,
                NodePath.path("event", "nether", "bossbar"),
                Placeholder.unparsed("completed", String.valueOf(completed)),
                Placeholder.unparsed("total", String.valueOf(total)))
        );
        show();
    }

    @Override
    public void onCompleted(Location origin, PortalBlueprint blueprint) {
        hide();
    }

    @Override
    public void onHidden() {
        hide();
    }

    private void show() {
        if (task != null && !task.isCancelled()) {
            return;
        }
        // Repeated so that players joining later see the bar as well
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, () ->
                plugin.getServer().getOnlinePlayers().forEach(player -> player.showBossBar(bossBar)), 0, 20);
    }

    private void hide() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        plugin.getServer().getOnlinePlayers().forEach(player -> player.hideBossBar(bossBar));
    }
}