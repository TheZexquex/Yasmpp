package dev.thezexquex.yasmpp.core.data.database.dao.location.special;

import de.chojo.sadu.base.QueryFactory;
import de.chojo.sadu.wrapper.util.UpdateResult;
import dev.thezexquex.yasmpp.core.data.database.dao.location.LocationContainer;
import dev.thezexquex.yasmpp.core.data.database.dao.location.LocationWrapper;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SqliteLocationDao extends QueryFactory implements LocationDao {

    public SqliteLocationDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public CompletableFuture<Optional<LocationContainer>> getLocation(String id) {

        return builder(LocationContainer.class)
                .query("SELECT world, x, y, z, yaw, pitch FROM location WHERE id = ?")
                .parameter(statement -> statement.setString(id))
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
    public CompletableFuture<List<LocationWrapper>> getLocations() {
        return builder(LocationWrapper.class)
                .query("SELECT id, world, x, y, z, yaw, pitch FROM location")
                .emptyParams()
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
    public CompletableFuture<Boolean> saveLocation(String id, LocationContainer location) {
        return builder()
                .query("INSERT INTO location(id, world, x, y, z, yaw, pitch) VALUES(?, ?, ?, ?, ?, ?, ?)")
                .parameter(stmt -> {
                    stmt.setString(id);
                    stmt.setString(location.world());
                    stmt.setDouble(location.x());
                    stmt.setDouble(location.y());
                    stmt.setDouble(location.z());
                    stmt.setFloat(location.yaw());
                    stmt.setFloat(location.pitch());
                })
                .insert()
                .send()
                .thenApply(UpdateResult::changed);

    }

    @Override
    public CompletableFuture<Boolean> updateLocation(String id, LocationContainer location) {
        return builder()
                .query("UPDATE location SET world = ?, x = ?, y = ?, z = ?, yaw = ?, pitch = ? WHERE id = ?")
                .parameter(stmt -> {
                    stmt.setString(location.world());
                    stmt.setDouble(location.x());
                    stmt.setDouble(location.y());
                    stmt.setDouble(location.z());
                    stmt.setFloat(location.yaw());
                    stmt.setFloat(location.pitch());
                    stmt.setString(id);
                })
                .insert()
                .send()
                .thenApply(UpdateResult::changed);
    }

    @Override
    public CompletableFuture<Boolean> deleteLocation(String id) {
        return builder()
                .query("DELETE FROM location WHERE id = ?")
                .parameter(stmt -> stmt.setString(id))
                .delete()
                .send()
                .thenApply(UpdateResult::changed);
    }
}
