package controller;

import gui.AreaPersonaleAmmGUI;
import model.*;

import javax.swing.*;
import java.util.*;

public class Controller {
    private List<Volo> voliGestiti = new ArrayList<>();
    private List<Prenotazione> prenotazioni = new ArrayList<>();

    public void aggiungiVolo(String codiceUnivoco, String compagniaAerea, String dataVolo, String orarioPrevisto, StatoVolo stato, Amministratore amministratore, TabellaOrario tabellaOrario) {
        Volo volo = new Volo(codiceUnivoco, compagniaAerea, dataVolo, orarioPrevisto, stato, amministratore, tabellaOrario);
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

    public void aggiungiPrenotazione(String numeroBiglietto, String posto, StatoPrenotazione stato, UtenteGenerico utenteGenerico, String nome, String cognome, String codiceFiscale, Volo volo) {
        DatiPasseggero datiPasseggero = new DatiPasseggero(nome, cognome, codiceFiscale);
        Prenotazione prenotazione = new Prenotazione(numeroBiglietto, posto, stato, utenteGenerico, datiPasseggero, volo);
        prenotazioni.add(prenotazione);
    }

    public void mostraAreaPersonaleAmm(JFrame finestraCorrente, Amministratore amministratore) {
        finestraCorrente.setVisible(false);
        AreaPersonaleAmmGUI nuovaGUI = new AreaPersonaleAmmGUI(this, amministratore);
        JFrame frame = new JFrame("Area Personale Amministratore");
        frame.setContentPane(nuovaGUI.getAreaPersonaleAmmPanel());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}