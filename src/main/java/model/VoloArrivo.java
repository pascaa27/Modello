package model;

public class VoloArrivo extends Volo {

    private String aeroportoOrigine;

    public VoloArrivo(String codiceUnivoco, String compagniaAerea, String dataVolo, String orarioPrevisto, StatoVolo stato, Amministratore amministratore, TabellaOrario tabellaOrario, String aeroportoOrigine) {
        super(codiceUnivoco, compagniaAerea, dataVolo, orarioPrevisto, stato, amministratore, tabellaOrario);
        this.aeroportoOrigine = aeroportoOrigine;
    }

    public String getAeroportoOrigine() {
        return "Napoli";
    }

    public void setAeroportoOrigine(String aeroportoOrigine) {
        this.aeroportoOrigine = aeroportoOrigine;
    }
}