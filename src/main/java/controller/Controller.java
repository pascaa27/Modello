package controller;

import gui.AreaPersonaleAmmGUI;
import dao.postgres.*;
import model.*;

import javax.swing.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller "persistente":
 * - carica i voli dal DB all'avvio
 * - fa seed dei voli iniziali SOLO se il DB è vuoto
 * - tutte le operazioni di aggiunta/rimozione/aggiornamento passano dai DAO (DB prima, poi cache in memoria)
 * - le liste in memoria sono cache della UI, NON la verità dei dati
 */
public class Controller {

    private static final Logger LOGGER = Logger.getLogger(Controller.class.getName());

    // Costanti ricorrenti
    private static final String ARRIVO = "in arrivo";
    private static final String PARTENZA = "in partenza";
    private static final String SEED_DATE = "2025-09-05";

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

        // Collega i DAO al controller (solo dove serve)
        adminDAO.setController(this);
        bagaglioDAO.setController(this);
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

        ricaricaPrenotazioniDaDB();
        ricaricaBagagliDaDB(); // valorizza anche la cache bagagli per evitare metodo "unused"
    }

    private void ricaricaBagagliDaDB() {
        bagagli.clear();
        try {
            List<Bagaglio> tutti = bagaglioDAO.findAll();
            bagagli.addAll(tutti);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Impossibile ricaricare i bagagli dal DB", e);
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
            LOGGER.log(Level.WARNING, "Impossibile ricaricare i voli dal DB", e);
            // La UI mostrerà liste vuote se il DB non risponde
        }
    }

    private void seedVoliInizialiNelDB() {
        for (String[] r : VOLI_INIZIALI) {
            StatoVolo stato = parseStato(r[2]);

            Volo v = new Volo(); // costruttore vuoto
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
                LOGGER.log(Level.FINE, () -> "Seed volo fallito per " + v.getCodiceUnivoco());
                LOGGER.log(Level.FINER, "Dettagli", e);
            }
        }
    }

    // Nuovi overload a oggetto per ridurre S107
    public static final class VoloInput {
        public final String codice;
        public final String compagnia;
        public final String data;
        public final String orarioPrevisto;
        public final String orarioStimato;
        public final StatoVolo stato;
        public final String direzione;   // "in arrivo" | "in partenza"
        public final String aeroporto;   // IATA
        public final String gate;

        public VoloInput(String codice, String compagnia, String data, String orarioPrevisto, String orarioStimato,
                         StatoVolo stato, String direzione, String aeroporto, String gate) {
            this.codice = codice;
            this.compagnia = compagnia;
            this.data = data;
            this.orarioPrevisto = orarioPrevisto;
            this.orarioStimato = orarioStimato;
            this.stato = stato;
            this.direzione = direzione;
            this.aeroporto = aeroporto;
            this.gate = gate;
        }
    }

    public void aggiungiVolo(VoloInput in) {
        Volo v = new Volo(); // costruttore vuoto
        v.setCodiceUnivoco(in.codice);
        v.setCompagniaAerea(in.compagnia);
        v.setDataVolo(in.data);
        v.setOrarioPrevisto(in.orarioPrevisto);
        v.setOrarioStimato(in.orarioStimato);
        v.setStato(in.stato);
        v.setArrivoPartenza(in.direzione);
        v.setAeroporto(in.aeroporto);
        v.setGate(in.gate);

        if (!voloDAO.insert(v)) {
            throw new ControllerOperationException("Impossibile inserire il volo " + in.codice);
        }
        this.voliGestiti.add(v);
    }

    @SuppressWarnings("java:S107")
    @Deprecated(since = "1.0", forRemoval = false)
    public void aggiungiVolo(String codice,
                             String compagnia,
                             String data,
                             String orarioPrevisto,
                             String orarioStimato,
                             StatoVolo stato,
                             String direzione,
                             String aeroporto,
                             String gate) {
        aggiungiVolo(new VoloInput(codice, compagnia, data, orarioPrevisto, orarioStimato, stato, direzione, aeroporto, gate));
    }

    public boolean rimuoviVolo(String codiceUnivoco) {
        return voloDAO.delete(codiceUnivoco)
                && voliGestiti.removeIf(v -> v.getCodiceUnivoco().equalsIgnoreCase(codiceUnivoco));
    }

    public void aggiornaVolo(VoloInput in) {
        for (Volo volo : voliGestiti) {
            if (safeEquals(volo.getCodiceUnivoco(), in.codice)) {
                volo.setCompagniaAerea(in.compagnia);
                volo.setDataVolo(in.data);
                volo.setOrarioPrevisto(in.orarioPrevisto);
                volo.setOrarioStimato(in.orarioStimato);
                volo.setStato(in.stato);
                volo.setArrivoPartenza(in.direzione);
                volo.setAeroporto(in.aeroporto);
                volo.setGate(in.gate);

                if (!voloDAO.update(volo)) {
                    throw new ControllerOperationException("Impossibile aggiornare il volo " + in.codice);
                }
                return;
            }
        }
        throw new NoSuchElementException("Volo non trovato: " + in.codice);
    }

    @SuppressWarnings("java:S107")
    @Deprecated(since = "1.0", forRemoval = false)
    public void aggiornaVolo(String codiceUnivoco,
                             String compagnia,
                             String dataVolo,
                             String orarioPrevisto,
                             String orarioStimato,
                             StatoVolo nuovoStato,
                             String direzione,
                             String aeroporto,
                             String gate) {
        aggiornaVolo(new VoloInput(codiceUnivoco, compagnia, dataVolo, orarioPrevisto, orarioStimato, nuovoStato, direzione, aeroporto, gate));
    }

    public Volo cercaVolo(String codiceUnivoco) {
        for (Volo volo : voliGestiti) {
            if (safeEquals(volo.getCodiceUnivoco(), codiceUnivoco)) {
                return volo;
            }
        }
        return null;
    }

    public Volo cercaVoloPerDestinazioneEData(String dest, String data) {
        for (Volo v : voliGestiti) {
            if (equalsIgnoreCase(v.getAeroporto(), dest) && safeEquals(v.getDataVolo(), data)) {
                return v;
            }
        }
        return null;
    }

    public List<Volo> cercaVoliPerDestinazione(String dest) {
        List<Volo> result = new ArrayList<>();
        for (Volo v : voliGestiti) {
            if (equalsIgnoreCase(v.getAeroporto(), dest)) {
                result.add(v);
            }
        }
        return result;
    }

    public boolean utenteHaPrenotazionePerVolo(String email, String codiceVolo) {
        List<Prenotazione> prenotazioniUtente = prenotazioneDAO.findByEmailUtente(email);
        for (Prenotazione p : prenotazioniUtente) {
            if (p.getVolo() != null && safeEquals(p.getVolo().getCodiceUnivoco(), codiceVolo)) {
                return true;
            }
        }
        return false;
    }

    // ==========================
    // Prenotazioni (persistenti)
    // ==========================
    private void ricaricaPrenotazioniDaDB() {
        prenotazioni.clear();
        try {
            prenotazioni.addAll(prenotazioneDAO.findAll());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Impossibile ricaricare le prenotazioni dal DB", e);
        }
    }

    public static final class PrenotazioneInput {
        public final String numeroBiglietto;
        public final String posto;
        public final StatoPrenotazione stato;
        public final String numeroVolo;
        public final UtenteGenerico utenteGenerico;
        public final String nome;
        public final String cognome;
        public final String codiceFiscale;
        public final String email;

        public PrenotazioneInput(String numeroBiglietto, String posto, StatoPrenotazione stato, String numeroVolo,
                                 UtenteGenerico utenteGenerico, String nome, String cognome,
                                 String codiceFiscale, String email) {
            this.numeroBiglietto = numeroBiglietto;
            this.posto = posto;
            this.stato = stato;
            this.numeroVolo = numeroVolo;
            this.utenteGenerico = utenteGenerico;
            this.nome = nome;
            this.cognome = cognome;
            this.codiceFiscale = codiceFiscale;
            this.email = email;
        }
    }

    public Prenotazione aggiungiPrenotazione(PrenotazioneInput in) {
        // Validazioni
        if (in.numeroBiglietto == null || in.numeroBiglietto.isBlank())
            throw new IllegalArgumentException("Numero biglietto mancante");
        if (in.stato == null)
            throw new IllegalArgumentException("Stato prenotazione mancante");
        if (in.numeroVolo == null || in.numeroVolo.isBlank())
            throw new IllegalArgumentException("Numero volo mancante");
        if (in.codiceFiscale == null || in.codiceFiscale.isBlank())
            throw new IllegalArgumentException("Codice fiscale mancante");
        if (in.email == null || in.email.isBlank())
            throw new IllegalArgumentException("Email mancante");

        // Recupera il volo
        Volo v = getVoloByCodice(in.numeroVolo);
        if (v == null) throw new IllegalArgumentException("Volo inesistente: " + in.numeroVolo);

        // Verifica passeggero via email
        DatiPasseggero dp = datiPasseggeroDAO.findByEmail(in.email);
        if (dp == null) {
            dp = new DatiPasseggero(in.nome, in.cognome, in.codiceFiscale, in.email);
        } else if (dp.getCodiceFiscale() == null || !dp.getCodiceFiscale().equalsIgnoreCase(in.codiceFiscale)) {
            dp.setCodiceFiscale(in.codiceFiscale);
        }

        String postoNormalizzato = normalizeSeatOrNull(in.posto);

        Prenotazione pren = new Prenotazione(in.numeroBiglietto, postoNormalizzato, in.stato, in.utenteGenerico, dp, v);

        boolean inserito = prenotazioneDAO.insert(pren, in.utenteGenerico);
        if (!inserito) {
            LOGGER.log(Level.WARNING, () -> "Impossibile inserire prenotazione " + in.numeroBiglietto + " per volo " + in.numeroVolo);
            return null;
        }

        LOGGER.log(Level.INFO,
                "OK prenotazione: biglietto={0}, posto={1}, stato={2}, emailutente={3}, idvolo={4}",
                new Object[]{pren.getNumBiglietto(), pren.getPostoAssegnato(), pren.getStato().name(), dp.getEmail(), v.getCodiceUnivoco()});

        prenotazioni.add(pren);
        return pren;
    }

    @SuppressWarnings("java:S107")
    @Deprecated(since = "1.0", forRemoval = false)
    public Prenotazione aggiungiPrenotazione(String numeroBiglietto, String posto, StatoPrenotazione stato,
                                             String numeroVolo, UtenteGenerico utenteGenerico, String nome, String cognome,
                                             String codiceFiscale, String email, Volo voloNonUsato) {
        if (voloNonUsato != null) {
            LOGGER.log(Level.FINER, "Parametro voloNonUsato ignorato: {0}", voloNonUsato.getCodiceUnivoco());
        }
        return aggiungiPrenotazione(new PrenotazioneInput(
                numeroBiglietto, posto, stato, numeroVolo, utenteGenerico, nome, cognome, codiceFiscale, email
        ));
    }

    public List<Prenotazione> getPrenotazioniUtente(UtenteGenerico utente) {
        if (utente == null) return Collections.emptyList();

        List<Prenotazione> result = new ArrayList<>();
        for (Prenotazione p : prenotazioni) {
            if (p.getUtenteGenerico() != null &&
                    utente.getNomeUtente() != null &&
                    equalsIgnoreCase(utente.getNomeUtente(), p.getUtenteGenerico().getNomeUtente())) {
                result.add(p);
            }
        }

        // Carica dal DB solo le prenotazioni mancanti
        try {
            List<Prenotazione> dalDB = prenotazioneDAO.findByEmailUtente(utente.getLogin());
            for (Prenotazione p : dalDB) {
                if (!containsPrenotazione(prenotazioni, p.getNumBiglietto())) prenotazioni.add(p);
                if (!containsPrenotazione(result, p.getNumBiglietto())) result.add(p);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Errore nel caricamento prenotazioni utente={0}", utente.getLogin());
            LOGGER.log(Level.FINE, "Dettagli", e);
        }

        return result;
    }

    private boolean containsPrenotazione(List<Prenotazione> list, String numBiglietto) {
        for (Prenotazione p : list) {
            if (safeEquals(p.getNumBiglietto(), numBiglietto)) return true;
        }
        return false;
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
        } catch (NumberFormatException _) {
            return null;
        }
    }

    public boolean rimuoviPrenotazione(String numeroPrenotazione) {
        return prenotazioneDAO.delete(numeroPrenotazione)
                && prenotazioni.removeIf(p -> safeEqualsIgnoreCase(p.getNumBiglietto(), numeroPrenotazione));
    }

    public Prenotazione cercaPrenotazione(String numeroBiglietto) {
        // Prima cerca in cache
        for (Prenotazione p : prenotazioni) {
            if (safeEquals(p.getNumBiglietto(), numeroBiglietto)) {
                return p;
            }
        }

        // Se non è in cache, prova a leggere dal DB
        Prenotazione dalDB = prenotazioneDAO.findByCodice(numeroBiglietto);
        if (dalDB != null && !containsPrenotazione(prenotazioni, dalDB.getNumBiglietto())) {
            prenotazioni.add(dalDB); // aggiorna la cache
        }

        return dalDB;
    }

    public boolean salvaPrenotazione(Prenotazione prenotazione) {
        if (prenotazione == null) return false;

        DatiPasseggero dp = prenotazione.getDatiPasseggero();
        if (dp != null) {
            try {
                // aggiorna SOLO se il passeggero è registrato
                if (datiPasseggeroDAO.findByEmail(dp.getEmail()) != null) {
                    if (!datiPasseggeroDAO.update(dp)) return false;
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Errore aggiornando DatiPasseggero per email={0}", dp.getEmail());
                LOGGER.log(Level.FINE, "Dettagli", e);
                return false;
            }
        }

        // aggiorna la prenotazione (tabella prenotazioni)
        if (!prenotazioneDAO.update(prenotazione)) return false;

        // riallinea la cache SENZA duplicati
        boolean found = false;
        for (int i = 0; i < prenotazioni.size(); i++) {
            if (safeEquals(prenotazioni.get(i).getNumBiglietto(), prenotazione.getNumBiglietto())) {
                prenotazioni.set(i, prenotazione);
                found = true;
                break;
            }
        }
        if (!found) prenotazioni.add(prenotazione);

        return true;
    }

    public boolean annullaPrenotazione(Prenotazione p) {
        return p != null && prenotazioneDAO.delete(p.getNumBiglietto());
    }

    public boolean emailRegistrata(String email) {
        return utentiDAO.emailEsiste(email);
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

    private boolean safeEquals(String a, String b) {
        return Objects.equals(a, b);
    }

    private boolean safeEqualsIgnoreCase(String a, String b) {
        return a == null ? b == null : b != null && a.equalsIgnoreCase(b);
    }

    private boolean containsIgnoreCase(String text, String needle) {
        if (needle == null) return true;
        if (text == null) return false;
        return text.toLowerCase().contains(needle.toLowerCase());
    }

    private boolean equalsIgnoreCase(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.trim().equalsIgnoreCase(b.trim());
    }

    private StatoVolo parseStato(String s) {
        if (s == null) return null;
        s = s.trim().toUpperCase();
        try {
            return StatoVolo.valueOf(s);
        } catch (IllegalArgumentException _) {
            LOGGER.log(Level.FINEST, "Stato volo sconosciuto: {0}", s);
            return null;
        }
    }

    private String canonicalAP(String ap) {
        if (ap == null) return null;
        String x = ap.trim().toLowerCase();
        if (ARRIVO.equalsIgnoreCase(x)) return ARRIVO;
        if (PARTENZA.equalsIgnoreCase(x)) return PARTENZA;
        return ap;
    }


    public List<Object[]> tuttiVoli() {
        List<Object[]> rows = new ArrayList<>();
        for (Volo v : voliGestiti) {
            rows.add(new Object[]{
                    v.getCodiceUnivoco(),
                    v.getCompagniaAerea(),
                    v.getStato() != null ? v.getStato().name() : "",
                    v.getDataVolo(),
                    v.getOrarioPrevisto(),
                    v.getOrarioStimato(),
                    v.getAeroporto(),
                    v.getGate(),
                    v.getArrivoPartenza()
            });
        }
        return rows;
    }

    private boolean matchVolo(Volo v,
                              String numeroVolo,
                              String compagnia,
                              String stato,
                              String data,
                              String orario,
                              String aeroporto,
                              String gate,
                              String arrivoPartenza) {
        return (numeroVolo == null || equalsIgnoreCase(v.getCodiceUnivoco(), numeroVolo))
                && (compagnia == null || containsIgnoreCase(v.getCompagniaAerea(), compagnia))
                && (stato == null || (v.getStato() != null && v.getStato().name().equalsIgnoreCase(stato)))
                && (data == null || safeEquals(v.getDataVolo(), data))
                && (orario == null || safeEquals(v.getOrarioPrevisto(), orario))
                && (aeroporto == null || equalsIgnoreCase(v.getAeroporto(), aeroporto))
                && (gate == null || equalsIgnoreCase(v.getGate(), gate))
                && (arrivoPartenza == null || equalsIgnoreCase(v.getArrivoPartenza(), arrivoPartenza));
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
        String ap = canonicalAP(arrivoPartenza);
        for (Volo v : voliGestiti) {
            if (matchVolo(v, numeroVolo, compagnia, stato, data, orario, aeroporto, gate, ap)) {
                result.add(v);
            }
        }
        return result;
    }

    public List<Object[]> ricercaVoli(String numeroVolo, String compagnia, String stato, String data,
                                      String orario, String aeroporto, String gate, String arrivoPartenza) {
        String ap = canonicalAP(arrivoPartenza);
        List<Volo> trovati = ricercaVoliRaw(numeroVolo, compagnia, stato, data, orario, aeroporto, gate, ap);
        List<Object[]> righe = new ArrayList<>();
        for (Volo v : trovati) {
            righe.add(voloToRow(v));
        }
        return righe;
    }

    private Object[] voloToRow(Volo v) {
        return new Object[]{
                safe(v.getCodiceUnivoco()),                          // 0 Numero Volo
                safe(v.getCompagniaAerea()),                         // 1 Compagnia
                v.getStato() != null ? v.getStato().name() : "",    // 2 Stato
                safe(v.getDataVolo()),                               // 3 Data
                safe(v.getOrarioPrevisto()),                         // 4 Orario previsto
                safe(v.getOrarioStimato()),                          // 5 Orario stimato
                safe(v.getAeroporto()),                              // 6 Aeroporto Destinazione
                safe(v.getGate()),                                   // 7 GATE
                safe(v.getArrivoPartenza())                          // 8 ARRIVO/PARTENZA
        };
    }

    // ==========================
    // Passeggeri (su cache, alimentata quando inserisci/aggiorni/ricarichi)
    // ==========================
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
            if (matchPrenotazione(p, n, c, em, cf, nv, np, pa, statoPrenotazione)) {
                risultati.add(prenotazioneToRow(p));
            }
        }
        return risultati;
    }

    private boolean matchPrenotazione(Prenotazione p,
                                      String n, String c, String em, String cf,
                                      String nv, String np, String pa,
                                      String statoPrenotazione) {
        DatiPasseggero dp = p.getDatiPasseggero();

        return (n == null || containsIgnoreCase(dp != null ? dp.getNome() : null, n))
                && (c == null || containsIgnoreCase(dp != null ? dp.getCognome() : null, c))
                && (em == null || containsIgnoreCase(dp != null ? dp.getEmail() : null, em))
                && (cf == null || containsIgnoreCase(dp != null ? dp.getCodiceFiscale() : null, cf))
                && (nv == null || containsIgnoreCase(p.getVolo() != null ? p.getVolo().getCodiceUnivoco() : null, nv))
                && (np == null || containsIgnoreCase(p.getNumBiglietto(), np))
                && (pa == null || containsIgnoreCase(p.getPostoAssegnato(), pa))
                && (statoPrenotazione == null || (p.getStato() != null && p.getStato().name().equalsIgnoreCase(statoPrenotazione)));
    }

    private Object[] prenotazioneToRow(Prenotazione p) {
        DatiPasseggero dp = p.getDatiPasseggero();
        return new Object[]{
                safe(dp != null ? dp.getNome() : ""),
                safe(dp != null ? dp.getCognome() : ""),
                safe(dp != null ? dp.getEmail() : ""),            // Email: 3a colonna
                safe(dp != null ? dp.getCodiceFiscale() : ""),
                p.getVolo() != null ? safe(p.getVolo().getCodiceUnivoco()) : "",
                safe(p.getNumBiglietto()),
                safe(p.getPostoAssegnato()),
                p.getStato() != null ? p.getStato().name() : ""
        };
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
        return bagaglio != null && bagaglioDAO.update(bagaglio);
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
            if (safeEquals(a.getLogin(), login)) {
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
        Volo v = new Volo(); // costruttore vuoto
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
            {"AZ123", "ITA Airways", "PROGRAMMATO", SEED_DATE, "08:15", "MIL", "3", ARRIVO},
            {"FR987", "Ryanair", "IMBARCO", SEED_DATE, "08:40", "BAR", "21", ARRIVO},
            {"LH455", "Lufthansa", "DECOLLATO", SEED_DATE, "08:55", "MAD", "15", PARTENZA},
            {"U23610", "easyJet", "CANCELLATO", SEED_DATE, "09:05", "LDN", "9", ARRIVO},
            {"AF101", "Air France", "INRITARDO", SEED_DATE, "09:20", "MYK", "5", PARTENZA},
            {"EK092", "Emirates", "ATTERRATO", SEED_DATE, "09:35", "PAR", "12", PARTENZA}
    };

    // Eccezione dedicata per operazioni controller fallite
    public static class ControllerOperationException extends RuntimeException {
        public ControllerOperationException(String message) { super(message); }
        public ControllerOperationException(String message, Throwable cause) { super(message, cause); }
    }
}