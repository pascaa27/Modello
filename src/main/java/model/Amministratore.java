package model;
import java.util.ArrayList;
import java.util.List;


public class Amministratore extends Utente {

    private String nomeAdmin;
    private String cognome;
    private List<Volo> voliGestiti = new ArrayList<>();

    public Amministratore(String login, String password, String nomeUtente, String cognomeUtente, String nomeAdmin, String cognome) {
        super(login, password, nomeUtente, cognomeUtente);
        this.nomeAdmin = nomeAdmin;
        this.cognome = cognome;
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

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
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