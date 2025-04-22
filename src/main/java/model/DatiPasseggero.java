package model;

public class DatiPasseggero {
    private String nome;
    private String cognome;
    private String numeroDocumento;

    public DatiPasseggero(String nome, String cognome, String numeroDocumento) {
        this.nome = nome;
        this.cognome = cognome;
        this.numeroDocumento = numeroDocumento;
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

    public String getNumeroDocumento() {
        return numeroDocumento;
    }
    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

}

