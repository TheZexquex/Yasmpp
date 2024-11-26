package dev.thezexquex.yasmpp.data.service;

import dev.thezexquex.yasmpp.data.adapter.LocationAdapter;
import dev.thezexquex.yasmpp.data.entity.Home;
import dev.thezexquex.yasmpp.data.database.dao.location.abst.HomeDao;
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

    public CompletableFuture<Optional<Home>> getHome(UUID playerId, String id) {
        return homeDao.getHome(playerId, id);
    }

    public CompletableFuture<Boolean> insertHome(UUID playerId, String id, Location location) {
        var home = new Home(playerId, id, LocationAdapter.asData(location));

        return homeDao.saveHome(home);
    }

    public CompletableFuture<Boolean> deleteHome(UUID playerId, String id) {
        return homeDao.deleteHome(playerId, id);
    }

    public CompletableFuture<List<Home>> getHomes(UUID playerId) {
        return homeDao.getHomes(playerId);
    }
}
