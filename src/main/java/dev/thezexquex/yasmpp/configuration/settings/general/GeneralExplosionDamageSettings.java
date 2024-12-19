package dev.thezexquex.yasmpp.configuration.settings.general;

import de.unknowncity.astralib.libs.com.fasterxml.jackson.annotation.JsonProperty;

public class GeneralExplosionDamageSettings {
    @JsonProperty
    private boolean doCreeperDamage = false;
    @JsonProperty
    private boolean doTntDamage = true;

    public GeneralExplosionDamageSettings() {

    }

    public boolean doCreeperDamage() {
        return doCreeperDamage;
    }

    public boolean doTntDamage() {
        return doTntDamage;
    }

    public void setDoCreeperDamage(boolean doCreeperDamage) {
        this.doCreeperDamage = doCreeperDamage;
    }

    public void setDoTntDamage(boolean doTntDamage) {
        this.doTntDamage = doTntDamage;
    }
}