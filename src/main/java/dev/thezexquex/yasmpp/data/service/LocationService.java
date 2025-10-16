package dev.thezexquex.yasmpp.data.service;

import dev.thezexquex.yasmpp.YasmpPlugin;
import dev.thezexquex.yasmpp.data.adapter.LocationAdapter;
import dev.thezexquex.yasmpp.data.database.dao.LocationDao;
import dev.thezexquex.yasmpp.data.entity.WorldPosition;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class LocationService {
    private final LocationDao locationDao;
    private final List<WorldPosition> locationCache;

    public LocationService(LocationDao locationDao) {
        this.locationDao = locationDao;
        this.locationCache = new ArrayList<>();
    }

    public Optional<WorldPosition> getLocation(String id) {
       return locationCache.stream()
                .filter(worldPosition -> worldPosition.name().equals(id))
                .findFirst();
    }

    public void loadLocations() {
        CompletableFuture.supplyAsync(locationDao::getLocations)
                .thenAccept(locationCache::addAll)
                .exceptionally(throwable -> {
                    YasmpPlugin.LOGGER.log(Level.SEVERE, "Failed to load ", throwable);
                    return null;
                });
    }

    public CompletableFuture<Boolean> saveLocation(String id, Location location) {
        var worldPosition = new WorldPosition(id, LocationAdapter.asData(location));

        locationCache.add(worldPosition);
        return CompletableFuture.supplyAsync(() -> locationDao.saveLocation(worldPosition))
                .exceptionally(throwable -> {
                    YasmpPlugin.LOGGER.log(Level.SEVERE, "Failed to save location ", throwable);
                    return false;
                });
    }

    public CompletableFuture<Boolean> deleteLocation(String id) {
        locationCache.removeIf(worldPosition -> worldPosition.name().equals(id));
        return CompletableFuture.supplyAsync(() -> locationDao.deleteLocation(id))
                .exceptionally(throwable -> {
                    YasmpPlugin.LOGGER.log(Level.SEVERE, "Failed to delete location ", throwable);
                    return false;
                });
    }
}
