package controller;

import gui.AreaPersonaleAmmGUI;
import implementazioneDAO.implementazionePostgresDAO.*;
import model.*;
import javax.swing.*;
import java.util.*;

public class Controller {
    private List<Volo> voliGestiti = new ArrayList<>();
    private List<Prenotazione> prenotazioni = new ArrayList<>();
    private List<Gate> gates = new ArrayList<>();
    private List<Amministratore> amministratori;
    private List<DatiPasseggero> datiPasseggeri = new ArrayList<>();
    private List<UtenteGenerico> utenti = new ArrayList<>();
    private final List<Bagaglio> bagagli = new ArrayList<>();
    private AmministratoreDAOPostgres adminDAO;
    private BagaglioDAOPostgres bagaglioDAO;
    private DatiPasseggeroDAOPostgres datiPasseggeroDAO;
    private PrenotazioneDAOPostgres prenotazioneDAO;
    private UtenteGenericoDAOPostgres utentiDAO;
    private VoloDAOPostgres voloDAO;

    public Controller() {
        this.prenotazioni = new ArrayList<>();
        this.amministratori = new ArrayList<>();
        this.adminDAO = new AmministratoreDAOPostgres();
        this.bagaglioDAO = new BagaglioDAOPostgres();
        this.datiPasseggeroDAO = new DatiPasseggeroDAOPostgres();
        this.prenotazioneDAO = new PrenotazioneDAOPostgres();
        this.utentiDAO = new UtenteGenericoDAOPostgres();
        this.voloDAO = new VoloDAOPostgres();

        adminDAO.setController(this);
        bagaglioDAO.setController(this);
        datiPasseggeroDAO.setController(this);
        prenotazioneDAO.setController(this);
        utentiDAO.setController(this);

        Amministratore a = adminDAO.findByEmail("admin1@gmail.com");


        AreaPersonale areaPersonale1 = new AreaPersonale();
        AreaPersonale areaPersonale2 = new AreaPersonale();

        UtenteGenerico u1 = new UtenteGenerico("luigiverdi@gmail.com", "54321", "Luigi", "Verdi", new ArrayList<>(), areaPersonale1);
        UtenteGenerico u2 = new UtenteGenerico("lucabianchi@gmail.com", "56789", "Luca", "Bianchi", new ArrayList<>(), areaPersonale2);

        Amministratore admin = new Amministratore("admin1", "pwd123", "", "");
        TabellaOrario tabella = new TabellaOrario();


        //System.out.println("DEBUG Costruttore Controller - prima creazione voli, size=" + voliGestiti.size());
        caricaVoliIniziali();



        //System.out.println("DEBUG Dopo aggiunta voli, size=" + voliGestiti.size());


        DatiPasseggero passeggero1 = new DatiPasseggero("Luigi", "Verdi", "ID12345", "luigiverdi@gmail.com");
        DatiPasseggero passeggero2 = new DatiPasseggero("Luca", "Bianchi", "ID67890", "lucabianchi@gmail.com");

        Volo volo1 = getVoloByCodice("AZ123");
        Volo volo2 = getVoloByCodice("FR987");
        if (volo1 == null || volo2 == null) {
            System.err.println("ERRORE: voli iniziali non trovati per le prenotazioni di esempio.");
        }

        Prenotazione p1 = new Prenotazione("ABC123", "12A", StatoPrenotazione.CONFERMATA, u1, passeggero1, volo1);
        Prenotazione p2 = new Prenotazione("DEF456", "12B", StatoPrenotazione.CONFERMATA, u2, passeggero2, volo2);

        prenotazioni.add(p1);
        prenotazioni.add(p2);

        bagagli.add(new Bagaglio("BAG001", 18.3, StatoBagaglio.SMARRITO, p1));
        bagagli.add(new Bagaglio("BAG002", 19.1, StatoBagaglio.CARICATO, p1));
        bagagli.add(new Bagaglio("BAG003", 13.4, StatoBagaglio.SMARRITO, p2));
        bagagli.add(new Bagaglio("BAG004", 22.0, StatoBagaglio.CARICATO, p2));
    }

    public List<Object[]> tuttiVoli() {
        return ricercaVoli(null, null, null, null, null, null, null, null);
    }

