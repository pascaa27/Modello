package implementazioneDAO.implementazionePostgresDAO;

import implementazioneDAO.BagaglioDAO;
import model.Bagaglio;
import model.StatoBagaglio;
import model.Prenotazione;
import database.ConnessioneDatabase;
import controller.Controller;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BagaglioDAOPostgres implements BagaglioDAO {

    private Connection conn;
    private Controller controller;

    public BagaglioDAOPostgres() {
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

    // Mapping da ResultSet al model Bagaglio passando per il Controller
    private Bagaglio mapResultSetToBagaglio(ResultSet rs) throws SQLException {
        String codUnivoco = rs.getString("codUnivoco");
        double pesoKg = rs.getDouble("pesoKg");
        StatoBagaglio stato = StatoBagaglio.valueOf(rs.getString("stato"));
        String numBiglietto = rs.getString("numBiglietto");

        // Recupera prenotazione già caricata dal Controller
        Prenotazione pren = controller.cercaPrenotazione(numBiglietto);

        // Se la prenotazione non è ancora in memoria, gestire un fallback
        if(pren == null) {
            System.err.println("Prenotazione non trovata per biglietto " + numBiglietto);
            return null; // oppure lancio eccezione
        }

        // Usa il Controller per aggiungere il bagaglio alla lista centrale
        Bagaglio b = new Bagaglio(codUnivoco, pesoKg, stato, pren);
        controller.aggiungiBagaglio(b);

        return b;
    }
}