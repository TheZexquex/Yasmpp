package dev.thezexquex.yasmpp.data.entity;

import dev.thezexquex.yasmpp.data.adapter.LocationAdapter;
import dev.thezexquex.yasmpp.data.service.HomeService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class SmpPlayer {
    private final UUID uuid;
    private final Map<String, Home> homeCache;
    private int maxHomeSlots;
    private final HomeService homeService;
    private boolean isCurrentlyInTeleport;

    public SmpPlayer(UUID uuid, HomeService homeService) {
        this.isCurrentlyInTeleport = false;
        this.uuid = uuid;
        this.homeCache = new HashMap<>();
        this.homeService = homeService;
    }

    public List<Home> getHomes() {
        return homeCache.values().stream().toList();
    }

    public Optional<Home> getHome(String id) {
        return Optional.ofNullable(homeCache.get(id));
    }

    public void deleteHome(String id) {
        homeCache.remove(id);
        homeService.deleteHome(uuid, id);
    }

    public void createOrUpdateHome(String id, Location location) {
        var home = new Home(uuid, id, LocationAdapter.asData(location));
        homeCache.put(id, home);
        homeService.insertHome(uuid, id, location);
    }

    public void loadHomes() {
        homeService.getHomes(uuid).whenComplete((
                homes, throwable) -> homeCache.putAll(homes.stream().collect(Collectors.toMap(Home::name, home -> home)))
        );
    }

    public void loadHomeSlots() {
        homeService.getHomeSlots(uuid).whenComplete((homeSlots, throwable) -> maxHomeSlots(homeSlots));
    }

    public boolean hasHomeWithName(String id) {
        return homeCache.containsKey(id);
    }

    public UUID uuid() {
        return uuid;
    }

    public Player toBukkitPlayer() {
        return Bukkit.getPlayer(uuid);
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
