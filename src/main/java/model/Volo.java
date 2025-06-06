package model;

import java.util.ArrayList;
import java.util.List;

public class Volo {

    private String codiceUnivoco;
    private String compagniaAerea;
    private String dataVolo;
    private String orarioPrevisto;
    private StatoVolo stato;
    private Amministratore amministratore;
    private TabellaOrario tabellaOrario;
    private List<Prenotazione> prenotazioni = new ArrayList<>();

    public Volo(String codiceUnivoco, String compagniaAerea, String dataVolo, String orarioPrevisto, StatoVolo stato, Amministratore amministratore, TabellaOrario tabellaOrario) {
        this.codiceUnivoco = codiceUnivoco;
        this.compagniaAerea = compagniaAerea;
        this.dataVolo = dataVolo;
        this.orarioPrevisto = orarioPrevisto;
        this.stato = stato;
        this.amministratore = amministratore;
        this.tabellaOrario = tabellaOrario;
    }

    public List<Prenotazione> getPrenotazioni() {
        return prenotazioni;
    }

    public TabellaOrario getTabellaOrario() {
        return tabellaOrario;
    }

    public Amministratore getAmministratore() {
        return amministratore;
    }

    public String getCodiceUnivoco() {
        return codiceUnivoco;
    }

    public String getCompagniaAerea() {
        return compagniaAerea;
    }

    public String getDataVolo() {
        return dataVolo;
    }

    public String getOrarioPrevisto() {
        return orarioPrevisto;
    }

    public StatoVolo getStato() {
        return stato;
    }

    public void setPrenotazioni(List<Prenotazione> prenotazioni) {
        this.prenotazioni = prenotazioni;
    }

    public void setTabellaOrario(TabellaOrario tabellaOrario) {
        this.tabellaOrario = tabellaOrario;
    }

    public void setAmministratore(Amministratore amministratore) {
        this.amministratore = amministratore;
    }

    public void setCodiceUnivoco(String codiceUnivoco) {
        this.codiceUnivoco = codiceUnivoco;
    }

    public void setCompagniaAerea(String compagniaAerea) {
        this.compagniaAerea = compagniaAerea;
    }

    public void setDataVolo(String dataVolo) {
        this.dataVolo = dataVolo;
    }

    public void setOrarioPrevisto(String orarioPrevisto) {
        this.orarioPrevisto = orarioPrevisto;
    }

    public void setStato(StatoVolo stato) {
        this.stato = stato;
    }
}