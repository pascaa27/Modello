package implementazioneDAO;

import model.Bagaglio;
import java.util.List;

//interface contenuta nel package dao contenente metodi astratti
public interface BagaglioDAO {
    List<Bagaglio> findByPrenotazione(String numBiglietto);
    boolean insert(Bagaglio bagaglio);
    boolean update(Bagaglio bagaglio);
    boolean delete(String codUnivoco);
}