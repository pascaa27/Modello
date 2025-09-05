package implementazioneDAO.implementazionePostgresDAO;
import implementazioneDAO.UtenteGenericoDAO;
import model.UtenteGenerico;

public class UtenteGenericoDAOPostgres implements UtenteGenericoDAO {
    //override del metodo presente nell'interface UtenteGenericoDAO (ancora da implementare)
    @Override
    public UtenteGenerico findByEmail(String email) {
        // Implementazione fittizia per ora
        return null;
    }
}