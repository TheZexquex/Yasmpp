package dev.thezexquex.yasmpp.configuration.typeserializer;

import dev.thezexquex.yasmpp.configuration.settings.TeleportSettings;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class TeleportSettingsTypeSerializer implements TypeSerializer<TeleportSettings> {
    @Override
    public TeleportSettings deserialize(Type type, ConfigurationNode node) throws SerializationException {
        var teleportCoolDownInSeconds = node.node("teleport-cooldown").getInt();
        var cancelOnMove = node.node("cancel-on-move").getBoolean();
        var permissionBypassesCoolDown = node.node("permission-bypasses-cooldown").getBoolean();

        return new TeleportSettings(
                teleportCoolDownInSeconds,
                cancelOnMove,
                permissionBypassesCoolDown
        );
    }

    @Override
    public void serialize(Type type, @Nullable TeleportSettings  teleportSettings, ConfigurationNode node) throws SerializationException {
        if (teleportSettings != null) {
            node.node("teleport-cooldown").set(teleportSettings.teleportCoolDownInSeconds());
            node.node("cancel-on-move").set(teleportSettings.cancelOnMove());
            node.node("permission-bypasses-cooldown").set(teleportSettings.permissionBypassesCoolDown());
        }
    }
}
