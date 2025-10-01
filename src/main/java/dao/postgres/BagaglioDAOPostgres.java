package dao.postgres;

import dao.BagaglioDAO;
import model.Bagaglio;
import model.StatoBagaglio;
import model.Prenotazione;
import database.ConnessioneDatabase;
import controller.Controller;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementazione Postgres del DAO per i Bagagli.
 * - Esegue le operazioni CRUD sulla tabella public.bagagli.
 * - Può collegarsi al Controller per risolvere la prenotazione associata (se presente).
 */
public class BagaglioDAOPostgres implements BagaglioDAO {

    private static final Logger LOGGER = Logger.getLogger(BagaglioDAOPostgres.class.getName());
    private static final String SELECT_COLUMNS = "codUnivoco, pesoKg, stato, numBiglietto";
    private static final String LOG_ROWS = " rows=";
    private static final String SQL_SELECT = "SELECT ";
    private static final String LOG_SQL_DETAILS = "Dettagli SQL exception";

    private final Connection conn;
    private Controller controller;

    /**
     * Costruttore: apre la connessione al database.
     * @throws DatabaseInitializationException se la connessione non può essere aperta
     */
    public BagaglioDAOPostgres() {
        try {
            this.conn = ConnessioneDatabase.getInstance().getConnection();
        } catch(SQLException e) {
            throw new DatabaseInitializationException("Errore nella connessione al database", e);
        }
    }

