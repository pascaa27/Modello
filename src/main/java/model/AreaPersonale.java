package model;

public class AreaPersonale {

    private String ritardoVolo;
    private String cancellazioneVolo;
    private String variazioneVolo;
    private Utente utente;

    public AreaPersonale(String ritardoVolo, String cancellazioneVolo, String variazioneVolo, Utente utente) {
        this.ritardoVolo = ritardoVolo;
        this.cancellazioneVolo = cancellazioneVolo;
        this.variazioneVolo = variazioneVolo;
        this.utente = utente;
    }

    // costruttore vuoto per il binding automatico per l'esempio statico nel controller
    public AreaPersonale() {
        // vuoto, necessario per il framework nel controller
    }

    public String getRitardoVolo() {
        return ritardoVolo;
    }

    public String getCancellazioneVolo() {
        return cancellazioneVolo;
    }

    public String getVariazioneVolo() {
        return variazioneVolo;
    }

    public Utente getUtente() {
        return utente;
    }


    public void setUtente(Utente utente) {
        this.utente = utente;
    }
    public void setRitardoVolo(String ritardoVolo) {
        this.ritardoVolo = ritardoVolo;
    }
    public void setCancellazioneVolo(String cancellazioneVolo) {
        this.cancellazioneVolo = cancellazioneVolo;
    }
    public void setVariazioneVolo(String variazioneVolo) {
        this.variazioneVolo = variazioneVolo;
    }

}