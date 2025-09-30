package model;

import java.util.logging.Logger;

public class Utente {

    private static final Logger LOGGER = Logger.getLogger(Utente.class.getName());

    protected String login;
    private String password;
    private String nomeUtente;
    private String cognomeUtente;

    public Utente(String login, String password, String nomeUtente, String cognomeUtente) {
        this.login = login;
        this.password = password;
        this.nomeUtente = nomeUtente;
        this.cognomeUtente = cognomeUtente;
    }

    // costruttore vuoto per il binding automatico (anche nella classe padre (Utente))
    public Utente() {
        // vuoto, necessario per il framework nel controller
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNomeUtente() {
        return nomeUtente;
    }

    public void setNomeUtente(String nomeUtente) {
        this.nomeUtente = nomeUtente;
    }

    public String getCognomeUtente() {
        return cognomeUtente;
    }

    public void setCognomeUtente(String cognomeUtente) {
        this.cognomeUtente = cognomeUtente;
    }

    public void modificaPrenotazione() {
        LOGGER.info("Modifica prenotazione");
    }

    public void visualizzaAggiornamentoVolo() {
        LOGGER.info("Visualizza aggiornamenti volo");
    }
}