package model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Rappresenta un utente di tipo amministratore all'interno del sistema.
 * <p>
 * L'amministratore estende {@link Utente} ed è responsabile della gestione
 * dei voli, compresa l'aggiunta, la modifica e l'assegnazione dei gate.
 * </p>
 *
 * @see Utente
 * @see Volo
 */
public class Amministratore extends Utente {

    private static final Logger LOGGER = Logger.getLogger(Amministratore.class.getName());

    private List<Volo> voliGestiti = new ArrayList<>();

    /**
     * Crea un nuovo oggetto {@link Amministratore}.
     *
     * @param login        credenziali di login dell'amministratore
     * @param password     password dell'amministratore
     * @param nomeUtente   nome dell'amministratore
     * @param cognomeUtente cognome dell'amministratore
     */
    public Amministratore(String login, String password, String nomeUtente, String cognomeUtente) {
        super(login, password, nomeUtente, cognomeUtente);
    }

    /**
     * Restituisce la lista dei voli gestiti dall'amministratore.
     *
     * @return lista dei voli gestiti
     */
    public List<Volo> getVoliGestiti() {
        return voliGestiti;
    }

    /**
     * Imposta la lista dei voli gestiti dall'amministratore.
     *
     * @param voliGestiti nuova lista di voli gestiti
     */
    public void setVoliGestiti(List<Volo> voliGestiti) {
        this.voliGestiti = voliGestiti;
    }

    /**
     * Registra un'attività di log che indica che l'amministratore ha gestito
     * un volo (inserimento o aggiornamento).
     * <p>
     * Non modifica realmente lo stato dei voli, ma segnala solo l'operazione
     * a livello di log.
     * </p>
     */
    public void gestioneVolo() {
        LOGGER.info(() -> "Volo inserito o aggiornato da: " + getNomeUtente());
    }

    /**
     * Registra un'attività di log che indica che l'amministratore ha modificato
     * l'assegnazione dei gate.
     * <p>
     * Anche in questo caso l'operazione è solo registrata a livello di log,
     * mentre l'implementazione effettiva della logica di modifica è
     * gestita altrove.
     * </p>
     */
    public void modificaAssegnazioneGate() {
        LOGGER.info(() -> "L'assegnazione dei gate è stata modificata da: " + getNomeUtente());
    }

    /**
     * Aggiunge un nuovo volo alla lista dei voli gestiti dall'amministratore
     * e imposta l'amministratore come responsabile del volo.
     *
     * @param volo il volo da aggiungere
     */
    public void aggiungiVolo(Volo volo) {
        voliGestiti.add(volo);
        volo.setAmministratore(this);
    }
}