package gui;

import javax.swing.*;
import controller.Controller;

public class TabellaOrarioGUI {
    private JPanel tabellaOrarioPanel;
    private JTable tabellaOrarioTable;
    private Controller controller;

    public TabellaOrarioGUI(Controller controller) {
        this.controller = controller;
        tabellaOrarioPanel = new JPanel();
        // Dati di esempio, da sostituire con chiamata al Controller
        String[] colonne = {"Codice Volo", "Compagnia", "Orario", "Stato"};
        Object[][] dati = {
                {"AZ123", "Alitalia", "10:30", "In orario"},
                {"LH456", "Lufthansa", "11:15", "Ritardo"},
        };

        tabellaOrarioTable = new JTable(dati, colonne);
        JScrollPane scrollPane = new JScrollPane(tabellaOrarioTable);
        tabellaOrarioPanel.add(scrollPane);
    }

    public JPanel getPanel() {
        return tabellaOrarioPanel;
    }

}
