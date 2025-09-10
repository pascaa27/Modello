package controller;

import gui.AreaPersonaleAmmGUI;
import implementazioneDAO.implementazionePostgresDAO.*;
import model.*;

import javax.swing.*;
import java.util.*;

public class Controller {
    private List<Volo> voliGestiti = new ArrayList<>();
    private List<Prenotazione> prenotazioni = new ArrayList<>();
    private List<Bagaglio> bagagliGestiti = new ArrayList<>();
    private List<Gate> gates = new ArrayList<>();
    private List<Amministratore> amministratori;
    private List<DatiPasseggero> datiPasseggeri = new ArrayList<>();
    private List<UtenteGenerico> utenti = new ArrayList<>();
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


        Amministratore a = adminDAO.findByEmail("admin1@gmail.com ");

        AreaPersonale areaPersonale1 = new AreaPersonale();
        AreaPersonale areaPersonale2 = new AreaPersonale();

        UtenteGenerico u1 = new UtenteGenerico("luigiverdi@gmail.com", "54321", "Luigi", "Verdi", new ArrayList<>(), areaPersonale1);
        UtenteGenerico u2 = new UtenteGenerico("lucabianchi@gmail.com", "56789", "Luca", "Bianchi", new ArrayList<>(), areaPersonale2);

        Amministratore admin = new Amministratore("admin1", "pwd123", "", "");
        TabellaOrario tabella = new TabellaOrario();

        Volo v1 = new Volo("VOLO1", "Alitalia", "2025-06-01", "10:00",
                StatoVolo.PROGRAMMATO, admin, tabella);
        Volo v2 = new Volo("VOLO2", "Lufthansa", "2025-07-10", "18:30",
                StatoVolo.PROGRAMMATO, admin, tabella);

        voliGestiti.add(v1);
        voliGestiti.add(v2);

        DatiPasseggero passeggero1 = new DatiPasseggero("Luigi", "Verdi", "ID12345", "luigiverdi@gmail.com");
        DatiPasseggero passeggero2 = new DatiPasseggero("Luca", "Bianchi", "ID67890", "lucabianchi@gmail.com");

        Prenotazione p1 = new Prenotazione("ABC123", "12A", StatoPrenotazione.CONFERMATA, u1, passeggero1, v1);
        Prenotazione p2 = new Prenotazione("DEF456", "12B", StatoPrenotazione.CONFERMATA, u2, passeggero2, v2);

        prenotazioni.add(p1);
        prenotazioni.add(p2);

