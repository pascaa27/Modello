package gui;

import controller.Controller;
import model.Bagaglio;
import model.StatoBagaglio;

import javax.swing.*;
import java.awt.*;

public class GestioneBagagliGUI {
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

    public GestioneBagagliGUI(Controller controller, AreaPersonaleAmmGUI areaAmmGUI) {
        this.controller = controller;
        this.areaAmmGUI = areaAmmGUI;

        // Gradient panel
        panelBagaglio = new JPanel() {
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

        // Popola combo stato
        for (StatoBagaglio stato : StatoBagaglio.values()) {
            statoBagaglioComboBox.addItem(stato);
        }

        // Listener pulsanti
        aggiungiBagaglioButton.addActionListener(e -> aggiungiBagaglio());
        rimuoviBagaglioButton.addActionListener(e -> rimuoviBagaglio());
        modificaBagaglioButton.addActionListener(e -> modificaBagaglio());
    }

    // --- Stile componenti ---
    private JLabel styledLabelWhite(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(Color.WHITE);
        return l;
    }
    private JTextField styledTextFieldWhite(String text) {
        JTextField tf = new JTextField(text, 13);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBackground(panelBgColor);
        tf.setForeground(mainGradientStart);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255,255,255,180), 1, true),
                BorderFactory.createEmptyBorder(5, 14, 5, 14)
        ));
        tf.setCaretColor(mainGradientStart);
        return tf;
    }
    private JComboBox<StatoBagaglio> styledComboBoxStato(StatoBagaglio[] items) {
        JComboBox<StatoBagaglio> cb = new JComboBox<>();
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb.setBackground(Color.WHITE);
        cb.setForeground(mainGradientStart);
        cb.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(mainGradientStart, 1, true),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
        for (StatoBagaglio s : items) cb.addItem(s);
        return cb;
    }
    private JButton gradientButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
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

    // --- Logica funzionale ---

    private void aggiungiBagaglio() {
        String codice = codiceBagaglioTextField.getText().trim();
        StatoBagaglio stato = (StatoBagaglio) statoBagaglioComboBox.getSelectedItem();

        if (codice.isEmpty() || stato == null) {
            JOptionPane.showMessageDialog(panelBagaglio, "Compila tutti i campi!");
            return;
        }

        boolean added = controller.aggiungiBagaglio(codice, stato);
        if (added) {
            JOptionPane.showMessageDialog(panelBagaglio, "Bagaglio aggiunto con successo!");
            codiceBagaglioTextField.setText("");
            areaAmmGUI.caricaTuttiBagagli();
        } else {
            JOptionPane.showMessageDialog(panelBagaglio, "Esiste già un bagaglio con questo codice!", "Duplicato", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void modificaBagaglio() {
        String codice = codiceBagaglioTextField.getText().trim();
        StatoBagaglio nuovoStato = (StatoBagaglio) statoBagaglioComboBox.getSelectedItem();

        if (codice.isEmpty() || nuovoStato == null) {
            JOptionPane.showMessageDialog(panelBagaglio, "Inserisci codice e stato!");
            return;
        }

        Bagaglio daAggiornare = controller.getBagagli().stream()
                .filter(b -> b.getCodUnivoco().equalsIgnoreCase(codice))
                .findFirst().orElse(null);

        if (daAggiornare == null) {
            JOptionPane.showMessageDialog(panelBagaglio, "Bagaglio non trovato.");
            return;
        }

        daAggiornare.setStato(nuovoStato);

        boolean ok = controller.aggiornaBagaglio(daAggiornare);
        if (ok) {
            JOptionPane.showMessageDialog(panelBagaglio, "Bagaglio aggiornato!");
            codiceBagaglioTextField.setText("");
            areaAmmGUI.caricaTuttiBagagli();
        } else {
            JOptionPane.showMessageDialog(panelBagaglio, "Aggiornamento fallito.", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rimuoviBagaglio() {
        String codice = codiceBagaglioTextField.getText().trim();

        if (codice.isEmpty()) {
            JOptionPane.showMessageDialog(panelBagaglio, "Inserisci il codice del bagaglio.");
            return;
        }

        int conferma = JOptionPane.showConfirmDialog(panelBagaglio,
                "Vuoi eliminare il bagaglio " + codice + "?",
                "Conferma eliminazione",
                JOptionPane.YES_NO_OPTION);

        if (conferma != JOptionPane.YES_OPTION) return;

        boolean removed = controller.rimuoviBagaglio(codice);
        if (removed) {
            JOptionPane.showMessageDialog(panelBagaglio, "Bagaglio rimosso con successo!");
            codiceBagaglioTextField.setText("");
            areaAmmGUI.caricaTuttiBagagli();
        } else {
            JOptionPane.showMessageDialog(panelBagaglio, "Nessun bagaglio trovato con quel codice.", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    public JPanel getPanelBagaglio() {
        return panelBagaglio;
    }
}