package dev.thezexquex.yasmpp.core.configuration.typeserializer.general;

import dev.thezexquex.yasmpp.core.configuration.settings.general.GeneralExplosionDamageSettings;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class GeneralExplosionDamageSettingsTypeSerializer implements TypeSerializer<GeneralExplosionDamageSettings> {
    @Override
    public GeneralExplosionDamageSettings deserialize(Type type, ConfigurationNode node) throws SerializationException {
        var doCreeperDamage = node.node("creeper").getBoolean();
        var doTntDamage = node.node("tnt").getBoolean();

        return new GeneralExplosionDamageSettings(doCreeperDamage, doTntDamage);
    }

    @Override
    public void serialize(Type type, @Nullable GeneralExplosionDamageSettings generalExplosionDamageSettings, ConfigurationNode node) throws SerializationException {
        if (generalExplosionDamageSettings != null) {
            node.node("creeper").set(generalExplosionDamageSettings.doCreeperDamage());
            node.node("tnt").set(generalExplosionDamageSettings.doTntDamage());
        }
    }
}
