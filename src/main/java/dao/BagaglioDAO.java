package dao;

import model.Bagaglio;
import java.util.List;

/**
 * Interfaccia DAO per la gestione dell'entità Bagaglio.
 * Definisce le operazioni di accesso ai dati e le principali operazioni CRUD.
 */
public interface BagaglioDAO {

    /**
     * Restituisce tutti i bagagli associati a una specifica prenotazione.
     *
     * @param numBiglietto codice univoco del biglietto/prenotazione
     * @return lista dei bagagli trovati per la prenotazione indicata (eventualmente vuota)
     */
    List<Bagaglio> findByPrenotazione(String numBiglietto);

    /**
     * Restituisce l'elenco completo di tutti i bagagli presenti.
     *
     * @return lista di tutti i bagagli (eventualmente vuota)
     */
    List<Bagaglio> findAll();

    /**
     * Inserisce un nuovo bagaglio.
     *
     * @param bagaglio istanza di Bagaglio da inserire
     * @return true se l'inserimento ha avuto successo; false altrimenti
     */
    boolean insert(Bagaglio bagaglio);

    /**
     * Aggiorna un bagaglio esistente.
     *
     * @param bagaglio istanza di Bagaglio con i nuovi dati
     * @return true se l'aggiornamento ha avuto successo; false altrimenti
     */
    boolean update(Bagaglio bagaglio);

    /**
     * Elimina un bagaglio identificato dal suo codice univoco.
     *
     * @param codUnivoco codice univoco del bagaglio da eliminare
     * @return true se almeno un record è stato eliminato; false altrimenti
     */
    boolean delete(String codUnivoco);
}