
package model;

public class VoloPartenzaBuilder {
    private String codiceUnivoco;
    private String compagniaAerea;
    private String dataVolo;
    private String orarioPrevisto;
    private StatoVolo stato;
    private Amministratore amministratore;
    private TabellaOrario tabellaOrario;
    private String aeroportoDestinazione;
    private String gate;

    public VoloPartenzaBuilder setCodiceUnivoco(String codiceUnivoco) {
        this.codiceUnivoco = codiceUnivoco;
        return this;
    }

    public VoloPartenzaBuilder setCompagniaAerea(String compagniaAerea) {
        this.compagniaAerea = compagniaAerea;
        return this;
    }

    public VoloPartenzaBuilder setDataVolo(String dataVolo) {
        this.dataVolo = dataVolo;
        return this;
    }

    public VoloPartenzaBuilder setOrarioPrevisto(String orarioPrevisto) {
        this.orarioPrevisto = orarioPrevisto;
        return this;
    }

    public VoloPartenzaBuilder setStato(StatoVolo stato) {
        this.stato = stato;
        return this;
    }

    public VoloPartenzaBuilder setAmministratore(Amministratore amministratore) {
        this.amministratore = amministratore;
        return this;
    }

    public VoloPartenzaBuilder setTabellaOrario(TabellaOrario tabellaOrario) {
        this.tabellaOrario = tabellaOrario;
        return this;
    }

    public VoloPartenzaBuilder setAeroportoDestinazione(String aeroportoDestinazione) {
        this.aeroportoDestinazione = aeroportoDestinazione;
        return this;
    }

    public VoloPartenzaBuilder setGate(String gate) {
        this.gate = gate;
        return this;
    }

    public VoloPartenza createVoloPartenza() {
        return new VoloPartenza(codiceUnivoco, compagniaAerea, dataVolo, orarioPrevisto, stato, amministratore, tabellaOrario, aeroportoDestinazione, gate);
    }
}
