package dao.postgres;

import dao.DatiPasseggeroDAO;
import model.DatiPasseggero;
import database.ConnessioneDatabase;
import java.sql.*;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementazione Postgres del DAO per la gestione dei dati anagrafici dei passeggeri.
 * Fornisce operazioni CRUD e funzioni di autenticazione di base.
 * le email sono sempre normalizzate (trim + lowercase) lato database.
 */
public class DatiPasseggeroDAOPostgres implements DatiPasseggeroDAO {

    private static final Logger LOGGER = Logger.getLogger(DatiPasseggeroDAOPostgres.class.getName());
    private static final String LOG_SQL_DETAILS = "Dettagli SQL exception";

    private final Connection conn;

    /**
     * Costruttore che inizializza la connessione al database
     *
     * @throws DatabaseInitializationException se si verifica un errore durante l'ottenimento della connessione
     */
    public DatiPasseggeroDAOPostgres() {
        try {
            this.conn = ConnessioneDatabase.getInstance().getConnection();
            this.conn.setAutoCommit(true);
        } catch(SQLException e) {
            // Eccezione dedicata invece di RuntimeException generica
            throw new DatabaseInitializationException("Errore nella connessione al database", e);
        }
    }


    /**
     * Recupera un DatiPasseggero a partire dall'email, applicando la normalizzazione (trim e lowercase).
     *
     * @param email
     * @return DatiPasseggero se trovato; altrimenti null
     */
    @Override
    public DatiPasseggero findByEmail(String email) {
        final String sql = "SELECT nome, cognome, email, password " +
                "FROM public.datipasseggeri WHERE email = LOWER(BTRIM(?))";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    return map(rs);
                }
            }
        } catch(SQLException e) {
            LOGGER.log(Level.WARNING, "Errore findByEmail per email={0}", safeNormEmail(email));
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
        }
        return null;
    }


    /**
     * Inserisce o aggiorna (upsert) un record del passeggero sulla base dell'email.
     *
     * @param p l'oggetto DatiPasseggero da salvare
     * @return true se l'operazione ha interessato almeno una riga; false altrimenti o in caso di errore
     */
    @Override
    public boolean insert(DatiPasseggero p) {
        if(p == null || isBlank(p.getNome()) || isBlank(p.getCognome()) || isBlank(p.getEmail())) {
            LOGGER.warning("Insert datipasseggeri fallita: campi obbligatori mancanti (nome, cognome, email)");
            return false;
        }
        final String sql =
                "INSERT INTO public.datipasseggeri (nome, cognome, email, password) " +
                        "VALUES (?, ?, LOWER(BTRIM(?)), ?) " +
                        "ON CONFLICT (email) DO UPDATE SET nome = EXCLUDED.nome, cognome = EXCLUDED.cognome, password = EXCLUDED.password";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getCognome());
            ps.setString(3, p.getEmail());
            ps.setString(4, p.getPassword() == null ? "" : p.getPassword());
            boolean ok = ps.executeUpdate() > 0;

            logDbContext("insert");
            LOGGER.log(Level.INFO, "REG OK={0} email_norm=''{1}''", new Object[]{ok, safeNormEmail(p.getEmail())});

            return ok;
        } catch(SQLException e) {
            LOGGER.log(Level.WARNING, "Errore INSERT/UPSERT datipasseggeri per email={0}", safeNormEmail(p == null ? null : p.getEmail()));
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
            return false;
        }
    }

    /**
     * Aggiorna i dati anagrafici del passeggero (nome, cognome, password) individuato dall'email.
     *
     * @param p l'oggetto DatiPasseggero contenente i nuovi valori e l'email di riferimento
     * @return true se almeno una riga è stata aggiornata; false altrimenti o in caso di errore
     */
    @Override
    public boolean update(DatiPasseggero p) {
        if(p == null || isBlank(p.getEmail())) {
            LOGGER.warning("Update datipasseggeri fallita: oggetto nullo o email mancante");
            return false;
        }

        final String sql = "UPDATE public.datipasseggeri " +
                "SET nome = ?, cognome = ?, password = ? " +
                "WHERE email = LOWER(BTRIM(?))";

        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getCognome());
            ps.setString(3, p.getPassword() == null ? "" : p.getPassword());
            ps.setString(4, p.getEmail());

            int rows = ps.executeUpdate();
            LOGGER.log(Level.INFO, "UPDATE datipasseggeri: {0} righe aggiornate", rows);
            return rows > 0;
        } catch(SQLException e) {
            LOGGER.log(Level.WARNING, "Errore UPDATE datipasseggeri per email={0}", safeNormEmail(p.getEmail()));
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
            return false;
        }
    }

    /**
     * Elimina il passeggero identificato dall'email dal database.
     *
     * @param email
     * @return true se una riga è stata eliminata; false altrimenti o in caso di errore
     */
    @Override
    public boolean deleteByEmail(String email) {
        final String sql = "DELETE FROM public.datipasseggeri WHERE email = LOWER(BTRIM(?))";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            return ps.executeUpdate() > 0;
        } catch(SQLException e) {
            LOGGER.log(Level.WARNING, "Errore DELETE datipasseggeri per email={0}", safeNormEmail(email));
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
            return false;
        }
    }

    /**
     * Verifica le credenziali di accesso confrontando la password in chiaro con quella memorizzata.
     * Esegue diagnosi di base su email non trovata o password errata.
     *
     *
     * @param email email dell'utente
     * @param passwordChiara password in chiaro da verificare
     * @return true se l'email esiste e la password coincide; false altrimenti o in caso di errore
     */
    public boolean checkCredenziali(String email, String passwordChiara) {
        if(isBlank(email) || passwordChiara == null) return false;

        logDbContext("login");
        LOGGER.log(Level.FINE, "LOGIN tentativo email_norm=''{0}''", safeNormEmail(email));

        final String sql = "SELECT password FROM public.datipasseggeri WHERE email = LOWER(BTRIM(?)) LIMIT 1";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try(ResultSet rs = ps.executeQuery()) {
                if(!rs.next()) {
                    LOGGER.fine("LOGIN KO: email non trovata");
                    return false;
                }
                String pwdDb = rs.getString(1);
                boolean ok = Objects.equals(pwdDb, passwordChiara);
                LOGGER.log(Level.FINE, "LOGIN {0}: confrontate password (db=''{1}'', in=''{2}'')", new Object[]{ok ? "OK" : "KO", pwdDb, passwordChiara});
                return ok;
            }
        } catch(SQLException e) {
            LOGGER.log(Level.WARNING, "Errore verifica credenziali (datipasseggeri) per email={0}", safeNormEmail(email));
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
            return false;
        }
    }

    /**
     * Registra nei log informazioni contestuali sul database (utente corrente, database e schema).
     *
     * @param where contesto testuale da associare al log (es. "login", "insert")
     */
    private void logDbContext(String where) {
        final String sql = "SELECT current_user, current_database(), current_schema()";
        try(Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if(rs.next()) {
                LOGGER.log(Level.FINER, "[{0}] DBCTX user={1} db={2} schema={3}",
                        new Object[]{where, rs.getString(1), rs.getString(2), rs.getString(3)});
            }
        } catch(SQLException e) {
            // Non critico: contesto diagnostico, loggato a livello FINER
            LOGGER.log(Level.FINER, "DB context query failed in " + where, e);
        }
    }

    /**
     * Effettua il mapping di una riga del ResultSet in un oggetto DatiPasseggero.
     *
     * @param rs ResultSet posizionato sulla riga da convertire
     * @return un nuovo oggetto DatiPasseggero popolato con i dati letti
     * @throws SQLException se si verifica un errore durante la lettura dei campi
     */
    private DatiPasseggero map(ResultSet rs) throws SQLException {
        String nome = rs.getString("nome");
        String cognome = rs.getString("cognome");
        String email = rs.getString("email");
        String password = rs.getString("password");
        return new DatiPasseggero(nome, cognome, null, email, password);
    }

    /**
     * Verifica se una stringa è vuota.
     *
     * @param s la stringa da verificare
     * @return true se la stringa è nulla o vuota dopo trim; false altrimenti
     */
    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    /**
     * Restituisce l'email normalizzata (trim e lowercase).
     *
     * @param email email di input, eventualmente nulla
     * @return email normalizzata oppure null se l'input è null
     */
    private static String safeNormEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    /**
     * Eccezione runtime che segnala un problema durante l'inizializzazione della connessione al database.
     */
    public class DatabaseInitializationException extends RuntimeException {

        /**
         * Crea una nuova DatabaseInitializationException con messaggio e causa.
         *
         * @param message messaggio descrittivo dell'errore
         * @param cause causa originale dell'errore
         */
        public DatabaseInitializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}