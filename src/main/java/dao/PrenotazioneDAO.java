package dao;

import model.Prenotazione;
import model.UtenteGenerico;

import java.sql.SQLException;
import java.util.List;

/**
 * Interfaccia DAO per la gestione delle prenotazioni.
 * Definisce le operazioni di lettura e scrittura sui dati di prenotazione.
 */
public interface PrenotazioneDAO {

    /**
     * Trova una prenotazione a partire dal codice (numero biglietto).
     *
     * @param codice codice univoco del biglietto/prenotazione
     * @return la prenotazione se trovata; altrimenti null
     */
    Prenotazione findByCodice(String codice);

    /**
     * Restituisce tutte le prenotazioni associate a un utente identificato dall'email.
     *
     * @param emailUtente email dell'utente
     * @return lista di prenotazioni dell'utente (eventualmente vuota)
     */
    List<Prenotazione> findAllByUtente(String emailUtente);

    /**
     * Restituisce l'elenco completo di tutte le prenotazioni presenti.
     *
     * @return lista di tutte le prenotazioni (eventualmente vuota)
     */
    List<Prenotazione> findAll();

    /**
     * Restituisce le prenotazioni filtrate per email utente (ricerca per email).
     *
     * @param emailUtente email dell'utente
     * @return lista di prenotazioni trovate (eventualmente vuota)
     * @throws SQLException in caso di errori di accesso ai dati
     */
    List<Prenotazione> findByEmailUtente(String emailUtente) throws SQLException;

    /**
     * Inserisce una nuova prenotazione.
     *
     * @param p prenotazione da inserire
     * @param utente utente che effettua l'operazione (registrato o generico)
     * @return true se l'inserimento ha avuto successo; false altrimenti
     */
    boolean insert(Prenotazione p, UtenteGenerico utente);

    /**
     * Aggiorna una prenotazione esistente.
     *
     * @param p prenotazione con i dati aggiornati
     * @return true se l'aggiornamento ha avuto successo; false altrimenti
     */
    boolean update(Prenotazione p);

    /**
     * Elimina una prenotazione identificata dal codice biglietto.
     *
     * @param codicePrenotazione codice univoco del biglietto/prenotazione
     * @return true se almeno una riga è stata eliminata; false altrimenti
     */
    boolean delete(String codicePrenotazione);
}