package dev.thezexquex.yasmpp.configuration.settings;

import de.unknowncity.astralib.libs.com.fasterxml.jackson.annotation.JsonProperty;
import dev.thezexquex.yasmpp.configuration.settings.general.GeneralBorderSettings;
import dev.thezexquex.yasmpp.configuration.settings.general.GeneralPortalSettings;
import dev.thezexquex.yasmpp.configuration.settings.general.GeneralExplosionDamageSettings;
import dev.thezexquex.yasmpp.configuration.settings.general.GeneralSpawnElytraSettings;

public class GeneralSettings {
    @JsonProperty
    private String planServerName = "Handwerksangriff";
    @JsonProperty
    private int planDataRefreshInterval = 120;
    @JsonProperty
    private GeneralBorderSettings border = new GeneralBorderSettings();
    @JsonProperty
    private GeneralPortalSettings portals = new GeneralPortalSettings();
    @JsonProperty
    private GeneralExplosionDamageSettings explosionDamage = new GeneralExplosionDamageSettings();
    @JsonProperty
    private GeneralSpawnElytraSettings spawnElytra = new GeneralSpawnElytraSettings();

    public GeneralSettings() {

    }

    public String planServerName() {
        return planServerName;
    }

    public int planDataRefreshInterval() {
        return planDataRefreshInterval;
    }

    public GeneralBorderSettings border() {
        return border;
    }

    public GeneralPortalSettings portals() {
        return portals;
    }

    public GeneralExplosionDamageSettings explosionDamage() {
        return explosionDamage;
    }

    public GeneralSpawnElytraSettings spawnElytra() {
        return spawnElytra;
    }
}
