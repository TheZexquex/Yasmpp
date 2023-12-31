package dev.thezexquex.yasmpp.core.configuration.typeserializer.general;

import dev.thezexquex.yasmpp.core.configuration.settings.general.GeneralBorderSettings;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class GeneralBorderSettingsTypeSerializer implements TypeSerializer<GeneralBorderSettings> {
    @Override
    public GeneralBorderSettings deserialize(Type type, ConfigurationNode node) {
        var borderDiameterLobbyPhase = node.node("diameter-in-lobby").getInt();
        var borderDiameterGamePhase = node.node("diameter-in-game").getInt();

        return new GeneralBorderSettings(borderDiameterLobbyPhase, borderDiameterGamePhase);
    }

    @Override
    public void serialize(Type type, @Nullable GeneralBorderSettings generalBorderSettings, ConfigurationNode node) throws SerializationException {
        if (generalBorderSettings != null) {
            node.node("diameter-in-lobby").set(generalBorderSettings.borderDiameterLobbyPhase());
            node.node("diameter-in-game").set(generalBorderSettings.borderDiameterGamePhase());
        }
    }
}
