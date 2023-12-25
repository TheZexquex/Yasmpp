package dev.thezexquex.yasmpp.configuration;

import dev.thezexquex.yasmpp.configuration.settings.ChatSettings;
import dev.thezexquex.yasmpp.configuration.settings.CountDownSettings;
import dev.thezexquex.yasmpp.configuration.settings.GeneralSettings;
import dev.thezexquex.yasmpp.configuration.settings.TeleportSettings;

public record Configuration(
        ChatSettings chatSettings,
        CountDownSettings countDownSettings,
        GeneralSettings generalSettings,
        TeleportSettings teleportSettings
) {}
