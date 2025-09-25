package controller;

import gui.AreaPersonaleAmmGUI;
import implementazioneDAO.implementazionePostgresDAO.*;
import model.*;

import javax.swing.*;
import java.util.*;

/**
 * Controller "persistente":
 * - carica i voli dal DB all'avvio
 * - fa seed dei voli iniziali SOLO se il DB è vuoto
 * - tutte le operazioni di aggiunta/rimozione/aggiornamento passano dai DAO (DB prima, poi cache in memoria)
 * - le liste in memoria sono cache della UI, NON la verità dei dati
 */
public class Controller {

    // Cache in memoria per la UI
    private final List<Volo> voliGestiti = new ArrayList<>();
    private final List<Prenotazione> prenotazioni = new ArrayList<>();
    private final List<Gate> gates = new ArrayList<>();
    private final List<Amministratore> amministratori = new ArrayList<>();
    private final List<DatiPasseggero> datiPasseggeri = new ArrayList<>();
    private final List<UtenteGenerico> utenti = new ArrayList<>();
    private final List<Bagaglio> bagagli = new ArrayList<>();

    // DAO
    private final AmministratoreDAOPostgres adminDAO;
    private final BagaglioDAOPostgres bagaglioDAO;
    private final DatiPasseggeroDAOPostgres datiPasseggeroDAO;
    private final PrenotazioneDAOPostgres prenotazioneDAO;
    private final UtenteGenericoDAOPostgres utentiDAO;
    private final VoloDAOPostgres voloDAO;

    public Controller() {
        // Istanzia DAO
        this.adminDAO = new AmministratoreDAOPostgres();
        this.bagaglioDAO = new BagaglioDAOPostgres();
        this.datiPasseggeroDAO = new DatiPasseggeroDAOPostgres();
        this.prenotazioneDAO = new PrenotazioneDAOPostgres();
        this.utentiDAO = new UtenteGenericoDAOPostgres();
        this.voloDAO = new VoloDAOPostgres();

        // Collega i DAO al controller (per i factory/metodi di mapping)
        adminDAO.setController(this);
        bagaglioDAO.setController(this);
        datiPasseggeroDAO.setController(this);
        prenotazioneDAO.setController(this);
        utentiDAO.setController(this);

        // Inizializza cache da DB
        init();
    }

    /**
     * Inizializzazione: carica da DB. Se non ci sono voli, fa SEED dei voli iniziali su DB e ricarica.
     */
    public final void init() {
        ricaricaVoliDaDB();

        if (voliGestiti.isEmpty()) {
            seedVoliInizialiNelDB();
            ricaricaVoliDaDB();
        }

        // Se hai un metodo prenotazioneDAO.findAll(), puoi abilitare il caricamento anche delle prenotazioni
        ricaricaPrenotazioniDaDB();
    }

