package gui;

import controller.Controller;
import model.Bagaglio;
import model.StatoBagaglio;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class GestioneBagagliGUI {
    private JPanel panelBagaglio;
    private JTextField codiceBagaglioTextField;
    private JComboBox<StatoBagaglio> statoBagaglioComboBox;
    private JButton aggiungiBagaglioButton;
    private JButton rimuoviBagaglioButton;
    private JTable bagagliTable;
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

        // Listener modifica
        modificaBagaglioButton.addActionListener(e -> {
            int row = bagagliTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(panelBagaglio, "Seleziona un bagaglio.");
                return;
            }
            String codice = bagagliTable.getValueAt(row, 0).toString();
            StatoBagaglio nuovoStato = (StatoBagaglio) statoBagaglioComboBox.getSelectedItem();
            if (nuovoStato == null) {
                JOptionPane.showMessageDialog(panelBagaglio, "Seleziona uno stato.");
                return;
            }
            // Trova in cache
            Bagaglio daAggiornare = controller.getBagagli().stream()
                    .filter(b -> b.getCodUnivoco().equalsIgnoreCase(codice))
                    .findFirst().orElse(null);
            if (daAggiornare == null) {
                JOptionPane.showMessageDialog(panelBagaglio, "Bagaglio non trovato in cache.");
                return;
            }
            daAggiornare.setStato(nuovoStato);
            // opzionale: daAggiornare.setPesoKg(...);

            boolean ok = controller.aggiornaBagaglio(daAggiornare);
            if (ok) {
                JOptionPane.showMessageDialog(panelBagaglio, "Bagaglio aggiornato!");
                refreshTabellaBagagli();
                areaAmmGUI.caricaTuttiBagagli();
            } else {
                JOptionPane.showMessageDialog(panelBagaglio, "Aggiornamento fallito.", "Errore", JOptionPane.ERROR_MESSAGE);
            }
        });

        // IMPORTANTE: NON ricreare la JTable. Usa quella del form.
        // bagagliTable = new JTable();  // <- RIMOSSO

        setupTabellaBagagli();
        refreshTabellaBagagli();
    }

    private void setupTabellaBagagli() {
        if (bagagliTable != null) {
            bagagliTable.setModel(new DefaultTableModel(new Object[]{"Codice Bagaglio", "Stato"}, 0) {
                @Override
                public boolean isCellEditable(int r, int c) { return false; }
            });
            bagagliTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
    }

    private void refreshTabellaBagagli() {
        if (bagagliTable == null) return;
        DefaultTableModel model = (DefaultTableModel) bagagliTable.getModel();
        model.setRowCount(0);
        List<Bagaglio> bagagli = controller.getBagagli();
        for (Bagaglio b : bagagli) {
            model.addRow(new Object[]{b.getCodUnivoco(), b.getStato()});
        }
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
            refreshTabellaBagagli();
            areaAmmGUI.caricaTuttiBagagli(); // aggiorna tabella principale
        } else {
            JOptionPane.showMessageDialog(panelBagaglio, "Esiste già un bagaglio con questo codice!", "Duplicato", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void rimuoviBagaglio() {
        String codice = codiceBagaglioTextField.getText().trim();

        if (bagagliTable != null && bagagliTable.getSelectedRow() >= 0) {
            codice = bagagliTable.getValueAt(bagagliTable.getSelectedRow(), 0).toString();
        }

        if (codice.isEmpty()) {
            JOptionPane.showMessageDialog(panelBagaglio, "Seleziona un bagaglio dalla tabella o inserisci il codice.");
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
            refreshTabellaBagagli();
            areaAmmGUI.caricaTuttiBagagli(); // aggiorna tabella principale
        } else {
            JOptionPane.showMessageDialog(panelBagaglio, "Nessun bagaglio trovato con quel codice.", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    public JPanel getPanelBagaglio() {
        return panelBagaglio;
    }
}