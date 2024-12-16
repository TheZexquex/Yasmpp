package dev.thezexquex.yasmpp.configuration.settings;

import de.unknowncity.astralib.libs.com.fasterxml.jackson.annotation.JsonProperty;
import dev.thezexquex.yasmpp.configuration.settings.general.GeneralBorderSettings;
import dev.thezexquex.yasmpp.configuration.settings.general.GeneralEndSettings;
import dev.thezexquex.yasmpp.configuration.settings.general.GeneralExplosionDamageSettings;
import dev.thezexquex.yasmpp.configuration.settings.general.GeneralSpawnElytraSettings;

public class GeneralSettings {
    @JsonProperty
    private String planServerName = "Handwerksangriff";
    private int planDataRefreshInterval = 120;
    @JsonProperty
    private GeneralBorderSettings generalBorderSettings = new GeneralBorderSettings();
    @JsonProperty
    private GeneralEndSettings generalEndSettings = new GeneralEndSettings();
    @JsonProperty
    private GeneralExplosionDamageSettings generalExplosionDamageSettings = new GeneralExplosionDamageSettings();
    @JsonProperty
    private GeneralSpawnElytraSettings generalSpawnElytraSettings = new GeneralSpawnElytraSettings();

    public GeneralSettings() {

    }

    public String planServerName() {
        return planServerName;
    }

    public int planDataRefreshInterval() {
        return planDataRefreshInterval;
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
}
