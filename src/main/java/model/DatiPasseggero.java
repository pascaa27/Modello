package model;

public class DatiPasseggero {
    private String nome;
    private String cognome;
    private String codiceFiscale;
    private String email;
    private String id;
    private String postoAssegnato;

    public DatiPasseggero(String nome, String cognome,  String codiceFiscale, String email) {
        this.nome = nome;
        this.cognome = cognome;
        this.codiceFiscale = codiceFiscale;
        this.email = email;
        this.id = id;
        this.postoAssegnato = postoAssegnato;

    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }
    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPostoAssegnato() {
        return postoAssegnato;
    }
    public void setPostoAssegnato(String postoAssegnato) {
        this.postoAssegnato = postoAssegnato;
    }
}