package model;

import java.util.ArrayList;
import java.util.List;

/**
 * La classe rappresenta un gate di imbarco all'interno di un aeroporto.
 * <p>
 * Ogni gate è identificato da un numero univoco e mantiene un elenco
 * dei voli in partenza che gli sono stati assegnati.
 * </p>
 *
 * @see VoloPartenza
 */
public class Gate {

    private int numero;
    private List<VoloPartenza> voliPartenza = new ArrayList<>();

    /**
     * Costruisce un nuovo gate con un numero identificativo specifico.
     *
     * @param numero numero univoco che identifica il gate
     */
    public Gate(int numero) {
        this.numero = numero;
    }

    /**
     * Restituisce la lista dei voli in partenza associati a questo gate.
     *
     * @return lista di oggetti {@link VoloPartenza}
     */
    public List<VoloPartenza> getVoliPartenza() {
        return voliPartenza;
    }

    /**
     * Restituisce il numero identificativo del gate.
     *
     * @return numero del gate
     */
    public int getNumero() {
        return numero;
    }

    /**
     * Imposta la lista dei voli in partenza associati al gate.
     *
     * @param voliPartenza nuova lista di voli in partenza da associare
     */
    public void setVoliPartenza(List<VoloPartenza> voliPartenza) {
        this.voliPartenza = voliPartenza;
    }

    /**
     * Modifica il numero identificativo del gate.
     *
     * @param numero nuovo numero del gate
     */
    public void setNumero(int numero) {
        this.numero = numero;
    }

    /**
     * Aggiunge un nuovo volo in partenza alla lista dei voli associati al gate.
     *
     * @param voloPartenza volo da aggiungere alla lista
     */
    public void aggiungiVoloPartenza(VoloPartenza voloPartenza) {
        voliPartenza.add(voloPartenza);
    }
}