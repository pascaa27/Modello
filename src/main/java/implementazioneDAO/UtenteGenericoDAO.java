package implementazioneDAO;

import model.UtenteGenerico;

//interface contenuta nel package dao contenente metodi astratti
public interface UtenteGenericoDAO {
    UtenteGenerico findByEmail(String email);
}