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
    private Controller controller;
    private Utente utente; // campo utente
    private DatiPasseggero datiPasseggero = null;
    private final AreaPersonaleAmmGUI areaPersonaleAmmGUI;

    public GestionePrenotazioniGUI(Controller controller, Utente utente, AreaPersonaleAmmGUI areaPersonaleAmmGUI) {
        this.controller = controller;
        this.utente = utente;
        this.areaPersonaleAmmGUI = areaPersonaleAmmGUI;

        for(StatoPrenotazione stato : StatoPrenotazione.values()) {
            statoPrenotazioneComboBox.addItem(stato);
        }

        UtenteGenerico utenteGenerico;
        if(controller.getUtenteByEmail(utente.getNomeUtente()) == null) {
            utenteGenerico = controller.creaUtenteGenerico(utente.getNomeUtente());
        } else {
            utenteGenerico = controller.getUtenteByEmail(utente.getNomeUtente());
        }

        aggiungiPrenotazioneButton.addActionListener(e -> {
            String numeroBiglietto = numeroPrenotazioneTextField.getText().trim();
            String posto = postoTextField.getText().trim();
            String numeroVolo = numeroVoloTextField.getText().trim();

            if(numeroBiglietto.isEmpty() || posto.isEmpty() || numeroVolo.isEmpty()) {
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

            //  Controllo dei campi passeggero
            if(nome == null || cognome == null || codiceFiscale == null ||
                    nome.isEmpty() || cognome.isEmpty() || codiceFiscale.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Compila correttamente tutti i dati del passeggero.",
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



            controller.aggiungiPrenotazione(
                    numeroBiglietto,
                    posto,
                    stato,
                    numeroVolo,
                    utenteGenerico,
                    nome,
                    cognome,
                    codiceFiscale,
                    null,
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
    }

    public JPanel getPanelPrenotazione() {
            return panelPrenotazione;
        }
}