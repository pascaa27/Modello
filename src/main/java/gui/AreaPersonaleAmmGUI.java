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
 * Versione desktop con layout BorderLayout, bottoni gestione visibili e tabella ben centrata.
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
    private JComboBox<String> statoBagaglioComboBox;
    private JButton cercaBagaglioButton;
    private JButton gestioneVoliButton;
    private JButton gestionePrenotazioniButton;
    private JButton gestioneBagagliButton;
    private JTextField orarioTextField;
    private boolean passwordVisibile = false;
    private TabellaOrarioGUI tabellaOrarioGUI;
    private JTable risultatiRicercaPasseggeroTable;
    private TabellaPasseggeroGUI tabellaPasseggeroGUI;
    private JTable risultatiRicercaBagaglioTable;
    private JTextField aeroportoTextField;
    private JTextField gateTextField;
    private JComboBox arrivoPartenzaComboBox;
    private JTextField codiceFiscaleTextField;
    private JTextField postoAssegnatoTextField;
    private JComboBox<StatoPrenotazione> statoPrenotazioneComboBox;
    private JTextField emailPasseggeroTextField;
    private JTable risultatiRicercaTable;

    // Palette colori
    private final Color mainGradientStart = new Color(30, 87, 153);
    private final Color mainGradientEnd = new Color(125, 185, 232);
    private final Color panelBgColor = new Color(245, 249, 255);
    private final Color buttonColor = new Color(60, 130, 200);
    private final Color buttonHoverColor = new Color(30, 87, 153);
    private final Color tabSelectedColor = new Color(220, 235, 250);
    private final Color searchPanelBg = new Color(237, 243, 251);
    private final Color searchBorderColor = new Color(210, 220, 237);




    public AreaPersonaleAmmGUI(Controller controller, Amministratore amministratore) {
        this.controller = controller;

        // Layout principale desktop!
        areaPersonaleAmmPanel = new JPanel() {
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
        areaPersonaleAmmPanel.setLayout(new BorderLayout(0, 0)); // <--- DESKTOP LAYOUT!

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
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBackground(panelBgColor);

        mostraPasswordButton = gradientButton("Show");
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
        tabbedPane1.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tabbedPane1.setBackground(panelBgColor);

        tabbedPane1.addTab("Voli", creaTabVoli());
        tabbedPane1.addTab("Passeggeri", creaTabPasseggeri());
        tabbedPane1.addTab("Bagagli", creaTabBagagli());

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


        // Bottoni gestione in basso
        JPanel bottoniPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 10)) {
            @Override
            public Dimension getPreferredSize() {
                // Altezza minima garantita per mostrare tutti i bottoni
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

// In BorderLayout, aggiungi così:
        areaPersonaleAmmPanel.add(bottoniPanel, BorderLayout.SOUTH);

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

        JPanel ricercaPanel = createSearchPanel(new Object[][]{
                {"Numero volo:", numeroVoloTextField = styledTextFieldBlue("", 8)},
                {"Compagnia:", compagniaTextField = styledTextFieldBlue("", 10)},
                {"Stato:", statoComboBox = styledComboBoxBlue(new String[]{"", "PROGRAMMATO", "IMBARCO", "DECOLLATO", "CANCELLATO", "INRITARDO", "ATTERRATO"})},
                {"Data:", dataTextField = styledTextFieldBlue("", 8)},
                {"Orario:", orarioTextField = styledTextFieldBlue("", 6)},
                {"Aeroporto:", aeroportoTextField = styledTextFieldBlue("", 10)},
                {"Gate:", gateTextField = styledTextFieldBlue("", 10)},
                {"Arrivo/Partenza:", arrivoPartenzaComboBox = styledComboBoxBlue(new String[]{"", "In arrivo", "In partenza"})},
        });

        cercaVoloButton = gradientButton("Cerca");
        cercaVoloButton.addActionListener(e -> ricercaVoli());

        JButton resetFiltriButton = gradientButton("Reset");
        resetFiltriButton.addActionListener(e -> {
            resetFiltriVoli();
            tabellaOrarioGUI.aggiornaVoli(controller.tuttiVoli());
        });

        JButton tuttiButton = gradientButton("Tutti");
        tuttiButton.addActionListener(e -> tabellaOrarioGUI.aggiornaVoli(controller.tuttiVoli()));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.setOpaque(false);
        btnPanel.add(cercaVoloButton);
        btnPanel.add(tuttiButton);
        btnPanel.add(resetFiltriButton);

        panel.add(Box.createVerticalStrut(8));
        panel.add(ricercaPanel);
        panel.add(btnPanel);

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

        JPanel ricercaPanel = createSearchPanel(new Object[][]{
                {"Nome:", nomePasseggeroTextField = styledTextFieldBlue("", 10)},
                {"Cognome:", cognomePasseggeroTextField = styledTextFieldBlue("", 10)},
                {"Email:", emailPasseggeroTextField = styledTextFieldBlue("", 10)},
                {"Codice Fiscale:", codiceFiscaleTextField = styledTextFieldBlue("", 10)},
                {"Numero volo:", numeroVoloPasseggeroTextField = styledTextFieldBlue("", 10)},
                {"Numero prenotazione:", numeroPrenotazionePasseggeroTextField = styledTextFieldBlue("", 10)},
                {"Posto assegnato: ", postoAssegnatoTextField = styledTextFieldBlue("", 10)},
                {"Stato prenotazione:", statoPrenotazioneComboBox = styledComboBoxBlueEnum(StatoPrenotazione.values())}
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        btnPanel.setOpaque(false);
        cercaPasseggeroButton = gradientButton("Cerca");
        JButton mostraTuttiBtn = gradientButton("Mostra tutti");
        JButton resetBtn = gradientButton("Reset");

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
            tabellaPasseggeroGUI.setRows(java.util.Collections.emptyList());
        });

        btnPanel.add(cercaPasseggeroButton);
        btnPanel.add(mostraTuttiBtn);
        btnPanel.add(resetBtn);

        panel.add(Box.createVerticalStrut(8));
        panel.add(ricercaPanel);
        panel.add(btnPanel);

        tabellaPasseggeroGUI = new TabellaPasseggeroGUI();
        panel.add(Box.createVerticalStrut(8));
        panel.add(tabellaPasseggeroGUI.getPanel());

        return panel;
    }

    // ---- TAB BAGAGLI ----
    private JPanel creaTabBagagli() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(panelBgColor);

        JPanel ricercaPanel = createSearchPanel(new Object[][]{
                {"Codice bagaglio:", codiceBagaglioTextField = styledTextFieldBlue("", 10)},
                {"Stato:", statoBagaglioComboBox = styledComboBoxBlue(new String[]{"", "Caricato", "Smarrito", "Registrato", "Trovato"})}
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        btnPanel.setOpaque(false);
        cercaBagaglioButton = gradientButton("Cerca");
        JButton mostraTuttiBagagliBtn = gradientButton("Mostra tutti");
        JButton resetBagagliBtn = gradientButton("Reset");
        btnPanel.add(cercaBagaglioButton);
        btnPanel.add(mostraTuttiBagagliBtn);
        btnPanel.add(resetBagagliBtn);

        cercaBagaglioButton.addActionListener(e -> ricercaBagagli());
        mostraTuttiBagagliBtn.addActionListener(e -> caricaTuttiBagagli());
        resetBagagliBtn.addActionListener(e -> {
            codiceBagaglioTextField.setText("");
            statoBagaglioComboBox.setSelectedIndex(0);
            if (risultatiRicercaBagaglioTable != null && risultatiRicercaBagaglioTable.getModel() instanceof DefaultTableModel m) {
                m.setRowCount(0);
            }
        });

        panel.add(Box.createVerticalStrut(8));
        panel.add(ricercaPanel);
        panel.add(btnPanel);

        risultatiRicercaBagaglioTable = new JTable(new DefaultTableModel(new String[]{"Codice Bagaglio", "Stato"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        });
        risultatiRicercaBagaglioTable.setAutoCreateRowSorter(true);
        risultatiRicercaBagaglioTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        risultatiRicercaBagaglioTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
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
        for (Object[] r : risultati) {
            model.addRow(r);
        }
    }

    public void caricaTuttiBagagli() {
        if (risultatiRicercaBagaglioTable == null) return;
        List<Object[]> risultati = controller.tuttiBagagliRows();
        DefaultTableModel model = (DefaultTableModel) risultatiRicercaBagaglioTable.getModel();
        model.setRowCount(0);
        for (Object[] r : risultati) {
            model.addRow(r);
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

    // ------ STILE COMPONENTI ------
    private JLabel styledLabelWhite(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(Color.WHITE);
        return l;
    }

    private JTextField styledTextFieldWhite(String text) {
        JTextField tf = new JTextField(text, 13);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
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
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(mainGradientStart);
        return l;
    }

    private JTextField styledTextFieldBlue(String text, int columns) {
        JTextField tf = new JTextField(text, columns);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
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
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
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
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
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
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
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
                b.setForeground(Color.WHITE);
                b.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                b.setForeground(Color.WHITE);
                b.repaint();
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

    private JPanel createSearchPanel(Object[][] pairs) {
        JPanel bgPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                int w = getWidth(), h = getHeight();
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