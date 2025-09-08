package implementazioneDAO;

import model.UtenteGenerico;

//interface contenuta nel package dao contenente metodi astratti
public interface UtenteGenericoDAO {
    UtenteGenerico findByEmail(String email);
    boolean insert(UtenteGenerico u);
    boolean update(UtenteGenerico u);
    boolean delete(String login);
}