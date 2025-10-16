package dev.thezexquex.yasmpp.data.database.dao;

import de.chojo.sadu.queries.api.call.Call;
import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import de.unknowncity.astralib.common.database.QueryConfigHolder;
import dev.thezexquex.yasmpp.data.entity.WorldPosition;
import org.intellij.lang.annotations.Language;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class LocationDao extends QueryConfigHolder {

    public LocationDao(QueryConfiguration queryConfiguration) {
        super(queryConfiguration);
    }

    public Optional<WorldPosition> getLocation(String id) {

        @Language("sql")
        var query = """
                SELECT id, world, x, y, z, yaw, pitch FROM location WHERE id = :id
                """;

        return config
                .query(query)
                .single(Call.call().bind("id", id))
                .mapAs(WorldPosition.class)
                .first();
    }

    public List<WorldPosition> getLocations() {

        @Language("sql")
        var query = """
                SELECT id, world, x, y, z, yaw, pitch FROM location
                """;

        return config
                .query(query)
                .single()
                .mapAs(WorldPosition.class)
                .all();
    }

    public boolean saveLocation(WorldPosition worldPosition) {

        @Language("sql")
        var query = """
                INSERT OR REPLACE INTO location(id, world, x, y, z, yaw, pitch) VALUES(:id, :world, :x, :y, :z, :yaw, :pitch)
                """;

        var location = worldPosition.locationContainer();
        var id = worldPosition.name();

        return config
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
                .changed();
    }

    public boolean deleteLocation(String id) {

        @Language("sql")
        var query = """
                DELETE FROM location WHERE id = :id
                """;

        return config
                .query(query)
                .single(Call.call().bind("id", id))
                .delete()
                .changed();
    }
}
