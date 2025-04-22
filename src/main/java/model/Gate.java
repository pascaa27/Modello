package model;

import java.util.ArrayList;
import java.util.List;

public class Gate {

    private int numero;
    private List<VoloPartenza> voliPartenza = new ArrayList<>();

    public Gate(int numero) {
        this.numero = numero;
    }

    public List<VoloPartenza> getVoliPartenza() {
        return voliPartenza;
    }

    public int getNumero() {
        return numero;
    }

    public void setVoliPartenza(List<VoloPartenza> voliPartenza) {
        this.voliPartenza = voliPartenza;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public void aggiungiVoloPartenza(VoloPartenza voloPartenza) {
        voliPartenza.add(voloPartenza);
    }
}