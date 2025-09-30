package model;

public class VoloPartenza extends Volo {

    private VoloPartenza(Builder builder) {
        super(builder);
    }

    @Override
    public String getAeroporto() {
        // Partenze → origine sempre Napoli
        return "Napoli";
    }

    public static class Builder extends Volo.Builder {
        public Builder(String codiceUnivoco) {
            super(codiceUnivoco);
        }

        @Override
        public VoloPartenza build() {
            return new VoloPartenza(this);
        }
    }
}