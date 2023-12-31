package dev.thezexquex.yasmpp.core.data.database.dao.location.home;

import dev.thezexquex.yasmpp.core.data.database.dao.location.LocationContainer;
import dev.thezexquex.yasmpp.core.data.database.dao.location.LocationWrapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface HomeDao {

    CompletableFuture<Optional<LocationContainer>> getHome(UUID playerId, String id);

    CompletableFuture<List<LocationWrapper>> getHomes(UUID playerId);

    CompletableFuture<Boolean> saveHome(UUID playerId, String id, LocationContainer location);

    CompletableFuture<Boolean> updateHome(UUID playerId, String id, LocationContainer location);

    CompletableFuture<Boolean> deleteHome(UUID playerId, String id);
}