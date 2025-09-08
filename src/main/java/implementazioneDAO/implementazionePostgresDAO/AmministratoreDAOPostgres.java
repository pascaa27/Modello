package implementazioneDAO.implementazionePostgresDAO;

import implementazioneDAO.AmministratoreDAO;
import model.Amministratore;
import database.ConnessioneDatabase;
import java.sql.*;

public class AmministratoreDAOPostgres implements AmministratoreDAO {

    private Connection conn;

    public AmministratoreDAOPostgres() {
        try {
            this.conn = ConnessioneDatabase.getInstance().getConnection();
        } catch(SQLException e) {
            throw new RuntimeException("Errore nella connessione al database", e);
        }
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

    // Mapping da ResultSet al model Amministratore
    private Amministratore mapResultSetToAmministratore(ResultSet rs) throws SQLException {
        // supponendo che la tabella amministratori abbia colonne: login, password, nome, cognome, email
        String login = rs.getString("login");
        String password = rs.getString("password");
        String nome = rs.getString("nome");
        String cognome = rs.getString("cognome");

        Amministratore admin = new Amministratore(login, password, nome, cognome);

        // per i voli gestiti creare una query separata
        // altrimenti lasciare la lista vuota per ora
        // esempio: admin.setVoliGestiti(...);

        return admin;
    }
}