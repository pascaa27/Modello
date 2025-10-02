package model;

import java.util.logging.Logger;

/**
 * Rappresenta un utente del sistema aeroportuale.
 * <p>
 * La classe contiene le informazioni principali legate a un utente,
 * come credenziali di accesso, nome e cognome.
 * </p>
 */
public class Utente {

    private static final Logger LOGGER = Logger.getLogger(Utente.class.getName());

    protected String login;
    private String password;
    private String nomeUtente;
    private String cognomeUtente;

    /**
     * Costruttore principale che inizializza un utente con tutte le informazioni necessarie.
     *
     * @param login identificativo di login dell'utente
     * @param password password dell'utente
     * @param nomeUtente nome dell'utente
     * @param cognomeUtente cognome dell'utente
     */
    public Utente(String login, String password, String nomeUtente, String cognomeUtente) {
        this.login = login;
        this.password = password;
        this.nomeUtente = nomeUtente;
        this.cognomeUtente = cognomeUtente;
    }

    /**
     * Costruttore vuoto richiesto per il binding automatico
     * (ad esempio nei controller o nei framework che necessitano di istanziare l'oggetto).
     */
    public Utente() {
        // vuoto, necessario per il framework nel controller
    }

    /**
     * Restituisce il login dell'utente.
     *
     * @return login dell'utente
     */
    public String getLogin() {
        return login;
    }

    /**
     * Restituisce la password dell'utente.
     *
     * @return password dell'utente
     */
    public String getPassword() {
        return password;
    }

    /**
     * Imposta il login dell'utente.
     *
     * @param login nuovo login da assegnare
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Imposta la password dell'utente.
     *
     * @param password nuova password da assegnare
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Restituisce il nome dell'utente.
     *
     * @return nome dell'utente
     */
    public String getNomeUtente() {
        return nomeUtente;
    }

    /**
     * Imposta il nome dell'utente.
     *
     * @param nomeUtente nuovo nome da assegnare
     */
    public void setNomeUtente(String nomeUtente) {
        this.nomeUtente = nomeUtente;
    }

    /**
     * Restituisce il cognome dell'utente.
     *
     * @return cognome dell'utente
     */
    public String getCognomeUtente() {
        return cognomeUtente;
    }

    /**
     * Imposta il cognome dell'utente.
     *
     * @param cognomeUtente nuovo cognome da assegnare
     */
    public void setCognomeUtente(String cognomeUtente) {
        this.cognomeUtente = cognomeUtente;
    }

    /**
     * Simula l'operazione di modifica di una prenotazione da parte dell'utente.
     * L'azione viene tracciata nei log di sistema.
     */
    public void modificaPrenotazione() {
        LOGGER.info("Modifica prenotazione");
    }

    /**
     * Simula la visualizzazione degli aggiornamenti relativi ai voli da parte dell'utente.
     * L'azione viene tracciata nei log di sistema.
     */
    public void visualizzaAggiornamentoVolo() {
        LOGGER.info("Visualizza aggiornamenti volo");
    }
}