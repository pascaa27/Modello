package gui;

import controller.Controller;
import model.StatoVolo;
import model.Volo;
import javax.swing.*;
import java.awt.*;
import java.util.regex.Pattern;

/**
 * Classe responsabile della gestione della GUI per l'inserimento,
 * modifica, ricerca e cancellazione dei voli da parte di un amministratore.
 */
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

    /**
     * Costruttore: crea la GUI per la gestione dei voli.
     *
     * @param controller   controller applicativo che gestisce la logica dei voli
     * @param areaAmmGUI   riferimento all'area amministratore per l'aggiornamento delle tabelle
     */
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
        for(StatoVolo sv : StatoVolo.values()) {
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

    /**
     * Imposta il valore di default per la direzione del volo (Partenza).
     */
    private void setDirezioneDefault() {
        if(partenzaRadioButton != null) {
            partenzaRadioButton.setSelected(true);
        }
    }

    /**
     * Abilita/disabilita campi in base alla selezione di arrivo/partenza.
     */
    private void toggleDirezione() {
        if(gateTextField != null) {
            gateTextField.setEnabled(true);
        }
    }

    /**
     * Aggiunge un nuovo volo sulla base dei dati inseriti nei campi della GUI.
     * Esegue validazioni e mostra eventuali messaggi di errore.
     */
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
        if(errore != null) {
            JOptionPane.showMessageDialog(
                    gestioneVoliPanel,
                    errore,
                    "Errore inserimento volo",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if(gateTextField == null) {
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
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(
                    gestioneVoliPanel,
                    "Errore: esiste già un volo con questo codice univoco!\nDettaglio: " + ex.getMessage(),
                    "Errore inserimento volo",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Rimuove un volo identificato dal codice univoco inserito.
     * Richiede conferma all'utente prima di procedere.
     */
    private void rimuoviVolo() {
        String codice = safeText(codiceUnivocoTextField);

        if(codice.isEmpty()) {
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

        if(conferma == JOptionPane.YES_OPTION) {
            boolean successo = controller.rimuoviVolo(codice);

            if(successo) {
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

    /**
     * Cerca un volo tramite codice univoco e ne popola i campi
     * nel form della GUI.
     */
    private void cercaVoloPerCodice() {
        String codice = safeText(codiceUnivocoTextField);
        if(codice.isEmpty()) {
            JOptionPane.showMessageDialog(gestioneVoliPanel,
                    "Inserisci un codice volo da cercare.",
                    TITLE_CERCA_VOLO, JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Volo v = controller.cercaVolo(codice);
            if(v == null) {
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

            if(statoVoloComboBox != null) {
                StatoVolo st = v.getStato();
                if(st != null) statoVoloComboBox.setSelectedItem(st);
                else if(statoVoloComboBox.getItemCount() > 0) statoVoloComboBox.setSelectedIndex(0);
            }

            String direzione = v.getArrivoPartenza();
            if(ARRIVO.equalsIgnoreCase(direzione)) arrivoRadioButton.setSelected(true);
            else partenzaRadioButton.setSelected(true);
            toggleDirezione();

            setLockCodice(true);
            codiceVoloSelezionato = v.getCodiceUnivoco();
            confermaModificheButton.setEnabled(true);

        } catch(Exception ex) {
            JOptionPane.showMessageDialog(gestioneVoliPanel,
                    "Errore durante la ricerca: " + ex.getMessage(),
                    TITLE_CERCA_VOLO, JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Conferma le modifiche apportate a un volo precedentemente selezionato
     * e lo aggiorna tramite il {@link Controller}.
     */
    private void confermaModifiche() {
        if(codiceVoloSelezionato == null || codiceVoloSelezionato.isEmpty()) {
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
        if(errore != null) {
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
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(gestioneVoliPanel,
                    "Aggiornamento non riuscito: " + ex.getMessage(),
                    "Modifica volo", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Valida i campi obbligatori di un volo.
     *
     * @param codice        codice univoco del volo
     * @param compagnia     compagnia aerea
     * @param data          data del volo (yyyy-MM-dd)
     * @param otherAirport  aeroporto di arrivo o partenza
     * @param orarioPrevisto orario previsto (HH:mm)
     * @param orarioStimato  orario stimato (HH:mm)
     * @param gate          gate di imbarco
     * @return stringa di errore, oppure {@code null} se la validazione è superata
     */
    private String valida(String codice,
                          String compagnia,
                          String data,
                          String otherAirport,
                          String orarioPrevisto,
                          String orarioStimato,
                          String gate) {

        if(codice.isEmpty()) return "Codice univoco volo obbligatorio.";

        if(compagnia.isEmpty()) return "Compagnia obbligatoria.";

        if(!Pattern.matches("\\d{4}-\\d{2}-\\d{2}", data))
            return "Data deve essere nel formato yyyy-MM-dd.";

        if(!Pattern.matches("[A-Za-z]{3}", otherAirport))
            return "Altro aeroporto deve essere un codice IATA (3 lettere).";

        if(otherAirport.equalsIgnoreCase(AEROPORTO_LOCALE))
            return "Altro aeroporto deve essere diverso da " + AEROPORTO_LOCALE + ".";

        if(orarioPrevisto == null || !Pattern.matches(TIME_PATTERN, orarioPrevisto))
            return "Orario previsto (HH:mm) obbligatorio.";

        if(orarioStimato == null || !Pattern.matches(TIME_PATTERN, orarioStimato))
            return "Orario stimato (HH:mm) obbligatorio.";

        if(gate == null || gate.isEmpty())
            return "Il campo Gate è obbligatorio.";

        return null;
    }

    /**
     * Pulisce tutti i campi della GUI e ripristina lo stato iniziale.
     */
    private void pulisci() {
        codiceUnivocoTextField.setText("");
        compagniaTextField.setText("");
        dataTextField.setText("");
        altroAeroportoTextField.setText("");
        if(orarioPrevistoTextField != null) orarioPrevistoTextField.setText("");
        if(orarioStimatoTextField != null) orarioStimatoTextField.setText("");
        if(gateTextField != null) gateTextField.setText("");
        if(statoVoloComboBox != null && statoVoloComboBox.getItemCount() > 0) statoVoloComboBox.setSelectedIndex(0);

        partenzaRadioButton.setSelected(true);
        toggleDirezione();

        setLockCodice(false);
        codiceVoloSelezionato = null;

        confermaModificheButton.setEnabled(false);
    }

    /**
     * Blocca/sblocca l'editing del codice univoco del volo.
     *
     * @param lock true per bloccare il campo codice, false per sbloccarlo
     */
    private void setLockCodice(boolean lock) {
        if(codiceUnivocoTextField != null) {
            codiceUnivocoTextField.setEditable(!lock);
        }
        if(cercaCodiceButton != null) {
            cercaCodiceButton.setEnabled(!lock);
        }
    }

    /**
     * Ritorna il testo contenuto in un {@link JTextField}, oppure stringa vuota se nullo.
     *
     * @param f text field da cui leggere il contenuto
     * @return testo pulito senza spazi o stringa vuota
     */
    private String safeText(JTextField f) {
        return f == null ? "" : f.getText().trim();
    }

    // --- Stile componenti ---
    /**
     * Crea un {@link JLabel} bianco con font predefinito.
     *
     * @param text testo della label
     * @return label stilizzata
     */
    private JLabel styledLabelWhite(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font(FONT_FAMILY, Font.BOLD, 14));
        l.setForeground(Color.WHITE);
        return l;
    }

    /**
     * Crea un {@link JTextField} con sfondo chiaro e bordi arrotondati.
     *
     * @param text valore iniziale del campo
     * @return text field stilizzato
     */
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

    /**
     * Crea una combo box per la selezione dello stato volo.
     *
     * @param items array di stati disponibili
     * @return combo box stilizzata
     */
    private JComboBox<StatoVolo> styledComboBoxStato(StatoVolo[] items) {
        JComboBox<StatoVolo> cb = new JComboBox<>();
        cb.setFont(new Font(FONT_FAMILY, Font.PLAIN, 13));
        cb.setBackground(Color.WHITE);
        cb.setForeground(mainGradientStart);
        cb.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(mainGradientStart, 1, true),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
        for(StatoVolo s : items) cb.addItem(s);
        return cb;
    }

    /**
     * Crea un pulsante con stile gradiente e arrotondato.
     *
     * @param text testo del pulsante
     * @return pulsante stilizzato
     */
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

    /**
     * Restituisce il pannello Swing principale contenente
     * tutti i campi e i pulsanti della gestione voli.
     *
     * @return pannello dati volo
     */
    public JPanel getPanelDatiVolo() {
        return gestioneVoliPanel;
    }
}