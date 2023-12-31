package dev.thezexquex.yasmpp.core.data.service;

import dev.thezexquex.yasmpp.core.data.database.dao.location.LocationContainer;
import dev.thezexquex.yasmpp.core.data.database.dao.location.LocationWrapper;
import dev.thezexquex.yasmpp.core.data.database.dao.location.home.HomeDao;
import org.bukkit.Location;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class HomeService {
    private final HomeDao homeDao;

    public HomeService(HomeDao homeDao) {
        this.homeDao = homeDao;
    }

    public CompletableFuture<Optional<LocationContainer>> getHome(UUID playerId, String id) {
        return homeDao.getHome(playerId, id);
    }

    public CompletableFuture<Boolean> saveHome(UUID playerId, String id, Location location) {
        return homeDao.saveHome(playerId, id, LocationContainer.of(location));
    }

    public CompletableFuture<Boolean> updateHome(UUID playerId, String id, Location location) {
        return homeDao.updateHome(playerId, id, LocationContainer.of(location));
    }

    public CompletableFuture<Boolean> deleteHome(UUID playerId, String id) {
        return homeDao.deleteHome(playerId, id);
    }

    public CompletableFuture<List<LocationWrapper>> getHomes(UUID playerId, String id) {
        return homeDao.getHomes(playerId);
    }
}
