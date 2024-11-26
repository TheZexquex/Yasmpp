package dev.thezexquex.yasmpp.data.entity;

import de.unknowncity.astralib.common.timer.Countdown;
import dev.thezexquex.yasmpp.data.adapter.LocationAdapter;
import dev.thezexquex.yasmpp.data.service.HomeService;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class SmpPlayer {
    private final Player player;
    private final List<Home> homeCache;
    private final HomeService homeService;
    private Countdown currentTeleportCountDown = null;

    public SmpPlayer(Player player, HomeService homeService) {
        this.player = player;
        this.homeCache = new ArrayList<>();
        this.homeService = homeService;
    }

    public List<Home> getHomeCache() {
        return homeCache;
    }

    public Optional<Home> getHome(String id) {
        return homeCache.stream().filter(home -> home.name().equalsIgnoreCase(id)).findFirst();
    }

    public void deleteHome(String id) {
        homeCache.removeIf(home -> home.name().equalsIgnoreCase(id));
        homeService.deleteHome(player.getUniqueId(), id);
    }

    public void createOrUpdateHome(String id, Location location) {
        var home = new Home(player.getUniqueId(), id, LocationAdapter.asData(location));
        homeCache.add(home);
        homeService.insertHome(player.getUniqueId(), id, location);
    }

    public void loadHomes() {
        homeService.getHomes(player.getUniqueId()).whenComplete((homes, throwable) -> homeCache.addAll(homes));
    }

    public boolean hasHome(String id) {
        return homeCache.stream().anyMatch(home -> home.name().equalsIgnoreCase(id));
    }

    public boolean isInTeleportWarmup() {
        return currentTeleportCountDown != null;
    }

    public void setCurrentTeleportCountDown(Countdown countdown) {
        this.currentTeleportCountDown = countdown;
    }

    public Player toBukkitPlayer() {
        return player;
    }

    public void cancelCurrentTeleport() {
        this.currentTeleportCountDown.abort();
        this.currentTeleportCountDown = null;
    }
}
