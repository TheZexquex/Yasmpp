package dev.thezexquex.yasmpp.configuration.typeserializer;

import dev.thezexquex.yasmpp.configuration.settings.CountDownLine;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class CountDownLineTypeSerializer implements TypeSerializer<CountDownLine> {
    @Override
    public CountDownLine deserialize(Type type, ConfigurationNode node) throws SerializationException {
        var second = node.node("second").getInt();
        var useChat = node.node("use-chat").getBoolean();
        var useTitle = node.node("use-title").getBoolean();
        var useSound = node.node("use-sound").getBoolean();
        var soundName = node.node("sound-name").getString();

        return new CountDownLine(
                second,
                useChat,
                useTitle,
                useSound,
                Sound.sound(Key.key(soundName), Sound.Source.MASTER, 1, 1)
        );
    }

    @Override
    public void serialize(Type type, @Nullable CountDownLine obj, ConfigurationNode node) throws SerializationException {

    }
}
