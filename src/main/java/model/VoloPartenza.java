package model;

public class VoloPartenza extends Volo {

    private String aeroportoDestinazione;
    private String gate;

    public VoloPartenza(String codiceUnivoco, String compagniaAerea, String dataVolo, String orarioPrevisto, StatoVolo stato, Amministratore amministratore, TabellaOrario tabellaOrario, String aeroportoDestinazione, String gate) {
        super(codiceUnivoco, compagniaAerea, dataVolo, orarioPrevisto, stato, amministratore, tabellaOrario);
        this.aeroportoDestinazione = aeroportoDestinazione;
        this.gate = gate;
    }

    public String getAeroportoDestinazione() {
        return "Napoli";
    }

    public String getGate() {
        return gate;
    }

    public void setAeroportoDestinazione(String aeroportoDestinazione) {
        this.aeroportoDestinazione = aeroportoDestinazione;
    }

    public void setGate(String gate) {
        this.gate = gate;
    }
}