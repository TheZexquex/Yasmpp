package dev.thezexquex.yasmpp.core.configuration.settings.general;

public class GeneralExplosionDamageSettings {
    private boolean doCreeperDamage;
    private boolean doTntDamage;

    public GeneralExplosionDamageSettings(
            boolean doCreeperDamage,
            boolean doTntDamage
    ) {
        this.doCreeperDamage = doCreeperDamage;
        this.doTntDamage = doTntDamage;
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