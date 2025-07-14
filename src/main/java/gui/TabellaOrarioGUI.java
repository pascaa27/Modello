package gui;

import javax.swing.*;
import controller.Controller;

public class TabellaOrarioGUI {
    private JPanel tabellaOrarioPanel;
    private JTable tabellaOrarioTable;
    private Controller controller;

    public TabellaOrarioGUI(Controller controller) {
        this.controller = controller;

        String[] colonne = {"Codice Volo", "Compagnia", "Orario", "Stato"};
        Object[][] dati = {
                {"AZ123", "Alitalia", "10:30", "In orario"},
                {"LH456", "Lufthansa", "11:15", "Ritardo"}
        };

        tabellaOrarioTable.setModel(new javax.swing.table.DefaultTableModel(dati, colonne));
    }

    // Metodo richiesto da IntelliJ quando si usa Custom Create su componenti nel .form
    public void createUIComponents() {
        tabellaOrarioTable = new JTable();
    }

    public JPanel getPanel() {
        return tabellaOrarioPanel;
    }
}