package model;
import java.util.ArrayList;
import java.util.List;

public class Amministratore extends Utente {

    private String nomeAdmin;
    private List<Volo> voliGestiti = new ArrayList<>();

    public Amministratore(String login, String password) {
        super(login, password);
    }

    public String getNomeAdmin() {
        return nomeAdmin;
    }

    public List<Volo> getVoliGestiti() {
        return voliGestiti;
    }

    public void setNomeAdmin(String nomeAdmin) {
        this.nomeAdmin = nomeAdmin;
    }

    public void setVoliGestiti(List<Volo> voliGestiti) {
        this.voliGestiti = voliGestiti;
    }

    public void gestioneVolo() {
        System.out.println("Volo inserito o aggiornato da: " + nomeAdmin);
    }

    public void modificaAssegnazioneGate() {
        System.out.println("L'assegnazione dei gate è stata modificata da: " + nomeAdmin);
    }

    public void aggiungiVolo(Volo volo) {
        voliGestiti.add(volo);
        volo.setAmministratore(this);
    }
}