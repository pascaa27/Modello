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

    private final Connection conn;
    private Controller controller;

    public BagaglioDAOPostgres() {
        try {
            this.conn = ConnessioneDatabase.getInstance().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Errore nella connessione al database", e);
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public List<Bagaglio> findByPrenotazione(String numBiglietto) {
        List<Bagaglio> bagagli = new ArrayList<>();
        String sql = "SELECT * FROM public.bagagli WHERE numBiglietto = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, numBiglietto);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Bagaglio b = mapResultSetToBagaglio(rs);
                    if (b != null) bagagli.add(b);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bagagli;
    }

    @Override
    public List<Bagaglio> findAll() {
        List<Bagaglio> bagagli = new ArrayList<>();
        String sql = "SELECT * FROM public.bagagli ORDER BY codUnivoco";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Bagaglio b = mapResultSetToBagaglio(rs);
                if (b != null) bagagli.add(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bagagli;
    }

    @Override
    public boolean insert(Bagaglio bagaglio) {
        String sql = "INSERT INTO public.bagagli (codUnivoco, pesoKg, stato, numBiglietto) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bagaglio.getCodUnivoco());
            ps.setDouble(2, bagaglio.getPesoKg());
            ps.setString(3, bagaglio.getStato().name());
            if (bagaglio.getPrenotazione() != null) {
                ps.setString(4, bagaglio.getPrenotazione().getNumBiglietto());
            } else {
                ps.setNull(4, Types.VARCHAR);
            }
            int n = ps.executeUpdate();
            System.out.println("[BagagliDAO] insert " + bagaglio.getCodUnivoco() + " rows=" + n);
            return n > 0;
        } catch (SQLException e) {
            System.err.println("[BagagliDAO] insert FAILED for " + bagaglio.getCodUnivoco());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Bagaglio bagaglio) {
        String sql = "UPDATE public.bagagli SET pesoKg = ?, stato = ?, numBiglietto = ? WHERE codUnivoco = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, bagaglio.getPesoKg());
            ps.setString(2, bagaglio.getStato().name());
            if (bagaglio.getPrenotazione() != null) {
                ps.setString(3, bagaglio.getPrenotazione().getNumBiglietto());
            } else {
                ps.setNull(3, Types.VARCHAR);
            }
            ps.setString(4, bagaglio.getCodUnivoco());
            int n = ps.executeUpdate();
            System.out.println("[BagagliDAO] update " + bagaglio.getCodUnivoco() + " rows=" + n);
            return n > 0;
        } catch (SQLException e) {
            System.err.println("[BagagliDAO] update FAILED for " + bagaglio.getCodUnivoco());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(String codUnivoco) {
        String sql = "DELETE FROM public.bagagli WHERE codUnivoco = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codUnivoco);
            int n = ps.executeUpdate();
            System.out.println("[BagagliDAO] delete " + codUnivoco + " rows=" + n);
            return n > 0;
        } catch (SQLException e) {
            System.err.println("[BagagliDAO] delete FAILED for " + codUnivoco);
            e.printStackTrace();
        }
        return false;
    }

    // Mapping senza effetti collaterali: restituisce SEMPRE un Bagaglio, anche con prenotazione nulla
    private Bagaglio mapResultSetToBagaglio(ResultSet rs) throws SQLException {
        String codUnivoco = rs.getString("codUnivoco");
        double pesoKg = rs.getDouble("pesoKg");
        StatoBagaglio stato = StatoBagaglio.valueOf(rs.getString("stato"));
        String numBiglietto = rs.getString("numBiglietto"); // può essere NULL

        Prenotazione pren = null;
        if (controller != null && numBiglietto != null) {
            pren = controller.cercaPrenotazione(numBiglietto); // ok se resta null
        }

        return new Bagaglio(codUnivoco, pesoKg, stato, pren);
    }
}