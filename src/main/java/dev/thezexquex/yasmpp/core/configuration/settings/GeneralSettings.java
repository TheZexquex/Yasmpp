package dev.thezexquex.yasmpp.core.configuration.settings;

import dev.thezexquex.yasmpp.core.configuration.settings.general.GeneralBorderSettings;
import dev.thezexquex.yasmpp.core.configuration.settings.general.GeneralEndSettings;
import dev.thezexquex.yasmpp.core.configuration.settings.general.GeneralExplosionDamageSettings;
import dev.thezexquex.yasmpp.core.configuration.settings.general.GeneralSpawnElytraSettings;

import java.util.Objects;

public class GeneralSettings {
    private GeneralBorderSettings generalBorderSettings;
    private GeneralEndSettings generalEndSettings;
    private GeneralExplosionDamageSettings generalExplosionDamageSettings;
    private GeneralSpawnElytraSettings generalSpawnElytraSettings;

    public GeneralSettings(
            GeneralBorderSettings generalBorderSettings,
            GeneralEndSettings generalEndSettings,
            GeneralExplosionDamageSettings generalExplosionDamageSettings,
            GeneralSpawnElytraSettings generalSpawnElytraSettings

    ) {
        this.generalBorderSettings = generalBorderSettings;
        this.generalEndSettings = generalEndSettings;
        this.generalExplosionDamageSettings = generalExplosionDamageSettings;
        this.generalSpawnElytraSettings = generalSpawnElytraSettings;
    }

    public GeneralBorderSettings generalBorderSettings() {
        return generalBorderSettings;
    }

    public GeneralEndSettings generalEndSettings() {
        return generalEndSettings;
    }

    public GeneralExplosionDamageSettings generalExplosionDamageSettings() {
        return generalExplosionDamageSettings;
    }

    public GeneralSpawnElytraSettings generalSpawnElytraSettings() {
        return generalSpawnElytraSettings;
    }

    public void setGeneralBorderSettings(GeneralBorderSettings generalBorderSettings) {
        this.generalBorderSettings = generalBorderSettings;
    }

    public void setGeneralEndSettings(GeneralEndSettings generalEndSettings) {
        this.generalEndSettings = generalEndSettings;
    }

    public void setGeneralExplosionDamageSettings(GeneralExplosionDamageSettings generalExplosionDamageSettings) {
        this.generalExplosionDamageSettings = generalExplosionDamageSettings;
    }

    public void setGeneralSpawnElytraSettings(GeneralSpawnElytraSettings generalSpawnElytraSettings) {
        this.generalSpawnElytraSettings = generalSpawnElytraSettings;
    }
}
