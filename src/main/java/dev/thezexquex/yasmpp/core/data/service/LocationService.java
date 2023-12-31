package dev.thezexquex.yasmpp.core.data.service;

import dev.thezexquex.yasmpp.core.data.database.dao.location.LocationWrapper;
import dev.thezexquex.yasmpp.core.data.database.dao.location.special.LocationDao;
import dev.thezexquex.yasmpp.core.data.database.dao.location.LocationContainer;
import org.bukkit.Location;
import org.bukkit.Server;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class LocationService {
    private final LocationDao locationDao;
    private final HashMap<String, Location> locationCache;

    public LocationService(LocationDao locationDao) {
        this.locationDao = locationDao;
        this.locationCache = new HashMap<>();
    }

    public CompletableFuture<Optional<LocationContainer>> getLocation(String id) {
        return locationCache.containsKey(id) ? CompletableFuture.completedFuture(
                Optional.of(LocationContainer.of(locationCache.get(id)))
        ) : locationDao.getLocation(id);
    }

    public CompletableFuture<Boolean> saveLocation(String id, Location location) {
        locationCache.put(id, location);
        return locationDao.saveLocation(id, LocationContainer.of(location));
    }

    public CompletableFuture<Boolean> updateLocation(String id, Location location) {
        locationCache.put(id, location);
        return locationDao.updateLocation(id, LocationContainer.of(location));
    }

    public CompletableFuture<Boolean> deleteLocation(String id) {
        locationCache.remove(id);
        return locationDao.deleteLocation(id);
    }

    public CompletableFuture<List<LocationWrapper>> getLocations() {
        return locationCache.isEmpty() ? locationDao.getLocations() : CompletableFuture.completedFuture(
                locationCache.keySet().stream().map(key -> new LocationWrapper(
                        key,
                        LocationContainer.of(locationCache.get(key)))).collect(Collectors.toList()
                )
        );
    }

    public List<LocationWrapper> getCachedLocations() {
        return locationCache.keySet().stream().map(key -> new LocationWrapper(
                key,
                LocationContainer.of(locationCache.get(key)))).collect(Collectors.toList()
        );
    }

    public boolean existsCachedLocation(String id) {
        return locationCache.containsKey(id);
    }

    public Location getCachedLocation(String id) {
        return locationCache.get(id);
    }

    public void cacheLocations(Server server) {
        if (locationCache.isEmpty()) {
            getLocations().thenAcceptAsync((locationWrappers -> {
                locationWrappers.forEach(locationWrapper -> {
                    locationCache.put(locationWrapper.name(), locationWrapper.locationContainer().toLocation(server));
                });
            }));
        }
    }
}
