package dev.thezexquex.yasmpp.configuration.settings.general;

public class GeneralExplosionDamageSettings {
    private boolean doCreeperDamage = false;
    private boolean doTntDamage = true;

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