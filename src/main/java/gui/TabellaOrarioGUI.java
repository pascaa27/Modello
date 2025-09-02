package gui;

import controller.Controller;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class TabellaOrarioGUI {
    private JPanel tabellaOrarioPanel;   // creato dal .form
    private JTable tabellaOrarioTable;   // creato dal .form (dentro lo JScrollPane)
    private final Controller controller;

    private static final String[] COLONNE = {"Numero Volo", "Compagnia", "Stato", "Data", "Orario previsto", "Aeroporto Destinazione", "GATE", "ARRIVO/PARTENZA"};

    private static final Object[][] DATI_ESEMPIO = {
            {"AZ123", "ITA Airways", "PROGRAMMATO", "2025-09-05", "08:15", "MIL", "3", "in arrivo"},
            {"FR987", "Ryanair", "IMBARCO", "2025-09-05", "08:40", "BAR", "21", "in arrivo"},
            {"LH455", "Lufthansa", "DECOLLATO", "2025-09-05", "08:55", "MAD", "15", "in partenza"},
            {"U23610", "easyJet", "CANCELLATO", "2025-09-05", "09:05", "LDN", "9", "in arrivo"},
            {"AF101", "Air France", "INRITARDO", "2025-09-05", "09:20", "MYK", "5", "in partenza"},
            {"EK092", "Emirates", "ATTERRATO", "2025-09-05", "09:35", "PAR", "12", "in partenza"}
    };

    public TabellaOrarioGUI(Controller controller) {
        this.controller = controller;
        inizializzaModello();
        caricaDatiEsempio(); // togli se non vuoi i dati subito
    }

    private void inizializzaModello() {
        if (tabellaOrarioTable == null) {
            System.err.println("ERRORE: tabellaOrarioTable è null (binding errato nel form).");
            // fallback d’emergenza (non dovrebbe accadere se il form è a posto):
            tabellaOrarioTable = new JTable();
            tabellaOrarioPanel.setLayout(new java.awt.BorderLayout());
            tabellaOrarioPanel.add(new JScrollPane(tabellaOrarioTable), java.awt.BorderLayout.CENTER);
        }
        DefaultTableModel model = new DefaultTableModel(COLONNE, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabellaOrarioTable.setModel(model);
        tabellaOrarioTable.setFillsViewportHeight(true);
    }

    public JPanel getPanel() {
        return tabellaOrarioPanel;
    }

    public void caricaDatiEsempio() {
        DefaultTableModel model = (DefaultTableModel) tabellaOrarioTable.getModel();
        model.setRowCount(0);
        for (Object[] r : DATI_ESEMPIO) model.addRow(r);
    }

    public void aggiornaVoli(List<Object[]> righe) {
        DefaultTableModel model = (DefaultTableModel) tabellaOrarioTable.getModel();
        model.setRowCount(0);
        if (righe != null) {
            for (Object[] r : righe) {
                if (r != null && r.length >= COLONNE.length) model.addRow(r);
            }
        }
    }

    public void aggiungiVolo(String numero, String compagnia, String stato, String data) {
        ((DefaultTableModel) tabellaOrarioTable.getModel())
                .addRow(new Object[]{numero, compagnia, stato, data});
    }
}