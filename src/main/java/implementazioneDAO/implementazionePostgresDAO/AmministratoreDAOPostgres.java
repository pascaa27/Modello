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
        String sql = "SELECT * FROM amministratori WHERE email = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                return mapResultSetToAmministratore(rs);
            }

        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null; // se non trovato
    }

    // Mapping da ResultSet al model Amministratore passando per il Controller
    private Amministratore mapResultSetToAmministratore(ResultSet rs) throws SQLException {
        String login = rs.getString("login");
        String password = rs.getString("password");
        String nome = rs.getString("nome");
        String cognome = rs.getString("cognome");

        return controller.creaAmministratore(login, password, nome, cognome);
    }
}