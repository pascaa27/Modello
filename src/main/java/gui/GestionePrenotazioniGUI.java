package gui;

import javax.swing.*;
import controller.Controller;
import model.*;

public class GestionePrenotazioniGUI {
    private JPanel panelPrenotazione;
    private JTextField numeroPrenotazioneTextField;
    private JTextField postoTextField;
    private JComboBox<StatoPrenotazione> statoPrenotazioneComboBox;
    private JButton aggiungiPrenotazioneButton;
    private JTextField numeroVoloTextField;
    private JButton rimuoviPrenotazioneButton;
    private Controller controller;
    private Utente utente;
    private final AreaPersonaleAmmGUI areaPersonaleAmmGUI;

    public GestionePrenotazioniGUI(Controller controller, Utente utente, AreaPersonaleAmmGUI areaPersonaleAmmGUI) {
        this.controller = controller;
        this.utente = utente;
        this.areaPersonaleAmmGUI = areaPersonaleAmmGUI;

        // popola combo stato
        for (StatoPrenotazione stato : StatoPrenotazione.values()) {
            statoPrenotazioneComboBox.addItem(stato);
        }

        UtenteGenerico utenteGenerico;
        if (controller.getUtenteByEmail(utente.getNomeUtente()) == null) {
            utenteGenerico = controller.creaUtenteGenerico(utente.getNomeUtente());
        } else {
            utenteGenerico = controller.getUtenteByEmail(utente.getNomeUtente());
        }

        // listener aggiungi prenotazione
        aggiungiPrenotazioneButton.addActionListener(e -> {
            String numeroBiglietto = numeroPrenotazioneTextField.getText().trim();
            String posto = postoTextField.getText().trim();
            String numeroVolo = numeroVoloTextField.getText().trim();

            if (numeroBiglietto.isEmpty() || posto.isEmpty() || numeroVolo.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Compila prima tutti i campi obbligatori.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Finestra per inserire i dati passeggero
            JOptionPane.showMessageDialog(null, "Per effettuare la prenotazione, inserire i seguenti dati:");
            JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(panelPrenotazione), "Inserisci dati passeggero", true);
            DatiPasseggeroGUI datiGUI = new DatiPasseggeroGUI(dialog);
            dialog.setContentPane(datiGUI.getPanel());
            dialog.pack();
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);

            String nome = datiGUI.getNomeInserito();
            String cognome = datiGUI.getCognomeInserito();
            String codiceFiscale = datiGUI.getCodiceFiscaleInserito();
            String email = datiGUI.getEmailInserita();   // <-- nuova riga

            //  Controllo dei campi passeggero
            if (nome == null || cognome == null || codiceFiscale == null || email == null ||
                    nome.isEmpty() || cognome.isEmpty() || codiceFiscale.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Compila correttamente tutti i dati del passeggero (inclusa l'email).",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            StatoPrenotazione stato = (StatoPrenotazione) statoPrenotazioneComboBox.getSelectedItem();

            Volo volo = controller.getVoloByCodice(numeroVolo);

            if (volo == null) {
                JOptionPane.showMessageDialog(null,
                        "Il volo con codice " + numeroVolo + " non esiste.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // chiamata al controller con email inclusa
            controller.aggiungiPrenotazione(
                    numeroBiglietto,
                    posto,
                    stato,
                    numeroVolo,
                    utenteGenerico,
                    nome,
                    cognome,
                    codiceFiscale,
                    email,    // <-- adesso passa l'email al controller
                    volo
            );

            areaPersonaleAmmGUI.aggiornaTabellaPasseggeri();

            JOptionPane.showMessageDialog(null, "Prenotazione completata con successo!");

            // reset campi
            numeroPrenotazioneTextField.setText("");
            postoTextField.setText("");
            numeroVoloTextField.setText("");
            statoPrenotazioneComboBox.setSelectedIndex(0);
        });

        rimuoviPrenotazioneButton.addActionListener(e -> rimuoviPrenotazione());
    }

    private void rimuoviPrenotazione() {
        String numeroBiglietto = numeroPrenotazioneTextField.getText().trim();

        if (numeroBiglietto.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Inserisci il numero della prenotazione da rimuovere.",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean rimosso = controller.rimuoviPrenotazione(numeroBiglietto);

        if (rimosso) {
            JOptionPane.showMessageDialog(null,
                    "Prenotazione rimossa con successo!");
            areaPersonaleAmmGUI.aggiornaTabellaPasseggeri();
            numeroPrenotazioneTextField.setText("");
            postoTextField.setText("");
            numeroVoloTextField.setText("");
            statoPrenotazioneComboBox.setSelectedIndex(0);
        } else {
            JOptionPane.showMessageDialog(null,
                    "Nessuna prenotazione trovata con numero " + numeroBiglietto,
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public JPanel getPanelPrenotazione() {
        return panelPrenotazione;
    }
}