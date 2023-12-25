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
        var doCreeperDamage = node.node("do-creeper-damage").getBoolean();
        var doTntDamage = node.node("do-tnt-damage").getBoolean();
        return null;
    }

    @Override
    public void serialize(Type type, @Nullable GeneralExplosionDamageSettings generalExplosionDamageSettings, ConfigurationNode node) throws SerializationException {
        if (generalExplosionDamageSettings != null) {
            node.node("do-creeper-damage").set(generalExplosionDamageSettings.doCreeperDamage());
            node.node("do-tnt-damage").set(generalExplosionDamageSettings.doTntDamage());
        }
    }
}
