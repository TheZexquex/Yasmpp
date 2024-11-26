package dev.thezexquex.yasmpp.configuration.settings.general;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GeneralBorderSettings {
    @JsonProperty
    private int borderDiameterLobbyPhase = 20;
    @JsonProperty
    private int borderDiameterGamePhase = 50000;

    public GeneralBorderSettings() {

    }

    public int borderDiameterLobbyPhase() {
        return borderDiameterLobbyPhase;
    }

    public int borderDiameterGamePhase() {
        return borderDiameterGamePhase;
    }

    public void setBorderDiameterLobbyPhase(int borderDiameterLobbyPhase) {
        this.borderDiameterLobbyPhase = borderDiameterLobbyPhase;
    }

    public void setBorderDiameterGamePhase(int borderDiameterGamePhase) {
        this.borderDiameterGamePhase = borderDiameterGamePhase;
    }
}
