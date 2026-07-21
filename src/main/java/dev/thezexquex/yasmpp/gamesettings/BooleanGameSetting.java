package dev.thezexquex.yasmpp.gamesettings;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class BooleanGameSetting extends GameSetting<Boolean> {
    public BooleanGameSetting(String id, String description, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        super(id, description, getter, setter);
    }

    @Override
    public String getValueAsString() {
        return String.valueOf(getValue());
    }
}