    private void caricaVoliIniziali() {
        for (String[] r : VOLI_INIZIALI) {
            // r: 0=codice,1=compagnia,2=statoString,3=data,4=orario,5=aeroporto,6=gate,7=arrivo/partenza
            StatoVolo stato = parseStato(r[2]);
            Volo v = new Volo(r[0], r[1], r[3], r[4], stato, null, null);
            v.setAeroporto(r[5]);
            v.setGate(r[6]);
            v.setArrivoPartenza(r[7]);
            voliGestiti.add(v);
        }
    }

    private StatoVolo parseStato(String s) {
        if (s == null) return null;
        s = s.trim().toUpperCase();
        try {
            return StatoVolo.valueOf(s);
        } catch (IllegalArgumentException ex) {
            System.err.println("Stato volo sconosciuto: " + s);
            return null;
        }
    }


    private static final String[][] VOLI_INIZIALI = {
            {"AZ123", "ITA Airways", "PROGRAMMATO", "2025-09-05", "08:15", "MIL", "3", "in arrivo"},
            {"FR987", "Ryanair", "IMBARCO", "2025-09-05", "08:40", "BAR", "21", "in arrivo"},
            {"LH455", "Lufthansa", "DECOLLATO", "2025-09-05", "08:55", "MAD", "15", "in partenza"},
            {"U23610", "easyJet", "CANCELLATO", "2025-09-05", "09:05", "LDN", "9", "in arrivo"},
            {"AF101", "Air France", "INRITARDO", "2025-09-05", "09:20", "MYK", "5", "in partenza"},
            {"EK092", "Emirates", "ATTERRATO", "2025-09-05", "09:35", "PAR", "12", "in partenza"}
    };

    public void aggiungiVolo(String codiceUnivoco,
                             String compagniaAerea,
                             String dataVolo,
                             String orarioPrevisto,
                             StatoVolo stato,
                             String arrivoPartenza,
                             String otherAirport,
                             String gate) {

        // crea con il costruttore ridotto
        Volo volo = new Volo(codiceUnivoco, compagniaAerea, dataVolo, orarioPrevisto, stato, null, null);

        // assegna i campi aggiuntivi con i setter
        volo.setAeroporto(otherAirport);
        volo.setGate(gate);
        volo.setArrivoPartenza(arrivoPartenza);

        voliGestiti.add(volo);

        // DEBUG: conferma in console
        // System.out.println("DEBUG Controller.aggiungiVolo - aggiunto volo " + codiceUnivoco + " gate='" + gate + "'");
    }

