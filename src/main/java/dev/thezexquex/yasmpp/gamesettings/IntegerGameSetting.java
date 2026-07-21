package dev.thezexquex.yasmpp.gamesettings;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class IntegerGameSetting extends GameSetting<Integer> {
    public IntegerGameSetting(String id, String description, Supplier<Integer> getter, Consumer<Integer> setter) {
        super(id, description, getter, setter);
    }

    @Override
    public String getValueAsString() {
        return String.valueOf(getValue());
    }
}
