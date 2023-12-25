package dev.thezexquex.yasmpp.configuration.typeserializer.general;

import dev.thezexquex.yasmpp.configuration.settings.general.GeneralSpawnElytraSettings;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class GeneralSpawnElytraSettingsTypeSerializer implements TypeSerializer<GeneralSpawnElytraSettings> {
    @Override
    public GeneralSpawnElytraSettings deserialize(Type type, ConfigurationNode node) {
        var useMaxBoosts = node.node("use-max-boosts").getBoolean();
        var maxBoosts = node.node("max-boosts").getInt();
        var radius = node.node("radius").getInt();

        return new GeneralSpawnElytraSettings(useMaxBoosts, maxBoosts, radius);
    }

    @Override
    public void serialize(Type type, @Nullable GeneralSpawnElytraSettings generalSpawnElytraSettings, ConfigurationNode node) throws SerializationException {
        if (generalSpawnElytraSettings != null) {
            node.node("use-max-boosts").set(generalSpawnElytraSettings.useMaxBoosts());
            node.node("max-boosts").set(generalSpawnElytraSettings.maxBoosts());
            node.node("radius").set(generalSpawnElytraSettings.radius());
        }
    }
}
