package implementazioneDAO.implementazionePostgresDAO;

import implementazioneDAO.VoloDAO;
import model.Volo;
import model.StatoVolo;
import database.ConnessioneDatabase;
import java.sql.*;

public class VoloDAOPostgres implements VoloDAO {

    private Connection conn;

    public VoloDAOPostgres() {
        try {
            this.conn = ConnessioneDatabase.getInstance().getConnection();
        } catch(SQLException e) {
            throw new RuntimeException("Errore nella connessione al database", e);
        }
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

    // Mapping da ResultSet al model Volo
    private Volo mapResultSetToVolo(ResultSet rs) throws SQLException {
        Volo v = new Volo();
        v.setCodiceUnivoco(rs.getString("codiceUnivoco"));
        v.setCompagniaAerea(rs.getString("compagniaAerea"));
        v.setDataVolo(rs.getString("dataVolo"));
        v.setOrarioPrevisto(rs.getString("orarioPrevisto"));
        v.setStato(StatoVolo.valueOf(rs.getString("stato")));
        v.setAeroporto(rs.getString("aeroporto"));
        v.setGate(rs.getString("gate"));
        v.setArrivoPartenza(rs.getString("arrivoPartenza"));

        // lascio amministratore, tabellaOrario e prenotazioni null/vuoti per ora
        return v;
    }
}