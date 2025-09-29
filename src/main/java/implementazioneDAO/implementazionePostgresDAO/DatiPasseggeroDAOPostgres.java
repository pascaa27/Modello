package implementazioneDAO.implementazionePostgresDAO;

import controller.Controller;
import implementazioneDAO.DatiPasseggeroDAO;
import model.DatiPasseggero;
import database.ConnessioneDatabase;

import java.sql.*;

public class DatiPasseggeroDAOPostgres implements DatiPasseggeroDAO {

    private final Connection conn;
    private Controller controller; // opzionale

    public DatiPasseggeroDAOPostgres() {
        try {
            this.conn = ConnessioneDatabase.getInstance().getConnection();
            this.conn.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException("Errore nella connessione al database", e);
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @Override @Deprecated
    public DatiPasseggero findByCodiceFiscale(String codiceFiscale) { return null; }

    @Override
    public DatiPasseggero findByEmail(String email) {
        final String sql = "SELECT nome, cognome, email, password FROM public.datipasseggeri WHERE email = LOWER(BTRIM(?))";
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

    // REGISTRAZIONE: upsert su email normalizzata
    @Override
    public boolean insert(DatiPasseggero p) {
        if (p == null || isBlank(p.getNome()) || isBlank(p.getCognome()) || isBlank(p.getEmail())) {
            System.err.println("Insert datipasseggeri fallita: campi obbligatori mancanti (nome, cognome, email)");
            return false;
        }
        final String sql =
                "INSERT INTO public.datipasseggeri (nome, cognome, email, password) " +
                        "VALUES (?, ?, LOWER(BTRIM(?)), ?) " +
                        "ON CONFLICT (email) DO UPDATE SET nome = EXCLUDED.nome, cognome = EXCLUDED.cognome, password = EXCLUDED.password";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getCognome());
            ps.setString(3, p.getEmail());
            ps.setString(4, p.getPassword() == null ? "" : p.getPassword());
            boolean ok = ps.executeUpdate() > 0;

            // DEBUG
            logDbContext("insert");
            System.out.printf("REG OK=%s email_norm='%s'%n", ok, p.getEmail() == null ? null : p.getEmail().trim().toLowerCase());

            return ok;
        } catch (SQLException e) {
            System.err.println("Errore INSERT/UPSERT datipasseggeri: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(DatiPasseggero p) {
        if (p == null || isBlank(p.getEmail())) {
            System.err.println("Update datipasseggeri fallita: oggetto nullo o email mancante");
            return false;
        }

        final String sql = "UPDATE public.datipasseggeri " +
                "SET nome = ?, cognome = ?, password = ? " +
                "WHERE email = LOWER(BTRIM(?))";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getCognome());
            ps.setString(3, p.getPassword() == null ? "" : p.getPassword());
            ps.setString(4, p.getEmail());

            int rows = ps.executeUpdate();
            System.out.println("UPDATE datipasseggeri: " + rows + " righe aggiornate");
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Errore UPDATE datipasseggeri: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteByEmail(String email) {
        final String sql = "DELETE FROM public.datipasseggeri WHERE email = LOWER(BTRIM(?))";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore DELETE datipasseggeri: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // LOGIN con diagnostica: capiamo se non trova l'email o se sbaglia la password
    public boolean checkCredenziali(String email, String passwordChiara) {
        if (isBlank(email) || passwordChiara == null) return false;

        logDbContext("login");
        System.out.printf("LOGIN tentativo email_norm='%s'%n", email.trim().toLowerCase());

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT password FROM public.datipasseggeri WHERE email = LOWER(BTRIM(?)) LIMIT 1")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("LOGIN KO: email non trovata");
                    return false;
                }
                String pwdDb = rs.getString(1);
                boolean ok = (pwdDb != null && pwdDb.equals(passwordChiara));
                System.out.printf("LOGIN %s: confrontate password (db='%s', in='%s')%n",
                        ok ? "OK" : "KO", pwdDb, passwordChiara);
                return ok;
            }
        } catch (SQLException e) {
            System.err.println("Errore verifica credenziali (datipasseggeri): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void logDbContext(String where) {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT current_user, current_database(), current_schema()")) {
            if (rs.next()) {
                System.out.printf("[%s] DBCTX user=%s db=%s schema=%s%n",
                        where, rs.getString(1), rs.getString(2), rs.getString(3));
            }
        } catch (SQLException ignored) {}
    }

    private DatiPasseggero map(ResultSet rs) throws SQLException {
        String nome = rs.getString("nome");
        String cognome = rs.getString("cognome");
        String email = rs.getString("email");
        String password = rs.getString("password");
        return new DatiPasseggero(nome, cognome, null, email, password);
    }

    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
}