    public boolean rimuoviVolo(String codiceUnivoco) {
        Iterator<Volo> iterator = voliGestiti.iterator();
        while (iterator.hasNext()) {
            Volo v = iterator.next();
            if (v.getCodiceUnivoco().equalsIgnoreCase(codiceUnivoco)) {
                iterator.remove();
                return true; // rimosso con successo
            }
        }
        return false; // nessun volo trovato
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

    public void aggiungiPrenotazione(String numeroBiglietto, String posto, StatoPrenotazione stato,
                                     String numeroVolo, UtenteGenerico utenteGenerico, String nome, String cognome,
                                     String codiceFiscale, String email, Volo volo) {
        DatiPasseggero datiPasseggero = new DatiPasseggero(nome, cognome, codiceFiscale, email);
        Prenotazione prenotazione = new Prenotazione(numeroBiglietto, posto, stato, utenteGenerico, datiPasseggero, volo);
        prenotazioni.add(prenotazione);
    }

    public boolean rimuoviPrenotazione(String numeroPrenotazione) {
        Iterator<Prenotazione> iterator = prenotazioni.iterator();
        while(iterator.hasNext()) {
            Prenotazione p = iterator.next();
            if(p.getNumBiglietto().equalsIgnoreCase(numeroPrenotazione)) {
                iterator.remove();
                return true; // prenotazione rimossa
            }
        }
        return false; // non trovata
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

    public Prenotazione cercaPrenotazione(String numeroBiglietto) {
        for(Prenotazione p : prenotazioni) {
            if(p.getNumBiglietto().equals(numeroBiglietto)) {
                return p;
            }
        }
        return null;
    }

    public boolean salvaPrenotazione(Prenotazione prenotazione) {
        for(int i = 0; i < prenotazioni.size(); i++) {
            if(prenotazioni.get(i).getNumBiglietto().equals(prenotazione.getNumBiglietto())) {
                prenotazioni.set(i, prenotazione);
                return true;
            }
        }
        return false;
    }

    public boolean annullaPrenotazione(Prenotazione prenotazione) {
        if(prenotazione != null) {
            prenotazione.setStato(StatoPrenotazione.CANCELLATA);
            return salvaPrenotazione(prenotazione);
        }
        return false;
    }

    // --- Ricerca voli (già presente) ---

    private String norm(String s) {
        if(s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    public List<Volo> ricercaVoliRaw(String numeroVolo, String compagnia, String stato, String data,
                                     String orario, String aeroporto, String gate, String arrivoPartenza) {
        numeroVolo = norm(numeroVolo);
        compagnia = norm(compagnia);
        stato = norm(stato);
        data = norm(data);
        orario = norm(orario);
        aeroporto = norm(aeroporto);
        gate = norm(gate);
        arrivoPartenza = norm(arrivoPartenza);

        /*System.out.printf("DEBUG ricercaVoliRaw parametri: num=%s comp=%s stato=%s data=%s orario=%s aero=%s gate=%s ap=%s%n",
                numeroVolo, compagnia, stato, data, orario, aeroporto, gate, arrivoPartenza);
        System.out.println("DEBUG voliGestiti size attuale = " + voliGestiti.size()); */

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
                if (v.getStato() == null || !v.getStato().name().equalsIgnoreCase(stato)) match = false;
            }

            if (data != null &&
                    (v.getDataVolo() == null ||
                            !v.getDataVolo().toLowerCase().contains(data.toLowerCase())))
                match = false;

            if (orario != null &&
                    (v.getOrarioPrevisto() == null ||
                            !v.getOrarioPrevisto().toLowerCase().contains(orario.toLowerCase())))
                match = false;

            if (aeroporto != null &&
                    (v.getAeroporto() == null ||
                            !v.getAeroporto().toLowerCase().contains(aeroporto.toLowerCase())))
                match = false;

            if (gate != null &&
                    (v.getGate() == null ||
                            !v.getGate().toLowerCase().contains(gate.toLowerCase())))
                match = false;

            if (arrivoPartenza != null &&
                    (v.getArrivoPartenza() == null ||
                            !v.getArrivoPartenza().equalsIgnoreCase(arrivoPartenza)))
                match = false;

            if (match) out.add(v);
        }
        //System.out.println("DEBUG ricercaVoliRaw -> trovati=" + out.size());
        return out;
    }

    private String safe(String s) { return s == null ? "" : s; }

    public List<Object[]> ricercaVoli(String numeroVolo, String compagnia, String stato, String data,
                                      String orario, String aeroporto, String gate, String arrivoPartenza) {

        if (arrivoPartenza != null) {
            String ap = arrivoPartenza.trim().toLowerCase();
            if (ap.equals("in arrivo")) arrivoPartenza = "in arrivo";
            else if (ap.equals("in partenza")) arrivoPartenza = "in partenza";
        }

        List<Volo> trovati = ricercaVoliRaw(numeroVolo, compagnia, stato, data, orario, aeroporto, gate, arrivoPartenza);
        List<Object[]> righe = new ArrayList<>();
        for (Volo v : trovati) {
            righe.add(new Object[]{
                    safe(v.getCodiceUnivoco()),
                    safe(v.getCompagniaAerea()),
                    v.getStato() != null ? v.getStato().name() : "",
                    safe(v.getDataVolo()),
                    safe(v.getOrarioPrevisto()),
                    safe(v.getAeroporto()),
                    safe(v.getGate()),
                    safe(v.getArrivoPartenza())
            });
        }
        //System.out.println("DEBUG ricercaVoli() -> righe=" + righe.size());
        return righe;
    }

    // --- Passeggeri ---
    public List<Object[]> ricercaPasseggeri(String nome,
                                            String cognome,
                                            String codiceFiscale,
                                            String numeroVolo,
                                            String numeroPrenotazione,
                                            String postoAssegnato,
                                            String statoPrenotazione) {
        List<Object[]> risultati = new ArrayList<>();

        String n = norm(nome);
        String c = norm(cognome);
        String cf = norm(codiceFiscale);
        String nv = norm(numeroVolo);
        String np = norm(numeroPrenotazione);
        String pa = norm(postoAssegnato);

        for (Prenotazione p : prenotazioni) {
            boolean match = true;
            DatiPasseggero dp = p.getDatiPasseggero();

            // Nome
            if (n != null && (dp == null || dp.getNome() == null || !dp.getNome().toLowerCase().contains(n.toLowerCase())))
                match = false;
            // Cognome
            if (match && c != null && (dp == null || dp.getCognome() == null || !dp.getCognome().toLowerCase().contains(c.toLowerCase())))
                match = false;
            // Codice fiscale
            if (match && cf != null && (dp == null || dp.getCodiceFiscale() == null ||
                    !dp.getCodiceFiscale().toLowerCase().contains(cf.toLowerCase())))
                match = false;
            // Numero volo
            if (match && nv != null && (p.getVolo() == null || p.getVolo().getCodiceUnivoco() == null ||
                    !p.getVolo().getCodiceUnivoco().toLowerCase().contains(nv.toLowerCase())))
                match = false;
            // Numero prenotazione
            if (match && np != null && (p.getNumBiglietto() == null ||
                    !p.getNumBiglietto().toLowerCase().contains(np.toLowerCase())))
                match = false;
            // Posto
            if (match && pa != null && (p.getPostoAssegnato() == null ||
                    !p.getPostoAssegnato().toLowerCase().contains(pa.toLowerCase())))
                match = false;
            // Stato prenotazione
            if (match && statoPrenotazione != null &&
                    (p.getStato() == null || !p.getStato().name().equalsIgnoreCase(statoPrenotazione)))
                match = false;

            if (match) {
                risultati.add(new Object[]{
                        safe(dp != null ? dp.getNome() : ""),
                        safe(dp != null ? dp.getCognome() : ""),
                        safe(dp != null ? dp.getCodiceFiscale() : ""),
                        p.getVolo() != null ? safe(p.getVolo().getCodiceUnivoco()) : "",
                        safe(p.getNumBiglietto()),
                        safe(p.getPostoAssegnato()),
                        p.getStato() != null ? p.getStato().name() : ""
                });
            }
        }
        return risultati;
    }

    // Utility per GUI: tutte le righe senza filtro
    public List<Object[]> tuttiPasseggeri() {
        return ricercaPasseggeri(null, null, null, null, null, null, null);
    }

    // Aggiunge un bagaglio (ritorna false se già esiste con lo stesso codice). Versione usata dal DAO
    public boolean aggiungiBagaglio(Bagaglio bagaglio) {
        for(Bagaglio b : bagagli) {
            if(b.getCodUnivoco().equalsIgnoreCase(bagaglio.getCodUnivoco())) {
                return false; // già esistente
            }
        }
        bagagli.add(bagaglio);
        return true;
    }

    // versione usata dalla GUI
    public boolean aggiungiBagaglio(String codice, StatoBagaglio stato) {
        Bagaglio nuovo = new Bagaglio(codice, 0.0, stato, null);
        return aggiungiBagaglio(nuovo);
    }

    // Restituisce tutti i bagagli
    public List<Bagaglio> getBagagli() {
        return new ArrayList<>(bagagli);
    }

    public List<Object[]> ricercaBagagli(String codiceBagaglio, String stato) {
        List<Object[]> risultati = new ArrayList<>();
        for(Bagaglio b : bagagli) {
            boolean match = true;
            if(codiceBagaglio != null && !codiceBagaglio.isEmpty() &&
                    (b.getCodUnivoco() == null || !b.getCodUnivoco().contains(codiceBagaglio)))
                match = false;
            if(stato != null && !stato.isEmpty() &&
                    (b.getStato() == null || !b.getStato().toString().equalsIgnoreCase(stato)))
                match = false;

            if(match) {
                risultati.add(new Object[]{
                        safe(b.getCodUnivoco()),
                        b.getStato() != null ? b.getStato().toString() : ""
                });
            }
        }
        return risultati;
    }

    public boolean rimuoviBagaglio(String codice) {
        return bagagli.removeIf(b -> b.getCodUnivoco().equalsIgnoreCase(codice));
    }


    public List<Object[]> tuttiBagagliRows() {
        return ricercaBagagli(null, null);
    }

    // --- Gate ---
    public boolean aggiungiGate(int numero) {
        for(Gate g : gates) {
            if(g.getNumero() == numero) {
                // RIMOSSO JOptionPane: la GUI gestisce i messaggi
                return false;
            }
        }
        gates.add(new Gate(numero));
        return true;
    }

    /**
     * Elimina un gate con il numero indicato.
     * @param numero numero del gate
     * @return true se eliminato, false se non trovato
     */
    public boolean eliminaGate(int numero) {
        return gates.removeIf(g -> g.getNumero() == numero);
    }

    public List<Gate> getGates() {
        return gates;
    }

    // AGGIUNGO METODI PER IL COLLEGAMENTO DAO - CONTROLLER
    //
    //

    // Factory method per creare amministratori dal DAO
    public Amministratore creaAmministratore(String login, String password, String nome, String cognome) {
        Amministratore admin = new Amministratore(login, password, nome, cognome);
        amministratori.add(admin);
        return admin;
    }

    // Recupera tutti gli amministratori
    public List<Amministratore> getAmministratori() {
        return amministratori;
    }

    // Trova un amministratore già caricato
    public Amministratore getAmministratoreByLogin(String login) {
        for (Amministratore a : amministratori) {
            if (a.getLogin().equals(login)) {
                return a;
            }
        }
        return null;
    }

    public DatiPasseggero creaDatiPasseggero(String nome, String cognome, String codiceFiscale, String email) {
        DatiPasseggero dp = new DatiPasseggero(nome, cognome, codiceFiscale, email);
        datiPasseggeri.add(dp);
        return dp;
    }

    // per trovare un passeggero già caricato (utile se DAO vuole cercare oggetti in memoria)
    public DatiPasseggero getDatiPasseggeroByCodiceFiscale(String codiceFiscale) {
        for(DatiPasseggero d : datiPasseggeri) {
            if(codiceFiscale != null && codiceFiscale.equals(d.getCodiceFiscale())) {
                return d;
            }
        }
        return null;
    }

    // factory per creare Prenotazione (usata dal DAO)
    public Prenotazione creaPrenotazione(String numBiglietto, String posto, StatoPrenotazione stato,
                                         UtenteGenerico utente, DatiPasseggero dp, Volo volo) {
        Prenotazione p = new Prenotazione(numBiglietto, posto, stato, utente, dp, volo);
        prenotazioni.add(p);
        return p;
    }

    public List<UtenteGenerico> getTuttiUtenti() {
        return utenti;
    }

    // recupero UtenteGenerico da email
    public UtenteGenerico getUtenteByEmail(String email) {
        for(UtenteGenerico u : getTuttiUtenti()) { // implementa getTuttiUtenti()
            if(email != null && email.equals(u.getNomeUtente())) {
                return u;
            }
        }
        return null;
    }

    // factory per creare UtenteGenerico
    public UtenteGenerico creaUtenteGenerico(String emailUtente) {
        UtenteGenerico u = new UtenteGenerico(emailUtente, "", "", "", new ArrayList<>(), new AreaPersonale());
        utenti.add(u);
        return u;
    }

    // recupero Volo da codice
    public Volo getVoloByCodice(String codiceVolo) {
        for(Volo v : voliGestiti) {
            if(v.getCodiceUnivoco() != null && v.getCodiceUnivoco().equals(codiceVolo)) {
                return v;
            }
        }
        return null;
    }

    // factory per creare Volo minimale
    public Volo creaVolo(String codiceVolo) {
        Volo v = new Volo(codiceVolo, "", "", "", null, null, null);
        voliGestiti.add(v);
        return v;
    }

    public DatiPasseggero getDatiPasseggeroByEmailUtente(String emailUtente) {
        for (Prenotazione p : prenotazioni) {
            if (p.getDatiPasseggero() != null &&
                    p.getDatiPasseggero().getEmail().equalsIgnoreCase(emailUtente)) {
                return p.getDatiPasseggero();
            }
        }
        return null; // se non trovato
    }

    // helper per DAO DatiPasseggero
    public DatiPasseggeroDAOPostgres getDatiPasseggeroDAO() {
        return datiPasseggeroDAO;
    }
}