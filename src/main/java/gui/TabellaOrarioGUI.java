package gui;

import controller.Controller;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

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
                int w = getWidth(), h = getHeight();
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
        if (righe != null) {
            for (Object[] r : righe) {
                if (r == null) continue;

                if (r.length >= 10) {
                    model.addRow(r);
                    continue;
                }

                if (r.length >= 9) {
                    String numeroVolo = safeStr(r[0]);
                    String compagnia = safeStr(r[1]);
                    String stato = safeStr(r[2]);
                    String data = safeStr(r[3]);
                    String orarioPrevisto = safeStr(r[4]);
                    String orarioStimato = safeStr(r[5]);
                    String aeroportoAltroEstremo = safeStr(r[6]);
                    String gate = safeStr(r[7]);
                    String direzione = safeStr(r[8]); // "in arrivo" | "in partenza"

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

                    Object[] nuovo = new Object[] {
                            numeroVolo,
                            compagnia,
                            stato,
                            data,
                            orarioPrevisto,
                            orarioStimato,
                            origine,
                            destinazione,
                            gate,
                            direzione
                    };
                    model.addRow(nuovo);
                } else {
                    Object[] nuovo = new Object[10];
                    for (int i = 0; i < Math.min(r.length, nuovo.length); i++) {
                        nuovo[i] = r[i];
                    }
                    model.addRow(nuovo);
                }
            }
        }
    }

    private static String safeStr(Object o) {
        return o == null ? "" : String.valueOf(o);
    }

    private static boolean equalsIgnoreCaseTrim(String a, String b) {
        if (a == null) return b == null;
        return a.trim().equalsIgnoreCase(b);
    }
}