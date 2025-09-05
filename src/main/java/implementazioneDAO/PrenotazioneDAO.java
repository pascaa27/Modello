package implementazioneDAO;

import model.Prenotazione;
import java.util.List;

//interface contenuta nel package dao contenente metodi astratti
public interface PrenotazioneDAO {
    Prenotazione findByCodice(String codice);
    List<Prenotazione> findAllByUtente(String emailUtente);
    boolean insert(Prenotazione p);
    boolean update(Prenotazione p);
    boolean delete(String codicePrenotazione);
}