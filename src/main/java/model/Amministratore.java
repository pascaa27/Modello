package model;

public class Amministratore extends Utente {

    private String nomeAdmin;

    public Amministratore(String login, String password) {
        super(login, password);
    }

    public String getNomeAdmin() {
        return nomeAdmin;
    }

    public void setNomeAdmin(String nomeAdmin) {
        this.nomeAdmin = nomeAdmin;
    }

    public void gestioneVolo() {
        System.out.println("Volo inserito o aggiornato da: " + nomeAdmin);
    }

    public void modificaAssegnazioneGate() {
        System.out.println("L'assegnazione dei gate è stata modificata da: " + nomeAdmin);
    }
}