package model;
import java.util.List;
import java.util.ArrayList;

public class Prenotazione {
    private String numBiglietto;
    private String postoAssegnato;
    private StatoPrenotazione stato;
    private UtenteGenerico utenteGenerico;
    private DatiPasseggero datiPasseggero;
    private List<Bagaglio> bagagli = new ArrayList<>();

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

    public List<Bagaglio> getBagagli() {
        return bagagli;
    }

    public void setBagagli(List<Bagaglio> bagagli) {
        this.bagagli = bagagli;
    }
}