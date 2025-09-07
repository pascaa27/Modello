package model;

public class TabellaOrario {
    private String orarioPartenza;
    private String orarioArrivo;

    public TabellaOrario(String orarioPartenza, String orarioArrivo) {
        this.orarioPartenza = orarioPartenza;
        this.orarioArrivo = orarioArrivo;
    }

    // costruttore vuoto per il binding automatico per l'esempio statico nel controller
    public TabellaOrario() {
        // vuoto, necessario per il framework nel controller
    }

    public String getOrarioPartenza() {
        return orarioPartenza;
    }

    public String getOrarioArrivo() {
        return orarioArrivo;
    }

    public void setOrarioPartenza(String orarioPartenza) {
        this.orarioPartenza = orarioPartenza;
    }

    public void setOrarioArrivo(String orarioArrivo) {
        this.orarioArrivo = orarioArrivo;
    }
}