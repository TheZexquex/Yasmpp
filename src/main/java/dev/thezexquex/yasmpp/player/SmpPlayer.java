package dev.thezexquex.yasmpp.player;

import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.core.data.database.furure.BukkitFutureResult;
import dev.thezexquex.yasmpp.core.data.service.HomeService;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    public Optional<Location> getHome(String id) {
        return homes.containsKey(id) ? Optional.of(homes.get(id)) : Optional.empty();
    }

    public void deleteHome(String id) {
        homes.remove(id);
    }

    public void addHome(String id, Location location) {
        homes.put(id, location);
    }

    public void loadHomes(Plugin plugin, HomeService homeService) {
        BukkitFutureResult.of(homeService.getHomes(player.getUniqueId())).whenComplete(plugin, (locationWrappers) -> {
                    locationWrappers.forEach(locationWrapper -> {
                        homes.put(locationWrapper.name(), locationWrapper.locationContainer().toLocation(plugin.getServer()));
                    });
                }
        );
    }

    public Player toBukkitPlayer() {
        return player;
    }
}
