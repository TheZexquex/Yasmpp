package dev.thezexquex.yasmpp.data.service;

import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.data.adapter.LocationAdapter;
import dev.thezexquex.yasmpp.data.database.dao.HomeDao;
import dev.thezexquex.yasmpp.data.database.dao.HomeSlotDao;
import dev.thezexquex.yasmpp.data.entity.Home;
import org.bukkit.Location;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class HomeService {
    private final HomeDao homeDao;
    private final HomeSlotDao homeSlotDao;
    private final SmpPlayerService smpPlayerService;

    public HomeService(HomeDao homeDao, HomeSlotDao homeSlotDao, SmpPlayerService smpPlayerService) {
        this.homeDao = homeDao;
        this.homeSlotDao = homeSlotDao;
        this.smpPlayerService = smpPlayerService;
    }

    public CompletableFuture<Optional<Home>> getHome(UUID playerId, String id) {
        return CompletableFuture.supplyAsync(() -> homeDao.getHome(playerId, id))
                .exceptionally(throwable -> {
                    YasmpPlugin.LOGGER.log(Level.SEVERE, "Failed to load home ", throwable);
                    return Optional.empty();
                });
    }

    public CompletableFuture<Boolean> insertHome(UUID playerId, String id, Location location) {
        var home = new Home(playerId, id, LocationAdapter.asData(location));

        return CompletableFuture.supplyAsync(() -> homeDao.saveHome(home))
                .exceptionally(throwable -> {
                    YasmpPlugin.LOGGER.log(Level.SEVERE, "Failed to save home ", throwable);
                    return false;
                });
    }

    public CompletableFuture<Boolean> deleteHome(UUID playerId, String id) {
        return CompletableFuture.supplyAsync(() -> homeDao.deleteHome(playerId, id))
                .exceptionally(throwable -> {
                    YasmpPlugin.LOGGER.log(Level.SEVERE, "Failed to delete home ", throwable);
                    return false;
                });
    }

    public CompletableFuture<List<Home>> getHomes(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> homeDao.getHomes(playerId))
                .exceptionally(throwable -> {
                    YasmpPlugin.LOGGER.log(Level.SEVERE, "Failed to load homes ", throwable);
                    return List.of();
                });
    }

    public CompletableFuture<Boolean> incrementHomeSlots(UUID playerId, int additionalSlots) {
        smpPlayerService.getSmpPlayer(playerId).ifPresent(smpPlayer -> smpPlayer.maxHomeSlots(smpPlayer.maxHomeSlots() + additionalSlots));
        return CompletableFuture.supplyAsync(() -> homeSlotDao.incrementHomeSlot(playerId, additionalSlots))
                .exceptionally(throwable -> {
                    YasmpPlugin.LOGGER.log(Level.SEVERE, "Failed to increment home slots ", throwable);
                    return false;
                });
    }

    public CompletableFuture<Integer> getHomeSlots(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> homeSlotDao.getMaxHomeSlot(playerId).orElse(0))
                .exceptionally(throwable -> {
                    YasmpPlugin.LOGGER.log(Level.SEVERE, "Failed to get home slots ", throwable);
                    return 0;
                });
    }
}
