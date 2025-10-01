package gui;

import controller.Controller;
import model.Bagaglio;
import model.StatoBagaglio;

import javax.swing.*;
import java.awt.*;

/**
 * Classe che gestisce l'interfaccia grafica per la gestione dei bagagli.
 */
public class GestioneBagagliGUI {
    private static final String FONT_FAMILY = "Segoe UI";

    private JPanel panelBagaglio;
    private JTextField codiceBagaglioTextField;
    private JComboBox<StatoBagaglio> statoBagaglioComboBox;
    private JButton aggiungiBagaglioButton;
    private JButton rimuoviBagaglioButton;
    private JButton modificaBagaglioButton;

    private final Controller controller;
    private final AreaPersonaleAmmGUI areaAmmGUI;

    // Palette colori
    private final Color mainGradientStart = new Color(30, 87, 153);
    private final Color mainGradientEnd   = new Color(125, 185, 232);
    private final Color panelBgColor      = new Color(245, 249, 255);
    private final Color buttonColor       = new Color(60, 130, 200);
    private final Color buttonHoverColor  = new Color(30, 87, 153);

    /**
     * Costruttore: crea l'interfaccia per gestire i bagagli.
     * Inizializza i campi di testo, la ComboBox degli stati e i bottoni.
     * Collega i bottoni ai rispettivi listener per aggiungere, modificare e rimuovere bagagli.
     *
     * @param controller Controller per gestire la logica dei bagagli
     * @param areaAmmGUI Riferimento alla GUI dell'area personale amministratore
     */
    public GestioneBagagliGUI(Controller controller, AreaPersonaleAmmGUI areaAmmGUI) {
        this.controller = controller;
        this.areaAmmGUI = areaAmmGUI;

        // Gradient panel
        panelBagaglio = new JPanel() {
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
        panelBagaglio.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(14, 18, 8, 18);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Codice
        gbc.gridx = 0; gbc.gridy = 0;
        panelBagaglio.add(styledLabelWhite("Codice Bagaglio:"), gbc);
        gbc.gridx = 1;
        codiceBagaglioTextField = styledTextFieldWhite("");
        panelBagaglio.add(codiceBagaglioTextField, gbc);

        // Stato
        gbc.gridx = 0; gbc.gridy++;
        panelBagaglio.add(styledLabelWhite("Stato:"), gbc);
        gbc.gridx = 1;
        statoBagaglioComboBox = styledComboBoxStato(StatoBagaglio.values());
        panelBagaglio.add(statoBagaglioComboBox, gbc);

        // Bottoni
        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 18, 10, 18);
        JPanel bottoniPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 4));
        bottoniPanel.setOpaque(false);
        aggiungiBagaglioButton = gradientButton("Aggiungi Bagaglio");
        modificaBagaglioButton = gradientButton("Modifica Bagaglio");
        rimuoviBagaglioButton = gradientButton("Rimuovi Bagaglio");
        bottoniPanel.add(aggiungiBagaglioButton);
        bottoniPanel.add(modificaBagaglioButton);
        bottoniPanel.add(rimuoviBagaglioButton);
        panelBagaglio.add(bottoniPanel, gbc);

        // Listener pulsanti
        aggiungiBagaglioButton.addActionListener(e -> aggiungiBagaglio());
        rimuoviBagaglioButton.addActionListener(e -> rimuoviBagaglio());
        modificaBagaglioButton.addActionListener(e -> modificaBagaglio());
    }

    // --- Stile componenti ---
    /**
     * Crea un'etichetta con testo bianco e font personalizzato.
     *
     * @param text Testo dell'etichetta
     * @return JLabel stilizzata
     */
    private JLabel styledLabelWhite(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font(FONT_FAMILY, Font.BOLD, 14));
        l.setForeground(Color.WHITE);
        return l;
    }

    /**
     * Crea un campo di testo stilizzato per l'inserimento di dati.
     *
     * @param text Testo iniziale
     * @return JTextField stilizzato
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
     * Crea una ComboBox per selezionare lo stato del bagaglio con stile personalizzato.
     *
     * @param items Array di valori {@link StatoBagaglio} da inserire nella ComboBox
     * @return JComboBox stilizzata contenente gli stati del bagaglio
     */
    private JComboBox<StatoBagaglio> styledComboBoxStato(StatoBagaglio[] items) {
        JComboBox<StatoBagaglio> cb = new JComboBox<>();
        cb.setFont(new Font(FONT_FAMILY, Font.PLAIN, 13));
        cb.setBackground(Color.WHITE);
        cb.setForeground(mainGradientStart);
        cb.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(mainGradientStart, 1, true),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
        for(StatoBagaglio s : items) cb.addItem(s);
        return cb;
    }

    /**
     * Crea un JButton con effetto gradiente e stile personalizzato.
     *
     * @param text Testo del pulsante
     * @return JButton stilizzato
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
            // mouseExited rimosso: era identico a mouseEntered (S4144)
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

    // --- Logica funzionale ---
    /**
     * Aggiunge un nuovo bagaglio tramite il controller.
     * Valida i campi e mostra messaggi di errore o conferma tramite JOptionPane.
     */
    private void aggiungiBagaglio() {
        String codice = codiceBagaglioTextField.getText().trim();
        StatoBagaglio stato = (StatoBagaglio) statoBagaglioComboBox.getSelectedItem();

        if(codice.isEmpty() || stato == null) {
            JOptionPane.showMessageDialog(panelBagaglio, "Compila tutti i campi!");
            return;
        }

        boolean added = controller.aggiungiBagaglio(codice, stato);
        if(added) {
            JOptionPane.showMessageDialog(panelBagaglio, "Bagaglio aggiunto con successo!");
            codiceBagaglioTextField.setText("");
            areaAmmGUI.caricaTuttiBagagli();
        } else {
            JOptionPane.showMessageDialog(panelBagaglio, "Esiste già un bagaglio con questo codice!", "Duplicato", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Modifica lo stato di un bagaglio esistente.
     * Verifica l'esistenza del bagaglio e aggiorna il controller.
     * Mostra messaggi di conferma o errore.
     */
    private void modificaBagaglio() {
        String codice = codiceBagaglioTextField.getText().trim();
        StatoBagaglio nuovoStato = (StatoBagaglio) statoBagaglioComboBox.getSelectedItem();

        if(codice.isEmpty() || nuovoStato == null) {
            JOptionPane.showMessageDialog(panelBagaglio, "Inserisci codice e stato!");
            return;
        }

        Bagaglio daAggiornare = controller.getBagagli().stream()
                .filter(b -> b.getCodUnivoco().equalsIgnoreCase(codice))
                .findFirst().orElse(null);

        if(daAggiornare == null) {
            JOptionPane.showMessageDialog(panelBagaglio, "Bagaglio non trovato.");
            return;
        }

        daAggiornare.setStato(nuovoStato);

        boolean ok = controller.aggiornaBagaglio(daAggiornare);
        if(ok) {
            JOptionPane.showMessageDialog(panelBagaglio, "Bagaglio aggiornato!");
            codiceBagaglioTextField.setText("");
            areaAmmGUI.caricaTuttiBagagli();
        } else {
            JOptionPane.showMessageDialog(panelBagaglio, "Aggiornamento fallito.", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Rimuove un bagaglio esistente dal sistema.
     * Chiede conferma all'utente prima di eliminare e aggiorna il controller.
     * Mostra messaggi di conferma o errore.
     */
    private void rimuoviBagaglio() {
        String codice = codiceBagaglioTextField.getText().trim();

        if(codice.isEmpty()) {
            JOptionPane.showMessageDialog(panelBagaglio, "Inserisci il codice del bagaglio.");
            return;
        }

        int conferma = JOptionPane.showConfirmDialog(panelBagaglio,
                "Vuoi eliminare il bagaglio " + codice + "?",
                "Conferma eliminazione",
                JOptionPane.YES_NO_OPTION);

        if(conferma != JOptionPane.YES_OPTION) return;

        boolean removed = controller.rimuoviBagaglio(codice);
        if(removed) {
            JOptionPane.showMessageDialog(panelBagaglio, "Bagaglio rimosso con successo!");
            codiceBagaglioTextField.setText("");
            areaAmmGUI.caricaTuttiBagagli();
        } else {
            JOptionPane.showMessageDialog(panelBagaglio, "Nessun bagaglio trovato con quel codice.", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Restituisce il pannello principale della GUI dei bagagli.
     *
     * @return JPanel contenente tutti i componenti dell'interfaccia
     */
    public JPanel getPanelBagaglio() {
        return panelBagaglio;
    }
}