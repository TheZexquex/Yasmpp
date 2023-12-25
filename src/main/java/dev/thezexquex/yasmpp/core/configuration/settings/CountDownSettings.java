package dev.thezexquex.yasmpp.core.configuration.settings;

import java.util.List;

public record CountDownSettings(
        List<CountDownLine> restartCountDownSettings,
        List<CountDownLine> gameStartCountDownSettings,
        List<CountDownLine> teleportCountDownSettings,
        List<CountDownLine> openEndCountDownSettings
) {}
