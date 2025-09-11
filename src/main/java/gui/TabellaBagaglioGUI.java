package gui;

import controller.Controller;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;


public class TabellaBagaglioGUI {
    private JPanel tabellaBagaglioPanel;
    private JTable tabellaBagaglioTable;

    private DefaultTableModel model;
    private Controller controller;

    private static final String[] COLONNE = {"Codice bagaglio", "stato"};

    public TabellaBagaglioGUI(Controller controller) {
        this.controller = controller;
        inizializzaModel();
    }

    private void inizializzaModel() {
        model = new DefaultTableModel(COLONNE, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabellaBagaglioTable = new JTable(model);
        tabellaBagaglioTable.setAutoCreateRowSorter(true);
    }

    public void setRows(List<Object[]> rows) {
        if(model == null) return;
        model.setRowCount(0);
        if(rows != null) {
            for(Object[] r : rows) model.addRow(r);
        }
    }

    /**
     * Carica tutti i bagagli dal controller (richiede metodo tuttiBagagliRows()).
     */
    public void caricaTuttiBagagli() {
        if(controller == null) return;
        try {
            List<Object[]> rows = controller.tuttiBagagliRows();
            setRows(rows);
        } catch(Exception ignored) {
        }
    }

    /**
     * Aggiunge una singola riga.
     */
    public void addRow(Object[] row) {
        if (model != null && row != null) model.addRow(row);
    }

    /**
     * Restituisce l'array di valori della riga selezionata (null se nessuna selezione).
     */
    public Object[] getSelectedRow() {
        if(tabellaBagaglioTable == null) return null;
        int viewRow = tabellaBagaglioTable.getSelectedRow();
        if(viewRow < 0) return null;
        int modelRow = tabellaBagaglioTable.convertRowIndexToModel(viewRow);
        Object[] out = new Object[model.getColumnCount()];
        for(int c = 0; c < model.getColumnCount(); c++) {
            out[c] = model.getValueAt(modelRow, c);
        }
        return out;
    }

    public JPanel getPanel() {
        return tabellaBagaglioPanel;
    }

    public JTable getTable() {
        return tabellaBagaglioTable;
    }

    public DefaultTableModel getModel() {
        return model;
    }
}