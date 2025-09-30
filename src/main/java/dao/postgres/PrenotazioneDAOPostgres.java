package dao.postgres;

import dao.PrenotazioneDAO;
import database.ConnessioneDatabase;
import controller.Controller;
import model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PrenotazioneDAOPostgres implements PrenotazioneDAO {

    private static final Logger LOGGER = Logger.getLogger(PrenotazioneDAOPostgres.class.getName());

    // Colonne ripetute
    private static final String COL_EMAILUTENTE = "emailutente";
    private static final String COL_IDVOLO = "idvolo";
    private static final String COL_NUMBIGLIETTO = "numbiglietto";
    private static final String COL_POSTO_ASSEGNATO = "postoassegnato";
    private static final String COL_STATO = "stato";

    // Messaggi log ripetuti
    private static final String LOG_SQL_DETAILS = "Dettagli SQL exception";

    private final Connection conn;
    private Controller controller; // opzionale
    private final UtenteGenericoDAOPostgres utenteDao = new UtenteGenericoDAOPostgres();
    private final DatiPasseggeroDAOPostgres dpDao;

    public PrenotazioneDAOPostgres() {
        try {
            this.conn = ConnessioneDatabase.getInstance().getConnection();
            this.conn.setAutoCommit(true);
            this.dpDao = new DatiPasseggeroDAOPostgres(); // inizializzazione del DAO dei passeggeri
        } catch (SQLException e) {
            throw new DatabaseInitializationException("Errore nella connessione al database", e);
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    private static final String BASE_SELECT =
            "SELECT p.numbiglietto, p.postoassegnato, p.stato, p.emailutente, p.idvolo, " +
                    "       p.dp_nome, p.dp_cognome, p.dp_codicefiscale, " +
                    "       v.compagniaaerea AS v_compagniaaerea, v.datavolo AS v_datavolo, " +
                    "       v.orarioprevisto AS v_orarioprevisto, v.orariostimato AS v_orariostimato, " +
                    "       v.stato AS v_stato, v.aeroporto AS v_aeroporto, v.gate AS v_gate, " +
                    "       v.arrivopartenza AS v_arrivopartenza " +
                    "FROM public.prenotazioni p " +
                    "LEFT JOIN public.voli v ON v.codiceunivoco = p.idvolo";

    private static final char[] SEAT_LETTERS = "ABCDEF".toCharArray();
    private static final int SEAT_MAX_NUMBER = 30;

    private String generateRandomSeat() {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        char letter = SEAT_LETTERS[r.nextInt(SEAT_LETTERS.length)];
        int number = r.nextInt(1, SEAT_MAX_NUMBER + 1);
        return letter + String.valueOf(number);
    }

    private static String norm(String s) {
        if (s == null) return "";
        return s.trim().toUpperCase().replaceAll("[^A-Z0-9]", "");
    }

    private static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private String parseSeatOrNull(String raw) {
        String t = norm(raw);
        if (t.isEmpty() || t.equals("AUTO") || t.equals("POSTO") || t.equals("POSTOAUTO")) return null;
        if (t.length() < 2) return null;
        char letter = t.charAt(0);
        if (letter < 'A' || letter > 'F') return null;
        try {
            int n = Integer.parseInt(t.substring(1));
            if (n >= 1 && n <= SEAT_MAX_NUMBER) return letter + String.valueOf(n);
        } catch (NumberFormatException e) {
            // valore non numerico dopo la lettera: posto non valido
            return null;
        }
        return null;
    }

    private boolean seatExists(String idVolo, String seat) throws SQLException {
        final String sql = "SELECT 1 FROM public.prenotazioni WHERE idvolo = ? AND postoassegnato = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idVolo);
            ps.setString(2, seat);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private String generateAvailableSeat(String idVolo) throws SQLException {
        // tentativi random
        for (int attempt = 0; attempt < 200; attempt++) {
            String candidate = generateRandomSeat();
            if (!seatExists(idVolo, candidate)) return candidate;
        }
        // ricerca esaustiva
        for (char letter : SEAT_LETTERS) {
            for (int num = 1; num <= SEAT_MAX_NUMBER; num++) {
                String candidate = letter + String.valueOf(num);
                if (!seatExists(idVolo, candidate)) return candidate;
            }
        }
        throw new SQLException("Nessun posto disponibile per il volo " + idVolo);
    }

    // ----------------- QUERY -----------------

    @Override
    public List<Prenotazione> findByEmailUtente(String emailUtente) {
        List<Prenotazione> out = new ArrayList<>();
        final String normalizedEmail = emailUtente == null ? null : emailUtente.trim().toLowerCase();

        String sql = BASE_SELECT + " WHERE LOWER(p.emailutente) = ? ORDER BY p.numbiglietto";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, normalizedEmail);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DatiPasseggero dp = dpDao.findByEmail(rs.getString(COL_EMAILUTENTE));
                    Volo volo = new Volo(rs.getString(COL_IDVOLO));
                    Prenotazione p = new Prenotazione(
                            rs.getString(COL_NUMBIGLIETTO),
                            rs.getString(COL_POSTO_ASSEGNATO),
                            rs.getString(COL_STATO) != null ? StatoPrenotazione.valueOf(rs.getString(COL_STATO)) : null,
                            dp,
                            volo
                    );
                    out.add(p);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Errore findByEmailUtente per email={0}", normalizedEmail);
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
        }
        return out;
    }

    @Override
    public List<Prenotazione> findAllByUtente(String emailUtente) {
        List<Prenotazione> out = new ArrayList<>();
        String sql = BASE_SELECT + " WHERE p.emailutente = ? ORDER BY p.numbiglietto";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, emailUtente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DatiPasseggero dp = dpDao.findByEmail(rs.getString(COL_EMAILUTENTE));
                    Prenotazione p = new Prenotazione(
                            rs.getString(COL_NUMBIGLIETTO),
                            rs.getString(COL_POSTO_ASSEGNATO),
                            rs.getString(COL_STATO) != null ? StatoPrenotazione.valueOf(rs.getString(COL_STATO)) : null,
                            dp,
                            new Volo(rs.getString(COL_IDVOLO))
                    );
                    out.add(p);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Errore findAllByUtente per email={0}", emailUtente);
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
        }
        return out;
    }

    @Override
    public List<Prenotazione> findAll() {
        List<Prenotazione> out = new ArrayList<>();
        String sql = "SELECT numbiglietto, postoassegnato, stato, emailutente, idvolo, " +
                "dp_nome, dp_cognome, dp_codicefiscale " +
                "FROM public.prenotazioni ORDER BY numbiglietto";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                DatiPasseggero dp = new DatiPasseggero(
                        rs.getString("dp_nome"),
                        rs.getString("dp_cognome"),
                        null, // telefono opzionale
                        rs.getString(COL_EMAILUTENTE),
                        null  // password non necessaria qui
                );
                dp.setCodiceFiscale(rs.getString("dp_codicefiscale"));

                Prenotazione p = new Prenotazione(
                        rs.getString(COL_NUMBIGLIETTO),
                        rs.getString(COL_POSTO_ASSEGNATO),
                        rs.getString(COL_STATO) != null ? StatoPrenotazione.valueOf(rs.getString(COL_STATO)) : null,
                        dp,
                        new Volo(rs.getString(COL_IDVOLO))
                );
                out.add(p);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Errore findAll()", e);
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
        }
        return out;
    }

    @Override
    public Prenotazione findByCodice(String codice) {
        final String sql = BASE_SELECT + " WHERE p.numbiglietto = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codice);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DatiPasseggero dp = dpDao.findByEmail(rs.getString(COL_EMAILUTENTE));
                    Volo volo = new Volo(rs.getString(COL_IDVOLO));
                    return new Prenotazione(
                            rs.getString(COL_NUMBIGLIETTO),
                            rs.getString(COL_POSTO_ASSEGNATO),
                            rs.getString(COL_STATO) != null ? StatoPrenotazione.valueOf(rs.getString(COL_STATO)) : null,
                            dp,
                            volo
                    );
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Errore findByCodice per numbiglietto={0}", codice);
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
        }
        return null;
    }

    // ----------------- INSERT / UPDATE / DELETE -----------------

    @Override
    public boolean insert(Prenotazione p, UtenteGenerico utente) {
        // Validazione base
        if (p == null || p.getNumBiglietto() == null || p.getNumBiglietto().isBlank() ||
                p.getStato() == null || p.getVolo() == null || p.getVolo().getCodiceUnivoco() == null ||
                p.getVolo().getCodiceUnivoco().isBlank() || p.getDatiPasseggero() == null ||
                p.getDatiPasseggero().getEmail() == null || p.getDatiPasseggero().getEmail().isBlank() ||
                p.getDatiPasseggero().getNome() == null || p.getDatiPasseggero().getCognome() == null ||
                p.getDatiPasseggero().getCodiceFiscale() == null) {
            LOGGER.warning("Prenotazione non valida");
            return false;
        }

        final String idVolo = p.getVolo().getCodiceUnivoco();

        // 1. Gestione del posto
        String posto = parseSeatOrNull(p.getPostoAssegnato());
        try {
            if (posto == null) {
                posto = generateAvailableSeat(idVolo);
                p.setPostoAssegnato(posto);
            } else if (seatExists(idVolo, posto)) {
                LOGGER.log(Level.WARNING, "Il posto {0} è già occupato", posto);
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Errore gestione posto per volo={0}", idVolo);
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
            return false;
        }

        // 2. Controllo duplicati per stesso volo e stesso utente completo
        try {
            if (existsPrenotazionePerVoloEUtente(p.getDatiPasseggero(), idVolo, utente)) {
                LOGGER.warning("Il passeggero ha già una prenotazione per questo volo");
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Errore controllo duplicati per volo={0}", idVolo);
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
            return false;
        }

        // 3. Inserimento in tabella prenotazioni
        final String sqlInsert = "INSERT INTO public.prenotazioni " +
                "(numbiglietto, postoassegnato, stato, emailutente, idvolo, dp_nome, dp_cognome, dp_codicefiscale) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
            ps.setString(1, p.getNumBiglietto());
            ps.setString(2, posto);
            ps.setString(3, p.getStato().name());
            ps.setString(4, normalizeEmail(p.getDatiPasseggero().getEmail()));
            ps.setString(5, idVolo);
            ps.setString(6, p.getDatiPasseggero().getNome());
            ps.setString(7, p.getDatiPasseggero().getCognome());
            ps.setString(8, p.getDatiPasseggero().getCodiceFiscale());

            ps.executeUpdate();
            LOGGER.log(Level.INFO,
                    "OK prenotazione: biglietto={0}, posto={1}, stato={2}, emailutente={3}, idvolo={4}",
                    new Object[]{p.getNumBiglietto(), posto, p.getStato().name(), p.getDatiPasseggero().getEmail(), idVolo});
            return true;

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Errore INSERT prenotazione numbiglietto={0}", p.getNumBiglietto());
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
            return false;
        }
    }

    @Override
    public boolean update(Prenotazione p) {
        if (p == null || p.getNumBiglietto() == null || p.getNumBiglietto().isBlank() ||
                p.getVolo() == null || p.getVolo().getCodiceUnivoco() == null ||
                p.getVolo().getCodiceUnivoco().isBlank() ||
                p.getDatiPasseggero() == null ||
                p.getDatiPasseggero().getNome() == null ||
                p.getDatiPasseggero().getCognome() == null ||
                p.getDatiPasseggero().getCodiceFiscale() == null) {
            LOGGER.warning("Prenotazione non valida per update");
            return false;
        }

        String idVolo = p.getVolo().getCodiceUnivoco();
        String normalizedEmail = normalizeEmail(p.getDatiPasseggero().getEmail());
        String posto = parseSeatOrNull(p.getPostoAssegnato());

        try {
            if (posto == null) {
                posto = generateAvailableSeat(idVolo);
                p.setPostoAssegnato(posto);
            } else if (seatTakenByAnother(idVolo, posto, p.getNumBiglietto())) {
                LOGGER.log(Level.WARNING, "Il posto {0} è già occupato da un altro passeggero", posto);
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Errore gestione posto per update volo={0}", idVolo);
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
            return false;
        }

        final String sql = "UPDATE public.prenotazioni " +
                "SET postoassegnato = ?, stato = ?, emailutente = ?, idvolo = ?, " +
                "dp_nome = ?, dp_cognome = ?, dp_codicefiscale = ? " +
                "WHERE numbiglietto = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, posto);
            ps.setString(2, p.getStato() != null ? p.getStato().name() : null);
            ps.setString(3, normalizedEmail);
            ps.setString(4, idVolo);
            ps.setString(5, p.getDatiPasseggero().getNome());
            ps.setString(6, p.getDatiPasseggero().getCognome());
            ps.setString(7, p.getDatiPasseggero().getCodiceFiscale());
            ps.setString(8, p.getNumBiglietto());

            int rows = ps.executeUpdate();
            LOGGER.log(Level.INFO, "UPDATE prenotazioni: {0} righe aggiornate", rows);
            return rows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Errore UPDATE prenotazione numbiglietto={0}", p.getNumBiglietto());
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
            return false;
        }
    }

    @Override
    public boolean delete(String codicePrenotazione) {
        if (codicePrenotazione == null || codicePrenotazione.isBlank()) {
            LOGGER.warning("Delete fallito: codice prenotazione nullo o vuoto");
            return false;
        }

        String normCodice = codicePrenotazione.trim();

        final String sql = "DELETE FROM public.prenotazioni WHERE numbiglietto = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, normCodice);

            int rows = ps.executeUpdate();
            LOGGER.log(Level.INFO, "Delete prenotazione [{0}]: righe eliminate = {1}", new Object[]{normCodice, rows});

            if (!conn.getAutoCommit()) conn.commit();
            return rows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Errore nel delete prenotazione numbiglietto={0}", normCodice);
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
            return false;
        }
    }

    // ----------------- HELPERS -----------------

    private static StatoPrenotazione parseStatoPren(String s) {
        if (s == null) return null;
        try { return StatoPrenotazione.valueOf(s.trim().toUpperCase()); }
        catch (IllegalArgumentException e) { return null; }
    }

    private static StatoVolo parseStatoVolo(String s) {
        if (s == null) return null;
        try { return StatoVolo.valueOf(s.trim().toUpperCase()); }
        catch (IllegalArgumentException e) { return null; }
    }

    private boolean seatTakenByAnother(String idVolo, String seat, String numBigliettoCorrente) throws SQLException {
        final String sql = "SELECT 1 FROM public.prenotazioni WHERE idvolo = ? AND postoassegnato = ? AND numbiglietto <> ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idVolo);
            ps.setString(2, seat);
            ps.setString(3, numBigliettoCorrente);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    public boolean existsPrenotazionePerVolo(String email, String idVolo) throws SQLException {
        final String sql = "SELECT 1 FROM public.prenotazioni WHERE LOWER(emailutente) = ? AND idvolo = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.toLowerCase());
            ps.setString(2, idVolo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Verifica duplicati lato Java (utente registrato vs anonimo)
    private boolean existsPrenotazionePerVoloEUtente(DatiPasseggero dp, String idVolo, UtenteGenerico utente) throws SQLException {
        if (utente != null && utente.isRegistrato()) {
            final String sql = "SELECT 1 FROM prenotazioni WHERE idvolo = ? AND emailutente = ? LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, idVolo);
                ps.setString(2, normalizeEmail(dp.getEmail()));
                try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
            }
        } else {
            // utente anonimo: controlla se l'email esiste nel DB degli utenti
            if (!utenteDao.emailEsiste(dp.getEmail())) {
                throw new IllegalArgumentException("Email non registrata. Devi registrarti prima di prenotare.");
            }
            // se l’email esiste, blocca comunque la prenotazione per sicurezza
            return true;
        }
    }
    public class DatabaseInitializationException extends RuntimeException {
        public DatabaseInitializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}