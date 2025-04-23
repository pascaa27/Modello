package model;

public class Utente {

    protected String login;
    private String password;

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
        System.out.println("Modifica prenotazione");
    }

    public void visualizzaAggiornamentoVolo(){
        System.out.println("Visualizza aggiornamenti volo");
    }
}