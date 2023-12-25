package dev.thezexquex.yasmpp.core.configuration.settings;

import dev.thezexquex.yasmpp.core.configuration.settings.general.GeneralBorderSettings;
import dev.thezexquex.yasmpp.core.configuration.settings.general.GeneralEndSettings;
import dev.thezexquex.yasmpp.core.configuration.settings.general.GeneralExplosionDamageSettings;
import dev.thezexquex.yasmpp.core.configuration.settings.general.GeneralSpawnElytraSettings;

public record GeneralSettings(
        GeneralBorderSettings generalBorderSettings,
        GeneralEndSettings generalEndSettings,
        GeneralExplosionDamageSettings generalExplosionDamageSettings,
        GeneralSpawnElytraSettings generalSpawnElytraSettings

) {}
