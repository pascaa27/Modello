package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import controller.Controller;

public class TabellaOrarioGUI {
    private JPanel tabellaOrarioPanel;   // generato dal .form
    private JTable tabellaOrarioTable;   // generato dal .form
    private final Controller controller;

    // Nuova colonna "Aeroporto di origine" aggiunta, lasciando inalterate le altre.
    private static final String[] COLONNE = {
            "Numero Volo",
            "Compagnia",
            "Stato",
            "Data",
            "Orario previsto",
            "Orario stimato",
            "Aeroporto di origine",
            "Aeroporto Destinazione",
            "GATE",
            "ARRIVO/PARTENZA"
    };

    // Palette colori per coerenza con l'app
    private final Color mainGradientStart = new Color(30, 87, 153);
    private final Color mainGradientEnd   = new Color(125, 185, 232);
    private final Color tableHeaderColor  = new Color(60, 130, 200);
    private final Color tableRowColor     = new Color(245, 249, 255);

    public TabellaOrarioGUI(Controller controller) {
        this.controller = controller;
        inizializzaModello();
        inizializzaPanel();
        aggiornaVoli(controller.tuttiVoli());
    }

    private void inizializzaModello() {
        if (tabellaOrarioTable == null) {
            tabellaOrarioTable = new JTable();
        }
        DefaultTableModel model = new DefaultTableModel(COLONNE, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabellaOrarioTable.setModel(model);
        tabellaOrarioTable.setFillsViewportHeight(true);
        tabellaOrarioTable.setAutoCreateRowSorter(true);

        // Stile tabella
        tabellaOrarioTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabellaOrarioTable.getTableHeader().setBackground(tableHeaderColor);
        tabellaOrarioTable.getTableHeader().setForeground(Color.WHITE);
        tabellaOrarioTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabellaOrarioTable.setRowHeight(26);
        tabellaOrarioTable.setBackground(tableRowColor);
        tabellaOrarioTable.setSelectionBackground(new Color(190, 215, 250));
        tabellaOrarioTable.setSelectionForeground(mainGradientStart);
    }

    private void inizializzaPanel() {
        tabellaOrarioPanel = new JPanel() {
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
        tabellaOrarioPanel.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(tabellaOrarioTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        tabellaOrarioPanel.add(scrollPane, BorderLayout.CENTER);
    }

    public JPanel getPanel() {
        return tabellaOrarioPanel;
    }

    public void aggiornaVoli(List<Object[]> righe) {
        DefaultTableModel model = (DefaultTableModel) tabellaOrarioTable.getModel();
        model.setRowCount(0);
        if (righe == null) return;

        for (Object[] r : righe) {
            Object[] normalized = normalizeRow(r);
            model.addRow(normalized);
        }
    }

    // Normalizza una riga alla struttura da 10 colonne richiesta dalla tabella
    private Object[] normalizeRow(Object[] r) {
        if (r == null) {
            return new Object[10];
        }

        // Caso già completo
        if (r.length >= 10) {
            return Arrays.copyOf(r, 10);
        }

        // Caso con 9 colonne (senza "Aeroporto di origine"): deriviamo origine/destinazione da direzione
        if (r.length >= 9) {
            return normalizeRowFrom9(r);
        }

        // Caso generico: copiamo i valori disponibili e riempiamo il resto con null
        Object[] nuovo = Arrays.copyOf(r, 10);
        return nuovo;
    }

    private Object[] normalizeRowFrom9(Object[] r) {
        String numeroVolo = safeStr(r[0]);
        String compagnia = safeStr(r[1]);
        String stato = safeStr(r[2]);
        String data = safeStr(r[3]);
        String orarioPrevisto = safeStr(r[4]);
        String orarioStimato = safeStr(r[5]);
        String aeroportoAltroEstremo = safeStr(r[6]);
        String gate = safeStr(r[7]);
        String direzione = safeStr(r[8]); // "in arrivo" | "in partenza"

        String[] od = computeOriginDest(aeroportoAltroEstremo, direzione);

        return new Object[] {
                numeroVolo,
                compagnia,
                stato,
                data,
                orarioPrevisto,
                orarioStimato,
                od[0],            // origine
                od[1],            // destinazione
                gate,
                direzione
        };
    }

    private String[] computeOriginDest(String aeroportoAltroEstremo, String direzione) {
        String origine;
        String destinazione;

        if (equalsIgnoreCaseTrim(direzione, "in partenza")) {
            origine = "NAP";
            destinazione = aeroportoAltroEstremo;
        } else if (equalsIgnoreCaseTrim(direzione, "in arrivo")) {
            origine = aeroportoAltroEstremo;
            destinazione = "NAP";
        } else {
            origine = aeroportoAltroEstremo;
            destinazione = aeroportoAltroEstremo;
        }
        return new String[] { origine, destinazione };
    }

    private static String safeStr(Object o) {
        return o == null ? "" : String.valueOf(o);
    }

    private static boolean equalsIgnoreCaseTrim(String a, String b) {
        if (a == null) return b == null;
        return a.trim().equalsIgnoreCase(b);
    }
}