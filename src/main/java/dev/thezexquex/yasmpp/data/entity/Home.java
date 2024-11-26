package dev.thezexquex.yasmpp.data.entity;

import de.chojo.sadu.mapper.annotation.MappingProvider;
import de.chojo.sadu.mapper.reader.StandardReader;
import de.chojo.sadu.mapper.rowmapper.RowMapping;
import dev.thezexquex.yasmpp.message.PlaceHolderProvider;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.UUID;

public record Home(
        UUID owner,
        String name,
        LocationContainer locationContainer
) implements PlaceHolderProvider {

    @MappingProvider("home")
    public static RowMapping<Home> map() {
        return row -> new Home(
                row.get("player_id", StandardReader.UUID_FROM_STRING),
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

    @Override
    public TagResolver[] tagResolvers() {
        return new TagResolver[]{
                Placeholder.parsed("home-name", name),
                Placeholder.parsed("owner-uuid", String.valueOf(owner)),
                Placeholder.parsed("world", locationContainer().world()),
                Placeholder.parsed("x", String.format("%.2f", locationContainer.x())),
                Placeholder.parsed("y", String.format("%.2f", locationContainer.y())),
                Placeholder.parsed("z", String.format("%.2f", locationContainer.z())),
                Placeholder.parsed("yaw", String.format("%.3f", locationContainer.yaw())),
                Placeholder.parsed("pitch", String.format("%.3f", locationContainer.pitch()))
        };
    }
}