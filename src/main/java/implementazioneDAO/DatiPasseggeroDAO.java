package implementazioneDAO;

import model.DatiPasseggero;

//interface contenuta nel package dao contenente metodi astratti
public interface DatiPasseggeroDAO {
    DatiPasseggero findByCodiceFiscale(String codiceFiscale);
    boolean insert(DatiPasseggero passeggero);
    boolean update(DatiPasseggero passeggero);
    boolean delete(String codiceFiscale);
}