package model;

/**
 * L'enumerazione definisce i possibili stati
 * in cui può trovarsi una prenotazione effettuata da un passeggero.
 *
 * @see Prenotazione
 */
public enum StatoPrenotazione {
    CONFERMATA,
    INATTESA,
    CANCELLATA,
}