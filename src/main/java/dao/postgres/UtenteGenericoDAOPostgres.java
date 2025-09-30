package dao.postgres;

import controller.Controller;
import model.UtenteGenerico;
import database.ConnessioneDatabase;
import dao.UtenteGenericoDAO;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UtenteGenericoDAOPostgres implements UtenteGenericoDAO {

    private static final Logger LOGGER = Logger.getLogger(UtenteGenericoDAOPostgres.class.getName());
    private static final String LOG_SQL_DETAILS = "Dettagli SQL exception";

    private final Connection conn;
    private Controller controller;

    public UtenteGenericoDAOPostgres() {
        try {
            this.conn = ConnessioneDatabase.getInstance().getConnection();
        } catch (SQLException e) {
            // Eccezione dedicata invece di una generica RuntimeException
            throw new DatabaseInitializationException("Errore nella connessione al database", e);
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public UtenteGenerico findByEmail(String email) {
        // Seleziona solo utenti con ruolo 'utente' (niente SELECT *)
        final String sql = "SELECT email, password, nome, cognome " +
                "FROM registrazioneutente WHERE email = ? AND ruolo = 'utente'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUtenteGenerico(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Errore findByEmail per email={0}", email);
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
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
            LOGGER.log(Level.WARNING, "Errore existsByEmail per email={0}", email);
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
            return false;
        }
    }

    // Verifica se l'email esiste nella tabella datipasseggeri (usato per utenti 'anonimi')
    public boolean emailEsiste(String email) {
        if (email == null || email.isBlank()) return false;
        final String sql = "SELECT 1 FROM datipasseggeri WHERE LOWER(email) = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Errore emailEsiste per email={0}", email);
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
            return false;
        }
    }

    @Override
    public boolean insert(UtenteGenerico u) {
        final String sql = "INSERT INTO registrazioneutente (email, password, nome, cognome, ruolo) " +
                "VALUES (?, ?, ?, ?, 'utente')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getLogin());       // login -> email
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getNomeUtente());
            ps.setString(4, u.getCognomeUtente());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Errore insert utente email={0}", u.getLogin());
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
        }
        return false;
    }

    @Override
    public boolean update(UtenteGenerico u) {
        final String sql = "UPDATE registrazioneutente " +
                "SET password = ?, nome = ?, cognome = ? " +
                "WHERE email = ? AND ruolo = 'utente'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getPassword());
            ps.setString(2, u.getNomeUtente());
            ps.setString(3, u.getCognomeUtente());
            ps.setString(4, u.getLogin());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Errore update utente email={0}", u.getLogin());
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
        }
        return false;
    }

    @Override
    public boolean delete(String email) {
        final String sql = "DELETE FROM registrazioneutente WHERE email = ? AND ruolo = 'utente'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Errore delete utente email={0}", email);
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
        }
        return false;
    }

    private UtenteGenerico mapResultSetToUtenteGenerico(ResultSet rs) throws SQLException {
        String email = rs.getString("email");
        String password = rs.getString("password");
        String nome = rs.getString("nome");
        String cognome = rs.getString("cognome");

        if (controller == null) {
            // Usa il costruttore completo presente nel progetto
            UtenteGenerico u = new UtenteGenerico(
                    email,
                    password,
                    nome,
                    cognome,
                    new java.util.ArrayList<>(),
                    new model.AreaPersonale()
            );
            return u;
        }

        UtenteGenerico utente = controller.getUtenteByEmail(email);
        if (utente == null) {
            utente = controller.creaUtenteGenerico(email);
        }

        utente.setPassword(password);
        utente.setNomeUtente(nome);
        utente.setCognomeUtente(cognome);

        return utente;
    }

    public class DatabaseInitializationException extends RuntimeException {
        public DatabaseInitializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}