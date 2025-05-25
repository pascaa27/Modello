package gui;

import javax.swing.*;
import model.DatiPasseggero;

public class DatiPasseggeroGUI {
    private JPanel panelDatiPasseggero;
    private JTextField nomeTextField;
    private JTextField cognomeTextField;
    private JTextField codiceFiscaleTextField;
    private JButton salvaDatiPasseggeroButton;
    private String nomeInserito;
    private String cognomeInserito;
    private String codiceFiscaleInserito;

    public DatiPasseggeroGUI(JDialog parentDialog) {
        salvaDatiPasseggeroButton.addActionListener(e -> {
            nomeInserito = nomeTextField.getText().trim();
            cognomeInserito = cognomeTextField.getText().trim();
            codiceFiscaleInserito = codiceFiscaleTextField.getText().trim();

            if(nomeInserito.isEmpty() || cognomeInserito.isEmpty() || codiceFiscaleInserito.isEmpty()) {
                JOptionPane.showMessageDialog(parentDialog, "Tutti i campi devono essere compilati.", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(parentDialog, "Dati passeggero salvati.");
            ((JDialog) SwingUtilities.getWindowAncestor(panelDatiPasseggero)).dispose();
        });
    }

    public JPanel getPanel() {
        return panelDatiPasseggero;
    }

    public String getNomeInserito() {
        return nomeInserito;
    }
    public String getCognomeInserito() {
        return cognomeInserito;
    }
    public String getCodiceFiscaleInserito() {
        return codiceFiscaleInserito;
    }
}