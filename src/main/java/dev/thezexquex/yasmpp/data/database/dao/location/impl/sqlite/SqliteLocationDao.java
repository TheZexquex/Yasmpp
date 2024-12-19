package dev.thezexquex.yasmpp.data.database.dao.location.impl.sqlite;

import de.chojo.sadu.queries.api.call.Call;
import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import de.unknowncity.astralib.common.database.QueryConfigHolder;
import dev.thezexquex.yasmpp.data.entity.WorldPosition;
import dev.thezexquex.yasmpp.data.database.dao.location.abst.LocationDao;
import org.intellij.lang.annotations.Language;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SqliteLocationDao extends QueryConfigHolder implements LocationDao {

    public SqliteLocationDao(QueryConfiguration queryConfiguration) {
        super(queryConfiguration);
    }

    @Override
    public CompletableFuture<Optional<WorldPosition>> getLocation(String id) {

        @Language("sql")
        var query = """
                SELECT id, world, x, y, z, yaw, pitch FROM location WHERE id = :id
                """;

        return CompletableFuture.supplyAsync(() -> config
                .query(query)
                .single(Call.call().bind("id", id))
                .map(WorldPosition.map())
                .first());
    }

    @Override
    public CompletableFuture<List<WorldPosition>> getLocations() {

        @Language("sql")
        var query = """
                SELECT id, world, x, y, z, yaw, pitch FROM location
                """;

        return CompletableFuture.supplyAsync(() -> config
                .query(query)
                .single()
                .map(WorldPosition.map())
                .all());
    }

    @Override
    public CompletableFuture<Boolean> saveLocation(WorldPosition worldPosition) {

        @Language("sql")
        var query = """
                INSERT OR REPLACE INTO location(id, world, x, y, z, yaw, pitch) VALUES(:id, :world, :x, :y, :z, :yaw, :pitch)
                """;

        var location = worldPosition.locationContainer();
        var id = worldPosition.name();

        return CompletableFuture.supplyAsync(() -> config
                .query(query)
                .single(Call.call()
                        .bind("id", id)
                        .bind("world", location.world())
                        .bind("x", location.x())
                        .bind("y", location.y())
                        .bind("z", location.z())
                        .bind("yaw", location.yaw())
                        .bind("pitch", location.pitch())
                )
                .insert()
                .changed());
    }

    @Override
    public CompletableFuture<Boolean> deleteLocation(String id) {

        @Language("sql")
        var query = """
                DELETE FROM location WHERE id = :id
                """;

        return CompletableFuture.supplyAsync(() -> config
                .query(query)
                .single(Call.call().bind("id", id))
                .delete()
                .changed());
    }
}
