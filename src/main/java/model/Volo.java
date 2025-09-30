package model;

import java.util.ArrayList;
import java.util.List;

public class Volo {

    private String codiceUnivoco;
    private String compagniaAerea;
    private String dataVolo;
    private String orarioPrevisto;
    private String orarioStimato;
    private StatoVolo stato;
    private Amministratore amministratore;
    private TabellaOrario tabellaOrario;
    private List<Prenotazione> prenotazioni = new ArrayList<>();
    private String aeroporto;
    private String gate;
    private String arrivoPartenza;

    protected Volo(Builder builder) {
        this.codiceUnivoco = builder.codiceUnivoco;
        this.compagniaAerea = builder.compagniaAerea;
        this.dataVolo = builder.dataVolo;
        this.orarioPrevisto = builder.orarioPrevisto;
        this.orarioStimato = builder.orarioStimato;
        this.stato = builder.stato;
        this.amministratore = builder.amministratore;
        this.tabellaOrario = builder.tabellaOrario;
        this.aeroporto = builder.aeroporto;
        this.gate = builder.gate;
        this.arrivoPartenza = builder.arrivoPartenza;
    }

    public Volo(String codiceUnivoco) {
        this.codiceUnivoco = codiceUnivoco;
    }

    // Costruttore vuoto per DAO / binding automatico
    public Volo() {}

    // Builder interno
    public static class Builder {
        private final String codiceUnivoco; // obbligatorio
        private String compagniaAerea;
        private String dataVolo;
        private String orarioPrevisto;
        private String orarioStimato;
        private StatoVolo stato;
        private Amministratore amministratore;
        private TabellaOrario tabellaOrario;
        private String aeroporto;
        private String gate;
        private String arrivoPartenza;

        public Builder(String codiceUnivoco) {
            this.codiceUnivoco = codiceUnivoco;
        }

        public Builder compagniaAerea(String compagniaAerea) { this.compagniaAerea = compagniaAerea; return this; }
        public Builder dataVolo(String dataVolo) { this.dataVolo = dataVolo; return this; }
        public Builder orarioPrevisto(String orarioPrevisto) { this.orarioPrevisto = orarioPrevisto; return this; }
        public Builder orarioStimato(String orarioStimato) { this.orarioStimato = orarioStimato; return this; }
        public Builder stato(StatoVolo stato) { this.stato = stato; return this; }
        public Builder amministratore(Amministratore amministratore) { this.amministratore = amministratore; return this; }
        public Builder tabellaOrario(TabellaOrario tabellaOrario) { this.tabellaOrario = tabellaOrario; return this; }
        public Builder aeroporto(String aeroporto) { this.aeroporto = aeroporto; return this; }
        public Builder gate(String gate) { this.gate = gate; return this; }
        public Builder arrivoPartenza(String arrivoPartenza) { this.arrivoPartenza = arrivoPartenza; return this; }

        public Volo build() {
            return new Volo(this);
        }
    }

    // Getter e Setter
    public List<Prenotazione> getPrenotazioni() { return prenotazioni; }
    public void setPrenotazioni(List<Prenotazione> prenotazioni) { this.prenotazioni = prenotazioni; }

    public TabellaOrario getTabellaOrario() { return tabellaOrario; }
    public void setTabellaOrario(TabellaOrario tabellaOrario) { this.tabellaOrario = tabellaOrario; }

    public Amministratore getAmministratore() { return amministratore; }
    public void setAmministratore(Amministratore amministratore) { this.amministratore = amministratore; }

    public String getCodiceUnivoco() { return codiceUnivoco; }
    public void setCodiceUnivoco(String codiceUnivoco) { this.codiceUnivoco = codiceUnivoco; }

    public String getCompagniaAerea() { return compagniaAerea; }
    public void setCompagniaAerea(String compagniaAerea) { this.compagniaAerea = compagniaAerea; }

    public String getDataVolo() { return dataVolo; }
    public void setDataVolo(String dataVolo) { this.dataVolo = dataVolo; }

    public String getOrarioPrevisto() { return orarioPrevisto; }
    public void setOrarioPrevisto(String orarioPrevisto) { this.orarioPrevisto = orarioPrevisto; }

    public String getOrarioStimato() { return orarioStimato; }
    public void setOrarioStimato(String orarioStimato) { this.orarioStimato = orarioStimato; }

    public StatoVolo getStato() { return stato; }
    public void setStato(StatoVolo stato) { this.stato = stato; }

    public String getAeroporto() { return aeroporto; }
    public void setAeroporto(String aeroporto) { this.aeroporto = aeroporto; }

    public String getGate() { return gate; }
    public void setGate(String gate) { this.gate = gate; }

    public String getArrivoPartenza() { return arrivoPartenza; }
    public void setArrivoPartenza(String arrivoPartenza) { this.arrivoPartenza = arrivoPartenza; }
}