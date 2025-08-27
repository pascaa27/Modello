package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import controller.Controller;
import java.util.List;

public class TabellaOrarioGUI {
    private JPanel tabellaOrarioPanel;
    private JTable tabellaOrarioTable;
    private Controller controller;

    private static final String[] COLONNE = {"Numero Volo", "Compagnia", "Stato", "Aeroporto", "Data"};

    public TabellaOrarioGUI(Controller controller) {
        this.controller = controller;
        // Modello vuoto iniziale
        DefaultTableModel model = new DefaultTableModel(COLONNE, 0);
        tabellaOrarioTable.setModel(model);
    }

    // Generato da IntelliJ per il .form
    public void createUIComponents() {
        tabellaOrarioTable = new JTable();
    }

    public JPanel getPanel() {
        return tabellaOrarioPanel;
    }

    public JTable getTable() {
        return tabellaOrarioTable;
    }

    public void aggiornaVoli(List<Object[]> righe) {
        DefaultTableModel model = (DefaultTableModel) tabellaOrarioTable.getModel();
        model.setRowCount(0);
        if (righe != null) {
            for (Object[] r : righe) {
                model.addRow(r);
            }
        }
    }
}