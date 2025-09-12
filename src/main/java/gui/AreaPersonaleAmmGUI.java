package gui;

import javax.swing.*;
import controller.Controller;
import model.Amministratore;
import java.awt.*;
import java.util.Collections;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 * Versione modificata per integrare TabellaPasseggeroGUI come componente della tab "Passeggeri".
 */
public class AreaPersonaleAmmGUI {
    private JTextField nomeTextField;
    private JTextField cognomeTextField;
    private JTextField emailTextField;
    private JPanel areaPersonaleAmmPanel;
    private JButton mostraPasswordButton;
    private Controller controller;
    private JPasswordField passwordField;
    private JTabbedPane tabbedPane1;
    private JTextField numeroVoloTextField;
    private JTextField compagniaTextField;
    private JComboBox<String> statoComboBox;
    private JTextField dataTextField;
    private JTextField nomePasseggeroTextField;
    private JTextField cognomePasseggeroTextField;
    private JTextField numeroVoloPasseggeroTextField;
    private JTextField numeroPrenotazionePasseggeroTextField;
    private JButton cercaVoloButton;
    private JButton cercaPasseggeroButton;
    private JTextField codiceBagaglioTextField;
    private JTextField numeroVoloBagaglioTextField; // (non utilizzato attualmente nella ricerca bagagli)
    private JTextField nomePasseggeroBagaglioTextField; // (non utilizzato attualmente)
    private JComboBox<String> statoBagaglioComboBox;
    private JButton cercaBagaglioButton;
    private JButton gestioneVoliButton;
    private JButton gestionePrenotazioniButton;
    private JButton gestioneGateButton;
    private JTextField orarioTextField;
    private boolean passwordVisibile = false;
    private TabellaOrarioGUI tabellaOrarioGUI;
    private JTable risultatiRicercaTable;
    private JTable risultatiRicercaPasseggeroTable;

    // NUOVO: componente tabella passeggeri
    private TabellaPasseggeroGUI tabellaPasseggeroGUI;
    // NUOVO: tabella bagagli (manteniamo JTable diretta o potresti farne un pannello analogo)
    private JTable risultatiRicercaBagaglioTable;
    private JTextField aeroportoTextField;
    private JTextField gateTextField;
    private JComboBox arrivoPartenzaComboBox;
    private JButton tabellaOrarioButton;

