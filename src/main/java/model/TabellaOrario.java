package model;

/**
 * La classe rappresenta una semplice entità
 * che memorizza gli orari di partenza e di arrivo associati a un volo
 * o a un collegamento.
 *
 * <p>
 * È utilizzata come modello di supporto all'interno del sistema
 * per rappresentare gli orari previsti e gestire eventuali variazioni.
 * </p>
 */
public class TabellaOrario {
    private String orarioPartenza;
    private String orarioArrivo;

    /**
     * Costruisce una nuova istanza di {@code TabellaOrario}
     * con gli orari di partenza e arrivo specificati.
     *
     * @param orarioPartenza orario di partenza del volo
     * @param orarioArrivo   orario di arrivo del volo
     */
    public TabellaOrario(String orarioPartenza, String orarioArrivo) {
        this.orarioPartenza = orarioPartenza;
        this.orarioArrivo = orarioArrivo;
    }

    /**
     * Costruttore vuoto richiesto per il binding automatico
     * o per utilizzo da parte di framework che necessitano
     * di istanziare la classe senza parametri.
     */
    public TabellaOrario() {
        // vuoto, necessario per il framework nel controller
    }

    /**
     * Restituisce l'orario di partenza.
     *
     * @return orario di partenza come stringa
     */
    public String getOrarioPartenza() {
        return orarioPartenza;
    }

    /**
     * Restituisce l'orario di arrivo.
     *
     * @return orario di arrivo come stringa
     */
    public String getOrarioArrivo() {
        return orarioArrivo;
    }

    /**
     * Imposta un nuovo orario di partenza.
     *
     * @param orarioPartenza orario di partenza da impostare
     */
    public void setOrarioPartenza(String orarioPartenza) {
        this.orarioPartenza = orarioPartenza;
    }

    /**
     * Imposta un nuovo orario di arrivo.
     *
     * @param orarioArrivo orario di arrivo da impostare
     */
    public void setOrarioArrivo(String orarioArrivo) {
        this.orarioArrivo = orarioArrivo;
    }
}