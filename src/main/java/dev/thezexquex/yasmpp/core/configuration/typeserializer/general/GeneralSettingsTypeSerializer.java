package dev.thezexquex.yasmpp.core.configuration.typeserializer.general;

import dev.thezexquex.yasmpp.core.configuration.settings.GeneralSettings;
import dev.thezexquex.yasmpp.core.configuration.settings.general.GeneralBorderSettings;
import dev.thezexquex.yasmpp.core.configuration.settings.general.GeneralEndSettings;
import dev.thezexquex.yasmpp.core.configuration.settings.general.GeneralExplosionDamageSettings;
import dev.thezexquex.yasmpp.core.configuration.settings.general.GeneralSpawnElytraSettings;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class GeneralSettingsTypeSerializer implements TypeSerializer<GeneralSettings> {
    @Override
    public GeneralSettings deserialize(Type type, ConfigurationNode node) throws SerializationException {
        var generalBorderSettings = node.node("border").get(GeneralBorderSettings.class);
        var generalEndSettings = node.node("end").get(GeneralEndSettings.class);
        var generalDamageSettings = node.node("damage").get(GeneralExplosionDamageSettings.class);
        var generalElytraSettings = node.node("elytra").get(GeneralSpawnElytraSettings.class);

        return new GeneralSettings(
                generalBorderSettings,
                generalEndSettings,
                generalDamageSettings,
                generalElytraSettings
        );
    }

    @Override
    public void serialize(Type type, @Nullable GeneralSettings generalSettings, ConfigurationNode node) throws SerializationException {
        if (generalSettings != null) {
            node.node("border").set(generalSettings.generalBorderSettings());
            node.node("end").set(generalSettings.generalEndSettings());
            node.node("damage").set(generalSettings.generalExplosionDamageSettings());
            node.node("elytra").set(generalSettings.generalSpawnElytraSettings());
        }
    }
}
