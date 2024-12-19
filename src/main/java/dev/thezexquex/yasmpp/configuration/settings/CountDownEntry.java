package dev.thezexquex.yasmpp.configuration.settings;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

public record CountDownEntry(
        int second,
        boolean useChat,
        boolean useTitle,
        boolean useSound,
        String soundName
) {

    public Sound sound() {
        return Sound.sound(Key.key(soundName), Sound.Source.MASTER, 1, 1);
    }
}
