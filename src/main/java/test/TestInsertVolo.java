package test;

import implementazioneDAO.implementazionePostgresDAO.VoloDAOPostgres;
import model.StatoVolo;
import model.Volo;

public class TestInsertVolo {
    public static void main(String[] args) {

        Volo volo = new Volo();
        volo.setCodiceUnivoco("AZ123");
        volo.setCompagniaAerea("ITA Airways");
        volo.setDataVolo("2025-09-15"); // tipo DATE nel db
        volo.setOrarioPrevisto("14:30"); // tipo TIME nel db
        volo.setStato(StatoVolo.INRITARDO);
        volo.setAeroporto("Fiumicino");
        volo.setGate("A12");
        volo.setArrivoPartenza("PARTENZA");

        VoloDAOPostgres voloDAO = new VoloDAOPostgres();
        boolean success = voloDAO.insert(volo);

        if(success) {
            System.out.println("Volo inserito correttamente!");
        } else {
            System.out.println("Errore nell'inserimento del volo.");
        }
    }
}