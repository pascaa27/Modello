package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe singleton per la gestione della connessione al database PostgreSQL.
 * <p>
 * Questa classe assicura che venga creata una sola istanza di connessione al database
 * durante l'intero ciclo di vita dell'applicazione. Utilizza il driver JDBC per connettersi
 * a un database PostgreSQL locale con le credenziali predefinite.
 * </p>
 */
public class ConnessioneDatabase {
    private static ConnessioneDatabase instance;
    private Connection connection;
    private static final String URL = "jdbc:postgresql://localhost:5432/aeroporto";
    private static final String USER = "studente";
    private static final String PASSWORD = "password";

    /**
     * Costruttore privato per la creazione della connessione al database.
     * <p>
     * Viene lanciata un'eccezione {@link SQLException} se la connessione fallisce.
     * </p>
     *
     * @throws SQLException se la connessione al database non può essere stabilita
     */
    private ConnessioneDatabase() throws SQLException {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch(SQLException e) {
            throw new SQLException("Errore nella connessione al database", e);
        }
    }

    /**
     * Restituisce l'istanza singleton di {@link ConnessioneDatabase}.
     * <p>
     * Se l'istanza non esiste ancora oppure la connessione è chiusa, viene creata una nuova istanza.
     * </p>
     *
     * @return istanza singleton di {@link ConnessioneDatabase}
     * @throws SQLException se la connessione al database non può essere stabilita
     */
    public static ConnessioneDatabase getInstance() throws SQLException {
        if(instance == null || instance.getConnection().isClosed()) {
            instance = new ConnessioneDatabase();
        }
        return instance;
    }

    /**
     * Restituisce l'oggetto {@link Connection} associato al database.
     *
     * @return connessione JDBC al database
     */
    public Connection getConnection() {
        return connection;
    }
}