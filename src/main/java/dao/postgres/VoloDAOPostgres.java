package dao.postgres;

import dao.VoloDAO;
import model.Volo;
import model.StatoVolo;
import database.ConnessioneDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VoloDAOPostgres implements VoloDAO {

    private static final Logger LOGGER = Logger.getLogger(VoloDAOPostgres.class.getName());
    private static final String LOG_SQL_DETAILS = "Dettagli SQL exception";

    private final Connection conn;

    public VoloDAOPostgres() {
        try {
            this.conn = ConnessioneDatabase.getInstance().getConnection();
        } catch (SQLException e) {
            // Eccezione dedicata al posto di RuntimeException generica
            throw new DatabaseInitializationException("Errore nella connessione al database", e);
        }
    }

    @Override
    public Volo findByCodiceUnivoco(String codiceUnivoco) {
        String sql = "SELECT codiceunivoco, compagniaaerea, datavolo, orarioprevisto, orariostimato, stato, aeroporto, gate, arrivopartenza " +
                "FROM public.voli WHERE codiceunivoco = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codiceUnivoco);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Errore findByCodiceUnivoco per codice={0}", codiceUnivoco);
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
        }
        return null; // non trovato
    }

    @Override
    public List<Volo> findAll() {
        String sql = "SELECT codiceunivoco, compagniaaerea, datavolo, orarioprevisto, orariostimato, stato, aeroporto, gate, arrivopartenza " +
                "FROM public.voli ORDER BY codiceunivoco";
        List<Volo> result = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Errore findAll()", e);
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
        }
        return result;
    }

    @Override
    public boolean insert(Volo v) {
        String sql = "INSERT INTO public.voli " +
                "(codiceunivoco, compagniaaerea, datavolo, orarioprevisto, orariostimato, stato, aeroporto, gate, arrivopartenza) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (codiceunivoco) DO NOTHING";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            fill(ps, v);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Errore insert volo codice={0}", v.getCodiceUnivoco());
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
        }
        return false;
    }

    @Override
    public boolean update(Volo v) {
        String sql = "UPDATE public.voli SET compagniaaerea=?, datavolo=?, orarioprevisto=?, orariostimato=?, stato=?, aeroporto=?, gate=?, arrivopartenza=? " +
                "WHERE codiceunivoco=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, v.getCompagniaAerea());
            ps.setString(2, v.getDataVolo());
            ps.setString(3, v.getOrarioPrevisto());
            ps.setString(4, v.getOrarioStimato());
            if (v.getStato() != null) ps.setString(5, v.getStato().name());
            else ps.setNull(5, Types.VARCHAR);
            ps.setString(6, v.getAeroporto());
            ps.setString(7, v.getGate());
            ps.setString(8, v.getArrivoPartenza());
            ps.setString(9, v.getCodiceUnivoco());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Errore update volo codice={0}", v.getCodiceUnivoco());
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
        }
        return false;
    }

    @Override
    public boolean delete(String codiceUnivoco) {
        String sql = "DELETE FROM public.voli WHERE codiceunivoco = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codiceUnivoco);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Errore delete volo codice={0}", codiceUnivoco);
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
        }
        return false;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM public.voli";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Errore count()", e);
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
        }
        return -1;
    }

    // ------------------------
    // Helpers
    // ------------------------
    private static void fill(PreparedStatement ps, Volo v) throws SQLException {
        ps.setString(1, v.getCodiceUnivoco());
        ps.setString(2, v.getCompagniaAerea());
        ps.setString(3, v.getDataVolo());
        ps.setString(4, v.getOrarioPrevisto());
        ps.setString(5, v.getOrarioStimato());
        if (v.getStato() != null) ps.setString(6, v.getStato().name());
        else ps.setNull(6, Types.VARCHAR);
        ps.setString(7, v.getAeroporto());
        ps.setString(8, v.getGate());
        ps.setString(9, v.getArrivoPartenza());
    }

    private static Volo mapRow(ResultSet rs) throws SQLException {
        Volo v = new Volo();  // costruttore vuoto
        v.setCodiceUnivoco(rs.getString("codiceunivoco"));
        v.setCompagniaAerea(rs.getString("compagniaaerea"));
        v.setDataVolo(rs.getString("datavolo"));
        v.setOrarioPrevisto(rs.getString("orarioprevisto"));
        v.setOrarioStimato(rs.getString("orariostimato"));
        v.setStato(parseStato(rs.getString("stato")));
        v.setAeroporto(rs.getString("aeroporto"));
        v.setGate(rs.getString("gate"));
        v.setArrivoPartenza(rs.getString("arrivopartenza"));
        return v;
    }

    private static StatoVolo parseStato(String s) {
        if (s == null) return null;
        try {
            return StatoVolo.valueOf(s.trim().toUpperCase());
        } catch (IllegalArgumentException _) { // richiede Java 21+: unnamed pattern
            // valore non riconosciuto in DB: restituisci null per non rompere la UI
            return null;
        }
    }

    public class DatabaseInitializationException extends RuntimeException {
        public DatabaseInitializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}