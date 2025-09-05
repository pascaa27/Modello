package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnessioneDatabase {
    private static ConnessioneDatabase instance;
    private Connection connection;
    private static final String URL = "jdbc:postgresql://localhost:5432/aeroporto";
    private static final String USER = "studente";
    private static final String PASSWORD = "password";

    private ConnessioneDatabase() throws SQLException {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch(SQLException e) {
            throw new SQLException("Errore nella connessione al database", e);
        }
    }

    public static ConnessioneDatabase getIstance() throws SQLException {
        if(instance == null || instance.getConnection().isClosed()) {
            instance = new ConnessioneDatabase();
        }
        return instance;
    }
    public Connection getConnection() {
        return connection;
    }
}