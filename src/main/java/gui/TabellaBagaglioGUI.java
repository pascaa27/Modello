package gui;

import controller.Controller;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe che gestisce la tabella dei bagagli nell'interfaccia grafica.
 */
public class TabellaBagaglioGUI {
    private static final Logger LOGGER = Logger.getLogger(TabellaBagaglioGUI.class.getName());
    private static final String[] COLONNE = {"Codice bagaglio", "Stato"};
    private static final Object[] EMPTY_ROW = new Object[0];

    private JPanel tabellaBagaglioPanel;
    private JTable tabellaBagaglioTable;
    private DefaultTableModel model;
    private Controller controller;

    // Palette colori per coerenza con l'app
    private final Color mainGradientStart = new Color(30, 87, 153);
    private final Color mainGradientEnd   = new Color(125, 185, 232);
    private final Color tableHeaderColor  = new Color(60, 130, 200);
    private final Color tableRowColor     = new Color(245, 249, 255);

    /**
     * Costruttore che inizializza la tabella dei bagagli e il relativo pannello grafico.
     *
     * @param controller il {@link Controller} che fornisce i dati da visualizzare.
     */
    public TabellaBagaglioGUI(Controller controller) {
        this.controller = controller;
        inizializzaModel();   // crea la tabella e il model
        inizializzaPanel();   // crea il pannello con gradient e la tabella
    }

    /**
     * Inizializza il modello dati della tabella bagagli e configura lo stile della JTable.
     * <p>
     * Il modello utilizza le colonne definite in {@link #COLONNE},
     * e rende le celle non editabili sovrascrivendo {@code isCellEditable()}.
     * </p>
     * Inoltre, viene abilitato l’ordinamento automatico delle righe.
     */
    private void inizializzaModel() {
        model = new DefaultTableModel(COLONNE, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabellaBagaglioTable = new JTable(model);
        tabellaBagaglioTable.setAutoCreateRowSorter(true);

        // Stile tabella
        tabellaBagaglioTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabellaBagaglioTable.getTableHeader().setBackground(tableHeaderColor);
        tabellaBagaglioTable.getTableHeader().setForeground(Color.WHITE);
        tabellaBagaglioTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabellaBagaglioTable.setRowHeight(26);
        tabellaBagaglioTable.setBackground(tableRowColor);
        tabellaBagaglioTable.setSelectionBackground(new Color(190, 215, 250));
        tabellaBagaglioTable.setSelectionForeground(mainGradientStart);
    }

    private void inizializzaPanel() {
        tabellaBagaglioPanel = new JPanel() {
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
        tabellaBagaglioPanel.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(tabellaBagaglioTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        tabellaBagaglioPanel.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Imposta le righe della tabella, sovrascrivendo i dati precedenti.
     *
     * @param rows lista di array di oggetti, ciascuno rappresenta una riga con valori
     *             corrispondenti alle colonne definite in {@link #COLONNE}.
     */
    public void setRows(List<Object[]> rows) {
        if(model == null) return;
        model.setRowCount(0);
        if(rows != null) {
            for(Object[] r : rows) model.addRow(r);
        }
    }

    /**
     * Carica tutti i bagagli disponibili dal {@link Controller}.
     * <p>
     * In caso di errore, viene mostrata una tabella vuota e viene registrato un warning nei log.
     * </p>
     */
    public void caricaTuttiBagagli() {
        if(controller == null) return;
        try {
            List<Object[]> rows = controller.tuttiBagagliRows();
            setRows(rows);
        } catch(Exception e) {
            // Non interrompere l'UI: logga e mostra tabella vuota
            LOGGER.log(Level.WARNING, "Impossibile caricare i bagagli.", e);
            setRows(Collections.emptyList());
        }
    }

    /**
     * Aggiunge una riga alla tabella.
     *
     * @param row array di oggetti che rappresenta i valori di una nuova riga.
     */
    public void addRow(Object[] row) {
        if(model != null && row != null) model.addRow(row);
    }

    /**
     * Restituisce la riga attualmente selezionata nella tabella.
     *
     * @return array di oggetti che rappresenta i valori della riga selezionata,
     *         oppure un array vuoto se nessuna riga è selezionata.
     */
    public Object[] getSelectedRow() {
        if(tabellaBagaglioTable == null) return EMPTY_ROW;
        int viewRow = tabellaBagaglioTable.getSelectedRow();
        if(viewRow < 0) return EMPTY_ROW;
        int modelRow = tabellaBagaglioTable.convertRowIndexToModel(viewRow);
        Object[] out = new Object[model.getColumnCount()];
        for(int c = 0; c < model.getColumnCount(); c++) {
            out[c] = model.getValueAt(modelRow, c);
        }
        return out;
    }

    /**
     * Restituisce il pannello principale che contiene la tabella dei bagagli.
     *
     * @return il pannello Swing della tabella.
     */
    public JPanel getPanel() {
        return tabellaBagaglioPanel;
    }

    /**
     * Restituisce il componente JTable interno.
     *
     * @return la tabella Swing che mostra i bagagli.
     */
    public JTable getTable() {
        return tabellaBagaglioTable;
    }

    /**
     * Restituisce il modello dati della tabella.
     *
     * @return il {@link DefaultTableModel} associato alla tabella.
     */
    public DefaultTableModel getModel() {
        return model;
    }
}