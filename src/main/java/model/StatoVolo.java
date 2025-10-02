package model;

/**
 * L'enumerazione rappresenta i diversi stati
 * operativi in cui un volo può trovarsi lungo il suo ciclo di vita.
 *
 * @see Volo
 */
public enum StatoVolo {
    PROGRAMMATO,
    IMBARCO,
    DECOLLATO,
    INRITARDO,
    ATTERRATO,
    CANCELLATO
}