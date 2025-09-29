package implementazioneDAO.implementazionePostgresDAO;

import controller.Controller;
import model.UtenteGenerico;
import database.ConnessioneDatabase;
import implementazioneDAO.UtenteGenericoDAO;
import java.sql.*;

public class UtenteGenericoDAOPostgres implements UtenteGenericoDAO {

    private Connection conn;
    private Controller controller;

    public UtenteGenericoDAOPostgres() {
        try {
            this.conn = ConnessioneDatabase.getInstance().getConnection();
        } catch(SQLException e) {
            throw new RuntimeException("Errore nella connessione al database", e);
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public UtenteGenerico findByEmail(String email) {
        // Seleziona solo utenti con ruolo 'utente'
        String sql = "SELECT * FROM registrazioneutente WHERE email = ? AND ruolo = 'utente'";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return mapResultSetToUtenteGenerico(rs);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null; // se non trovato
    }

    public boolean existsByEmail(String email) {
        if (email == null || email.isBlank()) return false;

        final String sql = "SELECT 1 FROM public.registrazioneutente WHERE LOWER(email) = LOWER(?) LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // true se trova almeno una riga
            }
        } catch (SQLException e) {
            System.err.println("Errore existsByEmail: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // UtenteGenericoDAOPostgres.java
    public boolean emailEsiste(String email) {
        if (email == null || email.isBlank()) return false;
        final String sql = "SELECT 1 FROM datipasseggeri WHERE LOWER(email) = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean insert(UtenteGenerico u) {
        String sql = "INSERT INTO registrazioneutente (email, password, nome, cognome, ruolo) VALUES (?, ?, ?, ?, 'utente')";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getLogin());       // login -> email
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getNomeUtente());
            ps.setString(4, u.getCognomeUtente());
            return ps.executeUpdate() > 0;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(UtenteGenerico u) {
        String sql = "UPDATE registrazioneutente SET password = ?, nome = ?, cognome = ? WHERE email = ? AND ruolo = 'utente'";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getPassword());
            ps.setString(2, u.getNomeUtente());
            ps.setString(3, u.getCognomeUtente());
            ps.setString(4, u.getLogin());
            return ps.executeUpdate() > 0;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(String email) {
        String sql = "DELETE FROM registrazioneutente WHERE email = ? AND ruolo = 'utente'";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            return ps.executeUpdate() > 0;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private UtenteGenerico mapResultSetToUtenteGenerico(ResultSet rs) throws SQLException {
        String email = rs.getString("email");
        String password = rs.getString("password");
        String nome = rs.getString("nome");
        String cognome = rs.getString("cognome");

        UtenteGenerico utente = controller.getUtenteByEmail(email);
        if (utente == null) {
            utente = controller.creaUtenteGenerico(email);
        }

        utente.setPassword(password);
        utente.setNomeUtente(nome);
        utente.setCognomeUtente(cognome);

        return utente;
    }
}