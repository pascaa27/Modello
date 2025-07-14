package gui;

import javax.swing.*;
import controller.Controller;
import model.DatiPasseggero;
import model.StatoPrenotazione;
import model.UtenteGenerico;

public class GestionePrenotazioni {
    private JPanel panelPrenotazione;
    private JTextField numeroBigliettoTextField;
    private JTextField postoTextField;
    private JComboBox<StatoPrenotazione> statoPrenotazioneComboBox;
    private JButton aggiungiPrenotazioneButton;
    private Controller controller;
    private UtenteGenerico utenteGenerico; // campo utente
    private DatiPasseggero datiPasseggero = null;

    public GestionePrenotazioni(Controller controller, UtenteGenerico utenteGenerico) {
        this.controller = controller;
        this.utenteGenerico = utenteGenerico;

        for (StatoPrenotazione stato : StatoPrenotazione.values()) {
            statoPrenotazioneComboBox.addItem(stato);
        }

        aggiungiPrenotazioneButton.addActionListener(e -> {
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

            if (nome == null || cognome == null || codiceFiscale == null ||
                    nome.isEmpty() || cognome.isEmpty() || codiceFiscale.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Dati passeggero non validi. Prenotazione annullata.");
                return;
            }

            String numeroBiglietto = numeroBigliettoTextField.getText();
            String posto = postoTextField.getText();
            StatoPrenotazione stato = (StatoPrenotazione) statoPrenotazioneComboBox.getSelectedItem();

            controller.aggiungiPrenotazione(
                    numeroBiglietto,
                    posto,
                    stato,
                    utenteGenerico,
                    nome,
                    cognome,
                    codiceFiscale,
                    null, // email
                    null  // altro campo
            );
            JOptionPane.showMessageDialog(null, "Prenotazione completata con successo!");

            numeroBigliettoTextField.setText("");
            postoTextField.setText("");
            statoPrenotazioneComboBox.setSelectedIndex(0);
        });
    }

    public JPanel getPanelPrenotazione() {
        return panelPrenotazione;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Controller controller = new Controller();
            // Devi avere un oggetto UtenteGenerico qui, ad esempio ottenuto dal login
            UtenteGenerico utente = new UtenteGenerico(
                    "login", "password", "Nome", "Cognome", null, null
            ); // Sostituisci con dati reali

            JFrame frame = new JFrame("Prenotazione");
            frame.setContentPane(new GestionePrenotazioni(controller, utente).getPanelPrenotazione());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}