    /**
     * Inietta il Controller per consentire la risoluzione della Prenotazione associata ai bagagli.
     * @param controller istanza di Controller dell'applicazione
     */
    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Trova tutti i bagagli associati a una prenotazione (per numero biglietto).
     * @param numBiglietto
     * @return lista di bagagli collegati (vuota se nessuno)
     */
    @Override
    public List<Bagaglio> findByPrenotazione(String numBiglietto) {
        List<Bagaglio> bagagli = new ArrayList<>();
        final String sql = SQL_SELECT + SELECT_COLUMNS + " FROM public.bagagli WHERE numBiglietto = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, numBiglietto);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bagagli.add(mapResultSetToBagaglio(rs));
                }
            }
        } catch(SQLException e) {
            LOGGER.log(Level.WARNING, "Errore in findByPrenotazione per numBiglietto={0}", numBiglietto);
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
        }
        return bagagli;
    }

    /**
     * Trova un bagaglio per codice univoco.
     * @param codice
     * @return bagaglio trovato oppure null se assente
     */
    public Bagaglio findById(String codice) {
        final String sql = SQL_SELECT + SELECT_COLUMNS + " FROM public.bagagli WHERE codUnivoco = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codice);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    String cod = rs.getString("codUnivoco");
                    double peso = rs.getDouble("pesoKg");
                    StatoBagaglio stato = StatoBagaglio.valueOf(rs.getString("stato"));
                    String numBiglietto = rs.getString("numBiglietto");
                    Prenotazione pren = null;
                    if(numBiglietto != null && controller != null) {
                        pren = controller.cercaPrenotazione(numBiglietto);
                    }
                    return new Bagaglio(cod, peso, stato, pren);
                }
            }
        } catch(SQLException e) {
            LOGGER.log(Level.WARNING, "Errore in findById per codUnivoco={0}", codice);
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
        }
        return null;
    }

    /**
     * Restituisce l'elenco completo dei bagagli.
     * @return lista di tutti i bagagli (può essere vuota)
     */
    @Override
    public List<Bagaglio> findAll() {
        List<Bagaglio> bagagli = new ArrayList<>();
        final String sql = SQL_SELECT + SELECT_COLUMNS + " FROM public.bagagli ORDER BY codUnivoco";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                bagagli.add(mapResultSetToBagaglio(rs));
            }
        } catch(SQLException e) {
            LOGGER.log(Level.WARNING, "Errore in findAll()", e);
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
        }
        return bagagli;
    }

    /**
     * Inserisce un nuovo bagaglio.
     * @param bagaglio
     * @return true se l'inserimento ha avuto successo
     */
    @Override
    public boolean insert(Bagaglio bagaglio) {
        final String sql = "INSERT INTO public.bagagli (codUnivoco, pesoKg, stato, numBiglietto) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bagaglio.getCodUnivoco());
            ps.setDouble(2, bagaglio.getPesoKg());
            ps.setString(3, bagaglio.getStato().name());
            if(bagaglio.getPrenotazione() != null) {
                ps.setString(4, bagaglio.getPrenotazione().getNumBiglietto());
            } else {
                ps.setNull(4, Types.VARCHAR);
            }
            int n = ps.executeUpdate();
            LOGGER.log(Level.INFO, "[BagagliDAO] insert {0}{1}{2}", new Object[]{bagaglio.getCodUnivoco(), LOG_ROWS, n});
            return n > 0;
        } catch(SQLException e) {
            LOGGER.log(Level.WARNING, "[BagagliDAO] insert FAILED for {0}", bagaglio.getCodUnivoco());
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
        }
        return false;
    }

    /**
     * Aggiorna un bagaglio esistente.
     * @param bagaglio
     * @return true se l'aggiornamento ha avuto successo
     */
    @Override
    public boolean update(Bagaglio bagaglio) {
        final String sql = "UPDATE public.bagagli SET pesoKg = ?, stato = ?, numBiglietto = ? WHERE codUnivoco = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, bagaglio.getPesoKg());
            ps.setString(2, bagaglio.getStato().name());
            if(bagaglio.getPrenotazione() != null) {
                ps.setString(3, bagaglio.getPrenotazione().getNumBiglietto());
            } else {
                ps.setNull(3, Types.VARCHAR);
            }
            ps.setString(4, bagaglio.getCodUnivoco());
            int n = ps.executeUpdate();
            LOGGER.log(Level.INFO, "[BagagliDAO] update {0}{1}{2}", new Object[]{bagaglio.getCodUnivoco(), LOG_ROWS, n});
            return n > 0;
        } catch(SQLException e) {
            LOGGER.log(Level.WARNING, "[BagagliDAO] update FAILED for {0}", bagaglio.getCodUnivoco());
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
        }
        return false;
    }

    /**
     * Elimina un bagaglio dal database.
     * @param codUnivoco
     * @return true se l'eliminazione ha avuto successo
     */
    @Override
    public boolean delete(String codUnivoco) {
        final String sql = "DELETE FROM public.bagagli WHERE codUnivoco = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codUnivoco);
            int n = ps.executeUpdate();
            LOGGER.log(Level.INFO, "[BagagliDAO] delete {0}{1}{2}", new Object[]{codUnivoco, LOG_ROWS, n});
            return n > 0;
        } catch(SQLException e) {
            LOGGER.log(Level.WARNING, "[BagagliDAO] delete FAILED for {0}", codUnivoco);
            LOGGER.log(Level.FINE, LOG_SQL_DETAILS, e);
        }
        return false;
    }

    /**
     * Mappa una riga del ResultSet in un oggetto Bagaglio.
     * Se disponibile, risolve anche la prenotazione associata tramite Controller.
     * @param rs ResultSet posizionato sulla riga corrente
     * @return istanza di Bagaglio costruita dai dati del ResultSet
     * @throws SQLException in caso di errori di lettura dal ResultSet
     */
    // Mapping senza effetti collaterali: restituisce SEMPRE un Bagaglio, anche con prenotazione nulla
    private Bagaglio mapResultSetToBagaglio(ResultSet rs) throws SQLException {
        String codUnivoco = rs.getString("codUnivoco");
        double pesoKg = rs.getDouble("pesoKg");
        StatoBagaglio stato = StatoBagaglio.valueOf(rs.getString("stato"));
        String numBiglietto = rs.getString("numBiglietto"); // può essere NULL

        Prenotazione pren = null;
        if(controller != null && numBiglietto != null) {
            pren = controller.cercaPrenotazione(numBiglietto); // ok se resta null
        }

        return new Bagaglio(codUnivoco, pesoKg, stato, pren);
    }

    /**
     * Eccezione di runtime dedicata ai problemi di inizializzazione della connessione al database.
     */
    public class DatabaseInitializationException extends RuntimeException {
        /**
         * Crea una nuova DatabaseInitializationException.
         * @param message
         * @param cause causa originale dell'errore
         */
        public DatabaseInitializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}