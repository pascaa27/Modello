package implementazioneDAO.implementazionePostgresDAO;

import implementazioneDAO.PrenotazioneDAO;
import database.ConnessioneDatabase;
import controller.Controller;
import model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PrenotazioneDAOPostgres implements PrenotazioneDAO {

    private final Connection conn;
    private Controller controller; // opzionale

    public PrenotazioneDAOPostgres() {
        try {
            this.conn = ConnessioneDatabase.getInstance().getConnection();
            this.conn.setAutoCommit(true); // esplicito
        } catch (SQLException e) {
            throw new RuntimeException("Errore nella connessione al database", e);
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    private static final String BASE_SELECT =
            "SELECT p.numbiglietto, p.postoassegnato, p.stato, p.emailutente, p.idvolo, " +
                    "       d.nome AS dp_nome, d.cognome AS dp_cognome, d.codicefiscale AS dp_codicefiscale, d.email AS dp_email, " +
                    "       v.compagniaaerea AS v_compagniaaerea, v.datavolo AS v_datavolo, v.orarioprevisto AS v_orarioprevisto, " +
                    "       v.stato AS v_stato, v.aeroporto AS v_aeroporto, v.gate AS v_gate, v.arrivopartenza AS v_arrivopartenza " +
                    "FROM public.prenotazioni p " +
                    "LEFT JOIN public.datipasseggeri d ON d.email = p.emailutente " +
                    "LEFT JOIN public.voli v ON v.codiceunivoco = p.idvolo ";

    // Parametri generazione posti: colonne A..F e file 1..30
    private static final char[] SEAT_LETTERS = "ABCDEF".toCharArray();
    private static final int SEAT_MAX_NUMBER = 30;

    private String generateRandomSeat() {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        char letter = SEAT_LETTERS[r.nextInt(SEAT_LETTERS.length)];
        int number = r.nextInt(1, SEAT_MAX_NUMBER + 1);
        return letter + String.valueOf(number);
    }

    // Normalizza l'input utente: rimuove caratteri non alfanumerici e porta in upper case
    private static String norm(String s) {
        if (s == null) return "";
        return s.trim().toUpperCase().replaceAll("[^A-Z0-9]", "");
    }

    // Ritorna un posto valido (es. "A12") se l'input corrisponde al formato A..F + 1..30; altrimenti null (=> auto)
    private String parseSeatOrNull(String raw) {
        String t = norm(raw);
        // Esplicite parole chiave per "auto"
        if (t.isEmpty() || t.equals("AUTO") || t.equals("POSTO") || t.equals("POSTOAUTO")) return null;

        if (t.length() < 2) return null;
        char letter = t.charAt(0);
        if (letter < 'A' || letter > 'F') return null;
        String numStr = t.substring(1);
        try {
            int n = Integer.parseInt(numStr);
            if (n >= 1 && n <= SEAT_MAX_NUMBER) {
                return letter + String.valueOf(n);
            }
        } catch (NumberFormatException ignored) { }
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
        // Tentativi random
        for (int attempt = 0; attempt < 200; attempt++) {
            String candidate = generateRandomSeat();
            if (!seatExists(idVolo, candidate)) return candidate;
        }
        // Fallback deterministico
        for (char letter : SEAT_LETTERS) {
            for (int num = 1; num <= SEAT_MAX_NUMBER; num++) {
                String candidate = letter + String.valueOf(num);
                if (!seatExists(idVolo, candidate)) return candidate;
            }
        }
        throw new SQLException("Nessun posto disponibile per il volo " + idVolo);
    }

    @Override
    public Prenotazione findByCodice(String codice) {
        String sql = BASE_SELECT + "WHERE p.numbiglietto = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codice);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("Errore findByCodice: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Prenotazione> findAllByUtente(String emailUtente) {
        List<Prenotazione> out = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE p.emailutente = ? ORDER BY p.numbiglietto";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, emailUtente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Errore findAllByUtente: " + e.getMessage());
            e.printStackTrace();
        }
        return out;
    }

    @Override
    public List<Prenotazione> findAll() {
        List<Prenotazione> out = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY p.numbiglietto";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Errore findAll: " + e.getMessage());
            e.printStackTrace();
        }
        return out;
    }

    @Override
    public boolean insert(Prenotazione p) {
        if (p == null) { System.err.println("Prenotazione nulla"); return false; }
        if (p.getNumBiglietto() == null || p.getNumBiglietto().isBlank()) { System.err.println("numbiglietto mancante"); return false; }
        if (p.getStato() == null) { System.err.println("stato mancante"); return false; }
        if (p.getVolo() == null || p.getVolo().getCodiceUnivoco() == null || p.getVolo().getCodiceUnivoco().isBlank()) { System.err.println("idvolo mancante"); return false; }
        if (p.getDatiPasseggero() == null || p.getDatiPasseggero().getEmail() == null || p.getDatiPasseggero().getEmail().isBlank()) { System.err.println("emailutente mancante"); return false; }

        final String idVolo = p.getVolo().getCodiceUnivoco();
        String posto = parseSeatOrNull(p.getPostoAssegnato());
        try {
            if (posto == null) {
                posto = generateAvailableSeat(idVolo); // A..F + 1..30, non occupato
                p.setPostoAssegnato(posto);            // aggiorna l’oggetto per mostrarlo in UI
            } else if (seatExists(idVolo, posto)) {
                System.err.println("Il posto " + posto + " per il volo " + idVolo + " è già occupato");
                return false;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }

        final String sql = "INSERT INTO public.prenotazioni (numbiglietto, postoassegnato, stato, emailutente, idvolo) VALUES (?, ?, ?, ?, ?)";

        // Retry in caso di collisione concorrente (unique 23505)
        final int MAX_RETRY = 3;
        for (int attempt = 0; attempt <= MAX_RETRY; attempt++) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, p.getNumBiglietto());
                ps.setString(2, posto);
                ps.setString(3, p.getStato().name());
                ps.setString(4, p.getDatiPasseggero().getEmail());
                ps.setString(5, idVolo);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                if ("23505".equals(e.getSQLState())) {
                    // Collisione su (idvolo, postoassegnato)
                    if (attempt == MAX_RETRY) {
                        System.err.println("ERRORE INSERT prenotazioni: collisione ripetuta su posto. Ultimo msg: " + e.getMessage());
                        return false;
                    }
                    try {
                        posto = generateAvailableSeat(idVolo);
                        p.setPostoAssegnato(posto);
                    } catch (SQLException ex) {
                        System.err.println("Errore nel ricalcolo del posto dopo 23505: " + ex.getMessage());
                        ex.printStackTrace();
                        return false;
                    }
                    continue;
                }
                System.err.println("ERRORE INSERT prenotazioni: sqlState=" + e.getSQLState() + " code=" + e.getErrorCode() + " msg=" + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    // NUOVO: controlla se il posto è preso da un'altra prenotazione (esclude numbiglietto corrente)
    private boolean seatTakenByAnother(String idVolo, String seat, String numBigliettoCorrente) throws SQLException {
        final String sql =
                "SELECT 1 " +
                        "FROM public.prenotazioni " +
                        "WHERE idvolo = ? AND postoassegnato = ? AND numbiglietto <> ? " +
                        "LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idVolo);
            ps.setString(2, seat);
            ps.setString(3, numBigliettoCorrente);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public boolean update(Prenotazione p) {
        if (p == null || p.getNumBiglietto() == null || p.getNumBiglietto().isBlank()) {
            System.err.println("Update Prenotazione fallita: oggetto nullo o numbiglietto mancante");
            return false;
        }
        final String idVolo = (p.getVolo() != null) ? p.getVolo().getCodiceUnivoco() : null;
        if (idVolo == null) {
            System.err.println("Update Prenotazione fallita: idvolo mancante");
            return false;
        }

        // Se posto nullo/non valido => rigenera; se valido, NON considerarlo “occupato” se è la stessa prenotazione
        String posto = parseSeatOrNull(p.getPostoAssegnato());
        try {
            if (posto == null) {
                posto = generateAvailableSeat(idVolo);
                p.setPostoAssegnato(posto);
            } else if (seatTakenByAnother(idVolo, posto, p.getNumBiglietto())) {
                System.err.printf("Update: posto %s per volo %s occupato da un'altra prenotazione%n", posto, idVolo);
                return false;
            }
        } catch (SQLException ex) {
            System.err.println("Errore durante la generazione/verifica del posto (update): " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }

        final String sql =
                "UPDATE public.prenotazioni " +
                        "SET postoassegnato = ?, stato = ?, emailutente = ?, idvolo = ? " +
                        "WHERE numbiglietto = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, posto);
            ps.setString(2, p.getStato() != null ? p.getStato().name() : null);
            ps.setString(3, (p.getDatiPasseggero() != null) ? p.getDatiPasseggero().getEmail() : null);
            ps.setString(4, idVolo);
            ps.setString(5, p.getNumBiglietto());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore UPDATE prenotazioni: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

// ... resto classe

    @Override
    public boolean delete(String codicePrenotazione) {
        final String sql = "DELETE FROM public.prenotazioni WHERE numbiglietto = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codicePrenotazione);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore DELETE prenotazioni: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Prenotazione mapRow(ResultSet rs) throws SQLException {
        String numBiglietto = rs.getString("numbiglietto");
        String posto = rs.getString("postoassegnato");
        StatoPrenotazione stato = parseStatoPren(rs.getString("stato"));
        String emailUtente = rs.getString("emailutente");

        String codiceVolo = rs.getString("idvolo");
        Volo volo = null;
        if (controller != null) volo = controller.getVoloByCodice(codiceVolo);
        if (volo == null) {
            String comp = rs.getString("v_compagniaaerea");
            String dataV = rs.getString("v_datavolo");
            String orario = rs.getString("v_orarioprevisto");
            StatoVolo statoV = parseStatoVolo(rs.getString("v_stato"));
            volo = new Volo(codiceVolo, comp, dataV, orario, statoV, null, null);
            volo.setAeroporto(rs.getString("v_aeroporto"));
            volo.setGate(rs.getString("v_gate"));
            volo.setArrivoPartenza(rs.getString("v_arrivopartenza"));
        }

        DatiPasseggero dp = null;
        String dpEmail = rs.getString("dp_email");
        if (dpEmail != null) {
            dp = new DatiPasseggero(
                    rs.getString("dp_nome"),
                    rs.getString("dp_cognome"),
                    rs.getString("dp_codicefiscale"),
                    dpEmail
            );
        }

        UtenteGenerico utente = null;
        if (controller != null && emailUtente != null) {
            utente = controller.getUtenteByEmail(emailUtente);
            if (utente == null) {
                utente = controller.creaUtenteGenerico(emailUtente); // rimuovi se non vuoi side-effect
            }
        }

        return new Prenotazione(numBiglietto, posto, stato, utente, dp, volo);
    }

    private static StatoPrenotazione parseStatoPren(String s) {
        if (s == null) return null;
        try { return StatoPrenotazione.valueOf(s.trim().toUpperCase()); }
        catch (IllegalArgumentException ex) { return null; }
    }

    private static StatoVolo parseStatoVolo(String s) {
        if (s == null) return null;
        try { return StatoVolo.valueOf(s.trim().toUpperCase()); }
        catch (IllegalArgumentException ex) { return null; }
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}