package model;

import java.util.List;
import java.util.ArrayList;     //ho bisogno di entrambe perchè dichiaro la lista come List<> e creo l'oggetto come ArrayList<>()

public class UtenteGenerico extends Utente {

    private String nomeUtente;
    private List<Prenotazione> prenotazioni= new ArrayList<>();     //attributo che rappresenta tutte le prenotazioni fatte da questo utente
    private AreaPersonale areaPersonale;
    private String ultimoCodicePrenotazione;
    private List<String> codiciPrenotazioni = new ArrayList<>();

    public void aggiungiPrenotazione(Prenotazione p) {
        prenotazioni.add(p);        //aggiunge alla lista le prenotazioni p
    }

    public UtenteGenerico(String login, String password, String nomeUtente, String cognomeUtente, List<Prenotazione> prenotazioni, AreaPersonale areaPersonale) {
        super(login, password, nomeUtente, cognomeUtente);
        this.nomeUtente = nomeUtente;
        this.prenotazioni = prenotazioni;
        this.areaPersonale = areaPersonale;
    }

    // costruttore vuoto per il binding automatico in PrenotazioneDAOPostgres
    public UtenteGenerico() {
        // vuoto, necessario per il framework nel controller
    }

    public String getNomeUtente() {
        return nomeUtente;
    }

    public void setNomeUtente(String nomeUtente) {
        this.nomeUtente = nomeUtente;
    }

    public List<Prenotazione> getPrenotazioni() {
        return prenotazioni;
    }

    public void setPrenotazioni(List<Prenotazione> prenotazioni) {
        this.prenotazioni = prenotazioni;
    }

    public void prenotaVolo(){
        System.out.println("Prenotazione effettuata da " + login);
    }

    public AreaPersonale getAreaPersonale() {
        return areaPersonale;
    }

    public void setAreaPersonale(AreaPersonale areaPersonale) {
        this.areaPersonale = areaPersonale;
    }

    public String getUltimoCodicePrenotazione() {
        return ultimoCodicePrenotazione;
    }

    public void setUltimoCodicePrenotazione(String ultimoCodicePrenotazione) {
        this.ultimoCodicePrenotazione = ultimoCodicePrenotazione;
    }

    public List<String> getCodiciPrenotazioni() {
        return codiciPrenotazioni;
    }

    public boolean isRegistrato() {
        return getLogin() != null && !getLogin().isBlank() &&
                getPassword() != null && !getPassword().isBlank();
    }

    public void aggiungiCodicePrenotazione(String codice) {
        if (codice != null && !codice.isBlank() && !codiciPrenotazioni.contains(codice)) {
            codiciPrenotazioni.add(codice);
        }
    }
}