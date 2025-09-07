package model;

public class Bagaglio {
    private final String codUnivoco;
    private StatoBagaglio stato;
    private Prenotazione prenotazione;
    private Double pesoKg;

    public Bagaglio(String codUnivoco, double pesoKg, StatoBagaglio stato, Prenotazione prenotazione) {
        this.codUnivoco = codUnivoco;
        this.pesoKg = pesoKg;
        this.stato = stato;
        this.prenotazione = prenotazione;
    }

    public String getCodUnivoco() {
        return codUnivoco;
    }

    public StatoBagaglio getStato() {
        return stato;
    }

    public void setStato(StatoBagaglio stato) {
        this.stato = stato;
    }

    public Prenotazione getPrenotazione() {
        return prenotazione;
    }

    public void setPrenotazione(Prenotazione prenotazione) {
        this.prenotazione = prenotazione;
    }

    public Double getPesoKg() {
        return pesoKg;
    }

    public void setPesoKg(Double pesoKg) {
        this.pesoKg = pesoKg;
    }

    @Override
    public String toString() {
        return "Bagaglio{" + "codUnivoco='" + codUnivoco + '\'' + ", stato=" + stato +
                (prenotazione != null ? ", prenotazione=" + prenotazione.getNumBiglietto() : "") +
                '}';
    }
}