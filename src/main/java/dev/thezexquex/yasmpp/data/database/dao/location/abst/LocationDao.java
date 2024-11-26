package dev.thezexquex.yasmpp.data.database.dao.location.abst;

import dev.thezexquex.yasmpp.data.entity.WorldPosition;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface LocationDao {

    CompletableFuture<Optional<WorldPosition>> getLocation(String id);
    CompletableFuture<Boolean> saveLocation(WorldPosition worldPosition);
    CompletableFuture<Boolean> deleteLocation(String id);

    CompletableFuture<List<WorldPosition>> getLocations();
}
