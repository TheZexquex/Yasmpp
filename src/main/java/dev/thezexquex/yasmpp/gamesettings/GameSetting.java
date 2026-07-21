package dev.thezexquex.yasmpp.gamesettings;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class GameSetting<T> {
    private final String id;
    private final String description;
    private final Supplier<T> getter;
    private final Consumer<T> setter;

    public GameSetting(String id, String description, Supplier<T> getter, Consumer<T> setter) {
        this.id = id;
        this.description = description;
        this.getter = getter;
        this.setter = setter;
    }

    public String id() {
        return id;
    }

    public String description() {
        return description;
    }

    public T getValue() {
        return getter.get();
    }

    public void setValue(T value) {
        setter.accept(value);
    }

    public abstract String getValueAsString();
}
