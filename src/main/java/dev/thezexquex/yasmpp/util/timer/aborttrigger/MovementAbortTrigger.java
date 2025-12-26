package dev.thezexquex.yasmpp.util.timer.aborttrigger;

import de.unknowncity.astralib.common.timer.aborttrigger.AbortTrigger;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class MovementAbortTrigger extends AbortTrigger {
    private final Player player;
    private Location previousLocation;
    private Runnable runOnTrigger;
    public MovementAbortTrigger(Player player, Runnable runOnTrigger) {
        this.player = player;
        this.previousLocation = player.getLocation();
        this.runOnTrigger = runOnTrigger;
    }

    @Override
    public boolean checkForPotentialTrigger() {
        if (previousLocation.distanceSquared(player.getLocation()) > 0.05) {
            runOnTrigger.run();
            return true;
        }
        previousLocation = player.getLocation().clone();
        return false;
    }
}