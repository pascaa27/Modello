package dao;

import model.Amministratore;

/**
 * Interfaccia DAO per la gestione dell'entità Amministratore.
 * Definisce le operazioni di accesso ai dati per gli amministratori.
 */
public interface AmministratoreDAO {

    /**
     * Recupera un amministratore in base all'email.
     *
     * @param email l'email dell'amministratore da cercare
     * @return l'istanza di Amministratore se trovata; altrimenti null
     */
    Amministratore findByEmail(String email);
}