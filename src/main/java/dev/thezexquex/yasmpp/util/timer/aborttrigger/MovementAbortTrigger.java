package dev.thezexquex.yasmpp.util.timer.aborttrigger;

import de.unknowncity.astralib.common.timer.aborttrigger.AbortTrigger;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class MovementAbortTrigger extends AbortTrigger {
    private final Player player;
    private Location prevoiusLocation;
    private Runnable runOnTrigger;
    public MovementAbortTrigger(Player player, Runnable runOnTrigger) {
        this.player = player;
        this.prevoiusLocation = player.getLocation();
        this.runOnTrigger = runOnTrigger;
    }

    @Override
    public boolean checkForPotentialTrigger() {
        if (prevoiusLocation.distanceSquared(player.getLocation()) > 0.05) {
            runOnTrigger.run();
            return true;
        }
        prevoiusLocation = player.getLocation();
        return false;
    }
}