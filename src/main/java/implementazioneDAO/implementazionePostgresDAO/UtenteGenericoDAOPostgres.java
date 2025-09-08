package implementazioneDAO.implementazionePostgresDAO;

import model.UtenteGenerico;
import database.ConnessioneDatabase;
import implementazioneDAO.UtenteGenericoDAO;
import java.sql.*;
import java.util.ArrayList;

public class UtenteGenericoDAOPostgres implements UtenteGenericoDAO {

    private Connection conn;

    public UtenteGenericoDAOPostgres() {
        try {
            this.conn = ConnessioneDatabase.getInstance().getConnection();
        } catch(SQLException e) {
            throw new RuntimeException("Errore nella connessione al database", e);
        }
    }

    @Override
    public UtenteGenerico findByEmail(String email) {
        // "login" nel DB corrisponde all'email
        String sql = "SELECT * FROM utentiGenerici WHERE login = ?";
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

    @Override
    public boolean insert(UtenteGenerico u) {
        String sql = "INSERT INTO utentiGenerici (login, password, nomeUtente, cognomeUtente) VALUES (?, ?, ?, ?)";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getLogin());
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
        String sql = "UPDATE utentiGenerici SET password = ?, nomeUtente = ?, cognomeUtente = ? WHERE login = ?";
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
    public boolean delete(String login) {
        String sql = "DELETE FROM utentiGenerici WHERE login = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);
            return ps.executeUpdate() > 0;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Mapping da ResultSet al model UtenteGenerico
    private UtenteGenerico mapResultSetToUtenteGenerico(ResultSet rs) throws SQLException {
        UtenteGenerico utente = new UtenteGenerico();  // costruttore vuoto
        utente.setLogin(rs.getString("login"));
        utente.setPassword(rs.getString("password"));
        utente.setNomeUtente(rs.getString("nomeUtente"));
        utente.setCognomeUtente(rs.getString("cognomeUtente"));

        // inizializziamo lista vuota e areaPersonale come null per ora
        utente.setPrenotazioni(new ArrayList<>());
        utente.setAreaPersonale(null);

        return utente;
    }
}