package dao.postgres;

import dao.AmministratoreDAO;
import model.Amministratore;
import database.ConnessioneDatabase;
import controller.Controller;

import java.sql.*;

public class AmministratoreDAOPostgres implements AmministratoreDAO {

    private Connection conn;
    private Controller controller;

    public AmministratoreDAOPostgres() {
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
    public Amministratore findByEmail(String email) {
        // Seleziona solo le colonne necessarie (niente SELECT *)
        final String sql = "SELECT email, password, nome, cognome " +
                "FROM registrazioneutente " +
                "WHERE email = ? AND ruolo = 'amministratore'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAmministratore(rs);
                }
            }
        } catch (SQLException e) {
            // Mantengo il comportamento attuale (puoi sostituire con logging se preferisci)
            e.printStackTrace();
        }
        return null; // se non trovato
    }

    private Amministratore mapResultSetToAmministratore(ResultSet rs) throws SQLException {
        String email = rs.getString("email");
        String password = rs.getString("password");
        String nome = rs.getString("nome");
        String cognome = rs.getString("cognome");

        return controller.creaAmministratore(email, password, nome, cognome);
    }

    public class DatabaseInitializationException extends RuntimeException {
        public DatabaseInitializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}