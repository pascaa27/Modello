package model;

public class VoloPartenza extends Volo {

    private String aeroportoDestinazione;

    public VoloPartenza(String codiceUnivoco, String compagniaAerea, String dataVolo, String orarioPrevisto, StatoVolo stato, Amministratore amministratore) {
        super(codiceUnivoco, compagniaAerea, dataVolo, orarioPrevisto, stato, amministratore);
    }

    public String getAeroportoDestinazione() {
        return "Napoli";
    }

    public void setAeroportoDestinazione(String aeroportoDestinazione) {
        this.aeroportoDestinazione = aeroportoDestinazione;
    }
}