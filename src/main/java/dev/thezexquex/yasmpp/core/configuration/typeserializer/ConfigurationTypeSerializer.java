package dev.thezexquex.yasmpp.core.configuration.typeserializer;

import dev.thezexquex.yasmpp.core.configuration.Configuration;
import dev.thezexquex.yasmpp.core.configuration.settings.ChatSettings;
import dev.thezexquex.yasmpp.core.configuration.settings.CountDownSettings;
import dev.thezexquex.yasmpp.core.configuration.settings.GeneralSettings;
import dev.thezexquex.yasmpp.core.configuration.settings.TeleportSettings;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class ConfigurationTypeSerializer implements TypeSerializer<Configuration> {
    @Override
    public Configuration deserialize(Type type, ConfigurationNode node) throws SerializationException {
        var chatSettings = node.node("chat").get(ChatSettings.class);
        var countDownSettings = node.node("countdown").get(CountDownSettings.class);
        var generalSettings = node.node("general").get(GeneralSettings.class);
        var teleportSettings = node.node("teleport").get(TeleportSettings.class);

        return new Configuration(
                chatSettings,
                countDownSettings,
                generalSettings,
                teleportSettings
        );
    }

    @Override
    public void serialize(Type type, @Nullable Configuration configuration, ConfigurationNode node) throws SerializationException {
        if (configuration != null) {
            node.node("chat").set(configuration.chatSettings());
            node.node("general").set(configuration.generalSettings());
            node.node("teleport").set(configuration.teleportSettings());
        }
    }
}
