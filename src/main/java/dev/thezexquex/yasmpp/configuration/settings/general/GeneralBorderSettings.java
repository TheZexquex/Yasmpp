package dev.thezexquex.yasmpp.configuration.settings.general;

public class GeneralBorderSettings {
    private int borderDiameterLobbyPhase = 20;
    private int borderDiameterGamePhase = 50000;

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
