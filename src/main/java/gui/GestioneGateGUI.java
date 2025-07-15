package gui;

import javax.swing.*;
import controller.Controller;

public class GestioneGateGUI {
    private JPanel panelGate;
    private JTextField numeroGateTextField;
    private JButton aggiungiGateButton;
    private JButton eliminaGateButton;
    private JTable gateEsistentiTable;
    private Controller controller;

    private void aggiungiGate() {
        String numeroStr = numeroGateTextField.getText();

        // Controllo campo obbligatorio e formato intero
        if (numeroStr == null || numeroStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Inserisci il numero del gate!");
            return;
        }

        int numero;
        try {
            numero = Integer.parseInt(numeroStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Il numero del gate deve essere un intero!");
            return;
        }

        controller.aggiungiGate(numero);
        JOptionPane.showMessageDialog(null, "Gate aggiunto con successo!");

        numeroGateTextField.setText("");
    }

    public GestioneGateGUI(Controller controller) {
        this.controller = controller;

        aggiungiGateButton.addActionListener(e -> aggiungiGate());
    }


    public JPanel getPanelGate() {
        return panelGate;
    }
}