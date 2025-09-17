package implementazioneDAO.implementazionePostgresDAO;

import implementazioneDAO.AmministratoreDAO;
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
        } catch(SQLException e) {
            throw new RuntimeException("Errore nella connessione al database", e);
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public Amministratore findByEmail(String email) {
        // Seleziona solo utenti con ruolo 'amministratore'
        String sql = "SELECT * FROM registrazioneutente WHERE email = ? AND ruolo = 'amministratore'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToAmministratore(rs);
            }
        } catch(SQLException e) {
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
}