package dev.thezexquex.yasmpp.configuration.typeserializer.general;

import dev.thezexquex.yasmpp.configuration.settings.general.GeneralBorderSettings;
import dev.thezexquex.yasmpp.configuration.settings.general.GeneralEndSettings;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class GeneralBEndSettingsTypeSerializer implements TypeSerializer<GeneralEndSettings> {
    @Override
    public GeneralEndSettings deserialize(Type type, ConfigurationNode node) {
        var lockEnd = node.node("lock-end").getBoolean();

        return new GeneralEndSettings(lockEnd);
    }

    @Override
    public void serialize(Type type, @Nullable GeneralEndSettings generalEndSettings, ConfigurationNode node) throws SerializationException {
        if (generalEndSettings != null) {
            node.node("lock-end").set(generalEndSettings.lockEnd());
        }
    }
}
