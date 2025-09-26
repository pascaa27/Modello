package gui;

import controller.Controller;
import model.Bagaglio;
import model.StatoBagaglio;

import javax.swing.*;

public class GestioneBagagliGUI {
    private JPanel panelBagaglio;
    private JTextField codiceBagaglioTextField;
    private JComboBox<StatoBagaglio> statoBagaglioComboBox;
    private JButton aggiungiBagaglioButton;
    private JButton rimuoviBagaglioButton;
    private JButton modificaBagaglioButton;

    private final Controller controller;
    private final AreaPersonaleAmmGUI areaAmmGUI;

    public GestioneBagagliGUI(Controller controller, AreaPersonaleAmmGUI areaAmmGUI) {
        this.controller = controller;
        this.areaAmmGUI = areaAmmGUI;

        // Popola combo stato
        for (StatoBagaglio stato : StatoBagaglio.values()) {
            statoBagaglioComboBox.addItem(stato);
        }

        // Listener pulsanti
        aggiungiBagaglioButton.addActionListener(e -> aggiungiBagaglio());
        rimuoviBagaglioButton.addActionListener(e -> rimuoviBagaglio());
        modificaBagaglioButton.addActionListener(e -> modificaBagaglio());
    }

    private void aggiungiBagaglio() {
        String codice = codiceBagaglioTextField.getText().trim();
        StatoBagaglio stato = (StatoBagaglio) statoBagaglioComboBox.getSelectedItem();

        if (codice.isEmpty() || stato == null) {
            JOptionPane.showMessageDialog(panelBagaglio, "Compila tutti i campi!");
            return;
        }

        boolean added = controller.aggiungiBagaglio(codice, stato);
        if (added) {
            JOptionPane.showMessageDialog(panelBagaglio, "Bagaglio aggiunto con successo!");
            codiceBagaglioTextField.setText("");
            areaAmmGUI.caricaTuttiBagagli(); // aggiorna tabella principale
        } else {
            JOptionPane.showMessageDialog(panelBagaglio, "Esiste già un bagaglio con questo codice!", "Duplicato", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void modificaBagaglio() {
        String codice = codiceBagaglioTextField.getText().trim();
        StatoBagaglio nuovoStato = (StatoBagaglio) statoBagaglioComboBox.getSelectedItem();

        if (codice.isEmpty() || nuovoStato == null) {
            JOptionPane.showMessageDialog(panelBagaglio, "Inserisci codice e stato!");
            return;
        }

        Bagaglio daAggiornare = controller.getBagagli().stream()
                .filter(b -> b.getCodUnivoco().equalsIgnoreCase(codice))
                .findFirst().orElse(null);

        if (daAggiornare == null) {
            JOptionPane.showMessageDialog(panelBagaglio, "Bagaglio non trovato.");
            return;
        }

        daAggiornare.setStato(nuovoStato);

        boolean ok = controller.aggiornaBagaglio(daAggiornare);
        if (ok) {
            JOptionPane.showMessageDialog(panelBagaglio, "Bagaglio aggiornato!");
            codiceBagaglioTextField.setText("");
            areaAmmGUI.caricaTuttiBagagli();
        } else {
            JOptionPane.showMessageDialog(panelBagaglio, "Aggiornamento fallito.", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rimuoviBagaglio() {
        String codice = codiceBagaglioTextField.getText().trim();

        if (codice.isEmpty()) {
            JOptionPane.showMessageDialog(panelBagaglio, "Inserisci il codice del bagaglio.");
            return;
        }

        int conferma = JOptionPane.showConfirmDialog(panelBagaglio,
                "Vuoi eliminare il bagaglio " + codice + "?",
                "Conferma eliminazione",
                JOptionPane.YES_NO_OPTION);

        if (conferma != JOptionPane.YES_OPTION) return;

        boolean removed = controller.rimuoviBagaglio(codice);
        if (removed) {
            JOptionPane.showMessageDialog(panelBagaglio, "Bagaglio rimosso con successo!");
            codiceBagaglioTextField.setText("");
            areaAmmGUI.caricaTuttiBagagli();
        } else {
            JOptionPane.showMessageDialog(panelBagaglio, "Nessun bagaglio trovato con quel codice.", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    public JPanel getPanelBagaglio() {
        return panelBagaglio;
    }
}