    private void ricaricaBagagliDaDB() {
        bagagli.clear();
        try {
            List<Bagaglio> tutti = bagaglioDAO.findAll();
            bagagli.addAll(tutti);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==========================
    // Voli (persistenti)
    // ==========================
    private void ricaricaVoliDaDB() {
        voliGestiti.clear();
        try {
            voliGestiti.addAll(voloDAO.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            // La UI mostrerà liste vuote se il DB non risponde
        }
    }

    private void seedVoliInizialiNelDB() {
        for (String[] r : VOLI_INIZIALI) {
            StatoVolo stato = parseStato(r[2]);

            Volo v = new Volo();                 // <— costruttore vuoto
            v.setCodiceUnivoco(r[0]);
            v.setCompagniaAerea(r[1]);
            v.setStato(stato);
            v.setDataVolo(r[3]);
            v.setOrarioPrevisto(r[4]);
            v.setAeroporto(r[5]);
            v.setGate(r[6]);
            v.setArrivoPartenza(r[7]);
            // opzionale: inizializza stimato uguale al previsto
            if (v.getOrarioStimato() == null || v.getOrarioStimato().isBlank()) {
                v.setOrarioStimato(v.getOrarioPrevisto());
            }

            try {
                voloDAO.insert(v); // ignora se già presente
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void aggiungiVolo(String codice,
                             String compagnia,
                             String data,
                             String orarioPrevisto,
                             String orarioStimato,
                             StatoVolo stato,
                             String direzione,     // "in arrivo" | "in partenza"
                             String aeroporto,     // IATA (3 lettere)
                             String gate) {
        Volo v = new Volo();             // <— costruttore vuoto
        v.setCodiceUnivoco(codice);
        v.setCompagniaAerea(compagnia);
        v.setDataVolo(data);
        v.setOrarioPrevisto(orarioPrevisto);
        v.setOrarioStimato(orarioStimato);
        v.setStato(stato);
        v.setArrivoPartenza(direzione);
        v.setAeroporto(aeroporto);
        v.setGate(gate);

        if (!voloDAO.insert(v)) {
            throw new RuntimeException("Impossibile inserire il volo " + codice);
        }
        this.voliGestiti.add(v);
    }

    public boolean rimuoviVolo(String codiceUnivoco) {
        if (voloDAO.delete(codiceUnivoco)) {
            return voliGestiti.removeIf(v -> v.getCodiceUnivoco().equalsIgnoreCase(codiceUnivoco));
        }
        return false;
    }

    public void aggiornaVolo(String codiceUnivoco,
                             String compagnia,
                             String dataVolo,
                             String orarioPrevisto,
                             String orarioStimato,
                             StatoVolo nuovoStato,
                             String direzione, // "in arrivo" | "in partenza"
                             String aeroporto,
                             String gate) {
        for (Volo volo : voliGestiti) {
            if (volo.getCodiceUnivoco().equals(codiceUnivoco)) {
                volo.setCompagniaAerea(compagnia);
                volo.setDataVolo(dataVolo);
                volo.setOrarioPrevisto(orarioPrevisto);
                volo.setOrarioStimato(orarioStimato);
                volo.setStato(nuovoStato);
                volo.setArrivoPartenza(direzione);
                volo.setAeroporto(aeroporto);
                volo.setGate(gate);

                if (!voloDAO.update(volo)) {
                    throw new RuntimeException("Impossibile aggiornare il volo " + codiceUnivoco);
                }
                return;
            }
        }
        throw new java.util.NoSuchElementException("Volo non trovato: " + codiceUnivoco);
    }

    public Volo cercaVolo(String codiceUnivoco) {
        for (Volo volo : voliGestiti) {
            if (volo.getCodiceUnivoco().equals(codiceUnivoco)) {
                return volo;
            }
        }
        return null;
    }

    // ==========================
    // Prenotazioni (persistenti)
    // ==========================
    private void ricaricaPrenotazioniDaDB() {
        prenotazioni.clear();


        try {
             prenotazioni.addAll(prenotazioneDAO.findAll());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Sostituisci l'intero metodo con questo
    public Prenotazione aggiungiPrenotazione(String numeroBiglietto, String posto, StatoPrenotazione stato,
                                             String numeroVolo, UtenteGenerico utenteGenerico, String nome, String cognome,
                                             String codiceFiscale, String email, Volo voloNonUsato) {

        if (numeroBiglietto == null || numeroBiglietto.isBlank())
            throw new IllegalArgumentException("Numero biglietto mancante");
        if (stato == null)
            throw new IllegalArgumentException("Stato prenotazione mancante");
        if (numeroVolo == null || numeroVolo.isBlank())
            throw new IllegalArgumentException("Numero volo mancante");
        if (codiceFiscale == null || codiceFiscale.isBlank())
            throw new IllegalArgumentException("Codice fiscale mancante");

        Volo v = getVoloByCodice(numeroVolo);
        if (v == null) throw new IllegalArgumentException("Volo inesistente: " + numeroVolo);

        // NON scriviamo su datipasseggeri: usiamo un oggetto "transitorio" solo per portare l'email al DAO prenotazioni.
        // Se esiste già in DB un datapasseggero con lo stesso CF/email, NON lo tocchiamo.
        DatiPasseggero dpEsistente = null;
        try {
            dpEsistente = datiPasseggeroDAO.findByCodiceFiscale(codiceFiscale);
        } catch (Exception ignored) {}

        DatiPasseggero dpForPren;
        if (dpEsistente != null) {
            // Usa i dati già presenti (ma NON fare update)
            dpForPren = dpEsistente;
        } else {
            // Crea un oggetto in memoria; NON chiamare insert/update su datipasseggeri
            dpForPren = new DatiPasseggero(nome, cognome, codiceFiscale, email);
        }

        // Normalizza il posto (se non valido => null, così lo genera il DAO)
        String postoNormalizzato = normalizeSeatOrNull(posto);

        Prenotazione pren = new Prenotazione(numeroBiglietto, postoNormalizzato, stato, utenteGenerico, dpForPren, v);

        if (!prenotazioneDAO.insert(pren)) {
            throw new RuntimeException("Impossibile inserire prenotazione " + numeroBiglietto);
        }

        System.out.printf("OK prenotazione: biglietto=%s, posto=%s, stato=%s, emailutente=%s, idvolo=%s%n",
                pren.getNumBiglietto(),
                pren.getPostoAssegnato(),
                pren.getStato().name(),
                dpForPren != null ? dpForPren.getEmail() : null,
                v.getCodiceUnivoco());

        prenotazioni.add(pren);
        return pren;
    }

    // Helper: ritorna sedile valido "A12" o null se raw è vuoto/non valido/"auto"
    private String normalizeSeatOrNull(String raw) {
        if (raw == null) return null;
        String t = raw.trim().toUpperCase().replaceAll("[^A-Z0-9]", "");
        if (t.isEmpty() || t.equals("AUTO") || t.equals("POSTOAUTO") || t.equals("POSTO")) return null;
        if (t.length() < 2) return null;
        char letter = t.charAt(0);
        if (letter < 'A' || letter > 'F') return null;
        try {
            int n = Integer.parseInt(t.substring(1));
            return (n >= 1 && n <= 30) ? (letter + String.valueOf(n)) : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public boolean rimuoviPrenotazione(String numeroPrenotazione) {
        if (prenotazioneDAO.delete(numeroPrenotazione)) {
            return prenotazioni.removeIf(p -> p.getNumBiglietto().equalsIgnoreCase(numeroPrenotazione));
        }
        return false;
    }

    public Prenotazione cercaPrenotazione(String numeroBiglietto) {
        // Prima cerca in cache
        for (Prenotazione p : prenotazioni) {
            if (p.getNumBiglietto().equals(numeroBiglietto)) {
                return p;
            }
        }

        // Se non è in cache, prova a leggere dal DB
        Prenotazione dalDB = prenotazioneDAO.findByCodice(numeroBiglietto);
        if (dalDB != null) {
            prenotazioni.add(dalDB); // aggiorna la cache
        }
        return dalDB;
    }

    public boolean salvaPrenotazione(Prenotazione prenotazione) {
        if (prenotazione == null) return false;

        // Persisti anche eventuali modifiche al passeggero
        DatiPasseggero dp = prenotazione.getDatiPasseggero();
        if (dp != null) {
            try {
                // update “morbido”: aggiorna solo se esiste
                if (datiPasseggeroDAO.findByCodiceFiscale(dp.getCodiceFiscale()) != null) {
                    if (!datiPasseggeroDAO.update(dp)) return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        if (!prenotazioneDAO.update(prenotazione)) return false;

        // riallinea la cache
        for (int i = 0; i < prenotazioni.size(); i++) {
            if (prenotazioni.get(i).getNumBiglietto().equals(prenotazione.getNumBiglietto())) {
                prenotazioni.set(i, prenotazione);
                return true;
            }
        }
        prenotazioni.add(prenotazione);
        return true;
    }

    public boolean annullaPrenotazione(Prenotazione prenotazione) {
        if (prenotazione == null) return false;
        prenotazione.setStato(StatoPrenotazione.CANCELLATA);
        return salvaPrenotazione(prenotazione);
    }

    // ==========================
    // Ricerca voli (su cache)
    // ==========================
    private String norm(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    private String safe(String s) { return s == null ? "" : s; }

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


    public String getVoloByAeroporto(String aeroporto) {
        if (aeroporto == null) return null;
        for (Volo v : voliGestiti) {
            // Confronta ignorando maiuscole/minuscole e spazi
            if (v.getAeroporto() != null && v.getAeroporto().trim().equalsIgnoreCase(aeroporto.trim())) {
                return v.getCodiceUnivoco();
            }
        }
        return null; // Nessun volo trovato per quell'aeroporto
    }


    public List<Volo> ricercaVoliRaw(String numeroVolo,
                                     String compagnia,
                                     String stato,
                                     String data,
                                     String orario,
                                     String aeroporto,
                                     String gate,
                                     String arrivoPartenza) {
        List<Volo> result = new ArrayList<>();
        for (Volo v : voliGestiti) {
            if (numeroVolo != null && (v.getCodiceUnivoco() == null || !v.getCodiceUnivoco().equalsIgnoreCase(numeroVolo))) continue;
            if (compagnia != null && (v.getCompagniaAerea() == null || !v.getCompagniaAerea().toLowerCase().contains(compagnia.toLowerCase()))) continue;
            if (stato != null && (v.getStato() == null || !v.getStato().name().equalsIgnoreCase(stato))) continue;
            if (data != null && (v.getDataVolo() == null || !v.getDataVolo().equals(data))) continue;
            if (orario != null && (v.getOrarioPrevisto() == null || !v.getOrarioPrevisto().equals(orario))) continue;
            if (aeroporto != null && (v.getAeroporto() == null || !v.getAeroporto().equalsIgnoreCase(aeroporto))) continue;
            if (gate != null && (v.getGate() == null || !v.getGate().equalsIgnoreCase(gate))) continue;
            if (arrivoPartenza != null && (v.getArrivoPartenza() == null || !v.getArrivoPartenza().equalsIgnoreCase(arrivoPartenza))) continue;

            result.add(v);
        }
        return result;
    }

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
                    safe(v.getCodiceUnivoco()),                          // 0 Numero Volo
                    safe(v.getCompagniaAerea()),                         // 1 Compagnia
                    v.getStato() != null ? v.getStato().name() : "",    // 2 Stato
                    safe(v.getDataVolo()),                               // 3 Data
                    safe(v.getOrarioPrevisto()),                         // 4 Orario previsto
                    safe(v.getOrarioStimato()),                          // 5 Orario stimato
                    safe(v.getAeroporto()),                              // 6 Aeroporto Destinazione
                    safe(v.getGate()),                                   // 7 GATE
                    safe(v.getArrivoPartenza())                          // 8 ARRIVO/PARTENZA
            });
        }
        return righe;
    }
    public List<Object[]> tuttiVoli() {
        List<Object[]> rows = new java.util.ArrayList<>();
        for (Volo v : voliGestiti) {
            rows.add(new Object[]{
                    v.getCodiceUnivoco(),                      // 0 Numero Volo
                    v.getCompagniaAerea(),                     // 1 Compagnia
                    v.getStato() != null ? v.getStato().name() : "", // 2 Stato
                    v.getDataVolo(),                           // 3 Data
                    v.getOrarioPrevisto(),                     // 4 Orario previsto
                    v.getOrarioStimato(),                      // 5 Orario stimato
                    v.getAeroporto(),                          // 6 Aeroporto Destinazione
                    v.getGate(),                               // 7 GATE
                    v.getArrivoPartenza()                      // 8 ARRIVO/PARTENZA
            });
        }
        return rows;
    }
    // ==========================
    // Passeggeri (su cache, alimentata quando inserisci/aggiorni/ricarichi)
    // ==========================
    // Sostituisci la firma esistente con questa (8 parametri)
    public List<Object[]> ricercaPasseggeri(String nome,
                                            String cognome,
                                            String email,
                                            String codiceFiscale,
                                            String numeroVolo,
                                            String numeroPrenotazione,
                                            String postoAssegnato,
                                            String statoPrenotazione) {
        List<Object[]> risultati = new ArrayList<>();

        String n  = norm(nome);
        String c  = norm(cognome);
        String em = norm(email);
        String cf = norm(codiceFiscale);
        String nv = norm(numeroVolo);
        String np = norm(numeroPrenotazione);
        String pa = norm(postoAssegnato);

        for (Prenotazione p : prenotazioni) {
            boolean match = true;
            DatiPasseggero dp = p.getDatiPasseggero();

            if (n != null && (dp == null || dp.getNome() == null ||
                    !dp.getNome().toLowerCase().contains(n.toLowerCase())))
                match = false;

            if (match && c != null && (dp == null || dp.getCognome() == null ||
                    !dp.getCognome().toLowerCase().contains(c.toLowerCase())))
                match = false;

            if (match && em != null && (dp == null || dp.getEmail() == null ||
                    !dp.getEmail().toLowerCase().contains(em.toLowerCase())))
                match = false;

            if (match && cf != null && (dp == null || dp.getCodiceFiscale() == null ||
                    !dp.getCodiceFiscale().toLowerCase().contains(cf.toLowerCase())))
                match = false;

            if (match && nv != null && (p.getVolo() == null || p.getVolo().getCodiceUnivoco() == null ||
                    !p.getVolo().getCodiceUnivoco().toLowerCase().contains(nv.toLowerCase())))
                match = false;

            if (match && np != null && (p.getNumBiglietto() == null ||
                    !p.getNumBiglietto().toLowerCase().contains(np.toLowerCase())))
                match = false;

            if (match && pa != null && (p.getPostoAssegnato() == null ||
                    !p.getPostoAssegnato().toLowerCase().contains(pa.toLowerCase())))
                match = false;

            if (match && statoPrenotazione != null &&
                    (p.getStato() == null || !p.getStato().name().equalsIgnoreCase(statoPrenotazione)))
                match = false;

            if (match) {
                risultati.add(new Object[]{
                        safe(dp != null ? dp.getNome() : ""),
                        safe(dp != null ? dp.getCognome() : ""),
                        safe(dp != null ? dp.getEmail() : ""),            // Email: 3a colonna
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

    public List<Object[]> tuttiPasseggeri() {
        return ricercaPasseggeri(null, null, null, null, null, null, null, null);
    }

    // ==========================
    // Bagagli (opzionale: persistenza)
    // ==========================
    public boolean aggiungiBagaglio(Bagaglio bagaglio) {
        // controlla duplicati direttamente nel DB
        if (bagaglioDAO.findById(bagaglio.getCodUnivoco()) != null) {
            return false;
        }
        return bagaglioDAO.insert(bagaglio);
    }

    public boolean aggiungiBagaglio(String codice, StatoBagaglio stato) {
        Bagaglio nuovo = new Bagaglio(codice, 0.0, stato, null);
        return aggiungiBagaglio(nuovo);
    }

    public List<Bagaglio> getBagagli() {
        return bagaglioDAO.findAll();
    }

    public List<Bagaglio> trovaTuttiBagagli() {
        return bagaglioDAO.findAll();
    }

    public List<Object[]> ricercaBagagli(String codiceBagaglio, String stato) {
        List<Object[]> risultati = new ArrayList<>();
        for (Bagaglio b : bagaglioDAO.findAll()) {
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

    public boolean rimuoviBagaglio(String codice) {
        return bagaglioDAO.delete(codice);
    }

    public List<Object[]> tuttiBagagliRows() {
        return ricercaBagagli(null, null);
    }

    public boolean aggiornaBagaglio(Bagaglio bagaglio) {
        if (bagaglio == null) return false;
        return bagaglioDAO.update(bagaglio);
    }




    // ==========================
    // Gate (solo in memoria – se vuoi, aggiungi tabella e DAO)
    // ==========================
    public boolean aggiungiGate(int numero) {
        for (Gate g : gates) {
            if (g.getNumero() == numero) {
                return false;
            }
        }
        gates.add(new Gate(numero));
        return true;
    }

    public boolean eliminaGate(int numero) {
        return gates.removeIf(g -> g.getNumero() == numero);
    }

    public List<Gate> getGates() {
        return gates;
    }

    // ==========================
    // DAO <-> Controller helpers
    // ==========================
    public Amministratore creaAmministratore(String login, String password, String nome, String cognome) {
        Amministratore admin = new Amministratore(login, password, nome, cognome);
        amministratori.add(admin);
        return admin;
    }

    public List<Amministratore> getAmministratori() {
        return amministratori;
    }

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

    public DatiPasseggero getDatiPasseggeroByCodiceFiscale(String codiceFiscale) {
        for (DatiPasseggero d : datiPasseggeri) {
            if (codiceFiscale != null && codiceFiscale.equals(d.getCodiceFiscale())) {
                return d;
            }
        }
        return null;
    }

    /*
    public Prenotazione creaPrenotazione(String numBiglietto, String posto, StatoPrenotazione stato,
                                         UtenteGenerico utente, DatiPasseggero dp, Volo volo) {
        Prenotazione p = new Prenotazione(numBiglietto, posto, stato, utente, dp, volo);
        prenotazioni.add(p);
        return p;
    }

     */

    public List<UtenteGenerico> getTuttiUtenti() {
        return utenti;
    }

    public UtenteGenerico getUtenteByEmail(String email) {
        for (UtenteGenerico u : getTuttiUtenti()) {
            // Se il tuo modello ha getEmail(), usa quello. Qui mantengo getNomeUtente come "email/login"
            if (email != null && email.equalsIgnoreCase(u.getNomeUtente())) {
                return u;
            }
        }
        return null;
    }

    public UtenteGenerico creaUtenteGenerico(String emailUtente) {
        UtenteGenerico u = new UtenteGenerico(emailUtente, "", "", "", new ArrayList<>(), new AreaPersonale());
        utenti.add(u);
        return u;
    }

    public Volo getVoloByCodice(String codiceVolo) {
        for (Volo v : voliGestiti) {
            if (v.getCodiceUnivoco() != null && v.getCodiceUnivoco().equals(codiceVolo)) {
                return v;
            }
        }
        return null;
    }

    public Volo creaVolo(String codiceVolo) {
        Volo v = new Volo();             // <— costruttore vuoto
        v.setCodiceUnivoco(codiceVolo);
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
        return null;
    }

    public DatiPasseggeroDAOPostgres getDatiPasseggeroDAO() {
        return datiPasseggeroDAO;
    }

    // ==========================
    // GUI helpers
    // ==========================
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

    // ==========================
    // Dati di seed
    // ==========================
    private static final String[][] VOLI_INIZIALI = {
            {"AZ123", "ITA Airways", "PROGRAMMATO", "2025-09-05", "08:15", "MIL", "3", "in arrivo"},
            {"FR987", "Ryanair", "IMBARCO", "2025-09-05", "08:40", "BAR", "21", "in arrivo"},
            {"LH455", "Lufthansa", "DECOLLATO", "2025-09-05", "08:55", "MAD", "15", "in partenza"},
            {"U23610", "easyJet", "CANCELLATO", "2025-09-05", "09:05", "LDN", "9", "in arrivo"},
            {"AF101", "Air France", "INRITARDO", "2025-09-05", "09:20", "MYK", "5", "in partenza"},
            {"EK092", "Emirates", "ATTERRATO", "2025-09-05", "09:35", "PAR", "12", "in partenza"}
    };

}