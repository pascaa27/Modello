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

    public void aggiungiPrenotazione(String numeroBiglietto, String posto, StatoPrenotazione stato, UtenteGenerico utenteGenerico, String nome, String cognome, String codiceFiscale, String email, Volo volo) {
        DatiPasseggero datiPasseggero = new DatiPasseggero(nome, cognome, codiceFiscale, email);
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

    /**
     * Cerca una prenotazione tramite codice biglietto
     */
    public Prenotazione cercaPrenotazione(String numeroBiglietto) {
        for (Prenotazione p : prenotazioni) {
            if (p.getNumBiglietto().equals(numeroBiglietto)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Salva le modifiche a una prenotazione (qui è già aggiornata, quindi ritorna true)
     */
    public boolean salvaPrenotazione(Prenotazione prenotazione) {
        // Se la lista contiene già l'oggetto, è sufficiente!
        // Puoi anche fare una ricerca per sicurezza
        for (int i = 0; i < prenotazioni.size(); i++) {
            if (prenotazioni.get(i).getNumBiglietto().equals(prenotazione.getNumBiglietto())) {
                prenotazioni.set(i, prenotazione);
                return true;
            }
        }
        return false;
    }

    /**
     * Annulla una prenotazione (imposta stato a ANNULLATA)
     */
    public boolean annullaPrenotazione(Prenotazione prenotazione) {
        if (prenotazione != null) {
            prenotazione.setStato(StatoPrenotazione.CANCELLATA);
            return salvaPrenotazione(prenotazione);
        }
        return false;
    }




}