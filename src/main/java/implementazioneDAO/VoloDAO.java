package implementazioneDAO;

import model.Volo;

//interface contenuta nel package dao contenente metodi astratti
public interface VoloDAO {
    Volo findById(int id);
}