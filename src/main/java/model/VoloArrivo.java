package model;

/**
 * Rappresenta un volo in arrivo. Estende la classe {@link Volo}.
 * In questa classe, l'aeroporto di destinazione è sempre "Napoli".
 * Utilizza il pattern Builder per la costruzione dell'oggetto.
 */
public class VoloArrivo extends Volo {

    /**
     * Costruttore privato utilizzato dal {@link Builder} per creare un'istanza di VoloArrivo.
     *
     * @param builder istanza del Builder contenente i valori da inizializzare
     */
    private VoloArrivo(Builder builder) {
        super(builder);
    }

    /**
     * Restituisce l'aeroporto di destinazione del volo in arrivo.
     * Per i voli in arrivo, l'aeroporto è sempre "Napoli".
     *
     * @return "Napoli"
     */
    @Override
    public String getAeroporto() {
        // Arrivi → destinazione sempre Napoli
        return "Napoli";
    }

    /**
     * Builder specifico per {@link VoloArrivo}.
     * Permette la costruzione fluente di un oggetto VoloArrivo.
     */
    public static class Builder extends Volo.Builder {

        /**
         * Costruttore del Builder.
         *
         * @param codiceUnivoco codice univoco del volo (obbligatorio)
         */
        public Builder(String codiceUnivoco) {
            super(codiceUnivoco);
        }

        /**
         * Costruisce un'istanza di {@link VoloArrivo} utilizzando i valori impostati nel Builder.
         *
         * @return nuova istanza di {@link VoloArrivo}
         */
        @Override
        public VoloArrivo build() {
            return new VoloArrivo(this);
        }
    }
}