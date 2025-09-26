package gui;

import controller.Controller;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
     * Supporta sia righe "vecchie" da 9 colonne (senza "Aeroporto di origine")
     * sia righe già "nuove" da 10 colonne.
     *
     * Atteso ordine vecchio (9 col):
     * 0:Numero Volo, 1:Compagnia, 2:Stato, 3:Data, 4:Orario previsto, 5:Orario stimato,
     * 6:Aeroporto (l'altro estremo), 7:GATE, 8:ARRIVO/PARTENZA
     *
     * Nuovo ordine (10 col):
     * 0:Numero Volo, 1:Compagnia, 2:Stato, 3:Data, 4:Orario previsto, 5:Orario stimato,
     * 6:Aeroporto di origine, 7:Aeroporto Destinazione, 8:GATE, 9:ARRIVO/PARTENZA
     *
     * Regola:
     * - Se ARRIVO/PARTENZA == "in partenza":
     *      Origine = "NAP"
     *      Destinazione = Aeroporto (colonna 6 del vecchio formato)
     * - Se ARRIVO/PARTENZA == "in arrivo":
     *      Origine = Aeroporto (colonna 6 del vecchio formato)
     *      Destinazione = "NAP"
     *
     * @param righe lista di Object[] ottenute da controller.tuttiVoli() o controller.ricercaVoli(...)
     */
    public void aggiornaVoli(List<Object[]> righe) {
        DefaultTableModel model = (DefaultTableModel) tabellaOrarioTable.getModel();
        model.setRowCount(0);
        if (righe != null) {
            for (Object[] r : righe) {
                if (r == null) continue;

                if (r.length >= 10) {
                    // Già nel nuovo formato (o più lungo): aggiungo così com'è
                    model.addRow(r);
                    continue;
                }

                if (r.length >= 9) {
                    // Vecchio formato: calcolo "origine" e "destinazione"
                    String numeroVolo = safeStr(r[0]);
                    String compagnia = safeStr(r[1]);
                    String stato = safeStr(r[2]);
                    String data = safeStr(r[3]);
                    String orarioPrevisto = safeStr(r[4]);
                    String orarioStimato = safeStr(r[5]);
                    String aeroportoAltroEstremo = safeStr(r[6]); // nel vecchio formato è l'altro estremo
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
                        // Direzione non riconosciuta: non rompiamo la UI, mettiamo i valori originali come best-effort
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
                    // Formato inaspettato: provo ad adattare in modo robusto
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

    // Se in futuro vorrai aggiornare in modo incrementale, potrai aggiungere metodi tipo:
    // public void aggiungiRiga(Object[] r) { ... }
}