package model;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Rappresenta un utente generico del sistema aeroportuale.
 * <p>
 * Gli utenti generici possono effettuare prenotazioni, avere un'area personale
 * e gestire codici di prenotazioni. Questa classe estende {@link Utente}
 * aggiungendo funzionalità specifiche per gli utenti non amministratori.
 * </p>
 */
public class UtenteGenerico extends Utente {

    private static final Logger LOGGER = Logger.getLogger(UtenteGenerico.class.getName());

    private String nomeUtente;
    private List<Prenotazione> prenotazioni = new ArrayList<>();     // tutte le prenotazioni fatte da questo utente
    private AreaPersonale areaPersonale;
    private String ultimoCodicePrenotazione;
    private List<String> codiciPrenotazioni = new ArrayList<>();

    /**
     * Costruttore principale che inizializza un utente generico con tutte le informazioni.
     *
     * @param login login dell'utente
     * @param password password dell'utente
     * @param nomeUtente nome dell'utente
     * @param cognomeUtente cognome dell'utente
     * @param prenotazioni lista iniziale delle prenotazioni
     * @param areaPersonale area personale associata all'utente
     */
    public UtenteGenerico(String login, String password, String nomeUtente, String cognomeUtente,
                          List<Prenotazione> prenotazioni, AreaPersonale areaPersonale) {
        super(login, password, nomeUtente, cognomeUtente);
        this.nomeUtente = nomeUtente;
        this.prenotazioni = prenotazioni;
        this.areaPersonale = areaPersonale;
    }

    /**
     * Costruttore vuoto richiesto per il binding automatico
     * (ad esempio nei DAO o framework che necessitano di istanziare l'oggetto).
     */
    public UtenteGenerico() {
        // vuoto, necessario per il framework nel controller
    }

    /**
     * Restituisce il nome dell'utente.
     *
     * @return nome dell'utente
     */
    @Override
    public String getNomeUtente() {
        return nomeUtente;
    }

    /**
     * Imposta il nome dell'utente.
     *
     * @param nomeUtente nuovo nome da assegnare
     */
    @Override
    public void setNomeUtente(String nomeUtente) {
        this.nomeUtente = nomeUtente;
    }

    /**
     * Restituisce la lista delle prenotazioni dell'utente.
     *
     * @return lista delle prenotazioni
     */
    public List<Prenotazione> getPrenotazioni() {
        return prenotazioni;
    }

    /**
     * Imposta la lista delle prenotazioni dell'utente.
     *
     * @param prenotazioni nuova lista di prenotazioni
     */
    public void setPrenotazioni(List<Prenotazione> prenotazioni) {
        this.prenotazioni = prenotazioni;
    }

    /**
     * Simula l'azione di prenotazione di un volo da parte dell'utente.
     * L'azione viene tracciata nei log di sistema.
     */
    public void prenotaVolo() {
        LOGGER.info(() -> "Prenotazione effettuata da " + login);
    }

    /**
     * Restituisce l'area personale dell'utente.
     *
     * @return area personale associata all'utente
     */
    public AreaPersonale getAreaPersonale() {
        return areaPersonale;
    }

    /**
     * Imposta l'area personale dell'utente.
     *
     * @param areaPersonale nuova area personale da assegnare
     */
    public void setAreaPersonale(AreaPersonale areaPersonale) {
        this.areaPersonale = areaPersonale;
    }

    /**
     * Restituisce il codice dell'ultima prenotazione effettuata.
     *
     * @return codice dell'ultima prenotazione
     */
    public String getUltimoCodicePrenotazione() {
        return ultimoCodicePrenotazione;
    }

    /**
     * Imposta il codice dell'ultima prenotazione effettuata.
     *
     * @param ultimoCodicePrenotazione nuovo codice da assegnare
     */
    public void setUltimoCodicePrenotazione(String ultimoCodicePrenotazione) {
        this.ultimoCodicePrenotazione = ultimoCodicePrenotazione;
    }

    /**
     * Restituisce la lista di tutti i codici di prenotazione dell'utente.
     *
     * @return lista dei codici prenotazioni
     */
    public List<String> getCodiciPrenotazioni() {
        return codiciPrenotazioni;
    }

    /**
     * Verifica se l'utente è registrato, ovvero se login e password sono presenti.
     *
     * @return true se l'utente è registrato, false altrimenti
     */
    public boolean isRegistrato() {
        return getLogin() != null && !getLogin().isBlank() &&
                getPassword() != null && !getPassword().isBlank();
    }

    /**
     * Aggiunge una prenotazione alla lista dell'utente.
     *
     * @param p prenotazione da aggiungere
     */
    public void aggiungiPrenotazione(Prenotazione p) {
        prenotazioni.add(p);
    }

    /**
     * Aggiunge un codice di prenotazione alla lista dei codici dell'utente,
     * evitando duplicati o valori vuoti/nulli.
     *
     * @param codice codice di prenotazione da aggiungere
     */
    public void aggiungiCodicePrenotazione(String codice) {
        if(codice != null && !codice.isBlank() && !codiciPrenotazioni.contains(codice)) {
            codiciPrenotazioni.add(codice);
        }
    }
}