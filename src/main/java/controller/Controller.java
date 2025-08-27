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

    public void aggiungiVolo(String codiceUnivoco, String compagniaAerea, String dataVolo, String orarioPrevisto,
                             StatoVolo stato, Amministratore amministratore, TabellaOrario tabellaOrario) {
        Volo volo = new Volo(codiceUnivoco, compagniaAerea, dataVolo, orarioPrevisto, stato, amministratore, tabellaOrario);
        voliGestiti.add(volo);
    }

    public List<Volo> getVoliGestiti() {
        return voliGestiti;
    }

    public void aggiornaVolo(String codiceUnivoco, StatoVolo nuovoStato, String nuovoOrario) {
        for (Volo volo : voliGestiti) {
            if (volo.getCodiceUnivoco().equals(codiceUnivoco)) {
                volo.setStato(nuovoStato);
                volo.setOrarioPrevisto(nuovoOrario);
                break;
            }
        }
    }

    public Volo cercaVolo(String codiceUnivoco) {
        for (Volo volo : voliGestiti) {
            if (volo.getCodiceUnivoco().equals(codiceUnivoco)) {
                return volo;
            }
        }
        return null;
    }

    public void aggiungiPrenotazione(String numeroBiglietto, String posto, StatoPrenotazione stato,
                                     UtenteGenerico utenteGenerico, String nome, String cognome,
                                     String codiceFiscale, String email, Volo volo) {
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

    // Prenotazioni
    public Prenotazione cercaPrenotazione(String numeroBiglietto) {
        for (Prenotazione p : prenotazioni) {
            if (p.getNumBiglietto().equals(numeroBiglietto)) {
                return p;
            }
        }
        return null;
    }

    public boolean salvaPrenotazione(Prenotazione prenotazione) {
        for (int i = 0; i < prenotazioni.size(); i++) {
            if (prenotazioni.get(i).getNumBiglietto().equals(prenotazione.getNumBiglietto())) {
                prenotazioni.set(i, prenotazione);
                return true;
            }
        }
        return false;
    }

    public boolean annullaPrenotazione(Prenotazione prenotazione) {
        if (prenotazione != null) {
            prenotazione.setStato(StatoPrenotazione.CANCELLATA);
            return salvaPrenotazione(prenotazione);
        }
        return false;
    }

    // ------------------- RICERCA VOLI (STEP A) -------------------

    /**
     * Normalizza stringa: trim e converte "" in null
     */
    private String norm(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    /**
     * Versione "raw" che restituisce la lista di Volo filtrata.
     * I parametri null significano "non filtrare".
     * Per codice e compagnia viene usato un match parziale (contains case-insensitive).
     * Per stato si confronta l'enum (name) ignorando maiuscole/minuscole oppure direttamente lo toString se preferisci.
     * Per data uso contains (parziale) dato che la tieni come stringa (puoi cambiare).
     */
    public List<Volo> ricercaVoliRaw(String numeroVolo, String compagnia, String stato, String data) {
        numeroVolo = norm(numeroVolo);
        compagnia = norm(compagnia);
        stato = norm(stato);
        data = norm(data);

        List<Volo> out = new ArrayList<>();
        for (Volo v : voliGestiti) {
            boolean match = true;

            if (numeroVolo != null &&
                    (v.getCodiceUnivoco() == null ||
                            !v.getCodiceUnivoco().toLowerCase().contains(numeroVolo.toLowerCase())))
                match = false;

            if (compagnia != null &&
                    (v.getCompagniaAerea() == null ||
                            !v.getCompagniaAerea().toLowerCase().contains(compagnia.toLowerCase())))
                match = false;

            if (stato != null) {
                // Se la combo ti dà "In partenza" ma l'enum è IN_PARTENZA devi mappare.
                // Qui confronto con name() grezzo:
                if (v.getStato() == null ||
                        !(v.getStato().name().equalsIgnoreCase(stato) ||
                                v.getStato().toString().equalsIgnoreCase(stato))) {
                    match = false;
                }
            }

            if (data != null &&
                    (v.getDataVolo() == null ||
                            !v.getDataVolo().toLowerCase().contains(data.toLowerCase())))
                match = false;

            if (match) out.add(v);
        }
        return out;
    }

    /**
     * Versione legacy per le GUI che si aspettano List<Object[]>
     * ORDINE COLONNE PROPOSTO: Codice, Compagnia, Data, Orario, Stato
     * (Allinea la JTable della GUI a questo ordine!)
     */
    public List<Object[]> ricercaVoli(String numeroVolo, String compagnia, String stato, String data) {
        List<Volo> trovati = ricercaVoliRaw(numeroVolo, compagnia, stato, data);
        List<Object[]> righe = new ArrayList<>();
        for (Volo v : trovati) {
            righe.add(new Object[]{
                    safe(v.getCodiceUnivoco()),
                    safe(v.getCompagniaAerea()),
                    safe(v.getDataVolo()),
                    safe(v.getOrarioPrevisto()),
                    v.getStato() != null ? v.getStato().name() : ""
            });
        }
        return righe;
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    // ------------------------------------------------------------

    // Passeggeri
    public List<Object[]> ricercaPasseggeri(String nome, String cognome, String numeroVolo, String numeroPrenotazione) {
        List<Object[]> risultati = new ArrayList<>();
        for (Prenotazione p : prenotazioni) {
            boolean match = true;
            DatiPasseggero dp = p.getDatiPasseggero();

            if (nome != null && !nome.isEmpty() && (dp.getNome() == null ||
                    !dp.getNome().toLowerCase().contains(nome.toLowerCase()))) match = false;
            if (cognome != null && !cognome.isEmpty() && (dp.getCognome() == null ||
                    !dp.getCognome().toLowerCase().contains(cognome.toLowerCase()))) match = false;
            if (numeroVolo != null && !numeroVolo.isEmpty() &&
                    (p.getVolo() == null || p.getVolo().getCodiceUnivoco() == null ||
                            !p.getVolo().getCodiceUnivoco().contains(numeroVolo))) match = false;
            if (numeroPrenotazione != null && !numeroPrenotazione.isEmpty() &&
                    (p.getNumBiglietto() == null || !p.getNumBiglietto().contains(numeroPrenotazione))) match = false;

            if (match) {
                risultati.add(new Object[]{
                        safe(dp.getNome()),
                        safe(dp.getCognome()),
                        p.getVolo() != null ? safe(p.getVolo().getCodiceUnivoco()) : "",
                        safe(p.getNumBiglietto())
                });
            }
        }
        return risultati;
    }

    // Bagagli
    public void aggiungiBagaglio(Bagaglio b) {
        bagagliGestiti.add(b);
    }

    public List<Bagaglio> getBagagliGestiti() {
        return bagagliGestiti;
    }

    public List<Object[]> ricercaBagagli(String codiceBagaglio, String stato) {
        List<Object[]> risultati = new ArrayList<>();
        for (Bagaglio b : bagagliGestiti) {
            boolean match = true;
            if (codiceBagaglio != null && !codiceBagaglio.isEmpty() &&
                    (b.getCodUnivoco() == null || !b.getCodUnivoco().contains(codiceBagaglio)))
                match = false;
            if (stato != null && !stato.isEmpty() &&
                    (b.getStato() == null || !b.getStato().toString().equalsIgnoreCase(stato)))
                match = false;

            if (match) {
                risultati.add(new Object[]{
                        safe(b.getCodUnivoco()),
                        b.getStato() != null ? b.getStato().toString() : ""
                });
            }
        }
        return risultati;
    }

    // Gate
    public void aggiungiGate(int numero) {
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