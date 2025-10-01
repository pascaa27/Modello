package controller;

import gui.AreaPersonaleAmmGUI;
import dao.postgres.*;
import model.*;

import javax.swing.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Controller {

    private static final Logger LOGGER = Logger.getLogger(Controller.class.getName());
    private static final String LOG_DETAILS = "Dettagli";


    private static final String ARRIVO = "in arrivo";
    private static final String PARTENZA = "in partenza";
    private static final String SEED_DATE = "2025-09-05";


    private final List<Volo> voliGestiti = new ArrayList<>();
    private final List<Prenotazione> prenotazioni = new ArrayList<>();
    private final List<Gate> gates = new ArrayList<>();
    private final List<Amministratore> amministratori = new ArrayList<>();
    private final List<DatiPasseggero> datiPasseggeri = new ArrayList<>();
    private final List<UtenteGenerico> utenti = new ArrayList<>();
    private final List<Bagaglio> bagagli = new ArrayList<>();


    private final AmministratoreDAOPostgres adminDAO;
    private final BagaglioDAOPostgres bagaglioDAO;
    private final DatiPasseggeroDAOPostgres datiPasseggeroDAO;
    private final PrenotazioneDAOPostgres prenotazioneDAO;
    private final UtenteGenericoDAOPostgres utentiDAO;
    private final VoloDAOPostgres voloDAO;

    /**
     * Costruttore: inizializza tutti i DAO e la cache, collegando i DAO al controller.
     */
    public Controller() {
        this.adminDAO = new AmministratoreDAOPostgres();
        this.bagaglioDAO = new BagaglioDAOPostgres();
        this.datiPasseggeroDAO = new DatiPasseggeroDAOPostgres();
        this.prenotazioneDAO = new PrenotazioneDAOPostgres();
        this.utentiDAO = new UtenteGenericoDAOPostgres();
        this.voloDAO = new VoloDAOPostgres();


        adminDAO.setController(this);
        bagaglioDAO.setController(this);
        prenotazioneDAO.setController(this);
        utentiDAO.setController(this);

        // Inizializza cache da DB
        init();
    }

    /**
     * Inizializza la cache dal DB e fa seed dei voli se necessario.
     */
    public final void init() {
        ricaricaVoliDaDB();

        if(voliGestiti.isEmpty()) {
            seedVoliInizialiNelDB();
            ricaricaVoliDaDB();
        }

        ricaricaPrenotazioniDaDB();
        ricaricaBagagliDaDB(); // valorizza anche la cache bagagli
    }

    /**
     * Ricarica tutti i bagagli dal database e aggiorna la cache locale.
     */
    private void ricaricaBagagliDaDB() {
        bagagli.clear();
        try {
            bagagli.addAll(bagaglioDAO.findAll());
        } catch(Exception e) {
            LOGGER.log(Level.WARNING, "Impossibile ricaricare i bagagli dal DB", e);
            LOGGER.log(Level.FINE, LOG_DETAILS, e);
        }
    }

    /**
     * Ricarica tutti i voli dal database e aggiorna la cache locale.
     */
    private void ricaricaVoliDaDB() {
        voliGestiti.clear();
        try {
            voliGestiti.addAll(voloDAO.findAll());
        } catch(Exception e) {
            LOGGER.log(Level.WARNING, "Impossibile ricaricare i voli dal DB", e);
            LOGGER.log(Level.FINE, LOG_DETAILS, e);
        }
    }

    /**
     * Inserisce alcuni voli iniziali nel database se è vuoto.
     */
    private void seedVoliInizialiNelDB() {
        for(String[] r : VOLI_INIZIALI) {
            StatoVolo stato = parseStato(r[2]);

            Volo v = new Volo();
            v.setCodiceUnivoco(r[0]);
            v.setCompagniaAerea(r[1]);
            v.setStato(stato);
            v.setDataVolo(r[3]);
            v.setOrarioPrevisto(r[4]);
            v.setAeroporto(r[5]);
            v.setGate(r[6]);
            v.setArrivoPartenza(r[7]);
            if(v.getOrarioStimato() == null || v.getOrarioStimato().isBlank()) {
                v.setOrarioStimato(v.getOrarioPrevisto());
            }

            try {
                voloDAO.insert(v);
            } catch(Exception e) {
                LOGGER.log(Level.FINE, () -> "Seed volo fallito per " + v.getCodiceUnivoco());
                LOGGER.log(Level.FINER, LOG_DETAILS, e);
            }
        }
    }


    public static final class VoloInput {
        public final String codice;
        public final String compagnia;
        public final String data;
        public final String orarioPrevisto;
        public final String orarioStimato;
        public final StatoVolo stato;
        public final String direzione;
        public final String aeroporto;
        public final String gate;


        //@SuppressWarnings("java:S107")
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


    private static final class VoloFilter {
        final String numeroVolo;
        final String compagnia;
        final String stato;
        final String data;
        final String orario;
        final String aeroporto;
        final String gate;
        final String arrivoPartenza;

        VoloFilter(String numeroVolo, String compagnia, String stato, String data,
                   String orario, String aeroporto, String gate, String arrivoPartenza) {
            this.numeroVolo = numeroVolo;
            this.compagnia = compagnia;
            this.stato = stato;
            this.data = data;
            this.orario = orario;
            this.aeroporto = aeroporto;
            this.gate = gate;
            this.arrivoPartenza = arrivoPartenza;
        }
    }

    private static final class PrenotazioneFilter {
        final String nome;
        final String cognome;
        final String email;
        final String codiceFiscale;
        final String numeroVolo;
        final String numeroPrenotazione;
        final String postoAssegnato;
        final String statoPrenotazione;

        PrenotazioneFilter(String nome, String cognome, String email, String codiceFiscale,
                           String numeroVolo, String numeroPrenotazione, String postoAssegnato,
                           String statoPrenotazione) {
            this.nome = nome;
            this.cognome = cognome;
            this.email = email;
            this.codiceFiscale = codiceFiscale;
            this.numeroVolo = numeroVolo;
            this.numeroPrenotazione = numeroPrenotazione;
            this.postoAssegnato = postoAssegnato;
            this.statoPrenotazione = statoPrenotazione;
        }
    }

    /**
     * Aggiunge un nuovo volo.
     * @param in dati del volo da aggiungere
     */
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

        if(!voloDAO.insert(v)) {
            throw new ControllerOperationException("Impossibile inserire il volo " + in.codice);
        }
        this.voliGestiti.add(v);
    }


    /**
     * Metodo legacy per aggiungere un volo tramite parametri singoli (deprecato).
     * @deprecated since 1.0, usare {@link #aggiungiVolo(VoloInput)}. Questo overload sarà rimosso in una futura release.
     * @param codice codice volo
     * @param compagnia compagnia aerea
     * @param data data del volo (YYYY-MM-DD)
     * @param orarioPrevisto orario previsto (HH:mm)
     * @param orarioStimato orario stimato (HH:mm)
     * @param stato stato del volo
     * @param direzione "in arrivo" | "in partenza"
     * @param aeroporto aeroporto (IATA)
     * @param gate gate
     */
    @Deprecated(since = "1.0", forRemoval = true)
    @SuppressWarnings({"java:S107", "java:S1133"})
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

    /**
     * Rimuove un volo dal database e dalla cache.
     * @param codiceUnivoco codice del volo da rimuovere
     * @return true se la rimozione ha avuto successo
     */
    public boolean rimuoviVolo(String codiceUnivoco) {
        return voloDAO.delete(codiceUnivoco)
                && voliGestiti.removeIf(v -> v.getCodiceUnivoco().equalsIgnoreCase(codiceUnivoco));
    }

    /**
     * Aggiorna i dati di un volo esistente.
     * @param in dati aggiornati del volo
     */
    public void aggiornaVolo(VoloInput in) {
        for(Volo volo : voliGestiti) {
            if(safeEquals(volo.getCodiceUnivoco(), in.codice)) {
                volo.setCompagniaAerea(in.compagnia);
                volo.setDataVolo(in.data);
                volo.setOrarioPrevisto(in.orarioPrevisto);
                volo.setOrarioStimato(in.orarioStimato);
                volo.setStato(in.stato);
                volo.setArrivoPartenza(in.direzione);
                volo.setAeroporto(in.aeroporto);
                volo.setGate(in.gate);

                if(!voloDAO.update(volo)) {
                    throw new ControllerOperationException("Impossibile aggiornare il volo " + in.codice);
                }
                return;
            }
        }
        throw new NoSuchElementException("Volo non trovato: " + in.codice);
    }

    /**
     * Metodo per aggiornare un volo tramite parametri singoli (deprecato).
     * @deprecated since 1.0, usare {@link #aggiungiVolo(VoloInput)}. Questo overload sarà rimosso in una futura release.
     * @param codiceUnivoco codice del volo
     * @param compagnia compagnia aerea
     * @param dataVolo data del volo (YYYY-MM-DD)
     * @param orarioPrevisto orario previsto (HH:mm)
     * @param orarioStimato orario stimato (HH:mm)
     * @param nuovoStato nuovo stato del volo
     * @param direzione "in arrivo" | "in partenza"
     * @param aeroporto aeroporto (IATA)
     * @param gate gate
     */
    @Deprecated(since = "1.0", forRemoval = true)
    @SuppressWarnings({"java:S107", "java:S1133"})
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

    /**
     * Cerca un volo in cache tramite codice univoco.
     * @param codiceUnivoco codice del volo
     * @return il volo trovato o null
     */
    public Volo cercaVolo(String codiceUnivoco) {
        for(Volo volo : voliGestiti) {
            if(safeEquals(volo.getCodiceUnivoco(), codiceUnivoco)) {
                return volo;
            }
        }
        return null;
    }

    /**
     * Cerca un volo per destinazione e data.
     * @param dest aeroporto destinazione
     * @param data data volo (YYYY-MM-DD)
     * @return il volo trovato o null
     */
    public Volo cercaVoloPerDestinazioneEData(String dest, String data) {
        for(Volo v : voliGestiti) {
            if(equalsIgnoreCase(v.getAeroporto(), dest) && safeEquals(v.getDataVolo(), data)) {
                return v;
            }
        }
        return null;
    }

    /**
     * Restituisce tutti i voli diretti verso una certa destinazione.
     * @param dest aeroporto destinazione
     * @return lista di voli trovati
     */
    public List<Volo> cercaVoliPerDestinazione(String dest) {
        List<Volo> result = new ArrayList<>();
        for(Volo v : voliGestiti) {
            if(equalsIgnoreCase(v.getAeroporto(), dest)) {
                result.add(v);
            }
        }
        return result;
    }

    /**
     * Verifica se un utente ha una prenotazione per un certo volo.
     * @param email email utente
     * @param codiceVolo codice del volo
     * @return true se esiste la prenotazione
     */
    public boolean utenteHaPrenotazionePerVolo(String email, String codiceVolo) {
        List<Prenotazione> prenotazioniUtente = prenotazioneDAO.findByEmailUtente(email);
        for(Prenotazione p : prenotazioniUtente) {
            if(p.getVolo() != null && safeEquals(p.getVolo().getCodiceUnivoco(), codiceVolo)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Ricarica tutte le prenotazioni dal database e aggiorna la cache locale.
     */
    private void ricaricaPrenotazioniDaDB() {
        prenotazioni.clear();
        try {
            prenotazioni.addAll(prenotazioneDAO.findAll());
        } catch(Exception e) {
            LOGGER.log(Level.WARNING, "Impossibile ricaricare le prenotazioni dal DB", e);
            LOGGER.log(Level.FINE, LOG_DETAILS, e);
        }
    }


    public static final class PrenotazioneBase {
        public final String numeroBiglietto;
        public final String posto;
        public final StatoPrenotazione stato;

        public PrenotazioneBase(String numeroBiglietto, String posto, StatoPrenotazione stato) {
            this.numeroBiglietto = numeroBiglietto;
            this.posto = posto;
            this.stato = stato;
        }
    }

    public static final class VoloRef {
        public final String numeroVolo;
        public final UtenteGenerico utenteGenerico;

        public VoloRef(String numeroVolo, UtenteGenerico utenteGenerico) {
            this.numeroVolo = numeroVolo;
            this.utenteGenerico = utenteGenerico;
        }
    }

    public static final class PasseggeroInfo {
        public final String nome;
        public final String cognome;
        public final String codiceFiscale;
        public final String email;

        public PasseggeroInfo(String nome, String cognome, String codiceFiscale, String email) {
            this.nome = nome;
            this.cognome = cognome;
            this.codiceFiscale = codiceFiscale;
            this.email = email;
        }
    }


    public static final class PrenotazioneInput {
        public final PrenotazioneBase base;
        public final VoloRef volo;
        public final PasseggeroInfo passeggero;

        public PrenotazioneInput(PrenotazioneBase base, VoloRef volo, PasseggeroInfo passeggero) {
            this.base = base;
            this.volo = volo;
            this.passeggero = passeggero;
        }

        /**
         * Factory per costruire rapidamente l'input prenotazione.
         * @param base dati base prenotazione
         * @param volo riferimento al volo e utente
         * @param passeggero dati passeggero
         * @return PrenotazioneInput composto
         */
        public static PrenotazioneInput of(PrenotazioneBase base, VoloRef volo, PasseggeroInfo passeggero) {
            return new PrenotazioneInput(base, volo, passeggero);
        }
    }

    /**
     * Assicura che l'email sia registrata come UtenteGenerico su DB, creandolo se necessario.
     * @param email email dell'utente da garantire su DB
     * @param providedOrNull utente già costruito (opzionale), altrimenti ne viene creato uno minimo
     * @return UtenteGenerico coerente e presente su DB
     * @throws IllegalArgumentException se l'email è mancante o vuota
     * @throws IllegalStateException se la creazione automatica fallisce
     */
    private UtenteGenerico ensureUserRegistered(String email, UtenteGenerico providedOrNull) {
        if(email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email mancante");
        }
        if(utentiDAO.emailEsiste(email)) {
            // Già registrato: se il chiamante non ha un oggetto, costruiscine uno basic coerente
            return providedOrNull != null
                    ? providedOrNull
                    : new UtenteGenerico(email, "", "", "", new ArrayList<>(), new AreaPersonale());
        }
        // Non registrato: crea utente minimale su DB
        UtenteGenerico nuovo = providedOrNull != null
                ? providedOrNull
                : new UtenteGenerico(email, "", "", "", new ArrayList<>(), new AreaPersonale());
        try {
            boolean ok = utentiDAO.insert(nuovo);
            if(!ok) {
                throw new IllegalStateException("Creazione utente fallita per email=" + email);
            }
        } catch(Exception e) {
            throw new IllegalStateException("Impossibile registrare automaticamente l'utente con email " + email + ". Registralo prima di prenotare.", e);
        }
        return nuovo;
    }

    /**
     * Aggiunge una nuova prenotazione (con controlli e inserimento utente se mancante).
     * @param in dati della prenotazione da aggiungere
     * @return la prenotazione creata, o null in caso di errore
     */
    public Prenotazione aggiungiPrenotazione(PrenotazioneInput in) {
        // 1) Validazioni di input (estratte)
        validatePrenotazioneInput(in);

        // 2) Recupero entità necessarie (estratte)
        Volo v = getAndValidateVolo(in.volo.numeroVolo);
        DatiPasseggero dp = resolvePasseggero(in);

        // 3) Assicura che l'utente esista su DB (già presente nel tuo Controller)
        UtenteGenerico utenteEffettivo = ensureUserRegistered(in.passeggero.email, in.volo.utenteGenerico);

        // 4) Costruzione prenotazione e inserimento
        String postoNormalizzato = normalizeSeatOrNull(in.base.posto);
        Prenotazione pren = new Prenotazione(
                in.base.numeroBiglietto,
                postoNormalizzato,
                in.base.stato,
                utenteEffettivo,
                dp,
                v
        );

        if(!tryInsertPrenotazione(pren, utenteEffettivo, in.passeggero.email)) {
            return null; // errore già loggato
        }

        LOGGER.log(Level.INFO,
                "OK prenotazione: biglietto={0}, posto={1}, stato={2}, emailutente={3}, idvolo={4}",
                new Object[]{pren.getNumBiglietto(), pren.getPostoAssegnato(), pren.getStato().name(), dp.getEmail(), v.getCodiceUnivoco()});

        prenotazioni.add(pren);
        return pren;
    }

    /**
     * Valida tutti i campi richiesti dell'input per una prenotazione.
     * @param in input prenotazione da validare
     * @throws IllegalArgumentException se mancano o sono vuoti campi obbligatori
     */
    private void validatePrenotazioneInput(PrenotazioneInput in) {
        if(in == null || in.base == null || in.volo == null || in.passeggero == null) {
            throw new IllegalArgumentException("Input prenotazione mancante o incompleto");
        }
        if(in.base.numeroBiglietto == null || in.base.numeroBiglietto.isBlank()) {
            throw new IllegalArgumentException("Numero biglietto mancante");
        }
        if(in.base.stato == null) {
            throw new IllegalArgumentException("Stato prenotazione mancante");
        }
        if(in.volo.numeroVolo == null || in.volo.numeroVolo.isBlank()) {
            throw new IllegalArgumentException("Numero volo mancante");
        }
        if(in.passeggero.codiceFiscale == null || in.passeggero.codiceFiscale.isBlank()) {
            throw new IllegalArgumentException("Codice fiscale mancante");
        }
        if(in.passeggero.email == null || in.passeggero.email.isBlank()) {
            throw new IllegalArgumentException("Email mancante");
        }
    }

    /**
     * Recupera un volo e solleva eccezione se non esiste.
     * @param numeroVolo codice del volo
     * @return Volo esistente
     * @throws IllegalArgumentException se il volo non esiste
     */
    private Volo getAndValidateVolo(String numeroVolo) {
        Volo v = getVoloByCodice(numeroVolo);
        if(v == null) {
            throw new IllegalArgumentException("Volo inesistente: " + numeroVolo);
        }
        return v;
    }

    /**
     * Trova/crea/aggiorna i dati passeggero coerentemente.
     * @param in input con i dati del passeggero
     * @return DatiPasseggero risolto/aggiornato
     */
    private DatiPasseggero resolvePasseggero(PrenotazioneInput in) {
        DatiPasseggero dp = datiPasseggeroDAO.findByEmail(in.passeggero.email);
        if(dp == null) {
            return new DatiPasseggero(in.passeggero.nome, in.passeggero.cognome, in.passeggero.codiceFiscale, in.passeggero.email);
        }
        if(dp.getCodiceFiscale() == null || !dp.getCodiceFiscale().equalsIgnoreCase(in.passeggero.codiceFiscale)) {
            dp.setCodiceFiscale(in.passeggero.codiceFiscale);
        }
        return dp;
    }

    /**
     * Incapsula il try/catch dell'inserimento della prenotazione.
     * @param pren prenotazione da inserire
     * @param utenteEffettivo utente a cui associare la prenotazione
     * @param emailPasseggero email del passeggero (per log)
     * @return true se l'inserimento va a buon fine
     */
    private boolean tryInsertPrenotazione(Prenotazione pren, UtenteGenerico utenteEffettivo, String emailPasseggero) {
        try {
            boolean ok = prenotazioneDAO.insert(pren, utenteEffettivo);
            if(!ok) {
                LOGGER.log(Level.WARNING, () -> "Impossibile inserire prenotazione " + pren.getNumBiglietto()
                        + " per volo " + (pren.getVolo() != null ? pren.getVolo().getCodiceUnivoco() : "?"));
            }
            return ok;
        } catch(IllegalArgumentException ie) {
            LOGGER.log(Level.WARNING, "Inserimento prenotazione rifiutato: {0}", ie.getMessage());
            LOGGER.log(Level.FINE, LOG_DETAILS, ie);
            return false;
        } catch(Exception e) {
            LOGGER.log(Level.WARNING, "Errore imprevisto in inserimento prenotazione per email={0}", emailPasseggero);
            LOGGER.log(Level.FINE, LOG_DETAILS, e);
            return false;
        }
    }

    /**
     * Metodo  per aggiungere una prenotazione tramite parametri singoli (deprecato).
     * @deprecated since 1.0, usare {@link #aggiungiVolo(VoloInput)}. Questo overload sarà rimosso in una futura release.
     * @param numeroBiglietto numero biglietto
     * @param posto posto richiesto (opzionale)
     * @param stato stato prenotazione
     * @param numeroVolo codice volo
     * @param utenteGenerico utente proprietario
     * @param nome nome passeggero
     * @param cognome cognome passeggero
     * @param codiceFiscale codice fiscale passeggero
     * @param email email passeggero
     * @param voloNonUsato parametro legacy non utilizzato
     * @return prenotazione creata o null
     */
    @Deprecated(since = "1.0", forRemoval = true)
    @SuppressWarnings({"java:S107", "java:S1133"})
    public Prenotazione aggiungiPrenotazione(String numeroBiglietto, String posto, StatoPrenotazione stato,
                                             String numeroVolo, UtenteGenerico utenteGenerico, String nome, String cognome,
                                             String codiceFiscale, String email, Volo voloNonUsato) {
        if(voloNonUsato != null) {
            LOGGER.log(Level.FINER, "Parametro voloNonUsato ignorato: {0}", voloNonUsato.getCodiceUnivoco());
        }
        return aggiungiPrenotazione(PrenotazioneInput.of(
                new PrenotazioneBase(numeroBiglietto, posto, stato),
                new VoloRef(numeroVolo, utenteGenerico),
                new PasseggeroInfo(nome, cognome, codiceFiscale, email)
        ));
    }

    /**
     * Restituisce la lista delle prenotazioni dell'utente, escludendo quelle cancellate.
     * @param utente utente per cui recuperare le prenotazioni
     * @return lista di prenotazioni (mai null)
     */
    public List<Prenotazione> getPrenotazioniUtente(UtenteGenerico utente) {
        if(utente == null) return Collections.emptyList();

        List<Prenotazione> result = new ArrayList<>();
        for(Prenotazione p : prenotazioni) {
            if(p.getUtenteGenerico() != null &&
                    utente.getNomeUtente() != null &&
                    equalsIgnoreCase(utente.getNomeUtente(), p.getUtenteGenerico().getNomeUtente()) &&
                    p.getStato() != StatoPrenotazione.CANCELLATA) {
                result.add(p);
            }
        }

        // Carica dal DB solo le prenotazioni mancanti
        try {
            List<Prenotazione> dalDB = prenotazioneDAO.findByEmailUtente(utente.getLogin());
            for(Prenotazione p : dalDB) {
                if(!containsPrenotazione(prenotazioni, p.getNumBiglietto())) prenotazioni.add(p);
                if(!containsPrenotazione(result, p.getNumBiglietto()) && p.getStato() != StatoPrenotazione.CANCELLATA)
                    result.add(p);
            }
        } catch(Exception e) {
            LOGGER.log(Level.WARNING, "Errore nel caricamento prenotazioni utente={0}", utente.getLogin());
            LOGGER.log(Level.FINE, LOG_DETAILS, e);
        }

        return result;
    }

    /**
     * Controlla se la lista contiene una prenotazione dato il numero biglietto.
     * @param list lista in cui cercare
     * @param numBiglietto numero biglietto
     * @return true se presente
     */
    private boolean containsPrenotazione(List<Prenotazione> list, String numBiglietto) {
        for(Prenotazione p : list) {
            if(safeEquals(p.getNumBiglietto(), numBiglietto)) return true;
        }
        return false;
    }

    /**
     * Normalizza il testo del posto assegnato in formato valido.
     * @param raw testo libero del posto
     * @return posto normalizzato (es. A12) o null se non valido/auto
     */
    private String normalizeSeatOrNull(String raw) {
        if(raw == null) return null;
        String t = raw.trim().toUpperCase().replaceAll("[^A-Z0-9]", "");
        if(t.isEmpty() || t.equals("AUTO") || t.equals("POSTOAUTO") || t.equals("POSTO")) return null;
        if(t.length() < 2) return null;
        char letter = t.charAt(0);
        if(letter < 'A' || letter > 'F') return null;
        try {
            int n = Integer.parseInt(t.substring(1));
            return (n >= 1 && n <= 30) ? (letter + String.valueOf(n)) : null;
        } catch(NumberFormatException _) { // Java 21 unnamed pattern
            return null;
        }
    }

    /**
     * Rimuove una prenotazione dal database e dalla cache.
     * @param numeroPrenotazione numero biglietto/prenotazione
     * @return true se rimossa con successo
     */
    public boolean rimuoviPrenotazione(String numeroPrenotazione) {
        return prenotazioneDAO.delete(numeroPrenotazione)
                && prenotazioni.removeIf(p -> safeEqualsIgnoreCase(p.getNumBiglietto(), numeroPrenotazione));
    }

    /**
     * Cerca una prenotazione dato il numero di biglietto, prima in cache poi su DB.
     * @param numeroBiglietto numero biglietto
     * @return prenotazione trovata o null
     */
    public Prenotazione cercaPrenotazione(String numeroBiglietto) {
        // Prima cerca in cache
        for(Prenotazione p : prenotazioni) {
            if(safeEquals(p.getNumBiglietto(), numeroBiglietto)) {
                return p;
            }
        }

        // Se non è in cache, prova a leggere dal DB
        Prenotazione dalDB = prenotazioneDAO.findByCodice(numeroBiglietto);
        if(dalDB != null && !containsPrenotazione(prenotazioni, dalDB.getNumBiglietto())) {
            prenotazioni.add(dalDB); // aggiorna la cache
        }

        return dalDB;
    }

    /**
     * Salva o aggiorna una prenotazione sia in DB che in cache.
     * @param prenotazione prenotazione da salvare/aggiornare
     * @return true se salvata con successo
     */
    public boolean salvaPrenotazione(Prenotazione prenotazione) {
        if(prenotazione == null) return false;

        DatiPasseggero dp = prenotazione.getDatiPasseggero();
        if(dp != null) {
            try {
                // aggiorna SOLO se il passeggero è registrato
                if (datiPasseggeroDAO.findByEmail(dp.getEmail()) != null && !datiPasseggeroDAO.update(dp)) return false;
            } catch(Exception e) {
                LOGGER.log(Level.WARNING, "Errore aggiornando DatiPasseggero per email={0}", dp.getEmail());
                LOGGER.log(Level.FINE, LOG_DETAILS, e);
                return false;
            }
        }

        // aggiorna la prenotazione (tabella prenotazioni)
        if(!prenotazioneDAO.update(prenotazione)) return false;

        // riallinea la cache senza duplicati
        boolean found = false;
        for(int i = 0; i < prenotazioni.size(); i++) {
            if(safeEquals(prenotazioni.get(i).getNumBiglietto(), prenotazione.getNumBiglietto())) {
                prenotazioni.set(i, prenotazione);
                found = true;
                break;
            }
        }
        if(!found) prenotazioni.add(prenotazione);

        return true;
    }

    /**
     * Annulla una prenotazione eliminandola dal db.
     * @param p prenotazione da annullare
     * @return true se eliminata
     */
    public boolean annullaPrenotazione(Prenotazione p) {
        return p != null && prenotazioneDAO.delete(p.getNumBiglietto());
    }

    /**
     * Verifica se una email risulta già registrata come utente.
     * @param email email da verificare
     * @return true se esiste un utente con quell'email
     */
    public boolean emailRegistrata(String email) {
        return utentiDAO.emailEsiste(email);
    }

    /**
     * Normalizza una stringa: restituisce null se vuota.
     * @param s testo da normalizzare
     * @return stringa normalizzata o null
     */
    private String norm(String s) {
        if(s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    /**
     * Restituisce una stringa non nulla.
     * @param s stringa in ingresso
     * @return s oppure stringa vuota se null
     */
    private String safe(String s) { return s == null ? "" : s; }

    /**
     * Confronta due stringhe.
     * @param a prima stringa
     * @param b seconda stringa
     * @return true se uguali (null-safe)
     */
    private boolean safeEquals(String a, String b) {
        return Objects.equals(a, b);
    }

    /**
     * Confronta due stringhe ignorando il caso.
     * @param a prima stringa
     * @param b seconda stringa
     * @return true se uguali (null-safe, case-insensitive)
     */
    private boolean safeEqualsIgnoreCase(String a, String b) {
        return a == null ? b == null : b != null && a.equalsIgnoreCase(b);
    }

    /**
     * Verifica se una stringa contiene l'altra.
     * @param text testo sorgente
     * @param needle testo da cercare
     * @return true se contiene (case-insensitive)
     */
    private boolean containsIgnoreCase(String text, String needle) {
        if(needle == null) return true;
        if(text == null) return false;
        return text.toLowerCase().contains(needle.toLowerCase());
    }

    /**
     * Confronta due stringhe (rimuove spazi e ignora il caso).
     * @param a prima stringa
     * @param b seconda stringa
     * @return true se uguali dopo trim, case-insensitive
     */
    private boolean equalsIgnoreCase(String a, String b) {
        if(a == null && b == null) return true;
        if(a == null || b == null) return false;
        return a.trim().equalsIgnoreCase(b.trim());
    }

    /**
     * Converte una stringa nello stato del volo corrispondente.
     * @param s stato come stringa
     * @return StatoVolo oppure null se non valido
     */
    private StatoVolo parseStato(String s) {
        if(s == null) return null;
        s = s.trim().toUpperCase();
        try {
            return StatoVolo.valueOf(s);
        } catch(IllegalArgumentException _) { // Java 21 unnamed pattern
            LOGGER.log(Level.FINEST, "Stato volo sconosciuto: {0}", s);
            return null;
        }
    }

    /**
     * Restituisce la stringa canonica per arrivo/partenza.
     * @param ap valore da normalizzare
     * @return "in arrivo" | "in partenza" oppure il valore originale
     */
    private String canonicalAP(String ap) {
        if(ap == null) return null;
        String x = ap.trim().toLowerCase();
        if(ARRIVO.equalsIgnoreCase(x)) return ARRIVO;
        if(PARTENZA.equalsIgnoreCase(x)) return PARTENZA;
        return ap;
    }

    /**
     * Restituisce tutte le informazioni dei voli come lista di array per la tabella.
     * @return lista di righe (Object[]) per la tabella voli
     */
    public List<Object[]> tuttiVoli() {
        List<Object[]> rows = new ArrayList<>();
        for(Volo v : voliGestiti) {
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

    /**
     * Verifica se un volo soddisfa i criteri del filtro.
     * @param v volo da verificare
     * @param f filtro
     * @return true se il volo corrisponde al filtro
     */
    private boolean matchVolo(Volo v, VoloFilter f) {
        return (f.numeroVolo == null || equalsIgnoreCase(v.getCodiceUnivoco(), f.numeroVolo))
                && (f.compagnia == null || containsIgnoreCase(v.getCompagniaAerea(), f.compagnia))
                && (f.stato == null || (v.getStato() != null && v.getStato().name().equalsIgnoreCase(f.stato)))
                && (f.data == null || safeEquals(v.getDataVolo(), f.data))
                && (f.orario == null || safeEquals(v.getOrarioPrevisto(), f.orario))
                && (f.aeroporto == null || equalsIgnoreCase(v.getAeroporto(), f.aeroporto))
                && (f.gate == null || equalsIgnoreCase(v.getGate(), f.gate))
                && (f.arrivoPartenza == null || equalsIgnoreCase(v.getArrivoPartenza(), f.arrivoPartenza));
    }

    /**
     * Restituisce la lista dei voli che rispettano il filtro.
     * @param filter filtro di ricerca
     * @return lista di voli trovati
     */
    public List<Volo> ricercaVoliRaw(VoloFilter filter) {
        String ap = canonicalAP(filter != null ? filter.arrivoPartenza : null);
        VoloFilter f = filter == null ? null :
                new VoloFilter(filter.numeroVolo, filter.compagnia, filter.stato, filter.data,
                        filter.orario, filter.aeroporto, filter.gate, ap);
        List<Volo> result = new ArrayList<>();
        for(Volo v : voliGestiti) {
            if(f == null || matchVolo(v, f)) {
                result.add(v);
            }
        }
        return result;
    }

    /**
     * Metodo per ricercare i voli tramite parametri singoli (deprecato).
     *
     * @deprecated since 1.0, usare {@link #ricercaVoliRaw(VoloFilter)}.
     *             Questo overload sarà rimosso in una futura release.
     *
     * @param numeroVolo codice volo
     * @param compagnia compagnia aerea
     * @param stato stato del volo
     * @param data data (YYYY-MM-DD)
     * @param orario orario previsto (HH:mm)
     * @param aeroporto aeroporto (IATA)
     * @param gate gate
     * @param arrivoPartenza "in arrivo" | "in partenza"
     * @return lista di voli trovati
     */
    @Deprecated(since = "1.0", forRemoval = false)
    @SuppressWarnings({ "java:S107", "java:S1133", "squid:S1133" }) // legacy; rimuovere quando la GUI migra al filtro
    public List<Volo> ricercaVoliRaw(String numeroVolo,
                                     String compagnia,
                                     String stato,
                                     String data,
                                     String orario,
                                     String aeroporto,
                                     String gate,
                                     String arrivoPartenza) {
        return ricercaVoliRaw(new VoloFilter(numeroVolo, compagnia, stato, data, orario, aeroporto, gate, arrivoPartenza));
    }

    /**
     * Restituisce le righe per la tabella dei voli, secondo il filtro.
     * @param filter filtro di ricerca
     * @return lista di righe (Object[]) per la tabella voli
     */
    public List<Object[]> ricercaVoli(VoloFilter filter) {
        List<Object[]> righe = new ArrayList<>();
        for(Volo v : ricercaVoliRaw(filter)) {
            righe.add(voloToRow(v));
        }
        return righe;
    }

    /**
     * Metodo  per ricercare i voli tramite parametri singoli (deprecato).
     * @deprecated since 1.0, usare {@link #aggiungiVolo(VoloInput)}. Questo overload sarà rimosso in una futura release.
     * @param numeroVolo codice volo
     * @param compagnia compagnia aerea
     * @param stato stato del volo
     * @param data data (YYYY-MM-DD)
     * @param orario orario previsto (HH:mm)
     * @param aeroporto aeroporto (IATA)
     * @param gate gate
     * @param arrivoPartenza "in arrivo" | "in partenza"
     * @return lista di righe (Object[]) per la tabella voli
     */
    @Deprecated(since = "1.0", forRemoval = false)
    @SuppressWarnings({"java:S107", "java:S1133"}) // legacy GUI
    public List<Object[]> ricercaVoli(String numeroVolo, String compagnia, String stato, String data,
                                      String orario, String aeroporto, String gate, String arrivoPartenza) {
        return ricercaVoli(new VoloFilter(numeroVolo, compagnia, stato, data, orario, aeroporto, gate, arrivoPartenza));
    }

    /**
     * Converte un oggetto Volo in una riga per la tabella.
     * @param v volo da convertire
     * @return array di colonne per la tabella
     */
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

    /**
     * Restituisce le righe per la tabella passeggeri in base al filtro.
     * @param f filtro di ricerca passeggeri (può essere null per tutti)
     * @return lista di righe (Object[]) per la tabella passeggeri
     */
    public List<Object[]> ricercaPasseggeri(PrenotazioneFilter f) {
        List<Object[]> risultati = new ArrayList<>();
        for(Prenotazione p : prenotazioni) {
            if(matchPrenotazione(p, f)) {
                risultati.add(prenotazioneToRow(p));
            }
        }
        return risultati;
    }

    /**
     * Metodo per ricercare i passeggeri tramite parametri singoli (deprecato).
     * @deprecated since 1.0, usare {@link #aggiungiVolo(VoloInput)}. Questo overload sarà rimosso in una futura release.
     * @param nome filtro nome
     * @param cognome filtro cognome
     * @param email filtro email
     * @param codiceFiscale filtro codice fiscale
     * @param numeroVolo filtro numero volo
     * @param numeroPrenotazione filtro numero prenotazione
     * @param postoAssegnato filtro posto assegnato
     * @param statoPrenotazione filtro stato prenotazione
     * @return lista di righe (Object[]) per la tabella passeggeri
     */
    @Deprecated(since = "1.0", forRemoval = false)
    @SuppressWarnings({"java:S107", "java:S1133"}) // legacy GUI
    public List<Object[]> ricercaPasseggeri(String nome,
                                            String cognome,
                                            String email,
                                            String codiceFiscale,
                                            String numeroVolo,
                                            String numeroPrenotazione,
                                            String postoAssegnato,
                                            String statoPrenotazione) {
        PrenotazioneFilter f = new PrenotazioneFilter(
                norm(nome), norm(cognome), norm(email), norm(codiceFiscale),
                norm(numeroVolo), norm(numeroPrenotazione), norm(postoAssegnato), statoPrenotazione
        );
        return ricercaPasseggeri(f);
    }

    /**
     * Verifica se una prenotazione soddisfa i criteri del filtro.
     * @param p prenotazione da valutare
     * @param f filtro da applicare (se null, corrisponde sempre)
     * @return true se corrisponde al filtro
     */
    private boolean matchPrenotazione(Prenotazione p, PrenotazioneFilter f) {
        if(f == null) return true;

        DatiPasseggero dp = p.getDatiPasseggero();

        // Estrai valori una sola volta
        String nome           = dp != null ? dp.getNome()          : null;
        String cognome        = dp != null ? dp.getCognome()       : null;
        String email          = dp != null ? dp.getEmail()         : null;
        String codiceFiscale  = dp != null ? dp.getCodiceFiscale() : null;
        String codiceVolo     = p.getVolo() != null ? p.getVolo().getCodiceUnivoco() : null;
        String numBiglietto   = p.getNumBiglietto();
        String postoAssegnato = p.getPostoAssegnato();

        return allTrue(
                matchesCI(nome,           f.nome),
                matchesCI(cognome,        f.cognome),
                matchesCI(email,          f.email),
                matchesCI(codiceFiscale,  f.codiceFiscale),
                matchesCI(codiceVolo,     f.numeroVolo),
                matchesCI(numBiglietto,   f.numeroPrenotazione),
                matchesCI(postoAssegnato, f.postoAssegnato),
                matchStato(p.getStato(),  f.statoPrenotazione)
        );
    }

    /**
     * Verifica se due stringhe corrispondono ignorando il caso (helper).
     * @param value valore sorgente
     * @param filter sottostringa/filtro
     * @return true se corrisponde o se il filtro è null
     */
    private boolean matchesCI(String value, String filter) {
        return filter == null || containsIgnoreCase(value, filter);
    }

    /**
     * Verifica se lo stato corrisponde al filtro.
     * @param stato stato della prenotazione
     * @param filter filtro (nome enum in stringa)
     * @return true se combacia o se il filtro è null
     */
    private boolean matchStato(StatoPrenotazione stato, String filter) {
        return filter == null || (stato != null && stato.name().equalsIgnoreCase(filter));
    }

    /**
     * Restituisce true se tutti i controlli sono verificati.
     * @param checks elenco di condizioni
     * @return true se tutte vere
     */
    private static boolean allTrue(boolean... checks) {
        for(boolean ok : checks) {
            if(!ok) return false;
        }
        return true;
    }

    /**
     * Converte una prenotazione in una riga per la tabella passeggeri.
     * @param p prenotazione da convertire
     * @return array di colonne per la tabella
     */
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

    /**
     * Restituisce la lista di tutti i passeggeri come righe tabellari.
     * @return lista di righe (Object[]) per la tabella passeggeri
     */
    public List<Object[]> tuttiPasseggeri() {
        return ricercaPasseggeri((PrenotazioneFilter) null);
    }

    /**
     * Aggiunge un bagaglio se non esiste già.
     * @param bagaglio bagaglio da inserire
     * @return true se inserito
     */
    public boolean aggiungiBagaglio(Bagaglio bagaglio) {
        return bagaglioDAO.findById(bagaglio.getCodUnivoco()) == null
                && bagaglioDAO.insert(bagaglio);
    }

    /**
     * Crea e aggiunge un nuovo bagaglio.
     * @param codice codice univoco bagaglio
     * @param stato stato del bagaglio
     * @return true se inserito
     */
    public boolean aggiungiBagaglio(String codice, StatoBagaglio stato) {
        return aggiungiBagaglio(new Bagaglio(codice, 0.0, stato, null));
    }

    /**
     * Restituisce tutti i bagagli dal DB.
     * @return lista di bagagli
     */
    public List<Bagaglio> getBagagli() {
        return bagaglioDAO.findAll();
    }

    /**
     * Restituisce tutti i bagagli.
     * @return lista di bagagli
     */
    public List<Bagaglio> trovaTuttiBagagli() {
        return bagaglioDAO.findAll();
    }

    /**
     * Ricerca bagagli secondo codice e stato.
     * @param codiceBagaglio filtro codice univoco
     * @param stato filtro stato
     * @return lista di righe (Object[]) per tabella bagagli
     */
    public List<Object[]> ricercaBagagli(String codiceBagaglio, String stato) {
        List<Object[]> risultati = new ArrayList<>();
        for(Bagaglio b : bagaglioDAO.findAll()) {
            boolean match = true;
            if(codiceBagaglio != null && !codiceBagaglio.isEmpty() &&
                    (b.getCodUnivoco() == null || !b.getCodUnivoco().contains(codiceBagaglio))) {
                match = false;
            }
            if(match && stato != null && !stato.isEmpty() &&
                    (b.getStato() == null || !b.getStato().toString().equalsIgnoreCase(stato))) {
                match = false;
            }

            if(match) {
                risultati.add(new Object[]{
                        safe(b.getCodUnivoco()),
                        b.getStato() != null ? b.getStato().toString() : ""
                });
            }
        }
        return risultati;
    }

    /**
     * Rimuove un bagaglio dal database.
     * @param codice codice univoco bagaglio
     * @return true se rimosso
     */
    public boolean rimuoviBagaglio(String codice) {
        return bagaglioDAO.delete(codice);
    }

    /**
     * Restituisce tutte le righe tabellari dei bagagli.
     * @return lista di righe (Object[]) per tabella bagagli
     */
    public List<Object[]> tuttiBagagliRows() {
        return ricercaBagagli(null, null);
    }

    /**
     * Aggiorna un bagaglio nel database.
     * @param bagaglio bagaglio da aggiornare
     * @return true se aggiornato
     */
    public boolean aggiornaBagaglio(Bagaglio bagaglio) {
        return bagaglio != null && bagaglioDAO.update(bagaglio);
    }

    /**
     * Aggiunge un gate se non già presente.
     * @param numero numero del gate
     * @return true se aggiunto, false se già presente
     */
    public boolean aggiungiGate(int numero) {
        if(gates.stream().anyMatch(g -> g.getNumero() == numero)) {
            return false;
        }
        gates.add(new Gate(numero));
        return true;
    }

    /**
     * Elimina un gate dalla lista in memoria dato il suo numero.
     * @param numero numero del gate da eliminare
     * @return true se è stato eliminato, false altrimenti
     */
    public boolean eliminaGate(int numero) {
        return gates.removeIf(g -> g.getNumero() == numero);
    }

    /**
     * Restituisce la lista di tutti i gate in memoria.
     * @return lista di gate
     */
    public List<Gate> getGates() {
        return gates;
    }

    /**
     * Crea un nuovo amministratore e lo aggiunge alla lista interna.
     * @param login login amministratore
     * @param password password amministratore
     * @param nome nome amministratore
     * @param cognome cognome amministratore
     * @return l'amministratore creato
     */
    public Amministratore creaAmministratore(String login, String password, String nome, String cognome) {
        Amministratore admin = new Amministratore(login, password, nome, cognome);
        amministratori.add(admin);
        return admin;
    }

    /**
     * Restituisce la lista di tutti gli amministratori.
     * @return lista di amministratori
     */
    public List<Amministratore> getAmministratori() {
        return amministratori;
    }

    /**
     * Cerca un amministratore tramite il login.
     * @param login login dell'amministratore
     * @return amministratore trovato o null se non esiste
     */
    public Amministratore getAmministratoreByLogin(String login) {
        for(Amministratore a : amministratori) {
            if(safeEquals(a.getLogin(), login)) {
                return a;
            }
        }
        return null;
    }

    /**
     * Crea un nuovo DatiPasseggero e lo aggiunge alla lista interna.
     * @param nome nome passeggero
     * @param cognome cognome passeggero
     * @param codiceFiscale codice fiscale passeggero
     * @param email email passeggero
     * @return il DatiPasseggero creato
     */
    public DatiPasseggero creaDatiPasseggero(String nome, String cognome, String codiceFiscale, String email) {
        DatiPasseggero dp = new DatiPasseggero(nome, cognome, codiceFiscale, email);
        datiPasseggeri.add(dp);
        return dp;
    }

    /**
     * Cerca un DatiPasseggero tramite il codice fiscale.
     * @param codiceFiscale codice fiscale da cercare
     * @return il DatiPasseggero trovato o null se non esiste
     */
    public DatiPasseggero getDatiPasseggeroByCodiceFiscale(String codiceFiscale) {
        for(DatiPasseggero d : datiPasseggeri) {
            if(codiceFiscale != null && codiceFiscale.equals(d.getCodiceFiscale())) {
                return d;
            }
        }
        return null;
    }

    /**
     * Restituisce la lista di tutti gli utenti generici in memoria.
     * @return lista di utenti generici
     */
    public List<UtenteGenerico> getTuttiUtenti() {
        return utenti;
    }

    /**
     * Cerca un utente generico tramite email.
     * @param email email da cercare
     * @return l'utente trovato o null se non esiste
     */
    public UtenteGenerico getUtenteByEmail(String email) {
        for(UtenteGenerico u : getTuttiUtenti()) {
            // Se il tuo modello ha getEmail(), usa quello. Qui mantengo getNomeUtente come "email/login"
            if(email != null && email.equalsIgnoreCase(u.getNomeUtente())) {
                return u;
            }
        }
        return null;
    }

    /**
     * Crea e aggiunge un nuovo utente generico con email come identificativo.
     * @param emailUtente email dell'utente
     * @return l'utente generico creato
     */
    public UtenteGenerico creaUtenteGenerico(String emailUtente) {
        UtenteGenerico u = new UtenteGenerico(emailUtente, "", "", "", new ArrayList<>(), new AreaPersonale());
        utenti.add(u);
        return u;
    }

    /**
     * Cerca un volo tramite codice nella lista in memoria.
     * @param codiceVolo codice univoco del volo
     * @return il volo trovato o null se non esiste
     */
    public Volo getVoloByCodice(String codiceVolo) {
        for(Volo v : voliGestiti) {
            if(v.getCodiceUnivoco() != null && v.getCodiceUnivoco().equals(codiceVolo)) {
                return v;
            }
        }
        return null;
    }

    /**
     * Crea e aggiunge un nuovo volo (solo in memoria) con il codice fornito.
     * @param codiceVolo codice univoco del volo
     * @return il volo creato
     */
    public Volo creaVolo(String codiceVolo) {
        Volo v = new Volo(); // costruttore vuoto
        v.setCodiceUnivoco(codiceVolo);
        voliGestiti.add(v);
        return v;
    }

    /**
     * Cerca DatiPasseggero tramite email utente scorrendo le prenotazioni.
     * @param emailUtente email del passeggero da cercare
     * @return i dati passeggero trovati o null
     */
    public DatiPasseggero getDatiPasseggeroByEmailUtente(String emailUtente) {
        for(Prenotazione p : prenotazioni) {
            if(p.getDatiPasseggero() != null &&
                    p.getDatiPasseggero().getEmail().equalsIgnoreCase(emailUtente)) {
                return p.getDatiPasseggero();
            }
        }
        return null;
    }

    /**
     * Restituisce il DAO Postgres per DatiPasseggero.
     * @return il DAO DatiPasseggero
     */
    public DatiPasseggeroDAOPostgres getDatiPasseggeroDAO() {
        return datiPasseggeroDAO;
    }

    /**
     * Mostra la finestra dell'area personale per l'amministratore passato.
     * @param finestraCorrente finestra da nascondere
     * @param amministratore amministratore autenticato
     */
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

    // Dati di seed
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
        /**
         * Crea una nuova ControllerOperationException con messaggio.
         * @param message messaggio descrittivo
         */
        public ControllerOperationException(String message) { super(message); }
        /**
         * Crea una nuova ControllerOperationException con messaggio e causa.
         * @param message messaggio descrittivo
         * @param cause causa originale
         */
        public ControllerOperationException(String message, Throwable cause) { super(message, cause); }
    }
}