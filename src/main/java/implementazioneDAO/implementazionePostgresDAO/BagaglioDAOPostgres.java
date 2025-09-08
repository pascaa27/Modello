package implementazioneDAO.implementazionePostgresDAO;

import implementazioneDAO.BagaglioDAO;
import model.Bagaglio;
import model.Prenotazione;
import model.StatoBagaglio;
import database.ConnessioneDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BagaglioDAOPostgres implements BagaglioDAO {

    private Connection conn;

    public BagaglioDAOPostgres() {
        try {
            this.conn = ConnessioneDatabase.getInstance().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Errore nella connessione al database", e);
        }
    }

    @Override
    public List<Bagaglio> findByPrenotazione(String numBiglietto) {
        List<Bagaglio> bagagli = new ArrayList<>();
        String sql = "SELECT * FROM bagagli WHERE numBiglietto = ?";

        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, numBiglietto);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                bagagli.add(mapResultSetToBagaglio(rs));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return bagagli;
    }

    @Override
    public boolean insert(Bagaglio bagaglio) {
        String sql = "INSERT INTO bagagli (codUnivoco, pesoKg, stato, numBiglietto) VALUES (?, ?, ?, ?)";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bagaglio.getCodUnivoco());
            ps.setDouble(2, bagaglio.getPesoKg());
            ps.setString(3, bagaglio.getStato().name());
            ps.setString(4, bagaglio.getPrenotazione().getNumBiglietto());
            return ps.executeUpdate() > 0;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Bagaglio bagaglio) {
        String sql = "UPDATE bagagli SET pesoKg = ?, stato = ?, numBiglietto = ? WHERE codUnivoco = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, bagaglio.getPesoKg());
            ps.setString(2, bagaglio.getStato().name());
            ps.setString(3, bagaglio.getPrenotazione().getNumBiglietto());
            ps.setString(4, bagaglio.getCodUnivoco());
            return ps.executeUpdate() > 0;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(String codUnivoco) {
        String sql = "DELETE FROM bagagli WHERE codUnivoco = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codUnivoco);
            return ps.executeUpdate() > 0;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Mapping da ResultSet al model Bagaglio
    private Bagaglio mapResultSetToBagaglio(ResultSet rs) throws SQLException {
        String codUnivoco = rs.getString("codUnivoco");
        double pesoKg = rs.getDouble("pesoKg");
        StatoBagaglio stato = StatoBagaglio.valueOf(rs.getString("stato"));

        // creo una Prenotazione solo con il codice biglietto
        Prenotazione pren = new Prenotazione();
        pren.setNumBiglietto(rs.getString("numBiglietto"));

        return new Bagaglio(codUnivoco, pesoKg, stato, pren);
    }
}