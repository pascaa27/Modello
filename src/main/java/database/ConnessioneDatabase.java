package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Factory per connessioni PostgreSQL.
 *
 * Scelte progettuali per conformità a SonarQube:
 * - Nessuna credenziale hardcodata: URL/utente/credenziale arrivano da variabili d'ambiente o proprietà JVM.
 * - Singleton thread-safe con Initialization-on-demand holder (stateless).
 * - Ogni getConnection() restituisce una nuova Connection: non manteniamo una singola connessione condivisa.
 */
public final class ConnessioneDatabase {

    // Nomi delle variabili d'ambiente/proprietà supportate (con fallback per la credenziale)
    private static final String KEY_URL  = "DB_URL";
    private static final String KEY_USER = "DB_USER";
    private static final String[] KEY_PWD_CANDIDATES = { "DB_PWD", "DB_PASSWORD" };

    private final String url;
    private final String user;
    private final String pwd;

    private ConnessioneDatabase() {
        this.url  = requireConfig(KEY_URL);
        this.user = requireConfig(KEY_USER);
        this.pwd  = requireFirstPresent(KEY_PWD_CANDIDATES);
    }

    // Holder idiom per inizializzazione lazy e thread-safe
    private static final class Holder {
        static final ConnessioneDatabase INSTANCE = new ConnessioneDatabase();
    }

    /**
     * Restituisce l'istanza factory.
     * Non crea connessioni qui: usa getConnection() per ottenerle.
     */
    public static ConnessioneDatabase getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Crea e restituisce una nuova Connection.
     * Il chiamante è responsabile della chiusura della connessione.
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, pwd);
    }

    // ========== Helpers ==========
    private static String requireConfig(String key) {
        String v = getEnvOrProp(key);
        if (isBlank(v)) {
            throw new IllegalStateException("Configurazione database mancante: " + key);
        }
        return v;
    }

    private static String requireFirstPresent(String[] keys) {
        for (String k : keys) {
            String v = getEnvOrProp(k);
            if (!isBlank(v)) return v;
        }
        throw new IllegalStateException("Configurazione database mancante: nessuna credenziale impostata. Attesi uno tra: DB_PWD o DB_PASSWORD");
    }

    private static String getEnvOrProp(String key) {
        String v = System.getenv(key);
        if (isBlank(v)) {
            v = System.getProperty(key);
        }
        return v;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}