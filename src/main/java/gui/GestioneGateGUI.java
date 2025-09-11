package gui;

import controller.Controller;
import model.Gate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class GestioneGateGUI {
    private JPanel panelGate;
    private JTextField numeroGateTextField;
    private JButton aggiungiGateButton;
    private JButton eliminaGateButton;
    private JTable gateEsistentiTable;
    private JLabel numeroGate;
    private final Controller controller;

    public GestioneGateGUI(Controller controller) {
        this.controller = controller;

        // Listener pulsanti
        aggiungiGateButton.addActionListener(e -> aggiungiGate());
        eliminaGateButton.addActionListener(e -> eliminaGate());

        // Inizializza tabella (una sola colonna per i gate)
        setupTabellaGate();
        refreshTabellaGate();
    }

    private void setupTabellaGate() {
        if(gateEsistentiTable != null) {
            gateEsistentiTable.setModel(new DefaultTableModel(new Object[]{"Numero Gate"}, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }  //rendo la tabella non editabile
            });
            gateEsistentiTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
    }

    private void refreshTabellaGate() {
        if(gateEsistentiTable == null) return;
        DefaultTableModel model = (DefaultTableModel) gateEsistentiTable.getModel();
        model.setRowCount(0);
        List<Gate> gates = controller.getGates();
        for(Gate g : gates) {
            model.addRow(new Object[]{g.getNumero()});
        }
    }

    private void aggiungiGate() {
        String numeroStr = numeroGateTextField.getText();

        if(numeroStr == null || numeroStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(panelGate, "Inserisci il numero del gate!");
            return;
        }

        int numero;
        try {
            numero = Integer.parseInt(numeroStr.trim());
        } catch(NumberFormatException e) {
            JOptionPane.showMessageDialog(panelGate, "Il numero del gate deve essere un intero!");
            return;
        }

        boolean added = controller.aggiungiGate(numero);
        if(added) {
            JOptionPane.showMessageDialog(panelGate, "Gate aggiunto con successo!");
            numeroGateTextField.setText("");
            refreshTabellaGate();
        } else {
            JOptionPane.showMessageDialog(panelGate, "Esiste già un gate con questo numero!", "Duplicato", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void eliminaGate() {
        // Possibilità 1: usare il numero nel text field
        String numeroStr = numeroGateTextField.getText();
        Integer numero = null;

        // Se c'è una riga selezionata nella tabella, uso quella
        if(gateEsistentiTable != null && gateEsistentiTable.getSelectedRow() >= 0) {
            Object val = gateEsistentiTable.getValueAt(gateEsistentiTable.getSelectedRow(), 0);
            if(val instanceof Integer) numero = (Integer) val;
            else if(val instanceof String) {
                try { numero = Integer.parseInt((String) val); } catch (Exception ignored) {}
            }
        }

        // Se non c'è selezione, provo a prendere dal text field
        if(numero == null) {
            if(numeroStr == null || numeroStr.trim().isEmpty()) {
                JOptionPane.showMessageDialog(panelGate, "Seleziona un gate dalla tabella o inserisci il numero nel campo.");
                return;
            }
            try {
                numero = Integer.parseInt(numeroStr.trim());
            } catch(NumberFormatException e) {
                JOptionPane.showMessageDialog(panelGate, "Il numero del gate deve essere un intero.");
                return;
            }
        }

        int conferma = JOptionPane.showConfirmDialog(panelGate,
                "Vuoi eliminare il gate " + numero + "?",
                "Conferma eliminazione",
                JOptionPane.YES_NO_OPTION);

        if(conferma != JOptionPane.YES_OPTION) return;

        boolean removed = controller.eliminaGate(numero);
        if(removed) {
            JOptionPane.showMessageDialog(panelGate, "Gate eliminato.");
            refreshTabellaGate();
        } else {
            JOptionPane.showMessageDialog(panelGate, "Nessun gate trovato con quel numero.", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    public JPanel getPanelGate() {
        return panelGate;
    }
}