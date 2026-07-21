package dev.thezexquex.yasmpp.gamesettings;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EnumGameSetting<E extends Enum<E>> extends GameSetting<E> {
    private final Class<E> enumClass;

    public EnumGameSetting(String name, String description, Class<E> enumClass, Supplier<E> getter, Consumer<E> setter) {
        super(name, description, getter, setter);
        this.enumClass = enumClass;
    }

    public Class<E> enumClass() {
        return enumClass;
    }

    @Override
    public String getValueAsString() {
        return getValue().name();
    }
}
