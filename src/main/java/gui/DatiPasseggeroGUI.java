package gui;

import javax.swing.*;
import model.DatiPasseggero;

public class DatiPasseggeroGUI {
    private JPanel panelDatiPasseggero;
    private JTextField nomeTextField;
    private JTextField cognomeTextField;
    private JTextField codiceFiscaleTextField;
    private JTextField emailTextField;         // nuovo campo email
    private JButton salvaDatiPasseggeroButton;

    private String nomeInserito;
    private String cognomeInserito;
    private String codiceFiscaleInserito;
    private String emailInserita;               // nuova variabile

    public DatiPasseggeroGUI(JDialog parentDialog) {
        salvaDatiPasseggeroButton.addActionListener(e -> {
            nomeInserito = nomeTextField.getText().trim();
            cognomeInserito = cognomeTextField.getText().trim();
            codiceFiscaleInserito = codiceFiscaleTextField.getText().trim();
            emailInserita = emailTextField.getText().trim();   // prendo anche email

            // L'email NON è più obbligatoria!
            if (nomeInserito.isEmpty() ||
                    cognomeInserito.isEmpty() ||
                    codiceFiscaleInserito.isEmpty()) {
                JOptionPane.showMessageDialog(parentDialog,
                        "I campi nome, cognome e codice fiscale sono obbligatori.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(parentDialog, "Dati passeggero salvati.");
            ((JDialog) SwingUtilities.getWindowAncestor(panelDatiPasseggero)).dispose();
        });
    }

    public JPanel getPanel() { return panelDatiPasseggero; }
    public String getNomeInserito() { return nomeInserito; }
    public String getCognomeInserito() { return cognomeInserito; }
    public String getCodiceFiscaleInserito() { return codiceFiscaleInserito; }
    public String getEmailInserita() { return emailInserita; } // può essere vuota/null
}