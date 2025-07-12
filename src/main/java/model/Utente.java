package model;

public class Utente {

    protected String login;
    private String password;
    private String nomeUtente;
    private String cognomeUtente;

    public Utente(String login, String password, String nomeUtente, String cognomeUtente) {
        this.login = login;
        this.password = password;
        this.nomeUtente = nomeUtente;
        this.cognomeUtente = cognomeUtente;
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

    public String getNomeUtente() {
        return nomeUtente;
    }

    public void setNomeUtente(String nomeUtente) {
        this.nomeUtente = nomeUtente;
    }

    public String getCognomeUtente() {
        return cognomeUtente;
    }

    public void setCognomeUtente(String cognomeUtente) {
        this.cognomeUtente = cognomeUtente;
    }

    public void modificaPrenotazione(){
        System.out.println("Modifica prenotazione");
    }

    public void visualizzaAggiornamentoVolo(){
        System.out.println("Visualizza aggiornamenti volo");
    }
}