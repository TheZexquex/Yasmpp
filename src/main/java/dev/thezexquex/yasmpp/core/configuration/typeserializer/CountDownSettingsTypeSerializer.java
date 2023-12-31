package dev.thezexquex.yasmpp.core.configuration.typeserializer;

import dev.thezexquex.yasmpp.core.configuration.settings.CountDownLine;
import dev.thezexquex.yasmpp.core.configuration.settings.CountDownSettings;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class CountDownSettingsTypeSerializer implements TypeSerializer<CountDownSettings> {
    @Override
    public CountDownSettings deserialize(Type type, ConfigurationNode node) throws SerializationException {
        var restartCountDownSettings = node.node("restart").getList(CountDownLine.class);
        var gameStartCountDownSettings = node.node("game-start").getList(CountDownLine.class);
        var teleportCountDownSettings = node.node("teleport").getList(CountDownLine.class);
        var openEndCountDownSettings = node.node("open-end").getList(CountDownLine.class);

        return new CountDownSettings(
                restartCountDownSettings,
                gameStartCountDownSettings,
                teleportCountDownSettings,
                openEndCountDownSettings
        );
    }

    @Override
    public void serialize(Type type, @Nullable CountDownSettings countDownSettings, ConfigurationNode node) throws SerializationException {

    }
}
