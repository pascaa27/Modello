package implementazioneDAO.implementazionePostgresDAO;

import controller.Controller;
import implementazioneDAO.DatiPasseggeroDAO;
import model.DatiPasseggero;
import database.ConnessioneDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatiPasseggeroDAOPostgres implements DatiPasseggeroDAO {

    private final Connection conn;
    private Controller controller; // opzionale

    public DatiPasseggeroDAOPostgres() {
        try {
            this.conn = ConnessioneDatabase.getInstance().getConnection();
            this.conn.setAutoCommit(true); // esplicito
        } catch (SQLException e) {
            throw new RuntimeException("Errore nella connessione al database", e);
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public DatiPasseggero findByCodiceFiscale(String codiceFiscale) {
        final String sql = "SELECT nome, cognome, codicefiscale, email " +
                "FROM public.datipasseggeri WHERE codicefiscale = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codiceFiscale);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            System.err.println("Errore findByCodiceFiscale: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Facoltativo ma utile
    public DatiPasseggero findByEmail(String email) {
        final String sql = "SELECT nome, cognome, codicefiscale, email " +
                "FROM public.datipasseggeri WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            System.err.println("Errore findByEmail: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean insert(DatiPasseggero p) {
        if (p == null || isBlank(p.getCodiceFiscale()) || isBlank(p.getNome()) || isBlank(p.getCognome())) {
            System.err.println("Insert datipasseggeri fallita: campi obbligatori mancanti");
            return false;
        }
        final String sql = "INSERT INTO public.datipasseggeri (nome, cognome, codicefiscale, email) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getCognome());
            ps.setString(3, p.getCodiceFiscale());
            if (isBlank(p.getEmail())) {
                ps.setNull(4, java.sql.Types.VARCHAR);
            } else {
                ps.setString(4, p.getEmail());
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore INSERT datipasseggeri: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(DatiPasseggero p) {
        if (p == null || isBlank(p.getCodiceFiscale())) {
            System.err.println("Update datipasseggeri fallita: oggetto nullo o CF mancante");
            return false;
        }
        final String sql = "UPDATE public.datipasseggeri " +
                "SET nome = ?, cognome = ?, email = ? " +
                "WHERE codicefiscale = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getCognome());
            // Se email è vuota o nulla, salva come null su DB
            if (isBlank(p.getEmail())) {
                ps.setNull(3, java.sql.Types.VARCHAR);
            } else {
                ps.setString(3, p.getEmail());
            }
            ps.setString(4, p.getCodiceFiscale());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore UPDATE datipasseggeri: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String codiceFiscale) {
        final String sql = "DELETE FROM public.datipasseggeri WHERE codicefiscale = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codiceFiscale);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore DELETE datipasseggeri: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private DatiPasseggero map(ResultSet rs) throws SQLException {
        String nome = rs.getString("nome");
        String cognome = rs.getString("cognome");
        String cf = rs.getString("codicefiscale");
        String email = rs.getString("email");
        // Evito side-effect sulla cache del Controller
        return new DatiPasseggero(nome, cognome, cf, email);
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}