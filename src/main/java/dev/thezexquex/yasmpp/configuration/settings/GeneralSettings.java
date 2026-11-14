package dev.thezexquex.yasmpp.configuration.settings;

import dev.thezexquex.yasmpp.configuration.settings.general.GeneralBorderSettings;
import dev.thezexquex.yasmpp.configuration.settings.general.GeneralPortalSettings;
import dev.thezexquex.yasmpp.configuration.settings.general.GeneralExplosionDamageSettings;
import dev.thezexquex.yasmpp.configuration.settings.general.GeneralSpawnElytraSettings;

public class GeneralSettings {
    private String planServerName = "Handwerksangriff";
    private int planDataRefreshInterval = 120;
    private GeneralBorderSettings border = new GeneralBorderSettings();
    private GeneralPortalSettings portals = new GeneralPortalSettings();
    private GeneralExplosionDamageSettings explosionDamage = new GeneralExplosionDamageSettings();
    private GeneralSpawnElytraSettings spawnElytra = new GeneralSpawnElytraSettings();

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
