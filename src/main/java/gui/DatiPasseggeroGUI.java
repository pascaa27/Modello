package gui;

import javax.swing.*;
import model.DatiPasseggero;

public class DatiPasseggeroGUI {
    private JPanel panelDatiPasseggero;
    private JTextField nomeTextField;
    private JTextField cognomeTextField;
    private JTextField codiceFiscaleTextField;
    private JButton salvaDatiPasseggeroButton;
    private  DatiPasseggero datiPasseggero;

    public DatiPasseggeroGUI(JDialog parentDialog) {
        salvaDatiPasseggeroButton.addActionListener(e -> {
            String nome = nomeTextField.getText();
            String cognome = cognomeTextField.getText();
            String codiceFiscale = codiceFiscaleTextField.getText();

            datiPasseggero = new DatiPasseggero(nome, cognome, codiceFiscale);
            JOptionPane.showMessageDialog(parentDialog, "Dati passeggero salvati.");
            ((JDialog) SwingUtilities.getWindowAncestor(panelDatiPasseggero)).dispose();
        });
    }

    public JPanel getPanel() {
        return panelDatiPasseggero;
    }

    public DatiPasseggero getDatiPasseggero() {
        return datiPasseggero;
    }
}