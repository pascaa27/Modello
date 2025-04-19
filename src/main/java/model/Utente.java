package model;

public class Utente {

    public String login;
    public String password;

    public Utente(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void modificaPrenotazione(){
    }

    public void visualizzaAggiornamentoVolo(){
    }
}
