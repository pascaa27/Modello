package gui;

import javax.swing.*;
import controller.Controller;
import model.DatiPasseggero;
import model.StatoPrenotazione;
import model.Utente;
import model.UtenteGenerico;

public class GestionePrenotazioniGUI {
    private JPanel panelPrenotazione;
    private JTextField numeroBigliettoTextField;
    private JTextField postoTextField;
    private JComboBox<StatoPrenotazione> statoPrenotazioneComboBox;
    private JButton aggiungiPrenotazioneButton;
    private Controller controller;
    private Utente utente; // campo utente
    private DatiPasseggero datiPasseggero = null;

    public GestionePrenotazioniGUI(Controller controller, Utente utente) {
        this.controller = controller;
        this.utente = utente;

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
            String numeroBiglietto = numeroBigliettoTextField.getText().trim();
            String posto = postoTextField.getText().trim();

            if(numeroBiglietto.isEmpty() || posto.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Compila prima tutti i campi obbligatori (Numero biglietto e Posto).",
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

            // 🔴 Controllo dei campi passeggero
            if(nome == null || cognome == null || codiceFiscale == null ||
                    nome.isEmpty() || cognome.isEmpty() || codiceFiscale.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Compila correttamente tutti i dati del passeggero.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            StatoPrenotazione stato = (StatoPrenotazione) statoPrenotazioneComboBox.getSelectedItem();

            controller.aggiungiPrenotazione(
                    numeroBiglietto,
                    posto,
                    stato,
                    utenteGenerico,
                    nome,
                    cognome,
                    codiceFiscale,
                    null,
                    null
            );

            JOptionPane.showMessageDialog(null, "Prenotazione completata con successo!");

            // reset campi
            numeroBigliettoTextField.setText("");
            postoTextField.setText("");
            statoPrenotazioneComboBox.setSelectedIndex(0);
        });
    }

    public JPanel getPanelPrenotazione() {
            return panelPrenotazione;
        }
}