package dev.thezexquex.yasmpp.data.entity;

import de.chojo.sadu.mapper.annotation.MappingProvider;
import de.chojo.sadu.mapper.rowmapper.RowMapping;

public record WorldPosition(
        String name,
        LocationContainer locationContainer
) {
    @MappingProvider({"location"})
    public static RowMapping<WorldPosition> map() {
        return row -> new WorldPosition(
                row.getString("id"),
                new LocationContainer(
                        row.getString("world"),
                        row.getDouble("x"),
                        row.getDouble("y"),
                        row.getDouble("z"),
                        row.getFloat("yaw"),
                        row.getFloat("pitch")
                )
        );
    }

    ;
}
