package implementazioneDAO.implementazionePostgresDAO;

import controller.Controller;
import implementazioneDAO.DatiPasseggeroDAO;
import model.DatiPasseggero;
import database.ConnessioneDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatiPasseggeroDAOPostgres implements DatiPasseggeroDAO {

    private final Connection conn;
    private Controller controller; // opzionale

    public DatiPasseggeroDAOPostgres() {
        try {
            this.conn = ConnessioneDatabase.getInstance().getConnection();
            this.conn.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException("Errore nella connessione al database", e);
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    // CF rimosso dallo schema: manteniamo il metodo deprecato ma restituiamo sempre null
    @Override
    @Deprecated
    public DatiPasseggero findByCodiceFiscale(String codiceFiscale) {
        return null;
    }

    @Override
    public DatiPasseggero findByEmail(String email) {
        final String sql = "SELECT nome, cognome, email, password FROM public.datipasseggeri WHERE email = LOWER(BTRIM(?))";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            System.err.println("Errore findByEmail: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // REGISTRAZIONE: upsert su email (email deve essere UNIQUE su datipasseggeri)
    @Override
    public boolean insert(DatiPasseggero p) {
        if (p == null || isBlank(p.getNome()) || isBlank(p.getCognome()) || isBlank(p.getEmail())) {
            System.err.println("Insert datipasseggeri fallita: campi obbligatori mancanti (nome, cognome, email)");
            return false;
        }
        final String sql =
                "INSERT INTO public.datipasseggeri (nome, cognome, email, password) " +
                        "VALUES (?, ?, LOWER(BTRIM(?)), ?) " +
                        "ON CONFLICT (email) DO UPDATE SET " +
                        "  nome = EXCLUDED.nome, " +
                        "  cognome = EXCLUDED.cognome, " +
                        "  password = EXCLUDED.password";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getCognome());
            ps.setString(3, p.getEmail());
            ps.setString(4, p.getPassword() == null ? "" : p.getPassword());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore INSERT/UPSERT datipasseggeri: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(DatiPasseggero p) {
        if (p == null || isBlank(p.getEmail())) {
            System.err.println("Update datipasseggeri fallita: oggetto nullo o email mancante");
            return false;
        }
        final String sql = "UPDATE public.datipasseggeri SET nome = ?, cognome = ?, password = ? WHERE email = LOWER(BTRIM(?))";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getCognome());
            ps.setString(3, p.getPassword() == null ? "" : p.getPassword());
            ps.setString(4, p.getEmail());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore UPDATE datipasseggeri: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteByEmail(String email) {
        final String sql = "DELETE FROM public.datipasseggeri WHERE email = LOWER(BTRIM(?))";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore DELETE datipasseggeri: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // LOGIN: verifica credenziali
    public boolean checkCredenziali(String email, String passwordChiara) {
        if (isBlank(email) || passwordChiara == null) return false;
        final String sql =
                "SELECT 1 FROM public.datipasseggeri " +
                        "WHERE email = LOWER(BTRIM(?)) AND password = ? " +
                        "LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, passwordChiara);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Errore verifica credenziali (datipasseggeri): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private DatiPasseggero map(ResultSet rs) throws SQLException {
        String nome = rs.getString("nome");
        String cognome = rs.getString("cognome");
        String email = rs.getString("email");
        String password = rs.getString("password");
        // codiceFiscale rimosso => passiamo null
        return new DatiPasseggero(nome, cognome, null, email, password);
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}