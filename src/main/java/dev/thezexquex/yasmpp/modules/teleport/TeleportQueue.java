package dev.thezexquex.yasmpp.modules.teleport;

import dev.thezexquex.yasmpp.core.timer.Countdown;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class TeleportQueue {
    private final HashMap<Player, Countdown> teleportQueue;


    public TeleportQueue() {
        teleportQueue = new HashMap<>();
    }

    public void addToTeleportQueue(Player player, Countdown countdown) {
        teleportQueue.put(player, countdown);
    }

    public void cancelTeleport(Player player) {
        teleportQueue.get(player).abort();
        clearQueue(player);
    }

    public boolean isTeleporting(Player player) {
        return teleportQueue.containsKey(player);
    }

    public void clearQueue(Player player) {
        teleportQueue.remove(player);
    }
}
