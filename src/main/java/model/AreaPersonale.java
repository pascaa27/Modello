package model;

/**
 * La classe rappresenta lo spazio informativo
 * di un utente riguardo notifiche e aggiornamenti sui voli.
 *
 * <p>Ogni istanza contiene eventuali informazioni su:</p>
 * <ul>
 *     <li>Ritardi dei voli</li>
 *     <li>Cancellazioni dei voli</li>
 *     <li>Variazioni (modifiche di orario, gate, ecc.) dei voli</li>
 * </ul>
 *
 * <p>Inoltre, è collegata a un oggetto {@link Utente}, che rappresenta
 * il proprietario dell'area personale.</p>
 *
 * @see Utente
 */
public class AreaPersonale {

    private String ritardoVolo;
    private String cancellazioneVolo;
    private String variazioneVolo;
    private Utente utente;

    /**
     * Costruisce una nuova {@code AreaPersonale} con i dettagli forniti.
     *
     * @param ritardoVolo       descrizione di un ritardo del volo
     * @param cancellazioneVolo descrizione di una cancellazione del volo
     * @param variazioneVolo    descrizione di una variazione del volo
     * @param utente            utente proprietario dell'area personale
     */
    public AreaPersonale(String ritardoVolo, String cancellazioneVolo, String variazioneVolo, Utente utente) {
        this.ritardoVolo = ritardoVolo;
        this.cancellazioneVolo = cancellazioneVolo;
        this.variazioneVolo = variazioneVolo;
        this.utente = utente;
    }

    /**
     * Costruttore vuoto richiesto da framework o strumenti di binding
     * che necessitano di un'istanza inizializzata senza parametri.
     *
     * <p>Utile ad esempio per il caricamento statico di dati da controller.</p>
     */
    public AreaPersonale() {
        // vuoto, necessario per il framework nel controller
    }

    /**
     * Restituisce la descrizione del ritardo del volo.
     *
     * @return stringa che descrive il ritardo, oppure {@code null} se assente
     */
    public String getRitardoVolo() {
        return ritardoVolo;
    }

    /**
     * Restituisce la descrizione della cancellazione del volo.
     *
     * @return stringa che descrive la cancellazione, oppure {@code null} se assente
     */
    public String getCancellazioneVolo() {
        return cancellazioneVolo;
    }

    /**
     * Restituisce la descrizione della variazione del volo.
     *
     * @return stringa che descrive la variazione, oppure {@code null} se assente
     */
    public String getVariazioneVolo() {
        return variazioneVolo;
    }

    /**
     * Restituisce l'utente proprietario di questa area personale.
     *
     * @return l'oggetto {@link Utente} associato
     */
    public Utente getUtente() {
        return utente;
    }

    /**
     * Imposta l'utente proprietario di questa area personale.
     *
     * @param utente nuovo utente da associare
     */
    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    /**
     * Imposta la descrizione di un ritardo del volo.
     *
     * @param ritardoVolo stringa che descrive il ritardo
     */
    public void setRitardoVolo(String ritardoVolo) {
        this.ritardoVolo = ritardoVolo;
    }

    /**
     * Imposta la descrizione di una cancellazione del volo.
     *
     * @param cancellazioneVolo stringa che descrive la cancellazione
     */
    public void setCancellazioneVolo(String cancellazioneVolo) {
        this.cancellazioneVolo = cancellazioneVolo;
    }

    /**
     * Imposta la descrizione di una variazione del volo.
     *
     * @param variazioneVolo stringa che descrive la variazione
     */
    public void setVariazioneVolo(String variazioneVolo) {
        this.variazioneVolo = variazioneVolo;
    }
}