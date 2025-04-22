package model;

public class VoloPartenza extends Volo {

    private String aeroportoDestinazione;
    private Gate gate;

    public VoloPartenza(String codiceUnivoco, String compagniaAerea, String dataVolo, String orarioPrevisto, StatoVolo stato, Amministratore amministratore) {
        super(codiceUnivoco, compagniaAerea, dataVolo, orarioPrevisto, stato, amministratore);
    }

    public String getAeroportoDestinazione() {
        return "Napoli";
    }

    public Gate getGate() {
        return gate;
    }

    public void setAeroportoDestinazione(String aeroportoDestinazione) {
        this.aeroportoDestinazione = aeroportoDestinazione;
    }

    public void setGate(Gate gate) {
        this.gate = gate;
    }
}