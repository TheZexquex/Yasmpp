package dev.thezexquex.yasmpp.player;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SmpPlayer {
    private final Player player;
    private final Map<String, Location> homes;

    public SmpPlayer(Player player) {
        this.player = player;
        this.homes = new HashMap<>();
    }

    public Map<String, Location> getHomes() {
        return homes;
    }
}
