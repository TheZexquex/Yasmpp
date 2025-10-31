package dev.thezexquex.yasmpp.modules.blockdamage;

import dev.thezexquex.yasmpp.YasmpPlugin;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class ExplosionBlockDamageListener implements Listener {
    private final YasmpPlugin plugin;

    public ExplosionBlockDamageListener(YasmpPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        var doCreeperDamage = plugin.configuration().general().explosionDamage().doCreeperDamage();
        var doTntDamage = plugin.configuration().general().explosionDamage().doTntDamage();
        if (!doCreeperDamage && event.getEntity() instanceof Creeper) {
            event.blockList().clear();
            return;
        }

        if (!doTntDamage && event.getEntity() instanceof TNTPrimed) {
            event.blockList().clear();
        }
    }
}
