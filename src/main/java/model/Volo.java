package model;

public class Volo {

    private String codiceUnivoco;
    private String compagniaAerea;
    private String dataVolo;
    private String orarioPrevisto;

    public Volo(String codiceUnivoco, String compagniaAerea, String dataVolo, String orarioPrevisto) {
        this.codiceUnivoco = codiceUnivoco;
        this.compagniaAerea = compagniaAerea;
        this.dataVolo = dataVolo;
        this.orarioPrevisto = orarioPrevisto;
    }

    public String getCodiceUnivoco() {
        return codiceUnivoco;
    }

    public String getCompagniaAerea() {
        return compagniaAerea;
    }

    public String getDataVolo() {
        return dataVolo;
    }

    public String getOrarioPrevisto() {
        return orarioPrevisto;
    }

    public void setCodiceUnivoco(String codiceUnivoco) {
        this.codiceUnivoco = codiceUnivoco;
    }

    public void setCompagniaAerea(String compagniaAerea) {
        this.compagniaAerea = compagniaAerea;
    }

    public void setDataVolo(String dataVolo) {
        this.dataVolo = dataVolo;
    }

    public void setOrarioPrevisto(String orarioPrevisto) {
        this.orarioPrevisto = orarioPrevisto;
    }


}
