package dao.postgres;

import controller.Controller; // mantenuto se referenziato altrove in compilazione, ma non usato qui
import dao.DatiPasseggeroDAO;
import model.DatiPasseggero;
import database.ConnessioneDatabase;

import java.sql.*;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatiPasseggeroDAOPostgres implements DatiPasseggeroDAO {

    private static final Logger LOGGER = Logger.getLogger(DatiPasseggeroDAOPostgres.class.getName());
    private static final String LOG_SQL_DETAILS = "Dettagli SQL exception";

    private final Connection conn;

    public DatiPasseggeroDAOPostgres() {
        try {
            this.conn = ConnessioneDatabase.getInstance().getConnection();
            this.conn.setAutoCommit(true);
        } catch (SQLException e) {
            // Eccezione dedicata invece di RuntimeException generica
            throw new DatabaseInitializationException("Errore nella connessione al database", e);
        }
    }

    /**
     * Metodo legacy non più utilizzato.
     * @deprecated usare findByEmail(String email)
     */
    @Deprecated(since = "1.0", forRemoval = true)
    @Override
    public DatiPasseggero findByCodiceFiscale(String codiceFiscale) {
        return null;
    }

    @Override
    public DatiPasseggero findByEmail(String email) {
        final String sql = "SELECT nome, cognome, email, password " +
                "FROM public.datipasseggeri WHERE email = LOWER(BTRIM(?))";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Errore findByEmail per email={0}", safeNormEmail(email));
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
        }
        return null;
    }

    // REGISTRAZIONE: upsert su email normalizzata
    @Override
    public boolean insert(DatiPasseggero p) {
        if (p == null || isBlank(p.getNome()) || isBlank(p.getCognome()) || isBlank(p.getEmail())) {
            LOGGER.warning("Insert datipasseggeri fallita: campi obbligatori mancanti (nome, cognome, email)");
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

            logDbContext("insert");
            LOGGER.log(Level.INFO, "REG OK={0} email_norm=''{1}''", new Object[]{ok, safeNormEmail(p.getEmail())});

            return ok;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Errore INSERT/UPSERT datipasseggeri per email={0}", safeNormEmail(p == null ? null : p.getEmail()));
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
            return false;
        }
    }

    @Override
    public boolean update(DatiPasseggero p) {
        if (p == null || isBlank(p.getEmail())) {
            LOGGER.warning("Update datipasseggeri fallita: oggetto nullo o email mancante");
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
            LOGGER.log(Level.INFO, "UPDATE datipasseggeri: {0} righe aggiornate", rows);
            return rows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Errore UPDATE datipasseggeri per email={0}", safeNormEmail(p.getEmail()));
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
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
            LOGGER.log(Level.WARNING, "Errore DELETE datipasseggeri per email={0}", safeNormEmail(email));
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
            return false;
        }
    }

    // LOGIN con diagnostica: capiamo se non trova l'email o se sbaglia la password
    public boolean checkCredenziali(String email, String passwordChiara) {
        if (isBlank(email) || passwordChiara == null) return false;

        logDbContext("login");
        LOGGER.log(Level.FINE, "LOGIN tentativo email_norm=''{0}''", safeNormEmail(email));

        final String sql = "SELECT password FROM public.datipasseggeri WHERE email = LOWER(BTRIM(?)) LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    LOGGER.fine("LOGIN KO: email non trovata");
                    return false;
                }
                String pwdDb = rs.getString(1);
                boolean ok = Objects.equals(pwdDb, passwordChiara);
                LOGGER.log(Level.FINE, "LOGIN {0}: confrontate password (db=''{1}'', in=''{2}'')", new Object[]{ok ? "OK" : "KO", pwdDb, passwordChiara});
                return ok;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Errore verifica credenziali (datipasseggeri) per email={0}", safeNormEmail(email));
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
            return false;
        }
    }

    private void logDbContext(String where) {
        final String sql = "SELECT current_user, current_database(), current_schema()";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                LOGGER.log(Level.FINER, "[{0}] DBCTX user={1} db={2} schema={3}",
                        new Object[]{where, rs.getString(1), rs.getString(2), rs.getString(3)});
            }
        } catch (SQLException e) {
            // Non critico: contesto diagnostico, loggato a livello FINER
            LOGGER.log(Level.FINER, "DB context query failed in " + where, e);
        }
    }

    private DatiPasseggero map(ResultSet rs) throws SQLException {
        String nome = rs.getString("nome");
        String cognome = rs.getString("cognome");
        String email = rs.getString("email");
        String password = rs.getString("password");
        return new DatiPasseggero(nome, cognome, null, email, password);
    }

    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    private static String safeNormEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    public class DatabaseInitializationException extends RuntimeException {
        public DatabaseInitializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}