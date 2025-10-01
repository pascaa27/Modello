package dao;

import model.UtenteGenerico;

/**
 * Interfaccia DAO per la gestione dell'entità UtenteGenerico.
 * Definisce le principali operazioni di accesso e modifica dei dati utente.
 */
public interface UtenteGenericoDAO {

    /**
     * Recupera un utente generico a partire dall'email (login).
     *
     * @param email email/login dell'utente da cercare
     * @return l'utente se trovato; altrimenti null
     */
    UtenteGenerico findByEmail(String email);

    /**
     * Inserisce un nuovo utente generico.
     *
     * @param u utente da inserire
     * @return true se l'inserimento ha avuto successo; false altrimenti
     */
    boolean insert(UtenteGenerico u);

    /**
     * Aggiorna i dati di un utente generico esistente.
     *
     * @param u utente con i dati aggiornati
     * @return true se l'aggiornamento ha avuto successo; false altrimenti
     */
    boolean update(UtenteGenerico u);

    /**
     * Elimina un utente generico identificato dal login (email).
     *
     * @param login identificativo univoco dell'utente (email)
     * @return true se almeno un record è stato eliminato; false altrimenti
     */
    boolean delete(String login);
}