    public AreaPersonaleAmmGUI(Controller controller, Amministratore amministratore) {
        this.controller = controller;

        areaPersonaleAmmPanel = new JPanel();
        areaPersonaleAmmPanel.setLayout(new BoxLayout(areaPersonaleAmmPanel, BoxLayout.Y_AXIS));

        // Dati amministratore
        JPanel datiPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        datiPanel.add(new JLabel("Nome:"));
        nomeTextField = new JTextField(amministratore.getNomeUtente());
        nomeTextField.setEditable(false);
        datiPanel.add(nomeTextField);

        datiPanel.add(new JLabel("Cognome:"));
        cognomeTextField = new JTextField(amministratore.getCognomeUtente());
        cognomeTextField.setEditable(false);
        datiPanel.add(cognomeTextField);

        datiPanel.add(new JLabel("Email:"));
        emailTextField = new JTextField(amministratore.getLogin());
        emailTextField.setEditable(false);
        datiPanel.add(emailTextField);

        datiPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(amministratore.getPassword());
        passwordField.setEditable(false);
        passwordField.setEchoChar('•');

        mostraPasswordButton = new JButton("Show");
        mostraPasswordButton.setPreferredSize(new Dimension(60, 28));
        mostraPasswordButton.setFocusPainted(false);
        mostraPasswordButton.setMargin(new Insets(2, 2, 2, 2));
        mostraPasswordButton.setBorderPainted(false);
        mostraPasswordButton.addActionListener(e -> mostraNascondiPassword());

        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        passwordPanel.add(mostraPasswordButton, BorderLayout.EAST);
        datiPanel.add(passwordPanel);

        areaPersonaleAmmPanel.add(datiPanel);


        // Tabs
        tabbedPane1 = new JTabbedPane();
        tabbedPane1.addTab("Voli", creaTabVoli());
        tabbedPane1.addTab("Passeggeri", creaTabPasseggeri());
        tabbedPane1.addTab("Bagagli", creaTabBagagli());

        areaPersonaleAmmPanel.add(tabbedPane1);

        JPanel bottoniPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        gestioneVoliButton = new JButton("Gestione Voli");
        gestionePrenotazioniButton = new JButton("Gestione Prenotazioni");
        gestioneGateButton = new JButton("Gestione Gate");
        tabellaOrarioButton = new JButton("Tabella Orario");
        bottoniPanel.add(tabellaOrarioButton);
        bottoniPanel.add(gestioneVoliButton);
        bottoniPanel.add(gestionePrenotazioniButton);
        bottoniPanel.add(gestioneGateButton);

        areaPersonaleAmmPanel.add(bottoniPanel);

        tabellaOrarioButton.addActionListener(e -> apriTabellaOrario());

        gestioneVoliButton.addActionListener(e -> {
            GestioneVoliGUI gestioneVoli = new GestioneVoliGUI(controller, this);
            JFrame frame = new JFrame("Gestione Voli");
            frame.setContentPane(gestioneVoli.getPanelDatiVolo());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });


        gestionePrenotazioniButton.addActionListener(e -> {
            GestionePrenotazioniGUI gestionePrenotazioni = new GestionePrenotazioniGUI(controller, amministratore);
            JFrame frame = new JFrame("Gestione Prenotazioni");
            frame.setContentPane(gestionePrenotazioni.getPanelPrenotazione());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        gestioneGateButton.addActionListener(e -> {
            GestioneGateGUI gestioneGate = new GestioneGateGUI(controller);
            JFrame frame = new JFrame("Gestione Gate");
            frame.setContentPane(gestioneGate.getPanelGate());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private void apriTabellaOrario() {
        TabellaOrarioGUI gui = new TabellaOrarioGUI(controller); // già carica dati
        JFrame frame = new JFrame("Tabella Orario");
        frame.setContentPane(gui.getPanel());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // ---- TAB VOLI ----
    private JPanel creaTabVoli() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        int GAP_COPPIE = 28;
        int GAP_LABEL_CAMPO = 4;

        JPanel ricercaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, GAP_COPPIE, 4));

        numeroVoloTextField = new JTextField(8);
        compagniaTextField = new JTextField(10);
        statoComboBox = new JComboBox<>(new String[]{"", "PROGRAMMATO", "IMBARCO", "DECOLLATO", "CANCELLATO", "INRITARDO", "ATTERRATO"});
        dataTextField = new JTextField(8);
        orarioTextField = new JTextField(6);
        aeroportoTextField = new JTextField(10);
        gateTextField = new JTextField(10);
        arrivoPartenzaComboBox = new JComboBox<>(new String[]{"", "In arrivo", "In partenza"}); // ✅ FIX

        ricercaPanel.add(creaPair("Numero volo:", numeroVoloTextField, GAP_LABEL_CAMPO));
        ricercaPanel.add(creaPair("Compagnia:", compagniaTextField, GAP_LABEL_CAMPO));
        ricercaPanel.add(creaPair("Stato:", statoComboBox, GAP_LABEL_CAMPO));
        ricercaPanel.add(creaPair("Data:", dataTextField, GAP_LABEL_CAMPO));
        ricercaPanel.add(creaPair("Orario:", orarioTextField, GAP_LABEL_CAMPO));
        ricercaPanel.add(creaPair("Aeroporto:", aeroportoTextField, GAP_LABEL_CAMPO));
        ricercaPanel.add(creaPair("Gate:", gateTextField, GAP_LABEL_CAMPO));
        ricercaPanel.add(creaPair("Arrivo/Partenza:", arrivoPartenzaComboBox, GAP_LABEL_CAMPO));

        // qui togli il vecchio aggiungi al ricercaPanel

        // pannello separato per il bottone
        cercaVoloButton = new JButton("Cerca");
        cercaVoloButton.addActionListener(e -> ricercaVoli());


        JButton resetFiltriButton = new JButton("Reset");
        resetFiltriButton.addActionListener(e -> {
            resetFiltriVoli();                       // pulisce i campi
            tabellaOrarioGUI.aggiornaVoli(Collections.emptyList()); // svuota la tabella
        });

        JButton tuttiButton = new JButton("Tutti");
        tuttiButton.addActionListener(e -> {
            List<Object[]> tutti = controller.tuttiVoli();
            tabellaOrarioGUI.aggiornaVoli(tutti);
        });


        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.add(cercaVoloButton);
        btnPanel.add(tuttiButton);
        btnPanel.add(resetFiltriButton);

        // aggiungi entrambi al panel principale
        panel.add(ricercaPanel);
        panel.add(btnPanel);

       tabellaOrarioGUI = new TabellaOrarioGUI(controller);
        panel.add(tabellaOrarioGUI.getPanel());



        return panel;
    }

    public void aggiornaTabellaOrario() {
        if (tabellaOrarioGUI != null) {
            tabellaOrarioGUI.aggiornaVoli(controller.tuttiVoli());
        }
    }


    private void resetFiltriVoli() {
        numeroVoloTextField.setText("");
        compagniaTextField.setText("");
        statoComboBox.setSelectedIndex(0);
        dataTextField.setText("");
        orarioTextField.setText("");
        aeroportoTextField.setText("");
        gateTextField.setText("");
        arrivoPartenzaComboBox.setSelectedIndex(0);
    }



    private JPanel creaPair(String labelText, JComponent campo, int gapLabelCampo) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, gapLabelCampo, 0));
        p.setOpaque(false);
        if(labelText != null && !labelText.isEmpty()) {
            p.add(new JLabel(labelText));
        }
        p.add(campo);
        return p;
    }

