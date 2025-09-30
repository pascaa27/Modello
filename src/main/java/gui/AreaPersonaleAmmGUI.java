package gui;

import javax.swing.*;
import controller.Controller;
import model.Amministratore;
import model.StatoPrenotazione;
import java.awt.*;
import java.util.Collections;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class AreaPersonaleAmmGUI {
    // Costanti testi UI
    private static final String FONT_FAMILY = "Segoe UI";
    private static final String BTN_CERCA = "Cerca";
    private static final String BTN_RESET = "Reset";
    private static final String BTN_MOSTRA_TUTTI = "Mostra tutti";
    private static final String BTN_SHOW = "Show";
    private static final String BTN_HIDE = "Hide";

    // Campi header
    public JPanel areaPersonaleAmmPanel; // bound dal Designer
    private JTextField nomeTextField;    // bound dal Designer
    private JTextField cognomeTextField; // bound dal Designer
    private JTextField emailTextField;   // bound dal Designer
    private JPasswordField passwordField;// bound dal Designer
    private JButton mostraPasswordButton;// bound dal Designer

    private final Controller controller;
    private JTabbedPane tabbedPane1;     // bound dal Designer

    // Voli (bound dal Designer)
    private JTextField numeroVoloTextField;
    private JTextField compagniaTextField;
    private JComboBox<String> statoComboBox;
    private JTextField dataTextField;
    private JTextField orarioTextField;
    private JTextField aeroportoTextField;
    private JTextField gateTextField;
    private JComboBox<String> arrivoPartenzaComboBox;
    private JButton cercaVoloButton;
    @SuppressWarnings("unused") // usata dal Designer
    private JTable risultatiRicercaTable;

    // Passeggeri (bound dal Designer)
    private JTextField nomePasseggeroTextField;
    private JTextField cognomePasseggeroTextField;
    private JTextField emailPasseggeroTextField;
    private JTextField codiceFiscaleTextField;
    private JTextField numeroVoloPasseggeroTextField;
    private JTextField numeroPrenotazionePasseggeroTextField;
    private JTextField postoAssegnatoTextField;
    private JComboBox<StatoPrenotazione> statoPrenotazioneComboBox;
    private JButton cercaPasseggeroButton;
    private JTable risultatiRicercaPasseggeroTable;

    // Bagagli (bound dal Designer)
    private JTextField codiceBagaglioTextField;
    private JComboBox<String> statoBagaglioComboBox;
    private JButton cercaBagaglioButton;
    private JTable risultatiRicercaBagaglioTable;

    // Bottoni gestione (bound dal Designer)
    private JButton gestioneVoliButton;
    private JButton gestionePrenotazioniButton;
    private JButton gestioneBagagliButton;

    // Stato interno
    private boolean passwordVisibile = false;

    // Widget custom (non del Designer)
    private TabellaOrarioGUI tabellaOrarioGUI;
    private TabellaPasseggeroGUI tabellaPasseggeroGUI;

    // Palette colori
    private final Color mainGradientStart = new Color(30, 87, 153);
    private final Color mainGradientEnd = new Color(125, 185, 232);
    private final Color panelBgColor = new Color(245, 249, 255);
    private final Color buttonColor = new Color(60, 130, 200);
    private final Color buttonHoverColor = new Color(30, 87, 153);
    private final Color searchPanelBg = new Color(237, 243, 251);
    private final Color searchBorderColor = new Color(210, 220, 237);

    public AreaPersonaleAmmGUI(Controller controller, Amministratore amministratore) {
        this.controller = controller;

        // Layout principale desktop
        areaPersonaleAmmPanel = new JPanel() {
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
        areaPersonaleAmmPanel.setLayout(new BorderLayout(0, 0));

        // Dati amministratore (in alto)
        JPanel datiPanel = new JPanel(new GridLayout(5, 3, 8, 8));
        datiPanel.setOpaque(false);

        datiPanel.add(styledLabelWhite("Nome:"));
        nomeTextField = styledTextFieldWhite(amministratore.getNomeUtente());
        nomeTextField.setEditable(false);
        datiPanel.add(nomeTextField);

        datiPanel.add(styledLabelWhite("Cognome:"));
        cognomeTextField = styledTextFieldWhite(amministratore.getCognomeUtente());
        cognomeTextField.setEditable(false);
        datiPanel.add(cognomeTextField);

        datiPanel.add(styledLabelWhite("Email:"));
        emailTextField = styledTextFieldWhite(amministratore.getLogin());
        emailTextField.setEditable(false);
        datiPanel.add(emailTextField);

        datiPanel.add(styledLabelWhite("Password:"));
        passwordField = new JPasswordField(amministratore.getPassword());
        passwordField.setEditable(false);
        passwordField.setEchoChar('•');
        passwordField.setFont(new Font(FONT_FAMILY, Font.PLAIN, 14));
        passwordField.setBackground(panelBgColor);

        mostraPasswordButton = gradientButton(BTN_SHOW);
        mostraPasswordButton.setPreferredSize(new Dimension(70, 28));
        mostraPasswordButton.addActionListener(e -> mostraNascondiPassword());

        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.setOpaque(false);
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        passwordPanel.add(mostraPasswordButton, BorderLayout.EAST);
        datiPanel.add(passwordPanel);

        // Tabs centrale
        tabbedPane1 = new JTabbedPane() {
            @Override
            public void setUI(javax.swing.plaf.TabbedPaneUI ui) {
                super.setUI(ui);
                setBackground(panelBgColor);
            }
        };
        tabbedPane1.setFont(new Font(FONT_FAMILY, Font.BOLD, 15));
        tabbedPane1.setBackground(panelBgColor);

        tabbedPane1.addTab("Voli", creaTabVoli());
        tabbedPane1.addTab("Passeggeri", creaTabPasseggeri());
        tabbedPane1.addTab("Bagagli", creaTabBagagli());

        tabbedPane1.addChangeListener(e -> {
            int idx = tabbedPane1.getSelectedIndex();
            if (idx >= 0) {
                String title = tabbedPane1.getTitleAt(idx);
                if ("Bagagli".equalsIgnoreCase(title)) {
                    caricaTuttiBagagli();
                } else if ("Passeggeri".equalsIgnoreCase(title)) {
                    // Carica subito tutti i passeggeri quando entri nella tab
                    SwingUtilities.invokeLater(this::caricaTuttiPasseggeri);
                }
            }
        });

        // Bottoni gestione in basso
        JPanel bottoniPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 10)) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(super.getPreferredSize().width, 60);
            }
        };
        bottoniPanel.setOpaque(false);
        gestioneVoliButton = gradientButton("Gestione Voli");
        gestionePrenotazioniButton = gradientButton("Gestione Prenotazioni");
        gestioneBagagliButton = gradientButton("Gestione Bagagli");
        bottoniPanel.add(gestioneVoliButton);
        bottoniPanel.add(gestionePrenotazioniButton);
        bottoniPanel.add(gestioneBagagliButton);

        areaPersonaleAmmPanel.add(datiPanel, BorderLayout.NORTH);
        areaPersonaleAmmPanel.add(tabbedPane1, BorderLayout.CENTER);
        areaPersonaleAmmPanel.add(bottoniPanel, BorderLayout.SOUTH);

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
        caricaTuttiBagagli();
    }

    // ---- TAB VOLI ----
    private JPanel creaTabVoli() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(panelBgColor);

        // Crea i componenti PRIMA di passarli a createSearchPanel
        numeroVoloTextField = styledTextFieldBlue("", 8);
        compagniaTextField = styledTextFieldBlue("", 10);
        statoComboBox = styledComboBoxBlue(new String[]{"", "PROGRAMMATO", "IMBARCO", "DECOLLATO", "CANCELLATO", "INRITARDO", "ATTERRATO"});
        dataTextField = styledTextFieldBlue("", 8);
        orarioTextField = styledTextFieldBlue("", 6);
        aeroportoTextField = styledTextFieldBlue("", 10);
        gateTextField = styledTextFieldBlue("", 10);
        arrivoPartenzaComboBox = styledComboBoxBlue(new String[]{"", "In arrivo", "In partenza"});

        JPanel ricercaPanel = createSearchPanel(new Object[][]{
                {"Numero volo:", numeroVoloTextField},
                {"Compagnia:", compagniaTextField},
                {"Stato:", statoComboBox},
                {"Data:", dataTextField},
                {"Orario:", orarioTextField},
                {"Aeroporto:", aeroportoTextField},
                {"Gate:", gateTextField},
                {"Arrivo/Partenza:", arrivoPartenzaComboBox},
        });

        // Pulsante come field (per binding .form)
        cercaVoloButton = gradientButton(BTN_CERCA);
        cercaVoloButton.addActionListener(e -> ricercaVoli());

        JButton resetFiltriButton = gradientButton(BTN_RESET);
        resetFiltriButton.addActionListener(e -> {
            resetFiltriVoli();
            tabellaOrarioGUI.aggiornaVoli(controller.tuttiVoli());
        });

        JButton tuttiButton = gradientButton(BTN_MOSTRA_TUTTI);
        tuttiButton.addActionListener(e -> tabellaOrarioGUI.aggiornaVoli(controller.tuttiVoli()));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.setOpaque(false);
        btnPanel.add(cercaVoloButton);
        btnPanel.add(tuttiButton);
        btnPanel.add(resetFiltriButton);

        panel.add(Box.createVerticalStrut(8));
        panel.add(ricercaPanel);
        panel.add(btnPanel);

        // Tabella custom per l'orario (se vuoi usare il widget custom)
        tabellaOrarioGUI = new TabellaOrarioGUI(controller);
        panel.add(Box.createVerticalStrut(8));
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

    // ---- TAB PASSEGGERI ----
    private JPanel creaTabPasseggeri() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(panelBgColor);

        // Crea i componenti prima
        nomePasseggeroTextField = styledTextFieldBlue("", 10);
        cognomePasseggeroTextField = styledTextFieldBlue("", 10);
        emailPasseggeroTextField = styledTextFieldBlue("", 10);
        codiceFiscaleTextField = styledTextFieldBlue("", 10);
        numeroVoloPasseggeroTextField = styledTextFieldBlue("", 10);
        numeroPrenotazionePasseggeroTextField = styledTextFieldBlue("", 10);
        postoAssegnatoTextField = styledTextFieldBlue("", 10);
        statoPrenotazioneComboBox = styledComboBoxBlueEnum(StatoPrenotazione.values());

        JPanel ricercaPanel = createSearchPanel(new Object[][]{
                {"Nome:", nomePasseggeroTextField},
                {"Cognome:", cognomePasseggeroTextField},
                {"Email:", emailPasseggeroTextField},
                {"Codice Fiscale:", codiceFiscaleTextField},
                {"Numero volo:", numeroVoloPasseggeroTextField},
                {"Numero prenotazione:", numeroPrenotazionePasseggeroTextField},
                {"Posto assegnato: ", postoAssegnatoTextField},
                {"Stato prenotazione:", statoPrenotazioneComboBox}
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        btnPanel.setOpaque(false);

        // Pulsante come field (per binding .form)
        cercaPasseggeroButton = gradientButton(BTN_CERCA);
        JButton mostraTuttiBtn = gradientButton(BTN_MOSTRA_TUTTI);
        JButton resetBtn = gradientButton(BTN_RESET);

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
            statoPrenotazioneComboBox.setSelectedIndex(0);
            // Svuota entrambe le viste se presenti
            if (risultatiRicercaPasseggeroTable != null) {
                DefaultTableModel m = (DefaultTableModel) risultatiRicercaPasseggeroTable.getModel();
                m.setRowCount(0);
            }
            if (tabellaPasseggeroGUI != null) {
                tabellaPasseggeroGUI.setRows(Collections.emptyList());
            }
        });

        btnPanel.add(cercaPasseggeroButton);
        btnPanel.add(mostraTuttiBtn);
        btnPanel.add(resetBtn);

        panel.add(Box.createVerticalStrut(8));
        panel.add(ricercaPanel);
        panel.add(btnPanel);

        // Widget custom per mostrare risultati passeggeri
        tabellaPasseggeroGUI = new TabellaPasseggeroGUI();
        panel.add(Box.createVerticalStrut(8));
        panel.add(tabellaPasseggeroGUI.getPanel());

        // Carica tutti i passeggeri alla prima creazione della tab
        SwingUtilities.invokeLater(this::caricaTuttiPasseggeri);

        return panel;
    }

    // ---- TAB BAGAGLI ----
    private JPanel creaTabBagagli() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(panelBgColor);

        // --- Pannello ricerca bagagli in stile azzurro ---
        JPanel ricercaPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();
                g2d.setColor(searchPanelBg);
                g2d.fillRoundRect(0, 0, w, h, 16, 16);
            }
        };
        ricercaPanel.setOpaque(false);
        ricercaPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(searchBorderColor, 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridy = 0;

        // Crea i componenti
        codiceBagaglioTextField = styledTextFieldBlue("", 10);
        statoBagaglioComboBox = styledComboBoxBlue(new String[]{"", "Caricato", "Smarrito", "Registrato", "Trovato"});

        // CODICE BAGAGLIO
        gbc.gridx = 0;
        ricercaPanel.add(styledLabelBlue("Codice bagaglio:"), gbc);
        gbc.gridx = 1;
        ricercaPanel.add(codiceBagaglioTextField, gbc);
        gbc.gridy++;

        // STATO
        gbc.gridx = 0;
        ricercaPanel.add(styledLabelBlue("Stato:"), gbc);
        gbc.gridx = 1;
        ricercaPanel.add(statoBagaglioComboBox, gbc);
        gbc.gridy++;

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        btnPanel.setOpaque(false);

        // Pulsante come field (per binding .form)
        cercaBagaglioButton = gradientButton(BTN_CERCA);
        JButton mostraTuttiBagagliBtn = gradientButton(BTN_MOSTRA_TUTTI);
        JButton resetBagagliBtn = gradientButton(BTN_RESET);
        btnPanel.add(cercaBagaglioButton);
        btnPanel.add(mostraTuttiBagagliBtn);
        btnPanel.add(resetBagagliBtn);

        cercaBagaglioButton.addActionListener(e -> ricercaBagagli());
        mostraTuttiBagagliBtn.addActionListener(e -> caricaTuttiBagagli());
        resetBagagliBtn.addActionListener(e -> {
            codiceBagaglioTextField.setText("");
            statoBagaglioComboBox.setSelectedIndex(0);
            if (risultatiRicercaBagaglioTable != null) {
                DefaultTableModel m = (DefaultTableModel) risultatiRicercaBagaglioTable.getModel();
                m.setRowCount(0);
            } else if (risultatiRicercaTable != null) {
                DefaultTableModel m = (DefaultTableModel) risultatiRicercaTable.getModel();
                m.setRowCount(0);
            }
        });

        panel.add(Box.createVerticalStrut(8));
        panel.add(ricercaPanel);
        panel.add(btnPanel);

        // Tabella risultati Bagagli (se usi il Designer, verrà istanziata dal .form)
        // Nel nostro layout programmatico la creiamo in ogni caso per sicurezza.
        risultatiRicercaBagaglioTable = new JTable(new DefaultTableModel(new String[]{"Codice Bagaglio", "Stato"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        });
        risultatiRicercaBagaglioTable.setAutoCreateRowSorter(true);
        risultatiRicercaBagaglioTable.setFont(new Font(FONT_FAMILY, Font.PLAIN, 13));
        risultatiRicercaBagaglioTable.getTableHeader().setFont(new Font(FONT_FAMILY, Font.BOLD, 13));
        risultatiRicercaBagaglioTable.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(risultatiRicercaBagaglioTable);
        panel.add(Box.createVerticalStrut(8));
        panel.add(scrollPane);

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
        // Usiamo il widget custom per i voli
        if (tabellaOrarioGUI != null) {
            tabellaOrarioGUI.aggiornaVoli(risultati);
        }
        // In alternativa, se vuoi popolare una JTable del Designer (risultatiRicercaTable), abilita qui:
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
        if (risultati == null) risultati = Collections.emptyList();

        // Aggiorna entrambe le viste se presenti
        if (tabellaPasseggeroGUI != null) {
            tabellaPasseggeroGUI.setRows(risultati);
        }
        if (risultatiRicercaPasseggeroTable != null) {
            setRowsIntoTable(risultatiRicercaPasseggeroTable, risultati);
        }
    }

    private void caricaTuttiPasseggeri() {
        List<Object[]> risultati = controller.tuttiPasseggeri();
        if (risultati == null) risultati = Collections.emptyList();

        // Aggiorna entrambe le viste se presenti
        if (tabellaPasseggeroGUI != null) {
            tabellaPasseggeroGUI.setRows(risultati);
        }
        if (risultatiRicercaPasseggeroTable != null) {
            setRowsIntoTable(risultatiRicercaPasseggeroTable, risultati);
        }
    }

    private void ricercaBagagli() {
        String codiceBagaglio = codiceBagaglioTextField.getText();
        String stato = (String) statoBagaglioComboBox.getSelectedItem();
        List<Object[]> risultati = controller.ricercaBagagli(codiceBagaglio, stato);
        if (risultati == null) risultati = Collections.emptyList();

        if (risultatiRicercaBagaglioTable != null) {
            setRowsIntoTable(risultatiRicercaBagaglioTable, risultati);
        } else if (risultatiRicercaTable != null) {
            setRowsIntoTable(risultatiRicercaTable, risultati);
        }
    }

    public void caricaTuttiBagagli() {
        List<Object[]> risultati = controller.tuttiBagagliRows();
        if (risultati == null) risultati = Collections.emptyList();

        if (risultatiRicercaBagaglioTable != null) {
            setRowsIntoTable(risultatiRicercaBagaglioTable, risultati);
        } else if (risultatiRicercaTable != null) {
            setRowsIntoTable(risultatiRicercaTable, risultati);
        }
    }

    // Helper per popolare una JTable in modo robusto
    private void setRowsIntoTable(JTable table, List<Object[]> rows) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int colCount = model.getColumnCount();
        if (colCount == 0 && rows != null && !rows.isEmpty()) {
            colCount = rows.get(0).length;
            String[] headers = new String[colCount];
            for (int i = 0; i < colCount; i++) headers[i] = "Col " + (i + 1);
            table.setModel(new DefaultTableModel(headers, 0) {
                @Override
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            });
            model = (DefaultTableModel) table.getModel();
        }
        model.setRowCount(0);
        if (rows != null) {
            for (Object[] r : rows) {
                if (r == null) continue;
                if (r.length != model.getColumnCount()) {
                    Object[] row = new Object[model.getColumnCount()];
                    System.arraycopy(r, 0, row, 0, Math.min(r.length, row.length));
                    model.addRow(row);
                } else {
                    model.addRow(r);
                }
            }
        }
    }

    private String trimOrNull(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    private void mostraNascondiPassword() {
        if (passwordVisibile) {
            passwordField.setEchoChar('•');
            mostraPasswordButton.setText(BTN_SHOW);
        } else {
            passwordField.setEchoChar((char) 0);
            mostraPasswordButton.setText(BTN_HIDE);
        }
        passwordVisibile = !passwordVisibile;
    }

    public JPanel getAreaPersonaleAmmPanel() {
        return areaPersonaleAmmPanel;
    }

    public void aggiornaTabellaPasseggeri() {
        List<Object[]> risultati = controller.tuttiPasseggeri();
        if (risultati == null) risultati = Collections.emptyList();

        if (tabellaPasseggeroGUI != null) {
            tabellaPasseggeroGUI.setRows(risultati);
        }
        if (risultatiRicercaPasseggeroTable != null) {
            setRowsIntoTable(risultatiRicercaPasseggeroTable, risultati);
        }
    }

    // ------ STILE COMPONENTI ------
    private JLabel styledLabelWhite(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font(FONT_FAMILY, Font.BOLD, 13));
        l.setForeground(Color.WHITE);
        return l;
    }

    private JTextField styledTextFieldWhite(String text) {
        JTextField tf = new JTextField(text, 13);
        tf.setFont(new Font(FONT_FAMILY, Font.PLAIN, 13));
        tf.setBackground(panelBgColor);
        tf.setForeground(mainGradientStart);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 180), 1, true),
                BorderFactory.createEmptyBorder(3, 7, 3, 7)
        ));
        tf.setCaretColor(mainGradientStart);
        return tf;
    }

    private JLabel styledLabelBlue(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font(FONT_FAMILY, Font.BOLD, 13));
        l.setForeground(mainGradientStart);
        return l;
    }

    private JTextField styledTextFieldBlue(String text, int columns) {
        JTextField tf = new JTextField(text, columns);
        tf.setFont(new Font(FONT_FAMILY, Font.PLAIN, 13));
        tf.setBackground(Color.WHITE);
        tf.setForeground(mainGradientStart);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(mainGradientStart, 1, true),
                BorderFactory.createEmptyBorder(3, 7, 3, 7)
        ));
        tf.setCaretColor(mainGradientStart);
        return tf;
    }

    private JComboBox<String> styledComboBoxBlue(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(new Font(FONT_FAMILY, Font.PLAIN, 12));
        cb.setBackground(Color.WHITE);
        cb.setForeground(mainGradientStart);
        cb.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(mainGradientStart, 1, true),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
        return cb;
    }

    private JComboBox<StatoPrenotazione> styledComboBoxBlueEnum(StatoPrenotazione[] items) {
        JComboBox<StatoPrenotazione> cb = new JComboBox<>();
        cb.setFont(new Font(FONT_FAMILY, Font.PLAIN, 12));
        cb.setBackground(Color.WHITE);
        cb.setForeground(mainGradientStart);
        cb.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(mainGradientStart, 1, true),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
        cb.addItem(null); // vuoto
        for (StatoPrenotazione sp : items) cb.addItem(sp);
        return cb;
    }

    private JButton gradientButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font(FONT_FAMILY, Font.BOLD, 13));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                onHover(b, true);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                onHover(b, false);
            }
        });
        b.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, buttonColor, 0, c.getHeight(), buttonHoverColor);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 16, 16);
                super.paint(g, c);
            }
        });
        return b;
    }

    private static void onHover(JButton b, boolean entered) {
        b.setForeground(Color.WHITE);
        b.repaint();
    }

    private JPanel createSearchPanel(Object[][] pairs) {
        JPanel bgPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();
                g2.setColor(searchPanelBg);
                g2.fillRoundRect(0, 0, w, h, 16, 16);
            }
        };
        bgPanel.setOpaque(false);
        bgPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(searchBorderColor, 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridy = 0;

        for (Object[] pair : pairs) {
            String label = (String) pair[0];
            JComponent field = (JComponent) pair[1];
            gbc.gridx = 0;
            bgPanel.add(styledLabelBlue(label), gbc);
            gbc.gridx = 1;
            bgPanel.add(field, gbc);
            gbc.gridy++;
        }
        return bgPanel;
    }
}