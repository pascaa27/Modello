package gui;

import controller.Controller;
import model.StatoVolo;
import model.Volo;

import javax.swing.*;
import java.awt.*;
import java.util.regex.Pattern;

public class GestioneVoliGUI {

    private static final String FONT_FAMILY = "Segoe UI";
    private static final String ARRIVO = "in arrivo";
    private static final String PARTENZA = "in partenza";
    private static final String TIME_PATTERN = "\\d{2}:\\d{2}";
    private static final String TITLE_CERCA_VOLO = "Cerca volo";

    private JPanel gestioneVoliPanel;
    private JTextField codiceUnivocoTextField;
    private JTextField compagniaTextField;
    private JTextField dataTextField;
    private JTextField altroAeroportoTextField;
    private JTextField orarioPrevistoTextField;
    private JTextField orarioStimatoTextField;
    private JTextField gateTextField;
    private JRadioButton arrivoRadioButton;
    private JRadioButton partenzaRadioButton;
    private JComboBox<StatoVolo> statoVoloComboBox;
    private JButton aggiungiVoloButton;
    private JButton rimuoviVoloButton;
    private JButton cercaCodiceButton;
    private JButton confermaModificheButton;

    private final Controller controller;
    private final AreaPersonaleAmmGUI areaAmmGUI;
    private static final String AEROPORTO_LOCALE = "NAP";

    private String codiceVoloSelezionato = null;

    // Palette colori
    private final Color mainGradientStart = new Color(30, 87, 153);
    private final Color mainGradientEnd   = new Color(125, 185, 232);
    private final Color panelBgColor      = new Color(245, 249, 255);
    private final Color buttonColor       = new Color(60, 130, 200);
    private final Color buttonHoverColor  = new Color(30, 87, 153);