    // ---- TAB PASSEGGERI ----
    private JPanel creaTabPasseggeri() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel ricercaPanel = new JPanel(new GridLayout(2, 4, 8, 8));
        ricercaPanel.add(new JLabel("Nome:"));
        nomePasseggeroTextField = new JTextField(10);
        ricercaPanel.add(nomePasseggeroTextField);

        ricercaPanel.add(new JLabel("Cognome:"));
        cognomePasseggeroTextField = new JTextField(10);
        ricercaPanel.add(cognomePasseggeroTextField);

        ricercaPanel.add(new JLabel("Numero volo:"));
        numeroVoloPasseggeroTextField = new JTextField(10);
        ricercaPanel.add(numeroVoloPasseggeroTextField);

        ricercaPanel.add(new JLabel("Numero prenotazione:"));
        numeroPrenotazionePasseggeroTextField = new JTextField(10);
        ricercaPanel.add(numeroPrenotazionePasseggeroTextField);

        panel.add(ricercaPanel);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        cercaPasseggeroButton = new JButton("Cerca");
        JButton mostraTuttiBtn = new JButton("Mostra tutti");
        JButton resetBtn = new JButton("Reset");

        cercaPasseggeroButton.addActionListener(e -> ricercaPasseggeri());
        mostraTuttiBtn.addActionListener(e -> caricaTuttiPasseggeri());
        resetBtn.addActionListener(e -> {
            nomePasseggeroTextField.setText("");
            cognomePasseggeroTextField.setText("");
            numeroVoloPasseggeroTextField.setText("");
            numeroPrenotazionePasseggeroTextField.setText("");
            tabellaPasseggeroGUI.setRows(java.util.Collections.emptyList());
        });

        btnPanel.add(cercaPasseggeroButton);
        btnPanel.add(mostraTuttiBtn);
        btnPanel.add(resetBtn);
        panel.add(btnPanel);

        tabellaPasseggeroGUI = new TabellaPasseggeroGUI();
        panel.add(tabellaPasseggeroGUI.getPanel());

