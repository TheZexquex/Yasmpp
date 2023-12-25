package dev.thezexquex.yasmpp.configuration.settings;

import net.kyori.adventure.sound.Sound;

public record CountDownLine(
        int second,
        boolean useChat,
        boolean useTitle,
        boolean useSound,
        Sound soundName
) {}
