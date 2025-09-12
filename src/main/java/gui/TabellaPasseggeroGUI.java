package gui;

import controller.Controller;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;


public class TabellaPasseggeroGUI {
    private JPanel tabellaPasseggeroPanel;
    private JTable tabellaPasseggeroTable;

    private DefaultTableModel model;
    private Controller controller;

    private static final String[] COLONNE = {"Nome", "Cognome", "Codice Fiscale", "Numero Volo", "Numero Prenotazione", "Posto assegnato", "Stato Prenotazione"};

    public TabellaPasseggeroGUI(Controller controller) {
        this.controller = controller;
        $$$setupUI$$$(); // (se IntelliJ già la inserisce, non duplicarla)
        inizializzaModel();
    }

     //Costruttore vuoto se vuoi istanziarla senza controller
    public TabellaPasseggeroGUI() {
        $$$setupUI$$$();
        inizializzaModel();
    }


    private void inizializzaModel() {
        model = new DefaultTableModel(COLONNE, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabellaPasseggeroTable.setModel(model);
        tabellaPasseggeroTable.setAutoCreateRowSorter(true);
    }

    public void setRows(List<Object[]> rows) {
        model.setRowCount(0);
        if(rows != null) {
            for(Object[] r : rows) {
                model.addRow(r);
            }
        }
    }

    public JPanel getPanel() {
        return tabellaPasseggeroPanel;
    }

    public JTable getTable() {
        return tabellaPasseggeroTable;
    }

    // Metodo creato automaticamente dal Designer (placeholder se non presente):
    private void $$$setupUI$$$() {
    }

    // Se vuoi usare il Designer per generare componenti custom, puoi usare createUIComponents().
    private void createUIComponents() {
        tabellaPasseggeroTable = new JTable();
    }
}