package model;

public class Utentegenerico extends Utente {

    public Utentegenerico(String login, String password) {
        super(login, password);
    }

    public String nomeUtente;

    public String getNomeUtente() {
        return nomeUtente;
    }

    public void setNomeUtente(String nomeUtente) {
        this.nomeUtente = nomeUtente;
    }

    public void prenotaVolo(){
        System.out.println("Prenotazione effettuata da " + login);
    }

    public void stampaUtente(){
        System.out.println("Utente: " + login);
    }
}

