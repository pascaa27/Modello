package model;

public class VoloArrivo extends Volo {

    private VoloArrivo(Builder builder) {
        super(builder);
    }

    @Override
    public String getAeroporto() {
        // Arrivi → destinazione sempre Napoli
        return "Napoli";
    }

    public static class Builder extends Volo.Builder {
        public Builder(String codiceUnivoco) {
            super(codiceUnivoco);
        }

        @Override
        public VoloArrivo build() {
            return new VoloArrivo(this);
        }
    }
}