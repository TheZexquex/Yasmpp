package dev.thezexquex.yasmpp.core.configuration.settings.general;

import java.util.Objects;

public class GeneralBorderSettings {
    private int borderDiameterLobbyPhase;
    private int borderDiameterGamePhase;

    public GeneralBorderSettings(
            int borderDiameterLobbyPhase,
            int borderDiameterGamePhase
    ) {
        this.borderDiameterLobbyPhase = borderDiameterLobbyPhase;
        this.borderDiameterGamePhase = borderDiameterGamePhase;
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
