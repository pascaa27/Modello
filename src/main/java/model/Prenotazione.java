package model;
import java.util.List;
import java.util.ArrayList;

/**
 * La classe rappresenta una prenotazione effettuata da un passeggero
 * per un volo specifico.
 * <p>
 * Ogni prenotazione contiene informazioni relative al biglietto, al posto assegnato,
 * allo stato della prenotazione (attiva, cancellata, in attesa, ecc.), ai dati del passeggero
 * e all'utente che l'ha effettuata. Inoltre, può contenere una lista di {@link Bagaglio}
 * associati e il riferimento al {@link Volo} prenotato.
 * </p>
 *
 * @see Bagaglio
 * @see Volo
 * @see StatoPrenotazione
 * @see UtenteGenerico
 * @see DatiPasseggero
 */
public class Prenotazione {
    private String numBiglietto;
    private String postoAssegnato;
    private StatoPrenotazione stato;
    private UtenteGenerico utenteGenerico;
    private DatiPasseggero datiPasseggero;
    private List<Bagaglio> bagagli = new ArrayList<>();
    private Volo volo;

    /**
     * Costruttore completo della classe {@code Prenotazione}.
     *
     * @param numBiglietto     numero univoco del biglietto
     * @param postoAssegnato   posto assegnato al passeggero
     * @param stato            stato della prenotazione
     * @param utenteGenerico   utente che ha effettuato la prenotazione
     * @param datiPasseggero   dati anagrafici del passeggero
     * @param volo             volo prenotato
     */
    public Prenotazione(String numBiglietto, String postoAssegnato, StatoPrenotazione stato, UtenteGenerico utenteGenerico, DatiPasseggero datiPasseggero, Volo volo) {
        this.numBiglietto = numBiglietto;
        this.postoAssegnato = postoAssegnato;
        this.stato = stato;
        this.utenteGenerico = utenteGenerico;
        this.datiPasseggero = datiPasseggero;
        this.volo = volo;
    }

    /**
     * Costruttore vuoto, necessario per il binding automatico o per framework
     * di persistenza/serializzazione.
     */
    public Prenotazione() {
        // vuoto, necessario per il framework nel controller
    }

    /**
     * Costruttore semplificato, utile per DAO e mapping rapido.
     *
     * @param numBiglietto    numero univoco del biglietto
     * @param postoAssegnato  posto assegnato al passeggero
     * @param stato           stato della prenotazione
     * @param datiPasseggero  dati anagrafici del passeggero
     * @param volo            volo prenotato
     */
    public Prenotazione(String numBiglietto, String postoAssegnato,
                        StatoPrenotazione stato, DatiPasseggero datiPasseggero, Volo volo) {
        this.numBiglietto = numBiglietto;
        this.postoAssegnato = postoAssegnato;
        this.stato = stato;
        this.datiPasseggero = datiPasseggero;
        this.volo = volo;
    }

    /**
     * Restituisce il volo associato alla prenotazione.
     *
     * @return il volo prenotato
     */
    public Volo getVolo() {
        return volo;
    }

    /**
     * Imposta il volo associato alla prenotazione.
     *
     * @param volo nuovo volo prenotato
     */
    public void setVolo(Volo volo) {
        this.volo = volo;
    }

    /**
     * Restituisce lo stato attuale della prenotazione.
     *
     * @return stato della prenotazione
     */
    public StatoPrenotazione getStato() {
        return stato;
    }

    /**
     * Aggiorna lo stato della prenotazione.
     *
     * @param stato nuovo stato della prenotazione
     */
    public void setStato(StatoPrenotazione stato) {
        this.stato = stato;
    }

    /**
     * Restituisce l'utente che ha effettuato la prenotazione.
     *
     * @return utente generico associato
     */
    public UtenteGenerico getUtenteGenerico() {
        return utenteGenerico;
    }

    /**
     * Imposta l'utente che ha effettuato la prenotazione.
     *
     * @param utenteGenerico nuovo utente associato
     */
    public void setUtenteGenerico(UtenteGenerico utenteGenerico) {
        this.utenteGenerico = utenteGenerico;
    }

    /**
     * Restituisce i dati anagrafici del passeggero.
     *
     * @return dati del passeggero
     */
    public DatiPasseggero getDatiPasseggero() {
        return datiPasseggero;
    }

    /**
     * Imposta i dati anagrafici del passeggero.
     *
     * @param datiPasseggero nuovi dati passeggero
     */
    public void setDatiPasseggero(DatiPasseggero datiPasseggero) {
        this.datiPasseggero = datiPasseggero;
    }

    /**
     * Restituisce il numero identificativo del biglietto.
     *
     * @return numero del biglietto
     */
    public String getNumBiglietto() {
        return numBiglietto;
    }

    /**
     * Imposta il numero identificativo del biglietto.
     *
     * @param numBiglietto nuovo numero biglietto
     */
    public void setNumBiglietto(String numBiglietto) {
        this.numBiglietto = numBiglietto;
    }

    /**
     * Restituisce il posto assegnato al passeggero.
     *
     * @return posto assegnato
     */
    public String getPostoAssegnato() {
        return postoAssegnato;
    }

    /**
     * Aggiorna il posto assegnato al passeggero.
     *
     * @param postoAssegnato nuovo posto assegnato
     */
    public void setPostoAssegnato(String postoAssegnato) {
        this.postoAssegnato = postoAssegnato;
    }

    /**
     * Restituisce la lista dei bagagli associati alla prenotazione.
     *
     * @return lista di bagagli
     */
    public List<Bagaglio> getBagagli() {
        return bagagli;
    }

    /**
     * Aggiorna la lista dei bagagli associati alla prenotazione.
     *
     * @param bagagli nuova lista di bagagli
     */
    public void setBagagli(List<Bagaglio> bagagli) {
        this.bagagli = bagagli;
    }
}