package gui;

import controller.Controller;
import model.Prenotazione;
import model.StatoPrenotazione;
import model.UtenteGenerico;
import model.Volo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

public class EffettuaPrenotazioneGUI {
    private JPanel effettuaPrenotazionePanel;
    private JTextField nomeTextField;
    private JTextField cognomeTextField;
    private JTextField codiceFiscaleTextField;
    private JTextField emailTextField;
    private JComboBox<Integer> annoInizioComboBox;
    private JComboBox<Integer> meseInizioComboBox;
    private JComboBox<Integer> giornoInizioComboBox;
    private JComboBox<Integer> annoFineComboBox;
    private JComboBox<Integer> meseFineComboBox;
    private JComboBox<Integer> giornoFineComboBox;
    private JTextField aeroportoDestinazioneTextField;
    private JButton prenotaButton;
    private JButton annullaButton;
    private Controller controller;
    private UtenteGenerico utente;

    // Palette colori
    private final Color mainGradientStart = new Color(30, 87, 153);
    private final Color mainGradientEnd   = new Color(125, 185, 232);
    private final Color panelBgColor      = new Color(245, 249, 255);
    private final Color buttonColor       = new Color(60, 130, 200);
    private final Color buttonHoverColor  = new Color(30, 87, 153);

    public EffettuaPrenotazioneGUI(Controller controller, UtenteGenerico utente) {
        this.controller = controller;
        this.utente = utente;

        // Gradient panel
        effettuaPrenotazionePanel = new JPanel() {
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
        effettuaPrenotazionePanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 18, 8, 18);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0;
        effettuaPrenotazionePanel.add(styledLabelWhite("Nome:"), gbc);
        gbc.gridx = 1;
        nomeTextField = styledTextFieldWhite("");
        effettuaPrenotazionePanel.add(nomeTextField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        effettuaPrenotazionePanel.add(styledLabelWhite("Cognome:"), gbc);
        gbc.gridx = 1;
        cognomeTextField = styledTextFieldWhite("");
        effettuaPrenotazionePanel.add(cognomeTextField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        effettuaPrenotazionePanel.add(styledLabelWhite("Codice Fiscale:"), gbc);
        gbc.gridx = 1;
        codiceFiscaleTextField = styledTextFieldWhite("");
        effettuaPrenotazionePanel.add(codiceFiscaleTextField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        effettuaPrenotazionePanel.add(styledLabelWhite("Email:"), gbc);
        gbc.gridx = 1;
        emailTextField = styledTextFieldWhite("");
        effettuaPrenotazionePanel.add(emailTextField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        effettuaPrenotazionePanel.add(styledLabelWhite("Aeroporto Destinazione:"), gbc);
        gbc.gridx = 1;
        aeroportoDestinazioneTextField = styledTextFieldWhite("");
        effettuaPrenotazionePanel.add(aeroportoDestinazioneTextField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        effettuaPrenotazionePanel.add(styledLabelWhite("Data Inizio:"), gbc);
        gbc.gridx = 1;
        JPanel dataInizioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        dataInizioPanel.setOpaque(false);
        annoInizioComboBox = styledComboBoxInt();
        meseInizioComboBox = styledComboBoxInt();
        giornoInizioComboBox = styledComboBoxInt();
        dataInizioPanel.add(annoInizioComboBox);
        dataInizioPanel.add(meseInizioComboBox);
        dataInizioPanel.add(giornoInizioComboBox);
        effettuaPrenotazionePanel.add(dataInizioPanel, gbc);

        gbc.gridx = 0; gbc.gridy++;
        effettuaPrenotazionePanel.add(styledLabelWhite("Data Fine:"), gbc);
        gbc.gridx = 1;
        JPanel dataFinePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        dataFinePanel.setOpaque(false);
        annoFineComboBox = styledComboBoxInt();
        meseFineComboBox = styledComboBoxInt();
        giornoFineComboBox = styledComboBoxInt();
        dataFinePanel.add(annoFineComboBox);
        dataFinePanel.add(meseFineComboBox);
        dataFinePanel.add(giornoFineComboBox);
        effettuaPrenotazionePanel.add(dataFinePanel, gbc);

        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 18, 10, 18);
        JPanel bottoniPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 4));
        bottoniPanel.setOpaque(false);
        prenotaButton = gradientButton("Effettua Prenotazione");
        annullaButton = gradientButton("Annulla");
        bottoniPanel.add(prenotaButton);
        bottoniPanel.add(annullaButton);
        effettuaPrenotazionePanel.add(bottoniPanel, gbc);

        // Listeners
        prenotaButton.addActionListener(e -> effettuaPrenotazione());
        annullaButton.addActionListener(e -> {
            JFrame f = (JFrame) SwingUtilities.getWindowAncestor(effettuaPrenotazionePanel);
            if (f != null) f.dispose();
        });

        // Popola e aggiusta le combo *dopo* l'inizializzazione
        SwingUtilities.invokeLater(() -> {
            if (annoInizioComboBox.getItemCount() == 0) {
                for (int anno = 2025; anno <= 2035; anno++) {
                    annoInizioComboBox.addItem(anno);
                    annoFineComboBox.addItem(anno);
                }
            }
            if (meseInizioComboBox.getItemCount() == 0) {
                for (int m = 1; m <= 12; m++) {
                    meseInizioComboBox.addItem(m);
                    meseFineComboBox.addItem(m);
                }
            }

            ActionListener aggiornaGiorniInizio = e -> aggiornaGiorni(annoInizioComboBox, meseInizioComboBox, giornoInizioComboBox);
            ActionListener aggiornaGiorniFine  = e -> aggiornaGiorni(annoFineComboBox, meseFineComboBox, giornoFineComboBox);

            annoInizioComboBox.addActionListener(aggiornaGiorniInizio);
            meseInizioComboBox.addActionListener(aggiornaGiorniInizio);
            annoFineComboBox.addActionListener(aggiornaGiorniFine);
            meseFineComboBox.addActionListener(aggiornaGiorniFine);

            aggiornaGiorni(annoInizioComboBox, meseInizioComboBox, giornoInizioComboBox);
            aggiornaGiorni(annoFineComboBox, meseFineComboBox, giornoFineComboBox);
        });
    }

    private void aggiornaGiorni(JComboBox<Integer> annoCombo, JComboBox<Integer> meseCombo, JComboBox<Integer> giornoCombo) {
        if (annoCombo == null || meseCombo == null || giornoCombo == null) return;
        Object oAnno = annoCombo.getSelectedItem();
        Object oMese = meseCombo.getSelectedItem();
        if (oAnno == null || oMese == null) return;

        int anno = (int) oAnno;
        int mese = (int) oMese;
        int giorniNelMese = YearMonth.of(anno, mese).lengthOfMonth();

        Integer precedente = (Integer) giornoCombo.getSelectedItem();

        giornoCombo.removeAllItems();
        for (int g = 1; g <= giorniNelMese; g++) {
            giornoCombo.addItem(g);
        }
        if (precedente != null && precedente <= giorniNelMese) {
            giornoCombo.setSelectedItem(precedente);
        } else {
            giornoCombo.setSelectedIndex(0);
        }
    }

    private void effettuaPrenotazione() {
        String nome = nomeTextField.getText().trim();
        String cognome = cognomeTextField.getText().trim();
        String codiceFiscale = codiceFiscaleTextField.getText().trim();
        String email = emailTextField.getText().trim();
        String destinazione = aeroportoDestinazioneTextField.getText().trim().toUpperCase();

        // Validazioni base
        if (nome.isEmpty() || cognome.isEmpty() || codiceFiscale.isEmpty() || destinazione.isEmpty()) {
            JOptionPane.showMessageDialog(effettuaPrenotazionePanel,
                    "Tutti i campi devono essere compilati.", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!Character.isUpperCase(nome.charAt(0)) || !Character.isUpperCase(cognome.charAt(0))) {
            JOptionPane.showMessageDialog(effettuaPrenotazionePanel,
                    "Nome e Cognome devono iniziare con lettera maiuscola.", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate dataInizio = LocalDate.of(
                (int) annoInizioComboBox.getSelectedItem(),
                (int) meseInizioComboBox.getSelectedItem(),
                (int) giornoInizioComboBox.getSelectedItem()
        );
        LocalDate dataFine = LocalDate.of(
                (int) annoFineComboBox.getSelectedItem(),
                (int) meseFineComboBox.getSelectedItem(),
                (int) giornoFineComboBox.getSelectedItem()
        );

        if (dataFine.isBefore(dataInizio)) {
            JOptionPane.showMessageDialog(effettuaPrenotazionePanel,
                    "La data di fine deve essere successiva o uguale alla data di inizio.", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Volo volo = controller.cercaVoloPerDestinazioneEData(destinazione, dataInizio.toString());
            if (volo == null) {
                JOptionPane.showMessageDialog(effettuaPrenotazionePanel,
                        "Nessun volo trovato per questa destinazione e data!", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Controllo duplicati / email registrata
            if (utente != null && utente.isRegistrato()) {
                if (controller.utenteHaPrenotazionePerVolo(utente.getLogin(), volo.getCodiceUnivoco())) {
                    JOptionPane.showMessageDialog(effettuaPrenotazionePanel,
                            "Hai già una prenotazione per questo volo!", "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                // Utente anonimo: deve avere email registrata
                if (!controller.emailRegistrata(email)) {
                    JOptionPane.showMessageDialog(effettuaPrenotazionePanel,
                            "Devi registrarti prima di prenotare con questa email.", "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            String numeroBiglietto = UUID.randomUUID().toString().substring(0, 8);
            Prenotazione pren = controller.aggiungiPrenotazione(
                    numeroBiglietto,
                    "",
                    StatoPrenotazione.CONFERMATA,
                    volo.getCodiceUnivoco(),
                    utente,
                    nome,
                    cognome,
                    codiceFiscale,
                    email,
                    null
            );

            if (pren == null) {
                JOptionPane.showMessageDialog(effettuaPrenotazionePanel,
                        "Creazione prenotazione fallita.", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(effettuaPrenotazionePanel,
                    "Prenotazione effettuata con successo!\n" +
                            "Codice prenotazione: " + pren.getNumBiglietto() + "\n" +
                            "Dal " + dataInizio + " al " + dataFine,
                    "Successo",
                    JOptionPane.INFORMATION_MESSAGE);

            if (utente != null) {
                utente.aggiungiCodicePrenotazione(pren.getNumBiglietto());
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(effettuaPrenotazionePanel,
                    "Errore durante la prenotazione: " + ex.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

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
    private JComboBox<Integer> styledComboBoxInt() {
        JComboBox<Integer> cb = new JComboBox<>();
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb.setBackground(Color.WHITE);
        cb.setForeground(mainGradientStart);
        cb.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(mainGradientStart, 1, true),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
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

    public JPanel getPanel() {
        return effettuaPrenotazionePanel;
    }
}