package implementazioneDAO.implementazionePostgresDAO;

import implementazioneDAO.PrenotazioneDAO;
import database.ConnessioneDatabase;
import controller.Controller;
import model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
        if (p.getPostoAssegnato() == null || p.getPostoAssegnato().isBlank()) { System.err.println("postoassegnato mancante"); return false; }
        if (p.getStato() == null) { System.err.println("stato mancante"); return false; } // NOT NULL in DB
        if (p.getVolo() == null || p.getVolo().getCodiceUnivoco() == null || p.getVolo().getCodiceUnivoco().isBlank()) { System.err.println("idvolo mancante"); return false; }
        if (p.getDatiPasseggero() == null || p.getDatiPasseggero().getEmail() == null || p.getDatiPasseggero().getEmail().isBlank()) { System.err.println("emailutente mancante"); return false; }

        final String sql = "INSERT INTO public.prenotazioni (numbiglietto, postoassegnato, stato, emailutente, idvolo) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNumBiglietto());
            ps.setString(2, p.getPostoAssegnato());
            ps.setString(3, p.getStato().name()); // DB richiede NOT NULL
            ps.setString(4, p.getDatiPasseggero().getEmail());
            ps.setString(5, p.getVolo().getCodiceUnivoco());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ERRORE INSERT prenotazioni: sqlState=" + e.getSQLState() + " code=" + e.getErrorCode() + " msg=" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Prenotazione p) {
        if (p == null || isBlank(p.getNumBiglietto())) {
            System.err.println("Update Prenotazione fallita: oggetto nullo o numbiglietto mancante");
            return false;
        }
        final String sql = "UPDATE public.prenotazioni " +
                "SET postoassegnato = ?, stato = ?, emailutente = ?, idvolo = ? " +
                "WHERE numbiglietto = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getPostoAssegnato());
            ps.setString(2, p.getStato() != null ? p.getStato().name() : null);
            ps.setString(3, (p.getDatiPasseggero() != null) ? p.getDatiPasseggero().getEmail() : null);
            ps.setString(4, (p.getVolo() != null) ? p.getVolo().getCodiceUnivoco() : null);
            ps.setString(5, p.getNumBiglietto());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore UPDATE prenotazioni: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

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