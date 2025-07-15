package controller;

import gui.AreaPersonaleAmmGUI;
import model.*;

import javax.swing.*;
import java.util.*;

public class Controller {
    private List<Volo> voliGestiti = new ArrayList<>();
    private List<Prenotazione> prenotazioni = new ArrayList<>();
    private List<Bagaglio> bagagliGestiti = new ArrayList<>();
    private List<Gate> gates = new ArrayList<>();

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


    public List<Object[]> ricercaVoli(String numeroVolo, String compagnia, String stato, String data) {
        List<Object[]> risultati = new ArrayList<>();
        for (Volo v : voliGestiti) {
            boolean match = true;
            if (numeroVolo != null && !numeroVolo.isEmpty() && !v.getCodiceUnivoco().contains(numeroVolo)) match = false;
            if (compagnia != null && !compagnia.isEmpty() && !v.getCompagniaAerea().toLowerCase().contains(compagnia.toLowerCase())) match = false;
            if (stato != null && !stato.isEmpty() && !v.getStato().toString().equalsIgnoreCase(stato)) match = false;
            if (data != null && !data.isEmpty() && !v.getDataVolo().contains(data)) match = false;
            if (match) {
                risultati.add(new Object[]{
                        v.getCodiceUnivoco(),
                        v.getCompagniaAerea(),
                        v.getStato().toString(),
                        v.getDataVolo()
                });
            }
        }
        return risultati;
    }

    /**
     * Ricerca passeggeri tra le prenotazioni
     */
    public List<Object[]> ricercaPasseggeri(String nome, String cognome, String numeroVolo, String numeroPrenotazione) {
        List<Object[]> risultati = new ArrayList<>();
        for (Prenotazione p : prenotazioni) {
            boolean match = true;
            DatiPasseggero dp = p.getDatiPasseggero();

            if (nome != null && !nome.isEmpty() && !dp.getNome().toLowerCase().contains(nome.toLowerCase())) match = false;
            if (cognome != null && !cognome.isEmpty() && !dp.getCognome().toLowerCase().contains(cognome.toLowerCase())) match = false;
            if (numeroVolo != null && !numeroVolo.isEmpty() && !p.getVolo().getCodiceUnivoco().contains(numeroVolo)) match = false;
            if (numeroPrenotazione != null && !numeroPrenotazione.isEmpty() && !p.getNumBiglietto().contains(numeroPrenotazione)) match = false;

            if (match) {
                risultati.add(new Object[]{
                        dp.getNome(),
                        dp.getCognome(),
                        p.getVolo().getCodiceUnivoco(),
                        p.getNumBiglietto()
                });
            }
        }
        return risultati;
    }


    public void aggiungiBagaglio(Bagaglio b) {
        bagagliGestiti.add(b);
    }

    public List<Bagaglio> getBagagliGestiti() {
        return bagagliGestiti;
    }

    /**
     * Ricerca bagagli in base ai criteri (aggiungi la lista bagagliGestiti in questo Controller!)
     */
    public List<Object[]> ricercaBagagli(String codiceBagaglio, String stato) {
        List<Object[]> risultati = new ArrayList<>();
        for (Bagaglio b : bagagliGestiti) {
            boolean match = true;
            if (codiceBagaglio != null && !codiceBagaglio.isEmpty() && !b.getCodUnivoco().contains(codiceBagaglio))
                match = false;
            if (stato != null && !stato.isEmpty() && !b.getStato().toString().equalsIgnoreCase(stato)) match = false;

            if (match) {
                risultati.add(new Object[]{
                        b.getCodUnivoco(),
                        b.getStato().toString()
                });
            }
        }
        return risultati;
    }

        public void aggiungiGate(int numero) {
            // Controllo se il gate esiste già (opzionale)
            for (Gate g : gates) {
                if (g.getNumero() == numero) {
                    JOptionPane.showMessageDialog(null, "Esiste già un gate con questo numero!", "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            gates.add(new Gate(numero));
        }

        public List<Gate> getGates() {
            return gates;
        }




}
