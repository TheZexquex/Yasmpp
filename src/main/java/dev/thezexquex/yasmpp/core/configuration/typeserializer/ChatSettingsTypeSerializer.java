package dev.thezexquex.yasmpp.core.configuration.typeserializer;

import dev.thezexquex.yasmpp.core.configuration.settings.ChatSettings;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class ChatSettingsTypeSerializer implements TypeSerializer<ChatSettings> {
    @Override
    public ChatSettings deserialize(Type type, ConfigurationNode node) {
        var handleChat = node.node("handle-chat").getBoolean();

        return new ChatSettings(handleChat);
    }

    @Override
    public void serialize(Type type, @Nullable ChatSettings chatSettings, ConfigurationNode node) throws SerializationException {
        if (chatSettings != null) {
            node.node("handle-chat").set(chatSettings.handleChat());
        }
    }
}
