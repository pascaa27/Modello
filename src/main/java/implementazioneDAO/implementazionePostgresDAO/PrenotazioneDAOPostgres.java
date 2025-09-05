package implementazioneDAO.implementazionePostgresDAO;

import implementazioneDAO.PrenotazioneDAO;
import model.*;

import java.util.*;

public class PrenotazioneDAOPostgres implements PrenotazioneDAO {

    //override dei metodi presenti nell'interface PrenotazioneDAO (ancora da implementare)
    @Override
    public Prenotazione findByCodice(String codice) {
        return null;
    }

    @Override
    public List<Prenotazione> findAllByUtente(String emailUtente) {
        // ... implementazione
        return new ArrayList<>();
    }

    @Override
    public boolean insert(Prenotazione p) {
        // ... implementazione
        return false;
    }

    @Override
    public boolean update(Prenotazione p) {
        // ... implementazione
        return false;
    }

    @Override
    public boolean delete(String codicePrenotazione) {
        // ... implementazione
        return false;
    }
}