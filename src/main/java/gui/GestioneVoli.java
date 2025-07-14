package gui;

import javax.swing.*;
import model.StatoVolo;
import controller.Controller;

public class GestioneVoli {
    private JPanel panelDatiVolo;
    private JTextField codiceTextField;
    private JTextField compagniaTextField;
    private JTextField dataTextField;
    private JTextField orarioPrevistoTextField;
    private JComboBox<StatoVolo> statoVoloComboBox;
    private JButton aggiungiVoloButton;
    private Controller controller;

    private void aggiungiVolo() {
        String codice = codiceTextField.getText();
        String compagnia = compagniaTextField.getText();
        String data = dataTextField.getText();
        String orarioPrevisto = orarioPrevistoTextField.getText();
        StatoVolo stato = (StatoVolo) statoVoloComboBox.getSelectedItem();

        controller.aggiungiVolo(codice, compagnia, data, orarioPrevisto, stato, null, null);
        JOptionPane.showMessageDialog(null, "Volo aggiunto con successo!");

        codiceTextField.setText("");
        compagniaTextField.setText("");
        dataTextField.setText("");
        orarioPrevistoTextField.setText("");
        statoVoloComboBox.setSelectedIndex(0);
    }

    public GestioneVoli(Controller controller) {
        this.controller = controller;

        for(StatoVolo stato : StatoVolo.values()) {
            statoVoloComboBox.addItem(stato);
        }

        aggiungiVoloButton.addActionListener(e -> aggiungiVolo());
    }


}