        return panel;
    }

    // ---- TAB BAGAGLI ----
    private JPanel creaTabBagagli() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel ricercaPanel = new JPanel(new GridLayout(1, 4, 8, 8));
        ricercaPanel.add(new JLabel("Codice bagaglio:"));
        codiceBagaglioTextField = new JTextField(10);
        ricercaPanel.add(codiceBagaglioTextField);

        ricercaPanel.add(new JLabel("Stato:"));
        statoBagaglioComboBox = new JComboBox<>(new String[]{"", "Caricato", "Smarrito"});
        ricercaPanel.add(statoBagaglioComboBox);

        panel.add(ricercaPanel);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        cercaBagaglioButton = new JButton("Cerca");
        JButton mostraTuttiBagagliBtn = new JButton("Mostra tutti");
        JButton resetBagagliBtn = new JButton("Reset");
        btnPanel.add(cercaBagaglioButton);
        btnPanel.add(mostraTuttiBagagliBtn);
        btnPanel.add(resetBagagliBtn);
        panel.add(btnPanel);

        cercaBagaglioButton.addActionListener(e -> ricercaBagagli());
        mostraTuttiBagagliBtn.addActionListener(e -> caricaTuttiBagagli());
        resetBagagliBtn.addActionListener(e -> {
            codiceBagaglioTextField.setText("");
            statoBagaglioComboBox.setSelectedIndex(0);
            // Svuota tabella
            if(risultatiRicercaBagaglioTable != null && risultatiRicercaBagaglioTable.getModel() instanceof DefaultTableModel m) {
                m.setRowCount(0);
            }
        });

        risultatiRicercaBagaglioTable = new JTable(new DefaultTableModel(new String[]{"Codice Bagaglio", "Stato"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        risultatiRicercaBagaglioTable.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(risultatiRicercaBagaglioTable);
        panel.add(scrollPane);

        return panel;
    }

    // ---- AZIONI RICERCA ----
    private void ricercaVoli() {
        String numeroVolo = trimOrNull(numeroVoloTextField.getText());
        String compagnia = trimOrNull(compagniaTextField.getText());
        String stato = trimOrNull((String) statoComboBox.getSelectedItem());
        String data = trimOrNull(dataTextField.getText());
        String orario = trimOrNull(orarioTextField.getText());
        String aeroporto = trimOrNull(aeroportoTextField.getText());
        String gate = trimOrNull(gateTextField.getText());
        String arrivoPartenzaUI = trimOrNull((String) arrivoPartenzaComboBox.getSelectedItem());

        String arrivoPartenza = null;
        if (arrivoPartenzaUI != null) {
            if (arrivoPartenzaUI.equalsIgnoreCase("In arrivo")) arrivoPartenza = "in arrivo";
            else if (arrivoPartenzaUI.equalsIgnoreCase("In partenza")) arrivoPartenza = "in partenza";
        }

        List<Object[]> risultati = controller.ricercaVoli(
                numeroVolo, compagnia, stato, data, orario, aeroporto, gate, arrivoPartenza
        );
        //System.out.println("DEBUG GUI ricerca -> risultati=" + risultati.size());
        tabellaOrarioGUI.aggiornaVoli(risultati);
    }

    private void ricercaPasseggeri() {
        String nome = nomePasseggeroTextField.getText();
        String cognome = cognomePasseggeroTextField.getText();
        String numeroVolo = numeroVoloPasseggeroTextField.getText();
        String numeroPrenotazione = numeroPrenotazionePasseggeroTextField.getText();

        List<Object[]> risultati = controller.ricercaPasseggeri(nome, cognome, numeroVolo, numeroPrenotazione);
        tabellaPasseggeroGUI.setRows(risultati);
    }

    private void caricaTuttiPasseggeri() {
        tabellaPasseggeroGUI.setRows(controller.tuttiPasseggeri());
    }

    private void ricercaBagagli() {
        String codiceBagaglio = codiceBagaglioTextField.getText();
        String stato = (String) statoBagaglioComboBox.getSelectedItem();
        List<Object[]> risultati = controller.ricercaBagagli(codiceBagaglio, stato);

        DefaultTableModel model = (DefaultTableModel) risultatiRicercaBagaglioTable.getModel();
        model.setRowCount(0);
        for(Object[] r : risultati) {
            model.addRow(r);
        }
    }

    private void caricaTuttiBagagli() {
        List<Object[]> risultati = controller.tuttiBagagliRows();
        DefaultTableModel model = (DefaultTableModel) risultatiRicercaBagaglioTable.getModel();
        model.setRowCount(0);
        for(Object[] r : risultati) {
            model.addRow(r);
        }
    }

    private String trimOrNull(String s) {
        if(s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    private void mostraNascondiPassword() {
        if(passwordVisibile) {
            passwordField.setEchoChar('•');
            mostraPasswordButton.setText("Show");
        } else {
            passwordField.setEchoChar((char) 0);
            mostraPasswordButton.setText("Hide");
        }
        passwordVisibile = !passwordVisibile;
    }

    public JPanel getAreaPersonaleAmmPanel() {
        return areaPersonaleAmmPanel;
    }
}