package implementazioneDAO.implementazionePostgresDAO;

import implementazioneDAO.PrenotazioneDAO;
import model.*;
import database.ConnessioneDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrenotazioneDAOPostgres implements PrenotazioneDAO {

    private Connection conn;

    public PrenotazioneDAOPostgres() {
        try {
            this.conn = ConnessioneDatabase.getInstance().getConnection();
        } catch(SQLException e) {
            throw new RuntimeException("Errore nella connessione al database", e);
        }
    }

    @Override
    public Prenotazione findByCodice(String codice) {
        String sql = "SELECT * FROM prenotazioni WHERE numBiglietto = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codice);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return mapResultSetToPrenotazione(rs);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null; // se non trovato
    }

    @Override
    public List<Prenotazione> findAllByUtente(String emailUtente) {
        List<Prenotazione> prenotazioni = new ArrayList<>();
        String sql = "SELECT * FROM prenotazioni WHERE emailUtente = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, emailUtente);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                prenotazioni.add(mapResultSetToPrenotazione(rs));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return prenotazioni;
    }

    @Override
    public boolean insert(Prenotazione p) {
        String sql = "INSERT INTO prenotazioni (numBiglietto, postoAssegnato, stato, emailUtente, idVolo) VALUES (?, ?, ?, ?, ?)";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNumBiglietto());
            ps.setString(2, p.getPostoAssegnato());
            ps.setString(3, p.getStato().name()); // enum → stringa
            ps.setString(4, p.getUtenteGenerico().getNomeUtente());
            ps.setString(5, p.getVolo().getCodiceUnivoco());
            return ps.executeUpdate() > 0;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Prenotazione p) {
        String sql = "UPDATE prenotazioni SET postoAssegnato=?, stato=?, emailUtente=?, idVolo=? WHERE numBiglietto=?";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getPostoAssegnato());
            ps.setString(2, p.getStato().name());
            ps.setString(3, p.getUtenteGenerico().getNomeUtente());
            ps.setString(4, p.getVolo().getCodiceUnivoco());
            ps.setString(5, p.getNumBiglietto());
            return ps.executeUpdate() > 0;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(String codicePrenotazione) {
        String sql = "DELETE FROM prenotazioni WHERE numBiglietto = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codicePrenotazione);
            return ps.executeUpdate() > 0;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Mapping da ResultSet al model Prenotazione
    private Prenotazione mapResultSetToPrenotazione(ResultSet rs) throws SQLException {
        Prenotazione p = new Prenotazione();
        p.setNumBiglietto(rs.getString("numBiglietto"));
        p.setPostoAssegnato(rs.getString("postoAssegnato"));
        p.setStato(StatoPrenotazione.valueOf(rs.getString("stato")));

        UtenteGenerico utente = new UtenteGenerico();
        utente.setNomeUtente(rs.getString("emailUtente"));
        p.setUtenteGenerico(utente);

        Volo volo = new Volo();
        volo.setCodiceUnivoco(rs.getString("idVolo"));
        p.setVolo(volo);

        return p;
    }
}