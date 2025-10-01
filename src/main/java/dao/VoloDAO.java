package dao;

import model.Volo;
import java.util.List;

/**
 * Interfaccia DAO per la gestione dell'entità Volo.
 * Definisce le principali operazioni di accesso e modifica dei dati relativi ai voli.
 */
public interface VoloDAO {

    /**
     * Recupera un volo a partire dal suo codice univoco.
     *
     * @param codiceUnivoco codice univoco del volo
     * @return l'istanza di Volo se trovata; altrimenti null
     */
    Volo findByCodiceUnivoco(String codiceUnivoco);

    /**
     * Restituisce l'elenco completo dei voli.
     *
     * @return lista di tutti i voli (eventualmente vuota)
     */
    List<Volo> findAll();

    /**
     * Inserisce un nuovo volo.
     *
     * @param v istanza di Volo da inserire
     * @return true se l'inserimento ha avuto successo; false altrimenti
     */
    boolean insert(Volo v);

    /**
     * Aggiorna i dati di un volo esistente.
     *
     * @param v istanza di Volo con i nuovi valori
     * @return true se l'aggiornamento ha avuto successo; false altrimenti
     */
    boolean update(Volo v);

    /**
     * Elimina un volo identificato dal suo codice univoco.
     *
     * @param codiceUnivoco codice univoco del volo da eliminare
     * @return true se almeno un record è stato eliminato; false altrimenti
     */
    boolean delete(String codiceUnivoco);

    /**
     * Conta il numero totale di voli presenti.
     *
     * @return numero di record; valore negativo in caso di errore a discrezione dell'implementazione
     */
    long count();
}