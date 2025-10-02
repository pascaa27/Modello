package model;

/**
 * Rappresenta un volo in partenza. Estende la classe {@link Volo}.
 * In questa classe, l'aeroporto di origine è sempre "Napoli".
 * Utilizza il pattern Builder per la costruzione dell'oggetto.
 */
public class VoloPartenza extends Volo {

    /**
     * Costruttore privato utilizzato dal {@link Builder} per creare un'istanza di VoloPartenza.
     *
     * @param builder istanza del Builder contenente i valori da inizializzare
     */
    private VoloPartenza(Builder builder) {
        super(builder);
    }

    /**
     * Restituisce l'aeroporto di origine del volo in partenza.
     * Per i voli in partenza, l'aeroporto è sempre "Napoli".
     *
     * @return "Napoli"
     */
    @Override
    public String getAeroporto() {
        // Partenze → origine sempre Napoli
        return "Napoli";
    }

    /**
     * Builder specifico per {@link VoloPartenza}.
     * Permette la costruzione fluente di un oggetto VoloPartenza.
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
         * Costruisce un'istanza di {@link VoloPartenza} utilizzando i valori impostati nel Builder.
         *
         * @return nuova istanza di {@link VoloPartenza}
         */
        @Override
        public VoloPartenza build() {
            return new VoloPartenza(this);
        }
    }
}