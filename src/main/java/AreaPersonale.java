import model.Utente;

public class AreaPersonale {

    private String ritardoVolo;
    private String cancellazioneVolo;
    private String variazioneVolo;

    private Utente utente;      //associazione con la classe utente

    public AreaPersonale(Utente utente) {
        this.utente = utente;
    }


    public String getritardoVolo() {
        return ritardoVolo;
    }

    public String getcancellazioneVolo() {
        return cancellazioneVolo;
    }

    public String getvariazioneVolo() {
        return variazioneVolo;
    }

    public Utente getUtente() {
        return utente;
    }


    public void setutente(Utente utente) {
        this.utente = utente;
    }
    public void setritardoVolo(String ritardoVolo) {
        this.ritardoVolo = ritardoVolo;
    }
    public void setcancellazioneVolo(String cancellazioneVolo) {
        this.cancellazioneVolo = cancellazioneVolo;
    }
    public void setvariazioneVolo(String variazioneVolo) {
        this.variazioneVolo = variazioneVolo;
    }


}
