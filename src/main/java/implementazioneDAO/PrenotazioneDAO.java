package implementazioneDAO;

import model.Prenotazione;

import java.sql.SQLException;
import java.util.List;

public interface PrenotazioneDAO {
    // Trova una prenotazione per numero biglietto
    Prenotazione findByCodice(String codice);

    // Tutte le prenotazioni di un utente (per email)
    List<Prenotazione> findAllByUtente(String emailUtente);

    // Tutte le prenotazioni
    List<Prenotazione> findAll();

    List<Prenotazione> findByEmailUtente(String emailUtente) throws SQLException;


    // Scrittura
    boolean insert(Prenotazione p);
    boolean update(Prenotazione p);
    boolean delete(String codicePrenotazione);
}