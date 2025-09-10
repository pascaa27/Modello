package implementazioneDAO.implementazionePostgresDAO;

import controller.Controller;
import implementazioneDAO.DatiPasseggeroDAO;
import model.DatiPasseggero;
import database.ConnessioneDatabase;
import java.sql.*;

public class DatiPasseggeroDAOPostgres implements DatiPasseggeroDAO {

    private Connection conn;
    private Controller controller;

    public DatiPasseggeroDAOPostgres() {
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
    public DatiPasseggero findByCodiceFiscale(String codiceFiscale) {
        String sql = "SELECT * FROM datiPasseggeri WHERE codiceFiscale = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codiceFiscale);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return mapResultSetToDatiPasseggero(rs);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null; // se non trovato
    }

    @Override
    public boolean insert(DatiPasseggero passeggero) {
        String sql = "INSERT INTO datiPasseggeri (nome, cognome, codiceFiscale, email) VALUES (?, ?, ?, ?)";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, passeggero.getNome());
            ps.setString(2, passeggero.getCognome());
            ps.setString(3, passeggero.getCodiceFiscale());
            ps.setString(4, passeggero.getEmail());
            return ps.executeUpdate() > 0;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(DatiPasseggero passeggero) {
        String sql = "UPDATE datiPasseggeri SET nome = ?, cognome = ?, email = ? WHERE codiceFiscale = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, passeggero.getNome());
            ps.setString(2, passeggero.getCognome());
            ps.setString(3, passeggero.getEmail());
            ps.setString(4, passeggero.getCodiceFiscale());
            return ps.executeUpdate() > 0;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(String codiceFiscale) {
        String sql = "DELETE FROM datiPasseggeri WHERE codiceFiscale = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codiceFiscale);
            return ps.executeUpdate() > 0;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Mapping da ResultSet al model DatiPasseggero passando per il Controller
    private DatiPasseggero mapResultSetToDatiPasseggero(ResultSet rs) throws SQLException {
        String nome = rs.getString("nome");
        String cognome = rs.getString("cognome");
        String codiceFiscale = rs.getString("codiceFiscale");
        String email = rs.getString("email");

        return controller.creaDatiPasseggero(nome, cognome, codiceFiscale, email);
    }
}