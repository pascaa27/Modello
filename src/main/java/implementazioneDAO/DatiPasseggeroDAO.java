package implementazioneDAO;

import model.DatiPasseggero;

public interface DatiPasseggeroDAO {
    DatiPasseggero findByEmail(String email);
    boolean insert(DatiPasseggero p);
    boolean update(DatiPasseggero p);
    boolean deleteByEmail(String email);

    @Deprecated
    default DatiPasseggero findByCodiceFiscale(String codiceFiscale) { return null; }
}