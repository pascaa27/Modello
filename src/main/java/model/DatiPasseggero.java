package model;

public class DatiPasseggero {
    private String nome;
    private String cognome;
    private String codiceFiscale; // opzionale
    private String email;         // usata per login (unique)
    private String password;      // persistita in DB

    public DatiPasseggero(String nome, String cognome, String codiceFiscale, String email) {
        this(nome, cognome, codiceFiscale, email, "");
    }

    public DatiPasseggero(String nome, String cognome, String codiceFiscale, String email, String password) {
        this.nome = nome;
        this.cognome = cognome;
        this.codiceFiscale = codiceFiscale;
        this.email = email;
        this.password = password;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getCodiceFiscale() { return codiceFiscale; }
    public void setCodiceFiscale(String codiceFiscale) { this.codiceFiscale = codiceFiscale; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}