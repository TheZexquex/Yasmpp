package dev.thezexquex.yasmpp.data.database.dao;

import de.chojo.sadu.queries.api.call.Call;
import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import de.chojo.sadu.queries.call.adapter.UUIDAdapter;
import de.unknowncity.astralib.common.database.QueryConfigHolder;
import dev.thezexquex.yasmpp.data.entity.Home;
import org.intellij.lang.annotations.Language;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class HomeDao extends QueryConfigHolder {

    public HomeDao(QueryConfiguration config) {
        super(config);
    }

    public Optional<Home> getHome(UUID playerId, String id) {

        @Language("sql")
        var query = """
                SELECT player_id, id, world, x, y, z, yaw, pitch FROM home WHERE id = :id AND player_id = :player_id
                """;

        return config.query(query)
                .single(Call.call().bind("id", id).bind("player_id", playerId, UUIDAdapter.AS_STRING))
                .mapAs(Home.class)
                .first();
    }

    public List<Home> getHomes(UUID playerId) {

        @Language("sql")
        var query = """
                SELECT player_id, id, world, x, y, z, yaw, pitch FROM home WHERE player_id = :player_id
                """;

        return config.query(query)
                .single(Call.call().bind("player_id", playerId, UUIDAdapter.AS_STRING))
                .mapAs(Home.class)
                .all();
    }

    public boolean saveHome(Home home) {

        @Language("sql")
        var query = """
                INSERT OR REPLACE INTO home(player_id, id, world, x, y, z, yaw, pitch) VALUES(:player_id, :id, :world, :x, :y, :z, :yaw, :pitch)
                """;

        var location = home.locationContainer();
        var id = home.name();
        var playerId = home.owner();

        return config.query(query)
                .single(Call.call()
                        .bind("player_id", playerId, UUIDAdapter.AS_STRING)
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

    public boolean updateHome(Home home) {

        var location = home.locationContainer();
        var id = home.name();
        var playerId = home.owner();

        @Language("sql")
        var query = """
                UPDATE home SET world = :world, x = :x, y = :y, z = :z, yaw = :yaw, pitch = :pitch WHERE id = :id AND player_id = :player_id
                """;

        return config.query(query)
                .single(Call.call()
                        .bind("player_id", playerId, UUIDAdapter.AS_STRING)
                        .bind("id", id)
                        .bind("world", location.world())
                        .bind("x", location.x())
                        .bind("y", location.y())
                        .bind("z", location.z())
                        .bind("yaw", location.yaw())
                        .bind("pitch", location.pitch())
                )
                .update()
                .changed();
    }

    public boolean deleteHome(UUID playerId, String id) {

        @Language("sql")
        var query = """
                DELETE FROM home WHERE id = :id AND player_id = :player_id
                """;

        return config.query(query)
                .single(Call.call().bind("id", id).bind("player_id", playerId, UUIDAdapter.AS_STRING))
                .delete()
                .changed();
    }
}
