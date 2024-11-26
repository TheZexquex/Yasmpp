package dev.thezexquex.yasmpp.data.database.dao.location.abst;

import dev.thezexquex.yasmpp.data.entity.Home;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface HomeDao {

    CompletableFuture<Optional<Home>> getHome(UUID playerId, String id);

    CompletableFuture<List<Home>> getHomes(UUID playerId);

    CompletableFuture<Boolean> saveHome(Home home);

    CompletableFuture<Boolean> updateHome(Home home);

    CompletableFuture<Boolean> deleteHome(UUID playerId, String id);
}