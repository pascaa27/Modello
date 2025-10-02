package model;

/**
 * La classe rappresenta le informazioni personali
 * di un passeggero registrato o associato a una prenotazione.
 * <p>
 * Contiene dati identificativi (nome, cognome, codice fiscale) e di accesso
 * (email e password), che possono essere utilizzati sia per la gestione delle
 * prenotazioni che per l'autenticazione nel sistema.
 * </p>
 *
 * @see Prenotazione
 * @see Utente
 */
public class DatiPasseggero {
    private String nome;
    private String cognome;
    private String codiceFiscale;
    private String email;
    private String password;

    /**
     * Costruisce un nuovo oggetto {@code DatiPasseggero} senza specificare la password.
     * La password viene inizializzata come stringa vuota.
     *
     * @param nome          nome del passeggero
     * @param cognome       cognome del passeggero
     * @param codiceFiscale codice fiscale del passeggero
     * @param email         email univoca del passeggero
     */
    public DatiPasseggero(String nome, String cognome, String codiceFiscale, String email) {
        this(nome, cognome, codiceFiscale, email, "");
    }

    /**
     * Costruisce un nuovo oggetto {@code DatiPasseggero} specificando anche la password.
     *
     * @param nome          nome del passeggero
     * @param cognome       cognome del passeggero
     * @param codiceFiscale codice fiscale del passeggero
     * @param email         email univoca del passeggero
     * @param password      password associata all'account
     */
    public DatiPasseggero(String nome, String cognome, String codiceFiscale, String email, String password) {
        this.nome = nome;
        this.cognome = cognome;
        this.codiceFiscale = codiceFiscale;
        this.email = email;
        this.password = password;
    }

    /**
     * Restituisce il nome del passeggero.
     *
     * @return nome del passeggero
     */
    public String getNome() { return nome; }

    /**
     * Imposta il nome del passeggero.
     *
     * @param nome nuovo nome da assegnare
     */
    public void setNome(String nome) { this.nome = nome; }

    /**
     * Restituisce il cognome del passeggero.
     *
     * @return cognome del passeggero
     */
    public String getCognome() { return cognome; }

    /**
     * Imposta il cognome del passeggero.
     *
     * @param cognome nuovo cognome da assegnare
     */
    public void setCognome(String cognome) { this.cognome = cognome; }

    /**
     * Restituisce il codice fiscale del passeggero.
     *
     * @return codice fiscale oppure {@code null} se non impostato
     */
    public String getCodiceFiscale() { return codiceFiscale; }

    /**
     * Imposta il codice fiscale del passeggero.
     *
     * @param codiceFiscale nuovo codice fiscale
     */
    public void setCodiceFiscale(String codiceFiscale) { this.codiceFiscale = codiceFiscale; }

    /**
     * Restituisce l'email del passeggero.
     *
     * @return email del passeggero
     */
    public String getEmail() { return email; }

    /**
     * Imposta l'email del passeggero.
     *
     * @param email nuova email da assegnare
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Restituisce la password associata all'account del passeggero.
     *
     * @return password del passeggero
     */
    public String getPassword() { return password; }

    /**
     * Imposta la password dell'account del passeggero.
     *
     * @param password nuova password da assegnare
     */
    public void setPassword(String password) { this.password = password; }
}