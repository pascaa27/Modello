package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta un volo gestito nel sistema aeroportuale.
 * <p>
 * Contiene informazioni sul volo come codice univoco, compagnia aerea, orari,
 * stato, amministratore responsabile, tabella orario, aeroporto, gate,
 * arrivo/partenza e prenotazioni associate.
 * </p>
 */
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

    /**
     * Costruttore protetto utilizzato dal Builder per creare un oggetto Volo.
     * Assegna tutti i valori impostati nel Builder ai corrispondenti campi dell'oggetto Volo.
     *
     * @param builder istanza del Builder contenente i valori da utilizzare per inizializzare il Volo
     */
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

    /**
     * Costruttore con solo codice univoco.
     *
     * @param codiceUnivoco codice identificativo del volo
     */
    public Volo(String codiceUnivoco) {
        this.codiceUnivoco = codiceUnivoco;
    }

    /**
     * Costruttore vuoto richiesto per DAO o binding automatico.
     */
    public Volo() {}

    /**
     * Builder per creare istanze di {@link Volo} in modo flessibile.
     */
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

        /**
         * Costruttore del builder con parametro obbligatorio codice univoco.
         *
         * @param codiceUnivoco codice identificativo del volo
         */
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

        /**
         * Costruisce l'oggetto {@link Volo} utilizzando i dati impostati.
         *
         * @return nuovo oggetto {@link Volo}
         */
        public Volo build() {
            return new Volo(this);
        }
    }

    /**
     * Restituisce la lista delle prenotazioni associate al volo.
     *
     * @return lista di {@link Prenotazione} relative a questo volo
     */
    public List<Prenotazione> getPrenotazioni() { return prenotazioni; }

    /**
     * Imposta la lista delle prenotazioni associate al volo.
     *
     * @param prenotazioni lista di {@link Prenotazione} da associare al volo
     */
    public void setPrenotazioni(List<Prenotazione> prenotazioni) { this.prenotazioni = prenotazioni; }

    /**
     * Restituisce la tabella orario associata al volo.
     *
     * @return tabella orario {@link TabellaOrario} del volo
     */
    public TabellaOrario getTabellaOrario() { return tabellaOrario; }

    /**
     * Imposta la tabella orario associata al volo.
     *
     * @param tabellaOrario tabella orario {@link TabellaOrario} da associare
     */
    public void setTabellaOrario(TabellaOrario tabellaOrario) { this.tabellaOrario = tabellaOrario; }

    /**
     * Restituisce l'amministratore responsabile del volo.
     *
     * @return {@link Amministratore} del volo
     */
    public Amministratore getAmministratore() { return amministratore; }

    /**
     * Imposta l'amministratore responsabile del volo.
     *
     * @param amministratore {@link Amministratore} da associare al volo
     */
    public void setAmministratore(Amministratore amministratore) { this.amministratore = amministratore; }

    /**
     * Restituisce il codice univoco del volo.
     *
     * @return codice univoco come {@link String}
     */
    public String getCodiceUnivoco() { return codiceUnivoco; }

    /**
     * Imposta il codice univoco del volo.
     *
     * @param codiceUnivoco codice univoco da assegnare
     */
    public void setCodiceUnivoco(String codiceUnivoco) { this.codiceUnivoco = codiceUnivoco; }

    /**
     * Restituisce la compagnia aerea del volo.
     *
     * @return nome della compagnia aerea
     */
    public String getCompagniaAerea() { return compagniaAerea; }

    /**
     * Imposta la compagnia aerea del volo.
     *
     * @param compagniaAerea nome della compagnia aerea
     */
    public void setCompagniaAerea(String compagniaAerea) { this.compagniaAerea = compagniaAerea; }

    /**
     * Restituisce la data del volo.
     *
     * @return data del volo come {@link String}
     */
    public String getDataVolo() { return dataVolo; }

    /**
     * Imposta la data del volo.
     *
     * @param dataVolo data del volo da assegnare
     */
    public void setDataVolo(String dataVolo) { this.dataVolo = dataVolo; }

    /**
     * Restituisce l'orario previsto del volo.
     *
     * @return orario previsto come {@link String}
     */
    public String getOrarioPrevisto() { return orarioPrevisto; }

    /**
     * Imposta l'orario previsto del volo.
     *
     * @param orarioPrevisto orario previsto da assegnare
     */
    public void setOrarioPrevisto(String orarioPrevisto) { this.orarioPrevisto = orarioPrevisto; }

    /**
     * Restituisce l'orario stimato del volo.
     *
     * @return orario stimato come {@link String}
     */
    public String getOrarioStimato() { return orarioStimato; }

    /**
     * Imposta l'orario stimato del volo.
     *
     * @param orarioStimato orario stimato da assegnare
     */
    public void setOrarioStimato(String orarioStimato) { this.orarioStimato = orarioStimato; }

    /**
     * Restituisce lo stato attuale del volo.
     *
     * @return stato come {@link StatoVolo}
     */
    public StatoVolo getStato() { return stato; }

    /**
     * Imposta lo stato del volo.
     *
     * @param stato {@link StatoVolo} da assegnare
     */
    public void setStato(StatoVolo stato) { this.stato = stato; }

    /**
     * Restituisce l'aeroporto associato al volo.
     *
     * @return aeroporto come {@link String}
     */
    public String getAeroporto() { return aeroporto; }

    /**
     * Imposta l'aeroporto associato al volo.
     *
     * @param aeroporto aeroporto da assegnare
     */
    public void setAeroporto(String aeroporto) { this.aeroporto = aeroporto; }

    /**
     * Restituisce il gate del volo.
     *
     * @return gate come {@link String}
     */
    public String getGate() { return gate; }

    /**
     * Imposta il gate del volo.
     *
     * @param gate gate da assegnare
     */
    public void setGate(String gate) { this.gate = gate; }

    /**
     * Restituisce se il volo è in arrivo o in partenza.
     *
     * @return "ARRIVO" o "PARTENZA" come {@link String}
     */
    public String getArrivoPartenza() { return arrivoPartenza; }

    /**
     * Imposta se il volo è in arrivo o in partenza.
     *
     * @param arrivoPartenza "ARRIVO" o "PARTENZA"
     */
    public void setArrivoPartenza(String arrivoPartenza) { this.arrivoPartenza = arrivoPartenza; }
}