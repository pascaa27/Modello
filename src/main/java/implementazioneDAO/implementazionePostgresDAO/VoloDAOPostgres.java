package implementazioneDAO.implementazionePostgresDAO;

import controller.Controller;
import implementazioneDAO.VoloDAO;
import model.Volo;
import model.StatoVolo;
import database.ConnessioneDatabase;
import java.sql.*;

public class VoloDAOPostgres implements VoloDAO {

    private Connection conn;
    private Controller controller;

    public VoloDAOPostgres() {
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
    public Volo findByCodiceUnivoco(String codiceUnivoco) {
        String sql = "SELECT * FROM voli WHERE codiceUnivoco = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codiceUnivoco);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return mapResultSetToVolo(rs);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null; // se non trovato
    }

    @Override
    public boolean insert(Volo v) {
        String sql = "INSERT INTO voli (codiceUnivoco, compagniaAerea, dataVolo, orarioPrevisto, stato, aeroporto, gate, arrivoPartenza) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, v.getCodiceUnivoco());
            ps.setString(2, v.getCompagniaAerea());
            ps.setString(3, v.getDataVolo());
            ps.setString(4, v.getOrarioPrevisto());
            ps.setString(5, v.getStato().name());
            ps.setString(6, v.getAeroporto());
            ps.setString(7, v.getGate());
            ps.setString(8, v.getArrivoPartenza());
            return ps.executeUpdate() > 0;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Volo v) {
        String sql = "UPDATE voli SET compagniaAerea=?, dataVolo=?, orarioPrevisto=?, stato=?, aeroporto=?, gate=?, arrivoPartenza=? WHERE codiceUnivoco=?";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, v.getCompagniaAerea());
            ps.setString(2, v.getDataVolo());
            ps.setString(3, v.getOrarioPrevisto());
            ps.setString(4, v.getStato().name());
            ps.setString(5, v.getAeroporto());
            ps.setString(6, v.getGate());
            ps.setString(7, v.getArrivoPartenza());
            ps.setString(8, v.getCodiceUnivoco());
            return ps.executeUpdate() > 0;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(String codiceUnivoco) {
        String sql = "DELETE FROM voli WHERE codiceUnivoco=?";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codiceUnivoco);
            return ps.executeUpdate() > 0;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Mapping da ResultSet al model Volo passando per il Controller
    private Volo mapResultSetToVolo(ResultSet rs) throws SQLException {
        String codice = rs.getString("codiceUnivoco");

        Volo volo = controller.getVoloByCodice(codice);

        // Se non esiste il volo, lo creo tramite il Controller
        if(volo == null) {
            volo = controller.creaVolo(codice);
        }

        // Aggiorno i campi dal ResultSet
        volo.setCompagniaAerea(rs.getString("compagniaAerea"));
        volo.setDataVolo(rs.getString("dataVolo"));
        volo.setOrarioPrevisto(rs.getString("orarioPrevisto"));
        volo.setStato(rs.getString("stato") != null ? StatoVolo.valueOf(rs.getString("stato")) : null);
        volo.setAeroporto(rs.getString("aeroporto"));
        volo.setGate(rs.getString("gate"));
        volo.setArrivoPartenza(rs.getString("arrivoPartenza"));

        // Lascio amministratore, tabellaOrario e prenotazioni come già gestiti dal Controller

        return volo;
    }
}