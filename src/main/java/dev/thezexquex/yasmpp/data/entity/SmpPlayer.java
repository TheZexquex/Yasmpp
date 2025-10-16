package dev.thezexquex.yasmpp.data.entity;

import dev.thezexquex.yasmpp.data.adapter.LocationAdapter;
import dev.thezexquex.yasmpp.data.service.HomeService;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class SmpPlayer {
    private final Player player;
    private final List<Home> homeCache;
    private int maxHomeSlots;
    private final HomeService homeService;
    private boolean isCurrentlyInTeleport = false;

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
        homeCache.removeIf(home1 -> home1.name().equalsIgnoreCase(id));
        homeCache.add(home);
        homeService.insertHome(player.getUniqueId(), id, location);
    }

    public void loadHomes() {
        homeService.getHomes(player.getUniqueId()).whenComplete((homes, throwable) -> homeCache.addAll(homes));
    }

    public void loadHomeSlots() {
        homeService.getHomeSlots(player.getUniqueId()).whenComplete((homeSlots, throwable) -> maxHomeSlots(homeSlots));
    }

    public boolean hasHome(String id) {
        return homeCache.stream().anyMatch(home -> home.name().equalsIgnoreCase(id));
    }

    public Player toBukkitPlayer() {
        return player;
    }

    public boolean isCurrentlyInTeleport() {
        return isCurrentlyInTeleport;
    }

    public void currentlyInTeleport(boolean currentlyInTeleport) {
        isCurrentlyInTeleport = currentlyInTeleport;
    }

    public int maxHomeSlots() {
        return maxHomeSlots;
    }

    public void maxHomeSlots(int maxHomeSlots) {
        this.maxHomeSlots = maxHomeSlots;
    }
}
