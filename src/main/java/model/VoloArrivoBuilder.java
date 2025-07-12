package model;

public class VoloArrivoBuilder {
    private String codiceUnivoco;
    private String compagniaAerea;
    private String dataVolo;
    private String orarioPrevisto;
    private StatoVolo stato;
    private Amministratore amministratore;
    private TabellaOrario tabellaOrario;
    private String aeroportoOrigine;

    public VoloArrivoBuilder setCodiceUnivoco(String codiceUnivoco) {
        this.codiceUnivoco = codiceUnivoco;
        return this;
    }

    public VoloArrivoBuilder setCompagniaAerea(String compagniaAerea) {
        this.compagniaAerea = compagniaAerea;
        return this;
    }

    public VoloArrivoBuilder setDataVolo(String dataVolo) {
        this.dataVolo = dataVolo;
        return this;
    }

    public VoloArrivoBuilder setOrarioPrevisto(String orarioPrevisto) {
        this.orarioPrevisto = orarioPrevisto;
        return this;
    }

    public VoloArrivoBuilder setStato(StatoVolo stato) {
        this.stato = stato;
        return this;
    }

    public VoloArrivoBuilder setAmministratore(Amministratore amministratore) {
        this.amministratore = amministratore;
        return this;
    }

    public VoloArrivoBuilder setTabellaOrario(TabellaOrario tabellaOrario) {
        this.tabellaOrario = tabellaOrario;
        return this;
    }

    public VoloArrivoBuilder setAeroportoOrigine(String aeroportoOrigine) {
        this.aeroportoOrigine = aeroportoOrigine;
        return this;
    }

    public VoloArrivo createVoloArrivo() {
        return new VoloArrivo(codiceUnivoco, compagniaAerea, dataVolo, orarioPrevisto, stato, amministratore, tabellaOrario, aeroportoOrigine);
    }
}