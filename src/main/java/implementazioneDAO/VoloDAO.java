package implementazioneDAO;

import model.Volo;

//interface contenuta nel package dao contenente metodi astratti
public interface VoloDAO {
    Volo findByCodiceUnivoco(String codiceUnivoco);
    boolean insert(Volo v);
    boolean update(Volo v);
    boolean delete(String codiceUnivoco);
}