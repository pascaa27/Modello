package model;

public class Bagaglio {
    private String codUnivoco;
    private StatoBagaglio stato;

    public Bagaglio(String codUnivoco, StatoBagaglio stato) {
        this.codUnivoco = codUnivoco;
        this.stato = stato;
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
    public void setCodUnivoco(String codUnivoco) {
        this.codUnivoco = codUnivoco;
    }
}


