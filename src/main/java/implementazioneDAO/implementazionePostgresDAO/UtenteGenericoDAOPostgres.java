package implementazioneDAO.implementazionePostgresDAO;

import controller.Controller;
import model.UtenteGenerico;
import database.ConnessioneDatabase;
import implementazioneDAO.UtenteGenericoDAO;
import java.sql.*;
import java.util.ArrayList;

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

    // Mapping da ResultSet al model UtenteGenerico passando per il Controller
    private UtenteGenerico mapResultSetToUtenteGenerico(ResultSet rs) throws SQLException {
        String email = rs.getString("login");

        UtenteGenerico utente = controller.getUtenteByEmail(email);

        // Se l'utente non esiste ancora, lo creo tramite il Controller
        if(utente == null) {
            utente = controller.creaUtenteGenerico(email);
        }

        utente.setPassword(rs.getString("password"));
        utente.setNomeUtente(rs.getString("nomeUtente"));
        utente.setCognomeUtente(rs.getString("cognomeUtente"));

        return utente;
    }
}