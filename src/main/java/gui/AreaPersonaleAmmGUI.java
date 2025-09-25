package gui;

import javax.swing.*;
import controller.Controller;
import model.Amministratore;
import model.StatoPrenotazione;

import java.awt.*;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
    private JButton gestioneBagagliButton;
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
    private JTextField codiceFiscaleTextField;
    private JTextField postoAssegnatoTextField;
    private JComboBox<StatoPrenotazione> statoPrenotazioneComboBox;
    private JTextField emailPasseggeroTextField;

    public AreaPersonaleAmmGUI(Controller controller, Amministratore amministratore) {
        this.controller = controller;

        areaPersonaleAmmPanel = new JPanel();
        areaPersonaleAmmPanel.setLayout(new BoxLayout(areaPersonaleAmmPanel, BoxLayout.Y_AXIS));

        // Dati amministratore
        JPanel datiPanel = new JPanel(new GridLayout(5, 3, 10, 10));
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

        // NEW: ricarica automaticamente i bagagli quando apri la tab "Bagagli"
        tabbedPane1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int idx = tabbedPane1.getSelectedIndex();
                if (idx >= 0) {
                    String title = tabbedPane1.getTitleAt(idx);
                    if ("Bagagli".equalsIgnoreCase(title)) {
                        caricaTuttiBagagli();
                    }
                }
            }
        });

        areaPersonaleAmmPanel.add(tabbedPane1);

        JPanel bottoniPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        gestioneVoliButton = new JButton("Gestione Voli");
        gestionePrenotazioniButton = new JButton("Gestione Prenotazioni");
        gestioneBagagliButton = new JButton("Gestione Bagagli");
        bottoniPanel.add(gestioneVoliButton);
        bottoniPanel.add(gestionePrenotazioniButton);
        bottoniPanel.add(gestioneBagagliButton);

        areaPersonaleAmmPanel.add(bottoniPanel);

        gestioneVoliButton.addActionListener(e -> {
            GestioneVoliGUI gestioneVoli = new GestioneVoliGUI(controller, this);
            JFrame frame = new JFrame("Gestione Voli");
            frame.setContentPane(gestioneVoli.getPanelDatiVolo());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        gestionePrenotazioniButton.addActionListener(e -> {
            GestionePrenotazioniGUI gestionePrenotazioni = new GestionePrenotazioniGUI(controller, amministratore, this);
            JFrame frame = new JFrame("Gestione Prenotazioni");
            frame.setContentPane(gestionePrenotazioni.getPanelPrenotazione());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        gestioneBagagliButton.addActionListener(e -> {
            GestioneBagagliGUI gestioneBagagli = new GestioneBagagliGUI(controller, this);
            JFrame frame = new JFrame("Gestione Bagagli");
            frame.setContentPane(gestioneBagagli.getPanelBagaglio());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        aggiornaTabellaOrario();

        // NEW: carica subito tutti i bagagli all'avvio dell'Area Personale
        caricaTuttiBagagli();
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
        arrivoPartenzaComboBox = new JComboBox<>(new String[]{"", "In arrivo", "In partenza"});

        ricercaPanel.add(creaPair("Numero volo:", numeroVoloTextField, GAP_LABEL_CAMPO));
        ricercaPanel.add(creaPair("Compagnia:", compagniaTextField, GAP_LABEL_CAMPO));
        ricercaPanel.add(creaPair("Stato:", statoComboBox, GAP_LABEL_CAMPO));
        ricercaPanel.add(creaPair("Data:", dataTextField, GAP_LABEL_CAMPO));
        ricercaPanel.add(creaPair("Orario:", orarioTextField, GAP_LABEL_CAMPO));
        ricercaPanel.add(creaPair("Aeroporto:", aeroportoTextField, GAP_LABEL_CAMPO));
        ricercaPanel.add(creaPair("Gate:", gateTextField, GAP_LABEL_CAMPO));
        ricercaPanel.add(creaPair("Arrivo/Partenza:", arrivoPartenzaComboBox, GAP_LABEL_CAMPO));

        cercaVoloButton = new JButton("Cerca");
        cercaVoloButton.addActionListener(e -> ricercaVoli());

        JButton resetFiltriButton = new JButton("Reset");
        resetFiltriButton.addActionListener(e -> {
            resetFiltriVoli();
            // INVECE di svuotare, ricarichiamo tutti i voli (più intuitivo)
            tabellaOrarioGUI.aggiornaVoli(controller.tuttiVoli());
        });

        JButton tuttiButton = new JButton("Tutti");
        tuttiButton.addActionListener(e -> tabellaOrarioGUI.aggiornaVoli(controller.tuttiVoli()));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.add(cercaVoloButton);
        btnPanel.add(tuttiButton);
        btnPanel.add(resetFiltriButton);

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

        JPanel ricercaPanel = new JPanel(new GridLayout(0, 2, 8, 8));
        ricercaPanel.add(new JLabel("Nome:"));
        nomePasseggeroTextField = new JTextField(10);
        ricercaPanel.add(nomePasseggeroTextField);

        ricercaPanel.add(new JLabel("Cognome:"));
        cognomePasseggeroTextField = new JTextField(10);
        ricercaPanel.add(cognomePasseggeroTextField);

        ricercaPanel.add(new JLabel("Email:"));
        emailPasseggeroTextField = new JTextField(10);
        ricercaPanel.add(emailPasseggeroTextField);

        ricercaPanel.add(new JLabel("Codice Fiscale:"));
        codiceFiscaleTextField = new JTextField(10);
        ricercaPanel.add(codiceFiscaleTextField);

        ricercaPanel.add(new JLabel("Numero volo:"));
        numeroVoloPasseggeroTextField = new JTextField(10);
        ricercaPanel.add(numeroVoloPasseggeroTextField);

        ricercaPanel.add(new JLabel("Numero prenotazione:"));
        numeroPrenotazionePasseggeroTextField = new JTextField(10);
        ricercaPanel.add(numeroPrenotazionePasseggeroTextField);

        ricercaPanel.add(new JLabel("Posto assegnato: "));
        postoAssegnatoTextField = new JTextField(10);
        ricercaPanel.add(postoAssegnatoTextField);

        ricercaPanel.add(new JLabel("Stato prenotazione:"));
        statoPrenotazioneComboBox = new JComboBox<>();
        statoPrenotazioneComboBox.addItem(null); // vuoto = qualsiasi
        for (StatoPrenotazione sp : StatoPrenotazione.values()) {
            statoPrenotazioneComboBox.addItem(sp);
        }
        ricercaPanel.add(statoPrenotazioneComboBox);

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
            emailPasseggeroTextField.setText("");
            codiceFiscaleTextField.setText("");
            numeroVoloPasseggeroTextField.setText("");
            numeroPrenotazionePasseggeroTextField.setText("");
            postoAssegnatoTextField.setText("");
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

        // NEW: carica subito tutti i bagagli la prima volta che si crea la tab
        SwingUtilities.invokeLater(this::caricaTuttiBagagli);

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
        tabellaOrarioGUI.aggiornaVoli(risultati);
    }

    private void ricercaPasseggeri() {
        String nome = nomePasseggeroTextField.getText();
        String cognome = cognomePasseggeroTextField.getText();
        String email = emailPasseggeroTextField.getText();
        String codiceFiscale = codiceFiscaleTextField.getText();
        String numeroVolo = numeroVoloPasseggeroTextField.getText();
        String numeroPrenotazione = numeroPrenotazionePasseggeroTextField.getText();
        String postoAssegnato = postoAssegnatoTextField.getText();

        StatoPrenotazione stato = (StatoPrenotazione) statoPrenotazioneComboBox.getSelectedItem();

        List<Object[]> risultati = controller.ricercaPasseggeri(
                nome,
                cognome,
                email,
                codiceFiscale,
                numeroVolo,
                numeroPrenotazione,
                postoAssegnato,
                stato != null ? stato.name() : null
        );

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

    public void caricaTuttiBagagli() {
        if (risultatiRicercaBagaglioTable == null) return;
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

    public void aggiornaTabellaPasseggeri() {
        if (tabellaPasseggeroGUI != null) {
            tabellaPasseggeroGUI.setRows(controller.tuttiPasseggeri());
        }
    }
}