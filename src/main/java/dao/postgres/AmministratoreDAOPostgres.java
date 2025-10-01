package dao.postgres;

import dao.AmministratoreDAO;
import model.Amministratore;
import database.ConnessioneDatabase;
import controller.Controller;

import java.sql.*;

/**
 * Implementazione Postgres dell'AmministratoreDAO.
 * - Gestisce l'accesso al database per gli amministratori.
  */
public class AmministratoreDAOPostgres implements AmministratoreDAO {

    private Connection conn;
    private Controller controller;

    /**
     * Costruttore: apre la connessione al database.
     * In caso di errore di inizializzazione lancia una DatabaseInitializationException.
     */
    public AmministratoreDAOPostgres() {
        try {
            this.conn = ConnessioneDatabase.getInstance().getConnection();
        } catch(SQLException e) {
            // Eccezione dedicata invece di una generica RuntimeException
            throw new DatabaseInitializationException("Errore nella connessione al database", e);
        }
    }

    /**
     * Inietta il Controller, utile per creare/registrare l'oggetto Amministratore nella cache applicativa.
     * @param controller controller dell'applicazione
     */
    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Trova un amministratore per email.
     * Esegue una SELECT mirata (senza SELECT *) filtrando per ruolo = 'amministratore'.
     *
     * @param email email dell'amministratore da cercare
     * @return l'Amministratore trovato oppure null se assente
     */
    @Override
    public Amministratore findByEmail(String email) {
        // Seleziona solo le colonne necessarie (niente SELECT *)
        final String sql = "SELECT email, password, nome, cognome " +
                "FROM registrazioneutente " +
                "WHERE email = ? AND ruolo = 'amministratore'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    return mapResultSetToAmministratore(rs);
                }
            }
        } catch(SQLException e) {
            // Mantengo il comportamento attuale (puoi sostituire con logging se preferisci)
            e.printStackTrace();
        }
        return null; // se non trovato
    }

    /**
     * Mappa la riga del ResultSet in un oggetto Amministratore.
     * Se il Controller è stato iniettato, crea l'istanza passando dal Controller (per aggiornare la cache).
     *
     * @param rs ResultSet posizionato sulla riga da mappare
     * @return istanza di Amministratore creata dai valori del ResultSet
     * @throws SQLException in caso di errori di lettura dal ResultSet
     */
    private Amministratore mapResultSetToAmministratore(ResultSet rs) throws SQLException {
        String email = rs.getString("email");
        String password = rs.getString("password");
        String nome = rs.getString("nome");
        String cognome = rs.getString("cognome");

        return controller.creaAmministratore(email, password, nome, cognome);
    }

    /**
     * Eccezione di runtime dedicata ai problemi di inizializzazione del database.
     */
    public class DatabaseInitializationException extends RuntimeException {
        /**
         * Crea una nuova eccezione di inizializzazione del database.
         * @param message messaggio descrittivo
         * @param cause causa originale dell'errore
         */
        public DatabaseInitializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}