        bagagliGestiti.add(new Bagaglio("BAG001", 18.3, StatoBagaglio.SMARRITO, p1));
        bagagliGestiti.add(new Bagaglio("BAG002", 19.1, StatoBagaglio.CARICATO, p1));
        bagagliGestiti.add(new Bagaglio("BAG003", 13.4, StatoBagaglio.SMARRITO, p2));
        bagagliGestiti.add(new Bagaglio("BAG004", 22.0, StatoBagaglio.CARICATO, p2));

    }

    public void aggiungiVolo(String codiceUnivoco, String compagniaAerea, String dataVolo, String orarioPrevisto,
                             StatoVolo stato, String direzione, String otherAirport) {
        Volo volo = new Volo(codiceUnivoco, compagniaAerea, dataVolo, orarioPrevisto, stato, null, null);
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

    // --- Ricerca voli (già presente) ---

    private String norm(String s) {
        if (s == null) return null;
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

        List<Volo> out = new ArrayList<>();
        for(Volo v : voliGestiti) {
            boolean match = true;

            if(numeroVolo != null &&
                    (v.getCodiceUnivoco() == null ||
                            !v.getCodiceUnivoco().toLowerCase().contains(numeroVolo.toLowerCase())))
                match = false;

            if(compagnia != null &&
                    (v.getCompagniaAerea() == null ||
                            !v.getCompagniaAerea().toLowerCase().contains(compagnia.toLowerCase())))
                match = false;

            if(stato != null) {
                if(v.getStato() == null ||
                        !(v.getStato().name().equalsIgnoreCase(stato) ||
                                v.getStato().toString().equalsIgnoreCase(stato))) {
                    match = false;
                }
            }

            if(data != null &&
                    (v.getDataVolo() == null ||
                            !v.getDataVolo().toLowerCase().contains(data.toLowerCase())))
                match = false;

            // --- Nuovi filtri ---
            if(orario != null &&
                    (v.getOrarioPrevisto() == null ||
                            !v.getOrarioPrevisto().toLowerCase().contains(orario.toLowerCase())))
                match = false;

            if(aeroporto != null &&
                    (v.getAeroporto() == null ||
                            !v.getAeroporto().toLowerCase().contains(aeroporto.toLowerCase())))
                match = false;

            if(gate != null &&
                    (v.getGate() == null ||
                            !v.getGate().toLowerCase().contains(gate.toLowerCase())))
                match = false;

            if (arrivoPartenza != null &&
                    (v.getArrivoPartenza() == null ||
                            !v.getArrivoPartenza().toLowerCase().contains(arrivoPartenza.toLowerCase())))
                match = false;

            if(match) out.add(v);
        }
        return out;
    }

    public List<Object[]> ricercaVoli(String numeroVolo, String compagnia, String stato, String data,
                                      String orario, String aeroporto, String gate, String arrivoPartenza) {
        List<Volo> trovati = ricercaVoliRaw(numeroVolo, compagnia, stato, data, orario, aeroporto, gate, arrivoPartenza);
        List<Object[]> righe = new ArrayList<>();
        for(Volo v : trovati) {
            righe.add(new Object[]{
                    safe(v.getCodiceUnivoco()),
                    safe(v.getCompagniaAerea()),
                    safe(v.getDataVolo()),
                    safe(v.getOrarioPrevisto()),
                    safe(v.getAeroporto()),
                    safe(v.getGate()),
                    safe(v.getArrivoPartenza()),
                    v.getStato() != null ? v.getStato().name() : ""
            });
        }
        return righe;
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    // --- Passeggeri ---
    public List<Object[]> ricercaPasseggeri(String nome, String cognome, String numeroVolo, String numeroPrenotazione) {
        List<Object[]> risultati = new ArrayList<>();
        for(Prenotazione p : prenotazioni) {
            boolean match = true;
            DatiPasseggero dp = p.getDatiPasseggero();

            if(nome != null && !nome.isEmpty() && (dp.getNome() == null ||
                    !dp.getNome().toLowerCase().contains(nome.toLowerCase()))) match = false;
            if(cognome != null && !cognome.isEmpty() && (dp.getCognome() == null ||
                    !dp.getCognome().toLowerCase().contains(cognome.toLowerCase()))) match = false;
            if(numeroVolo != null && !numeroVolo.isEmpty() &&
                    (p.getVolo() == null || p.getVolo().getCodiceUnivoco() == null ||
                            !p.getVolo().getCodiceUnivoco().contains(numeroVolo))) match = false;
            if(numeroPrenotazione != null && !numeroPrenotazione.isEmpty() &&
                    (p.getNumBiglietto() == null || !p.getNumBiglietto().contains(numeroPrenotazione))) match = false;

            if(match) {
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

    // Utility per GUI: tutte le righe senza filtro
    public List<Object[]> tuttiPasseggeri() {
        return ricercaPasseggeri(null, null, null, null);
    }

    // --- Bagagli ---
    public void aggiungiBagaglio(Bagaglio b) {
        bagagliGestiti.add(b);
        bagaglioDAO.insert(b);
    }

    public List<Bagaglio> getBagagliGestiti() {
        return bagagliGestiti;
    }

    public List<Object[]> ricercaBagagli(String codiceBagaglio, String stato) {
        List<Object[]> risultati = new ArrayList<>();
        for(Bagaglio b : bagagliGestiti) {
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