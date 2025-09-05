package implementazioneDAO;

import model.DatiPasseggero;

//interface contenuta nel package dao contenente metodi astratti
public interface DatiPasseggeroDAO {
    DatiPasseggero findById(int id);
}