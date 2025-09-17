package implementazioneDAO.implementazionePostgresDAO;

import implementazioneDAO.VoloDAO;
import model.Volo;
import model.StatoVolo;
import database.ConnessioneDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VoloDAOPostgres implements VoloDAO {

    private final Connection conn;

    public VoloDAOPostgres() {
        try {
            this.conn = ConnessioneDatabase.getInstance().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Errore nella connessione al database", e);
        }
    }

    @Override
    public Volo findByCodiceUnivoco(String codiceUnivoco) {
        String sql = "SELECT codiceunivoco, compagniaaerea, datavolo, orarioprevisto, stato, aeroporto, gate, arrivopartenza " +
                "FROM public.voli WHERE codiceunivoco = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codiceUnivoco);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // non trovato
    }

    @Override
    public List<Volo> findAll() {
        String sql = "SELECT codiceunivoco, compagniaaerea, datavolo, orarioprevisto, stato, aeroporto, gate, arrivopartenza " +
                "FROM public.voli ORDER BY codiceunivoco";
        List<Volo> result = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean insert(Volo v) {
        String sql = "INSERT INTO public.voli " +
                "(codiceunivoco, compagniaaerea, datavolo, orarioprevisto, stato, aeroporto, gate, arrivopartenza) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (codiceunivoco) DO NOTHING";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            fill(ps, v);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Volo v) {
        String sql = "UPDATE public.voli SET compagniaaerea=?, datavolo=?, orarioprevisto=?, stato=?, aeroporto=?, gate=?, arrivopartenza=? " +
                "WHERE codiceunivoco=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, v.getCompagniaAerea());
            ps.setString(2, v.getDataVolo());
            ps.setString(3, v.getOrarioPrevisto());
            // Stato può essere null: se la colonna è NOT NULL in DB, evita di passare null dal chiamante
            if (v.getStato() != null) ps.setString(4, v.getStato().name());
            else ps.setNull(4, Types.VARCHAR);
            ps.setString(5, v.getAeroporto());
            ps.setString(6, v.getGate());
            ps.setString(7, v.getArrivoPartenza());
            ps.setString(8, v.getCodiceUnivoco());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
        if (v.getStato() != null) ps.setString(5, v.getStato().name());
        else ps.setNull(5, Types.VARCHAR);
        ps.setString(6, v.getAeroporto());
        ps.setString(7, v.getGate());
        ps.setString(8, v.getArrivoPartenza());
    }

    private static Volo mapRow(ResultSet rs) throws SQLException {
        String codice = rs.getString("codiceunivoco");
        String compagnia = rs.getString("compagniaaerea");
        String dataVolo = rs.getString("datavolo");
        String orario = rs.getString("orarioprevisto");
        String statoStr = rs.getString("stato");
        String aeroporto = rs.getString("aeroporto");
        String gate = rs.getString("gate");
        String arrivoPartenza = rs.getString("arrivopartenza");

        Volo v = new Volo(codice, compagnia, dataVolo, orario, parseStato(statoStr), null, null);
        v.setAeroporto(aeroporto);
        v.setGate(gate);
        v.setArrivoPartenza(arrivoPartenza);
        return v;
    }

    private static StatoVolo parseStato(String s) {
        if (s == null) return null;
        try {
            return StatoVolo.valueOf(s.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            // valore non riconosciuto in DB: restituisci null per non rompere la UI
            return null;
        }
    }
}