    public GestioneVoliGUI(Controller controller, AreaPersonaleAmmGUI areaAmmGUI) {
        this.controller = controller;
        this.areaAmmGUI = areaAmmGUI;

        // Gradient panel
        gestioneVoliPanel = new JPanel() {
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
        gestioneVoliPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 18, 8, 18);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Codice
        gbc.gridx = 0; gbc.gridy = 0;
        gestioneVoliPanel.add(styledLabelWhite("Codice Univoco:"), gbc);
        gbc.gridx = 1;
        codiceUnivocoTextField = styledTextFieldWhite("");
        gestioneVoliPanel.add(codiceUnivocoTextField, gbc);

        // Compagnia
        gbc.gridx = 0; gbc.gridy++;
        gestioneVoliPanel.add(styledLabelWhite("Compagnia:"), gbc);
        gbc.gridx = 1;
        compagniaTextField = styledTextFieldWhite("");
        gestioneVoliPanel.add(compagniaTextField, gbc);

        // Data
        gbc.gridx = 0; gbc.gridy++;
        gestioneVoliPanel.add(styledLabelWhite("Data:"), gbc);
        gbc.gridx = 1;
        dataTextField = styledTextFieldWhite("");
        gestioneVoliPanel.add(dataTextField, gbc);

        // Aeroporto destinazione/origine
        gbc.gridx = 0; gbc.gridy++;
        gestioneVoliPanel.add(styledLabelWhite("Altro Aeroporto:"), gbc);
        gbc.gridx = 1;
        altroAeroportoTextField = styledTextFieldWhite("");
        gestioneVoliPanel.add(altroAeroportoTextField, gbc);

        // Orario previsto
        gbc.gridx = 0; gbc.gridy++;
        gestioneVoliPanel.add(styledLabelWhite("Orario Previsto:"), gbc);
        gbc.gridx = 1;
        orarioPrevistoTextField = styledTextFieldWhite("");
        gestioneVoliPanel.add(orarioPrevistoTextField, gbc);

        // Orario stimato
        gbc.gridx = 0; gbc.gridy++;
        gestioneVoliPanel.add(styledLabelWhite("Orario Stimato:"), gbc);
        gbc.gridx = 1;
        orarioStimatoTextField = styledTextFieldWhite("");
        gestioneVoliPanel.add(orarioStimatoTextField, gbc);

        // Gate
        gbc.gridx = 0; gbc.gridy++;
        gestioneVoliPanel.add(styledLabelWhite("Gate:"), gbc);
        gbc.gridx = 1;
        gateTextField = styledTextFieldWhite("");
        gestioneVoliPanel.add(gateTextField, gbc);

        // Direzione (radio)
        gbc.gridx = 0; gbc.gridy++;
        gestioneVoliPanel.add(styledLabelWhite("Arrivo/Partenza:"), gbc);
        gbc.gridx = 1;
        JPanel direzionePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        direzionePanel.setOpaque(false);
        arrivoRadioButton = new JRadioButton("Arrivo");
        partenzaRadioButton = new JRadioButton("Partenza");
        ButtonGroup direzioneGroup = new ButtonGroup();
        direzioneGroup.add(arrivoRadioButton);
        direzioneGroup.add(partenzaRadioButton);
        direzionePanel.add(arrivoRadioButton);
        direzionePanel.add(partenzaRadioButton);
        gestioneVoliPanel.add(direzionePanel, gbc);
        setDirezioneDefault();

        // Stato volo
        gbc.gridx = 0; gbc.gridy++;
        gestioneVoliPanel.add(styledLabelWhite("Stato Volo:"), gbc);
        gbc.gridx = 1;
        statoVoloComboBox = styledComboBoxStato(StatoVolo.values());
        gestioneVoliPanel.add(statoVoloComboBox, gbc);

        // Bottoni principali
        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 18, 10, 18);
        JPanel bottoniPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 4));
        bottoniPanel.setOpaque(false);
        aggiungiVoloButton = gradientButton("Aggiungi Volo");
        rimuoviVoloButton = gradientButton("Rimuovi Volo");
        cercaCodiceButton = gradientButton(TITLE_CERCA_VOLO);
        confermaModificheButton = gradientButton("Conferma Modifiche");
        bottoniPanel.add(aggiungiVoloButton);
        bottoniPanel.add(rimuoviVoloButton);
        bottoniPanel.add(cercaCodiceButton);
        bottoniPanel.add(confermaModificheButton);
        gestioneVoliPanel.add(bottoniPanel, gbc);

        // Combo stato
        statoVoloComboBox.removeAllItems();
        for (StatoVolo sv : StatoVolo.values()) {
            statoVoloComboBox.addItem(sv);
        }

        arrivoRadioButton.addActionListener(e -> toggleDirezione());
        partenzaRadioButton.addActionListener(e -> toggleDirezione());
        toggleDirezione();

        aggiungiVoloButton.addActionListener(e -> aggiungiVolo());
        rimuoviVoloButton.addActionListener(e -> rimuoviVolo());
        cercaCodiceButton.addActionListener(e -> cercaVoloPerCodice());
        confermaModificheButton.addActionListener(e -> confermaModifiche());
        confermaModificheButton.setEnabled(false);
    }

    private void setDirezioneDefault() {
        if (partenzaRadioButton != null) {
            partenzaRadioButton.setSelected(true);
        }
    }

    private void toggleDirezione() {
        if (gateTextField != null) {
            gateTextField.setEnabled(true);
        }
    }

    private void aggiungiVolo() {
        String codice = safeText(codiceUnivocoTextField);
        String compagnia = safeText(compagniaTextField);
        String data = safeText(dataTextField);
        String orarioPrevisto = safeText(orarioPrevistoTextField);
        String orarioStimato = safeText(orarioStimatoTextField);
        String otherAirport = safeText(altroAeroportoTextField).toUpperCase();
        String gate = safeText(gateTextField);

        StatoVolo stato = (StatoVolo) statoVoloComboBox.getSelectedItem();
        String direzione = arrivoRadioButton.isSelected() ? ARRIVO : PARTENZA;

        String errore = valida(codice, compagnia, data, otherAirport, orarioPrevisto, orarioStimato, gate);
        if (errore != null) {
            JOptionPane.showMessageDialog(
                    gestioneVoliPanel,
                    errore,
                    "Errore inserimento volo",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (gateTextField == null) {
            JOptionPane.showMessageDialog(gestioneVoliPanel,
                    "Errore: il campo GATE non è collegato nel form.",
                    "Configurazione form", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Controller.VoloInput input = new Controller.VoloInput(
                    codice,
                    compagnia,
                    data,
                    orarioPrevisto,
                    orarioStimato,
                    stato,
                    direzione,
                    otherAirport,
                    gate
            );
            controller.aggiungiVolo(input);
            JOptionPane.showMessageDialog(gestioneVoliPanel, "Volo aggiunto con successo!");
            pulisci();
            areaAmmGUI.aggiornaTabellaOrario();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    gestioneVoliPanel,
                    "Errore: esiste già un volo con questo codice univoco!\nDettaglio: " + ex.getMessage(),
                    "Errore inserimento volo",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void rimuoviVolo() {
        String codice = safeText(codiceUnivocoTextField);

        if (codice.isEmpty()) {
            JOptionPane.showMessageDialog(
                    gestioneVoliPanel,
                    "Inserisci il codice univoco del volo da rimuovere.",
                    "Errore rimozione",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        int conferma = JOptionPane.showConfirmDialog(
                gestioneVoliPanel,
                "Sei sicuro di voler rimuovere il volo con codice " + codice + "?",
                "Conferma rimozione",
                JOptionPane.YES_NO_OPTION
        );

        if (conferma == JOptionPane.YES_OPTION) {
            boolean successo = controller.rimuoviVolo(codice);

            if (successo) {
                JOptionPane.showMessageDialog(
                        gestioneVoliPanel,
                        "Volo rimosso con successo!",
                        "Rimozione completata",
                        JOptionPane.INFORMATION_MESSAGE
                );
                pulisci();
                areaAmmGUI.aggiornaTabellaOrario();
            } else {
                JOptionPane.showMessageDialog(
                        gestioneVoliPanel,
                        "Nessun volo trovato con codice " + codice,
                        "Errore rimozione",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void cercaVoloPerCodice() {
        String codice = safeText(codiceUnivocoTextField);
        if (codice.isEmpty()) {
            JOptionPane.showMessageDialog(gestioneVoliPanel,
                    "Inserisci un codice volo da cercare.",
                    TITLE_CERCA_VOLO, JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Volo v = controller.cercaVolo(codice);
            if (v == null) {
                JOptionPane.showMessageDialog(gestioneVoliPanel,
                        "Nessun volo trovato con codice " + codice,
                        TITLE_CERCA_VOLO, JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            codiceUnivocoTextField.setText(v.getCodiceUnivoco());
            compagniaTextField.setText(v.getCompagniaAerea());
            dataTextField.setText(v.getDataVolo());
            orarioPrevistoTextField.setText(v.getOrarioPrevisto());
            orarioStimatoTextField.setText(v.getOrarioStimato());
            altroAeroportoTextField.setText(v.getAeroporto());
            gateTextField.setText(v.getGate());

            if (statoVoloComboBox != null) {
                StatoVolo st = v.getStato();
                if (st != null) statoVoloComboBox.setSelectedItem(st);
                else if (statoVoloComboBox.getItemCount() > 0) statoVoloComboBox.setSelectedIndex(0);
            }

            String direzione = v.getArrivoPartenza();
            if (ARRIVO.equalsIgnoreCase(direzione)) arrivoRadioButton.setSelected(true);
            else partenzaRadioButton.setSelected(true);
            toggleDirezione();

            setLockCodice(true);
            codiceVoloSelezionato = v.getCodiceUnivoco();
            confermaModificheButton.setEnabled(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(gestioneVoliPanel,
                    "Errore durante la ricerca: " + ex.getMessage(),
                    TITLE_CERCA_VOLO, JOptionPane.ERROR_MESSAGE);
        }
    }

    private void confermaModifiche() {
        if (codiceVoloSelezionato == null || codiceVoloSelezionato.isEmpty()) {
            JOptionPane.showMessageDialog(gestioneVoliPanel,
                    "Prima cerca e carica un volo da modificare.",
                    "Modifica volo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String compagnia = safeText(compagniaTextField);
        String data = safeText(dataTextField);
        String orarioPrevisto = safeText(orarioPrevistoTextField);
        String orarioStimato = safeText(orarioStimatoTextField);
        String aeroporto = safeText(altroAeroportoTextField).toUpperCase();
        String gate = safeText(gateTextField);
        StatoVolo stato = (StatoVolo) statoVoloComboBox.getSelectedItem();
        String direzione = arrivoRadioButton.isSelected() ? ARRIVO : PARTENZA;

        String errore = valida(codiceVoloSelezionato, compagnia, data, aeroporto, orarioPrevisto, orarioStimato, gate);
        if (errore != null) {
            JOptionPane.showMessageDialog(gestioneVoliPanel, errore, "Errore modifica volo", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Controller.VoloInput input = new Controller.VoloInput(
                    codiceVoloSelezionato,
                    compagnia,
                    data,
                    orarioPrevisto,
                    orarioStimato,
                    stato,
                    direzione,
                    aeroporto,
                    gate
            );
            controller.aggiornaVolo(input);

            JOptionPane.showMessageDialog(gestioneVoliPanel, "Volo aggiornato con successo!");
            areaAmmGUI.aggiornaTabellaOrario();
            pulisci();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(gestioneVoliPanel,
                    "Aggiornamento non riuscito: " + ex.getMessage(),
                    "Modifica volo", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Ridotta a 7 parametri ed elimina parametri inutilizzati (direzione, stato)
    private String valida(String codice,
                          String compagnia,
                          String data,
                          String otherAirport,
                          String orarioPrevisto,
                          String orarioStimato,
                          String gate) {

        if (codice.isEmpty()) return "Codice univoco volo obbligatorio.";

        if (compagnia.isEmpty()) return "Compagnia obbligatoria.";

        if (!Pattern.matches("\\d{4}-\\d{2}-\\d{2}", data))
            return "Data deve essere nel formato yyyy-MM-dd.";

        if (!Pattern.matches("[A-Za-z]{3}", otherAirport))
            return "Altro aeroporto deve essere un codice IATA (3 lettere).";

        if (otherAirport.equalsIgnoreCase(AEROPORTO_LOCALE))
            return "Altro aeroporto deve essere diverso da " + AEROPORTO_LOCALE + ".";

        if (orarioPrevisto == null || !Pattern.matches(TIME_PATTERN, orarioPrevisto))
            return "Orario previsto (HH:mm) obbligatorio.";

        if (orarioStimato == null || !Pattern.matches(TIME_PATTERN, orarioStimato))
            return "Orario stimato (HH:mm) obbligatorio.";

        if (gate == null || gate.isEmpty())
            return "Il campo Gate è obbligatorio.";

        return null;
    }

    private void pulisci() {
        codiceUnivocoTextField.setText("");
        compagniaTextField.setText("");
        dataTextField.setText("");
        altroAeroportoTextField.setText("");
        if (orarioPrevistoTextField != null) orarioPrevistoTextField.setText("");
        if (orarioStimatoTextField != null) orarioStimatoTextField.setText("");
        if (gateTextField != null) gateTextField.setText("");
        if (statoVoloComboBox != null && statoVoloComboBox.getItemCount() > 0) statoVoloComboBox.setSelectedIndex(0);

        partenzaRadioButton.setSelected(true);
        toggleDirezione();

        setLockCodice(false);
        codiceVoloSelezionato = null;

        confermaModificheButton.setEnabled(false);
    }

    private void setLockCodice(boolean lock) {
        if (codiceUnivocoTextField != null) {
            codiceUnivocoTextField.setEditable(!lock);
        }
        if (cercaCodiceButton != null) {
            cercaCodiceButton.setEnabled(!lock);
        }
    }

    private String safeText(JTextField f) {
        return f == null ? "" : f.getText().trim();
    }

    // --- Stile componenti ---
    private JLabel styledLabelWhite(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font(FONT_FAMILY, Font.BOLD, 14));
        l.setForeground(Color.WHITE);
        return l;
    }
    private JTextField styledTextFieldWhite(String text) {
        JTextField tf = new JTextField(text, 13);
        tf.setFont(new Font(FONT_FAMILY, Font.PLAIN, 14));
        tf.setBackground(panelBgColor);
        tf.setForeground(mainGradientStart);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255,255,255,180), 1, true),
                BorderFactory.createEmptyBorder(5, 14, 5, 14)
        ));
        tf.setCaretColor(mainGradientStart);
        return tf;
    }
    private JComboBox<StatoVolo> styledComboBoxStato(StatoVolo[] items) {
        JComboBox<StatoVolo> cb = new JComboBox<>();
        cb.setFont(new Font(FONT_FAMILY, Font.PLAIN, 13));
        cb.setBackground(Color.WHITE);
        cb.setForeground(mainGradientStart);
        cb.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(mainGradientStart, 1, true),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
        for (StatoVolo s : items) cb.addItem(s);
        return cb;
    }
    private JButton gradientButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font(FONT_FAMILY, Font.BOLD, 14));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 32, 8, 32));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                b.setForeground(Color.WHITE);
                b.repaint();
            }
            // mouseExited rimosso: identico a mouseEntered (S4144)
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

    public JPanel getPanelDatiVolo() {
        return gestioneVoliPanel;
    }
}