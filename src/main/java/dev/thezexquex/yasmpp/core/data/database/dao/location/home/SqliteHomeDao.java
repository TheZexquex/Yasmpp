package dev.thezexquex.yasmpp.core.data.database.dao.location.home;

import de.chojo.sadu.base.QueryFactory;
import de.chojo.sadu.wrapper.util.UpdateResult;
import dev.thezexquex.yasmpp.core.data.database.dao.location.LocationContainer;
import dev.thezexquex.yasmpp.core.data.database.dao.location.LocationWrapper;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SqliteHomeDao extends QueryFactory implements HomeDao {

    public SqliteHomeDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public CompletableFuture<Optional<LocationContainer>> getHome(UUID playerId, String id) {

        return builder(LocationContainer.class)
                .query("SELECT world, x, y, z, yaw, pitch FROM home WHERE id = ? AND player_id = ?")
                .parameter(statement -> statement.setString(id).setString(playerId.toString()))
                .readRow(rs -> LocationContainer.of(
                        rs.getString("world"),
                        rs.getDouble("x"),
                        rs.getDouble("y"),
                        rs.getDouble("z"),
                        rs.getFloat("yaw"),
                        rs.getFloat("pitch")
                ))
                .first();
    }

    @Override
    public CompletableFuture<List<LocationWrapper>> getHomes(UUID playerId) {
        return builder(LocationWrapper.class)
                .query("SELECT id, world, x, y, z, yaw, pitch FROM home WHERE player_id = ?")
                .parameter(statement -> statement.setString(playerId.toString()))
                .readRow(rs -> new LocationWrapper(
                        rs.getString("id"),
                        LocationContainer.of(
                                rs.getString("world"),
                                rs.getDouble("x"),
                                rs.getDouble("y"),
                                rs.getDouble("z"),
                                rs.getFloat("yaw"),
                                rs.getFloat("pitch")
                        )
                ))
                .all();
    }

    @Override
    public CompletableFuture<Boolean> saveHome(UUID playerId, String id, LocationContainer location) {
        return builder()
                .query("INSERT INTO home(player_id, id, world, x, y, z, yaw, pitch) VALUES(?, ?, ?, ?, ?, ?, ?, ?)")
                .parameter(stmt -> {
                    stmt.setString(playerId.toString())
                            .setString(id)
                            .setString(location.world())
                            .setDouble(location.x())
                            .setDouble(location.y())
                            .setDouble(location.z())
                            .setFloat(location.yaw())
                            .setFloat(location.pitch());
                })
                .insert()
                .send()
                .thenApply(UpdateResult::changed);

    }

    @Override
    public CompletableFuture<Boolean> updateHome(UUID playerId, String id, LocationContainer location) {
        return builder()
                .query("UPDATE home SET world = ?, x = ?, y = ?, z = ?, yaw = ?, pitch = ? WHERE id = ? AND player_id = ?")
                .parameter(stmt -> {
                    stmt.setString(location.world())
                    .setDouble(location.x())
                    .setDouble(location.y())
                    .setDouble(location.z())
                    .setFloat(location.yaw())
                    .setFloat(location.pitch())
                    .setString(id)
                    .setString(playerId.toString());
                })
                .insert()
                .send()
                .thenApply(UpdateResult::changed);
    }

    @Override
    public CompletableFuture<Boolean> deleteHome(UUID playerId, String id) {
        return builder()
                .query("DELETE FROM home WHERE id = ? AND player_id = ?")
                .parameter(stmt -> stmt.setString(id).setString(playerId.toString()))
                .delete()
                .send()
                .thenApply(UpdateResult::changed);
    }
}
