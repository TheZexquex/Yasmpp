package dev.thezexquex.yasmpp.gamesettings;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class GameSettingRegistry {
    private final Map<String, GameSetting<?>> settings = new LinkedHashMap<>();

    public void register(GameSetting<?> setting) {
        settings.put(setting.id().toLowerCase(), setting);
    }

    public Optional<GameSetting<?>> get(String name) {
        return Optional.ofNullable(settings.get(name.toLowerCase()));
    }

    public Collection<GameSetting<?>> all() {
        return Collections.unmodifiableCollection(settings.values());
    }
}
