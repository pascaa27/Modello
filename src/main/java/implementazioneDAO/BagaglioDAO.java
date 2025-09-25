package implementazioneDAO;

import model.Bagaglio;
import java.util.List;

//interface contenuta nel package dao contenente metodi astratti
public interface BagaglioDAO {
    List<Bagaglio> findByPrenotazione(String numBiglietto);
    List<Bagaglio> findAll(); // NEW: caricare tutti i bagagli
    boolean insert(Bagaglio bagaglio);
    boolean update(Bagaglio bagaglio);
    boolean delete(String codUnivoco);
}