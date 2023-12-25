package dev.thezexquex.yasmpp.core.configuration;

import dev.thezexquex.yasmpp.core.configuration.settings.ChatSettings;
import dev.thezexquex.yasmpp.core.configuration.settings.CountDownSettings;
import dev.thezexquex.yasmpp.core.configuration.settings.GeneralSettings;
import dev.thezexquex.yasmpp.core.configuration.settings.TeleportSettings;

public record Configuration(
        ChatSettings chatSettings,
        CountDownSettings countDownSettings,
        GeneralSettings generalSettings,
        TeleportSettings teleportSettings
) {}
