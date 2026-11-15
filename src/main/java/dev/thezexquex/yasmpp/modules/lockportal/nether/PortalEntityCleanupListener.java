package dev.thezexquex.yasmpp.modules.lockportal.nether;

import dev.thezexquex.yasmpp.YasmpPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.EntitiesLoadEvent;

/**
 * Portal entities are persistent, so entities of a previous render (e.g. from before a restart, or from a
 * chunk that was unloaded while despawning) come back when their chunk loads. Those are removed here.
 */
public class PortalEntityCleanupListener implements Listener {
    private final YasmpPlugin plugin;

    public PortalEntityCleanupListener(YasmpPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntitiesLoad(EntitiesLoadEvent event) {
        var manager = plugin.netherPortalManager();
        var renderer = manager == null ? null : manager.portalRenderer();
        event.getEntities().forEach(entity -> {
            if (PortalRenderer.isPortalEntity(entity) && (renderer == null || !renderer.owns(entity))) {
                entity.remove();
            }
        });
    }
}
