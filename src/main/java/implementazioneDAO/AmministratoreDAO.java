package implementazioneDAO;

import model.Amministratore;

//interface contenuta nel package dao contenente metodi astratti
public interface AmministratoreDAO {
    Amministratore findByEmail(String email);
}