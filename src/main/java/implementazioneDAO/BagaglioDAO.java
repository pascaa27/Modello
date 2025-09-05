package implementazioneDAO;

import model.Bagaglio;
import java.util.List;

//interface contenuta nel package dao contenente metodi astratti
public interface BagaglioDAO {
    List<Bagaglio> findByPrenotazione(String numBiglietto);
}