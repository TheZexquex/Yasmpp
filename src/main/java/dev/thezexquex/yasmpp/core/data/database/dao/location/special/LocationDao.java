package dev.thezexquex.yasmpp.core.data.database.dao.location.special;

import dev.thezexquex.yasmpp.core.data.database.dao.location.LocationContainer;
import dev.thezexquex.yasmpp.core.data.database.dao.location.LocationWrapper;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface LocationDao {

    CompletableFuture<Optional<LocationContainer>> getLocation(String id);
    CompletableFuture<Boolean> saveLocation(String id, LocationContainer location);
    CompletableFuture<Boolean> updateLocation(String id, LocationContainer location);
    CompletableFuture<Boolean> deleteLocation(String id);

    CompletableFuture<List<LocationWrapper>> getLocations();
}
