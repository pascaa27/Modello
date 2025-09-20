package gui;

import controller.Controller;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class TabellaOrarioGUI {
    private JPanel tabellaOrarioPanel;   // generato dal .form
    private JTable tabellaOrarioTable;   // generato dal .form
    private final Controller controller;

    private static final String[] COLONNE = {
            "Numero Volo",
            "Compagnia",
            "Stato",
            "Data",
            "Orario previsto",
            "Aeroporto Destinazione",
            "GATE",
            "ARRIVO/PARTENZA"
    };

    public TabellaOrarioGUI(Controller controller) {
        this.controller = controller;
        inizializzaModello();
        // Carica SUBITO i voli reali presenti nel Controller
        aggiornaVoli(controller.tuttiVoli());
    }

    private void inizializzaModello() {
        if (tabellaOrarioTable == null) {
            System.err.println("ERRORE: tabellaOrarioTable è null (binding errato nel form).");
            tabellaOrarioTable = new JTable();
            tabellaOrarioPanel.setLayout(new java.awt.BorderLayout());
            tabellaOrarioPanel.add(new JScrollPane(tabellaOrarioTable), java.awt.BorderLayout.CENTER);
        }
        DefaultTableModel model = new DefaultTableModel(COLONNE, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabellaOrarioTable.setModel(model);
        tabellaOrarioTable.setFillsViewportHeight(true);
        tabellaOrarioTable.setAutoCreateRowSorter(true);
    }

    public JPanel getPanel() {
        return tabellaOrarioPanel;
    }

    /**
     * Aggiorna (sostituisce) tutte le righe della tabella.
     * @param righe lista di Object[] ottenute da controller.tuttiVoli() o controller.ricercaVoli(...)
     */
    public void aggiornaVoli(List<Object[]> righe) {
        DefaultTableModel model = (DefaultTableModel) tabellaOrarioTable.getModel();
        model.setRowCount(0);
        if (righe != null) {
            for (Object[] r : righe) {
                if (r != null) model.addRow(r);
            }
        }
    }

    // Se in futuro vorrai aggiornare in modo incrementale, potrai aggiungere metodi tipo:
    // public void aggiungiRiga(Object[] r) { ... }
}
