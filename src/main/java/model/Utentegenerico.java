package model;

public class Utentegenerico extends Utente {

    public String nomeUtente;

    public Utentegenerico(String login, String password) {
        super(login, password);
    }

    public String getNomeUtente() {
        return nomeUtente;
    }

    public void setNomeUtente(String nomeUtente) {
        this.nomeUtente = nomeUtente;
    }

    public void prenotaVolo(){
        System.out.println("Prenotazione effettuata da " + login);
    }
}

