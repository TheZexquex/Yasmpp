package dev.thezexquex.yasmpp.data.service;

import dev.thezexquex.yasmpp.data.adapter.LocationAdapter;
import dev.thezexquex.yasmpp.data.database.dao.location.abst.LocationDao;
import dev.thezexquex.yasmpp.data.entity.WorldPosition;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class LocationService {
    private final LocationDao locationDao;
    private final List<WorldPosition> locationCache;

    public LocationService(LocationDao locationDao) {
        this.locationDao = locationDao;
        this.locationCache = new ArrayList<>();
    }

    public CompletableFuture<Optional<WorldPosition>> getLocation(String id) {
        var worldPositionOpt = locationCache.stream()
                .filter(worldPosition -> worldPosition.name().equals(id))
                .findFirst();

        if (worldPositionOpt.isPresent()) {
            return CompletableFuture.completedFuture(worldPositionOpt);
        }
        return locationDao.getLocation(id);
    }

    public CompletableFuture<Boolean> saveLocation(String id, Location location) {
        var worldPosition = new WorldPosition(id, LocationAdapter.asData(location));

        locationCache.add(worldPosition);
        return locationDao.saveLocation(worldPosition);
    }

    public CompletableFuture<Boolean> deleteLocation(String id) {
        locationCache.removeIf(worldPosition -> worldPosition.name().equals(id));
        return locationDao.deleteLocation(id);
    }
}
