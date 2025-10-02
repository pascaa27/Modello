package model;

/**
 * La classe rappresenta un bagaglio associato ad una
 * prenotazione di volo. Ogni bagaglio è identificato da un codice univoco,
 * ha un peso in chilogrammi, uno stato corrente e può essere collegato a
 * una {@link Prenotazione}.
 *
 * <p>
 * Lo stato del bagaglio è gestito tramite l'enum {@link StatoBagaglio}, che
 * descrive fasi come check-in, imbarco, consegna, ecc.
 * </p>
 *
 * @see StatoBagaglio
 * @see Prenotazione
 */
public class Bagaglio {
    private final String codUnivoco;
    private StatoBagaglio stato;
    private Prenotazione prenotazione;
    private Double pesoKg;

    /**
     * Costruisce un nuovo {@code Bagaglio} con i dettagli forniti.
     *
     * @param codUnivoco   codice univoco che identifica il bagaglio
     * @param pesoKg       peso del bagaglio in chilogrammi
     * @param stato        stato attuale del bagaglio
     * @param prenotazione prenotazione a cui è associato il bagaglio
     */
    public Bagaglio(String codUnivoco, double pesoKg, StatoBagaglio stato, Prenotazione prenotazione) {
        this.codUnivoco = codUnivoco;
        this.pesoKg = pesoKg;
        this.stato = stato;
        this.prenotazione = prenotazione;
    }

    /**
     * Restituisce il codice univoco del bagaglio.
     *
     * @return stringa che rappresenta il codice univoco
     */
    public String getCodUnivoco() {
        return codUnivoco;
    }

    /**
     * Restituisce lo stato attuale del bagaglio.
     *
     * @return l'enum {@link StatoBagaglio} che descrive lo stato
     */
    public StatoBagaglio getStato() {
        return stato;
    }

    /**
     * Imposta un nuovo stato per il bagaglio.
     *
     * @param stato nuovo stato da associare
     */
    public void setStato(StatoBagaglio stato) {
        this.stato = stato;
    }

    /**
     * Restituisce la prenotazione associata al bagaglio.
     *
     * @return oggetto {@link Prenotazione} associato, oppure {@code null} se non presente
     */
    public Prenotazione getPrenotazione() {
        return prenotazione;
    }

    /**
     * Associa una prenotazione al bagaglio.
     *
     * @param prenotazione la prenotazione da collegare
     */
    public void setPrenotazione(Prenotazione prenotazione) {
        this.prenotazione = prenotazione;
    }

    /**
     * Restituisce il peso del bagaglio in chilogrammi.
     *
     * @return peso espresso come {@link Double}, oppure {@code null} se non impostato
     */
    public Double getPesoKg() {
        return pesoKg;
    }

    /**
     * Imposta il peso del bagaglio in chilogrammi.
     *
     * @param pesoKg nuovo peso da assegnare
     */
    public void setPesoKg(Double pesoKg) {
        this.pesoKg = pesoKg;
    }

    /**
     * Restituisce una rappresentazione testuale del bagaglio, contenente:
     * <ul>
     *     <li>Codice univoco</li>
     *     <li>Stato</li>
     *     <li>Numero biglietto della prenotazione (se presente)</li>
     * </ul>
     *
     * @return stringa descrittiva del bagaglio
     */
    @Override
    public String toString() {
        return "Bagaglio{" + "codUnivoco='" + codUnivoco + '\'' + ", stato=" + stato +
                (prenotazione != null ? ", prenotazione=" + prenotazione.getNumBiglietto() : "") +
                '}';
    }
}