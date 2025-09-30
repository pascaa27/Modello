package gui;

import controller.Controller;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TabellaPasseggeroGUI {
    private static final String FONT_FAMILY = "Segoe UI";

    private JPanel tabellaPasseggeroPanel;
    private JTable tabellaPasseggeroTable;

    private DefaultTableModel model;
    private Controller controller;

    private static final String[] COLONNE = {
            "Nome",
            "Cognome",
            "Email",
            "Codice Fiscale",
            "Numero Volo",
            "Numero Prenotazione",
            "Posto assegnato",
            "Stato Prenotazione"
    };

    // Palette colori per coerenza con l'app
    private final Color mainGradientStart = new Color(30, 87, 153);
    private final Color mainGradientEnd   = new Color(125, 185, 232);
    private final Color tableHeaderColor  = new Color(60, 130, 200);
    private final Color tableRowColor     = new Color(245, 249, 255);

    public TabellaPasseggeroGUI(Controller controller) {
        this.controller = controller;
        inizializzaPanel();
        inizializzaModel();
    }

    public TabellaPasseggeroGUI() {
        inizializzaPanel();
        inizializzaModel();
    }

    private void inizializzaModel() {
        model = new DefaultTableModel(COLONNE, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabellaPasseggeroTable.setModel(model);
        tabellaPasseggeroTable.setAutoCreateRowSorter(true);

        // Stile tabella
        tabellaPasseggeroTable.getTableHeader().setFont(new Font(FONT_FAMILY, Font.BOLD, 14));
        tabellaPasseggeroTable.getTableHeader().setBackground(tableHeaderColor);
        tabellaPasseggeroTable.getTableHeader().setForeground(Color.WHITE);
        tabellaPasseggeroTable.setFont(new Font(FONT_FAMILY, Font.PLAIN, 13));
        tabellaPasseggeroTable.setRowHeight(26);
        tabellaPasseggeroTable.setBackground(tableRowColor);
        tabellaPasseggeroTable.setSelectionBackground(new Color(190, 215, 250));
        tabellaPasseggeroTable.setSelectionForeground(mainGradientStart);
    }

    private void inizializzaPanel() {
        tabellaPasseggeroTable = new JTable();
        tabellaPasseggeroPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, mainGradientStart, 0, h, mainGradientEnd);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        tabellaPasseggeroPanel.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(tabellaPasseggeroTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        tabellaPasseggeroPanel.add(scrollPane, BorderLayout.CENTER);
    }

    public void setRows(List<Object[]> rows) {
        model.setRowCount(0);
        if (rows != null) {
            for (Object[] r : rows) {
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
}