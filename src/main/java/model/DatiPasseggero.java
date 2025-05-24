package model;

public class DatiPasseggero {
    private String nome;
    private String cognome;
    private String codiceFiscale;

    public DatiPasseggero(String nome, String cognome, String numeroDocumento) {
        this.nome = nome;
        this.cognome = cognome;
        this.codiceFiscale = numeroDocumento;
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

}