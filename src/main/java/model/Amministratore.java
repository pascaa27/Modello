package model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Amministratore extends Utente {

    private static final Logger LOGGER = Logger.getLogger(Amministratore.class.getName());

    private List<Volo> voliGestiti = new ArrayList<>();

    public Amministratore(String login, String password, String nomeUtente, String cognomeUtente) {
        super(login, password, nomeUtente, cognomeUtente);
    }


    public List<Volo> getVoliGestiti() {
        return voliGestiti;
    }

    public void setVoliGestiti(List<Volo> voliGestiti) {
        this.voliGestiti = voliGestiti;
    }

    /**
     * gestisce il volo
     */
    public void gestioneVolo() {
        LOGGER.info(() -> "Volo inserito o aggiornato da: " + getNomeUtente());
    }

    public void modificaAssegnazioneGate() {
        LOGGER.info(() -> "L'assegnazione dei gate è stata modificata da: " + getNomeUtente());
    }

    public void aggiungiVolo(Volo volo) {
        voliGestiti.add(volo);
        volo.setAmministratore(this);
    }
}