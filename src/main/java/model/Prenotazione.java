package model;

public class Prenotazione {
    private String numBiglietto;
    private String postoAssegnato;
    private StatoPrenotazione stato;
    private UtenteGenerico utenteGenerico;
    private DatiPasseggero datiPasseggero;

    public Prenotazione() {
        this.numBiglietto = numBiglietto;
        this.postoAssegnato = postoAssegnato;
        this.stato = stato;
        this.utenteGenerico = utenteGenerico;
        this.datiPasseggero = datiPasseggero;
    }

    public StatoPrenotazione getStato() {
        return stato;
    }

    public void setStato(StatoPrenotazione stato) {
        this.stato = stato;
    }

    public UtenteGenerico getUtenteGenerico() {
        return utenteGenerico;
    }

    public void setUtenteGenerico(UtenteGenerico utenteGenerico) {
        this.utenteGenerico = utenteGenerico;
    }

    public DatiPasseggero getDatiPasseggero() {
        return datiPasseggero;
    }

    public void setDatiPasseggero(DatiPasseggero datiPasseggero) {
        this.datiPasseggero = datiPasseggero;
    }

    public String getNumBiglietto() {
        return numBiglietto;
    }

    public void setNumBiglietto(String numBiglietto) {
        this.numBiglietto = numBiglietto;
    }

    public String getPostoAssegnato() {
        return postoAssegnato;
    }

    public void setPostoAssegnato(String postoAssegnato) {
        this.postoAssegnato = postoAssegnato;
    }
}