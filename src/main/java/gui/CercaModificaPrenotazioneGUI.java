package gui;

import javax.swing.*;
import java.awt.event.ActionListener;
import controller.Controller;
import model.Prenotazione;
import model.Utente;
import model.Volo;
import model.StatoPrenotazione;

public class CercaModificaPrenotazioneGUI {
    private JPanel panelCercaModificaPrenotazione;
    private JButton cercaButton;
    private JTextField codiceInserimentoTextField;
    private JTextField nomeTextField;
    private JTextField cognomeTextField;
    private JTextField emailTextField;
    private JTextField voloTextField;
    private JComboBox<String> statoVoloComboBox;
    private JButton salvaModificheButton;
    private JButton annullaPrenotazioneButton;
    private JTextArea messaggioTextArea;
    private Controller controller;
    private Prenotazione prenotazioneCorrente;
    private Utente utente;


    // Costruttore con Controller passato dall'area personale
    public CercaModificaPrenotazioneGUI(Controller controller, Utente utente, String codicePrenotazione) {
        this.controller = controller;
        this.utente = utente;
        if (codicePrenotazione != null && !codicePrenotazione.isEmpty()) {
            codiceInserimentoTextField.setText(codicePrenotazione);
            cercaPrenotazione(); // esegue subito la ricerca
        }

        // Popola la comboBox con tutti gli stati disponibili
        statoVoloComboBox.removeAllItems();
        for(StatoPrenotazione stato : StatoPrenotazione.values()) {
            statoVoloComboBox.addItem(stato.name());
        }

        setCampiPrenotazioneAbilitati(false);

        cercaButton.addActionListener(e -> cercaPrenotazione());
        salvaModificheButton.addActionListener(e -> salvaModifiche());
        annullaPrenotazioneButton.addActionListener(e -> annullaPrenotazione());
    }

    // Logica per la ricerca prenotazione
    private void cercaPrenotazione() {
        String codice = codiceInserimentoTextField.getText().trim();
        if(codice.isEmpty()) {
            messaggioTextArea.setText("Inserisci un codice prenotazione.");
            setCampiPrenotazioneAbilitati(false);
            return;
        }

        prenotazioneCorrente = controller.cercaPrenotazione(codice);
        if(prenotazioneCorrente != null) {
            nomeTextField.setText(prenotazioneCorrente.getDatiPasseggero().getNome());
            cognomeTextField.setText(prenotazioneCorrente.getDatiPasseggero().getCognome());
            emailTextField.setText(prenotazioneCorrente.getDatiPasseggero().getEmail());
            voloTextField.setText(prenotazioneCorrente.getVolo().getCodiceUnivoco());
            statoVoloComboBox.setSelectedItem(prenotazioneCorrente.getStato());

            codiceInserimentoTextField.setEditable(false);

            messaggioTextArea.setText("Prenotazione trovata!\nPuoi modificarla oppure annullarla.");
            setCampiPrenotazioneAbilitati(true);
        } else {
            messaggioTextArea.setText("Prenotazione non trovata.");
            pulisciCampiPrenotazione();
            setCampiPrenotazioneAbilitati(false);
        }
    }

    // Logica per il salvataggio delle modifiche
    private void salvaModifiche() {
        if(prenotazioneCorrente == null) {
            messaggioTextArea.setText("Nessuna prenotazione da modificare.");
            return;
        }

        prenotazioneCorrente.getDatiPasseggero().setNome(nomeTextField.getText());
        prenotazioneCorrente.getDatiPasseggero().setCognome(cognomeTextField.getText());
        prenotazioneCorrente.getDatiPasseggero().setEmail(emailTextField.getText());

        String codiceVolo = voloTextField.getText().trim();
        Volo volo = controller.cercaVolo(codiceVolo);
        if(volo == null) {
            messaggioTextArea.setText("Volo non trovato.");
            return;
        }
        prenotazioneCorrente.setVolo(volo);

        String statoString = (String) statoVoloComboBox.getSelectedItem();
        StatoPrenotazione stato;
        try {
            stato = StatoPrenotazione.valueOf(statoString.toUpperCase());
        } catch(Exception e) {
            messaggioTextArea.setText("Stato non valido.");
            return;
        }
        prenotazioneCorrente.setStato(stato);

        boolean successo = controller.salvaPrenotazione(prenotazioneCorrente);
        if(successo) {
            messaggioTextArea.setText("Modifiche salvate con successo!");

            pulisciCampiPrenotazione();
            setCampiPrenotazioneAbilitati(false);
            codiceInserimentoTextField.setText("");
            codiceInserimentoTextField.setEditable(true);
        } else {
            messaggioTextArea.setText("Errore nel salvataggio delle modifiche.");
        }
    }

    // Logica per annullare la prenotazione
    private void annullaPrenotazione() {
        if(prenotazioneCorrente == null) {
            messaggioTextArea.setText("Nessuna prenotazione da annullare.");
            return;
        }

        boolean successo = controller.annullaPrenotazione(prenotazioneCorrente); // implementa questo nel Controller!
        if(successo) {
            messaggioTextArea.setText("Prenotazione annullata!");
            pulisciCampiPrenotazione();
            setCampiPrenotazioneAbilitati(false);

            codiceInserimentoTextField.setEditable(true);
        } else {
            messaggioTextArea.setText("Errore nell'annullamento.");
        }
    }

    // Utility per abilitare/disabilitare i campi
    private void setCampiPrenotazioneAbilitati(boolean abilitati) {
        nomeTextField.setEnabled(abilitati);
        cognomeTextField.setEnabled(abilitati);
        emailTextField.setEnabled(abilitati);
        voloTextField.setEnabled(false);
        statoVoloComboBox.setEnabled(false);
        salvaModificheButton.setEnabled(abilitati);
        annullaPrenotazioneButton.setEnabled(abilitati);
    }

    // Pulisce i campi quando non c'è una prenotazione
    private void pulisciCampiPrenotazione() {
        nomeTextField.setText("");
        cognomeTextField.setText("");
        emailTextField.setText("");
        voloTextField.setText("");
        statoVoloComboBox.setSelectedIndex(0);
    }

    // Metodo per recuperare il pannello da inserire nel JFrame principale
    public JPanel getPanel() {
        return panelCercaModificaPrenotazione;
    }
}