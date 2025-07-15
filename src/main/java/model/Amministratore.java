package model;
import java.util.ArrayList;
import java.util.List;

public class Amministratore extends Utente {

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

    public void gestioneVolo() {
        System.out.println("Volo inserito o aggiornato da: " + getNomeUtente());
    }

    public void modificaAssegnazioneGate() {
        System.out.println("L'assegnazione dei gate è stata modificata da: " + getNomeUtente());
    }

    public void aggiungiVolo(Volo volo) {
        voliGestiti.add(volo);
        volo.setAmministratore(this);
    }
}