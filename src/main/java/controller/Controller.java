package controller;

import model.*;
import java.util.*;

public class Controller {
    private List<Volo> voliGestiti = new ArrayList<>();

    public void aggiungiVolo(String codiceUnivoco, String compagniaAerea, Date dataVolo, String orarioPrevisto, StatoVolo stato, Amministratore amministratore, TabellaOrario tabellaOrario) {
        Volo volo = new Volo(codiceUnivoco, compagniaAerea,dataVolo, orarioPrevisto, stato, amministratore, tabellaOrario);
        voliGestiti.add(volo);
    }

    public List<Volo> getVoliGestiti() {
        return voliGestiti;
    }

    public void aggiornaVolo(String codiceUnivoco, StatoVolo nuovoStato, String nuovoOrario) {
        for(Volo volo : voliGestiti) {
            if(volo.getCodiceUnivoco().equals(codiceUnivoco)) {
                volo.setStato(nuovoStato);
                volo.setOrarioPrevisto(nuovoOrario);
                break;
            }
        }
    }

    public Volo cercaVolo(String codiceUnivoco) {
        for(Volo volo : voliGestiti) {
            if(volo.getCodiceUnivoco().equals(codiceUnivoco)) {
                return volo;
            }
        }
        return null;
    }

}