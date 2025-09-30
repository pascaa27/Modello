package dao;

import model.DatiPasseggero;

/**
 * DAO per l'entita' DatiPasseggero.
 */
public interface DatiPasseggeroDAO {

    /**
     * Trova un passeggero per email (case-insensitive lato DB).
     * @param email email normalizzata o grezza (la normalizzazione e' demandata all'implementazione)
     * @return il DatiPasseggero se presente, altrimenti null
     */
    DatiPasseggero findByEmail(String email);

    /**
     * Inserisce o registra un passeggero.
     * @param p dati passeggero
     * @return true se l'operazione ha avuto successo
     */
    boolean insert(DatiPasseggero p);

    /**
     * Aggiorna un passeggero esistente.
     * @param p dati passeggero
     * @return true se l'operazione ha avuto successo
     */
    boolean update(DatiPasseggero p);

    /**
     * Elimina un passeggero tramite email.
     * @param email email del passeggero
     * @return true se almeno una riga e' stata eliminata
     */
    boolean deleteByEmail(String email);

    /**
     * Metodo legacy. Usare {@link #findByEmail(String)}.
     *
     * @deprecated since 1.0, planned for removal in a future major release.
     *             Utilizzare findByEmail(String) come sostituto.
     */
    @Deprecated(since = "1.0", forRemoval = true)
    default DatiPasseggero findByCodiceFiscale(String codiceFiscale) {
        return null;
    }
}