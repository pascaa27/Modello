package gui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import controller.Controller;
import model.*;

public class CercaModificaPrenotazioneGUI {
    private static final String FONT_FAMILY = "Segoe UI";

    private JPanel panelCercaModificaPrenotazione;
    private JButton cercaButton;
    private JTextField codiceInserimentoTextField;
    private JTextField nomeTextField;
    private JTextField cognomeTextField;
    private JTextField emailTextField;
    private JTextField voloTextField;
    private JComboBox<String> statoVoloComboBox;
    private JButton salvaModificheButton;
    private JButton annullaPrenotazioneButton;
    private JTextArea messaggioTextArea;
    private final Controller controller;
    private Prenotazione prenotazioneCorrente;
    private final Utente utente;
    private DefaultListModel<String> listaModel;

    // Palette colori
    private final Color mainGradientStart = new Color(30, 87, 153);
    private final Color mainGradientEnd   = new Color(125, 185, 232);
    private final Color panelBgColor      = new Color(245, 249, 255);
    private final Color buttonColor       = new Color(60, 130, 200);
    private final Color buttonHoverColor  = new Color(30, 87, 153);

    public CercaModificaPrenotazioneGUI(Controller controller, Utente utente, String codicePrenotazione) {
        this.controller = controller;
        this.utente = utente;

        panelCercaModificaPrenotazione = createGradientPanel();
        panelCercaModificaPrenotazione.setLayout(new BorderLayout(0, 0));

        JPanel cardPanel = buildCardPanel();
        panelCercaModificaPrenotazione.add(cardPanel, BorderLayout.CENTER);

        setupPrenotazioniList();
        populateStatoCombo();
        setCampiPrenotazioneAbilitati(false);
        initialSearchIfPresent(codicePrenotazione);
        attachActionListeners();
    }

    // UI builders

    private JPanel createGradientPanel() {
        return new JPanel() {
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
    }

    private JPanel buildCardPanel() {
        JPanel cardPanel = new JPanel(new GridBagLayout()) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(480, 420);
            }
        };
        cardPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(9, 17, 9, 17);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0;
        cardPanel.add(styledLabelWhite("Codice Prenotazione:"), gbc);
        gbc.gridx = 1;
        codiceInserimentoTextField = styledTextFieldWhite("");
        cardPanel.add(codiceInserimentoTextField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        cardPanel.add(styledLabelWhite("Nome:"), gbc);
        gbc.gridx = 1;
        nomeTextField = styledTextFieldWhite("");
        cardPanel.add(nomeTextField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        cardPanel.add(styledLabelWhite("Cognome:"), gbc);
        gbc.gridx = 1;
        cognomeTextField = styledTextFieldWhite("");
        cardPanel.add(cognomeTextField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        cardPanel.add(styledLabelWhite("Email:"), gbc);
        gbc.gridx = 1;
        emailTextField = styledTextFieldWhite("");
        cardPanel.add(emailTextField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        cardPanel.add(styledLabelWhite("Volo:"), gbc);
        gbc.gridx = 1;
        voloTextField = styledTextFieldWhite("");
        cardPanel.add(voloTextField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        cardPanel.add(styledLabelWhite("Stato:"), gbc);
        gbc.gridx = 1;
        statoVoloComboBox = styledComboBoxWhite();
        cardPanel.add(statoVoloComboBox, gbc);

        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        messaggioTextArea = styledTextArea();
        cardPanel.add(messaggioTextArea, gbc);

        // Bottoni su una riga
        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        JPanel rowBottoniPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        rowBottoniPanel.setOpaque(false);
        cercaButton = gradientButton("Cerca");
        salvaModificheButton = gradientButton("Salva Modifiche");
        rowBottoniPanel.add(cercaButton);
        rowBottoniPanel.add(salvaModificheButton);
        cardPanel.add(rowBottoniPanel, gbc);

        // Bottone annulla prenotazione sotto, centrale
        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        JPanel annullaPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        annullaPanel.setOpaque(false);
        annullaPrenotazioneButton = gradientButton("Annulla Prenotazione");
        annullaPrenotazioneButton.setPreferredSize(new Dimension(240, 34));
        annullaPanel.add(annullaPrenotazioneButton);
        cardPanel.add(annullaPanel, gbc);

        return cardPanel;
    }

    private void setupPrenotazioniList() {
        List<Prenotazione> prenotazioniUtente = controller.getPrenotazioniUtente((UtenteGenerico) utente);
        if (prenotazioniUtente == null || prenotazioniUtente.isEmpty()) {
            return; // lista a sinistra solo se esistono
        }

        // Usa la variabile di istanza
        listaModel = new DefaultListModel<>();
        JList<String> listaPrenotazioni = new JList<>(listaModel);
        listaPrenotazioni.setFont(new Font(FONT_FAMILY, Font.PLAIN, 13));
        listaPrenotazioni.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (c instanceof JLabel label && value != null) {
                    label.setText("Codice Prenotazione: " + value);
                }
                return c;
            }
        });

        JScrollPane listaPrenotazioniScrollPane = new JScrollPane(listaPrenotazioni);
        listaPrenotazioniScrollPane.setPreferredSize(new Dimension(220, 420));
        panelCercaModificaPrenotazione.add(listaPrenotazioniScrollPane, BorderLayout.WEST);

        listaPrenotazioni.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String codiceSelezionato = listaPrenotazioni.getSelectedValue();
                codiceInserimentoTextField.setText(codiceSelezionato);
                cercaPrenotazione();
            }
        });

        // Popola il modello
        for (Prenotazione p : prenotazioniUtente) {
            listaModel.addElement(p.getNumBiglietto());
        }
    }

    private void populateStatoCombo() {
        statoVoloComboBox.removeAllItems();
        for (StatoPrenotazione stato : StatoPrenotazione.values()) {
            statoVoloComboBox.addItem(stato.name());
        }
    }

    private void initialSearchIfPresent(String codicePrenotazione) {
        if (codicePrenotazione != null && !codicePrenotazione.isEmpty()) {
            codiceInserimentoTextField.setText(codicePrenotazione);
            cercaPrenotazione();
        }
    }

    private void attachActionListeners() {
        cercaButton.addActionListener(e -> cercaPrenotazione());
        salvaModificheButton.addActionListener(e -> salvaModifiche());
        annullaPrenotazioneButton.addActionListener(e -> annullaPrenotazione());
    }

    // --- Stile componenti ---
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
                BorderFactory.createLineBorder(new Color(255,255,255, 180), 1, true),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));
        tf.setCaretColor(mainGradientStart);
        return tf;
    }

    private JComboBox<String> styledComboBoxWhite() {
        JComboBox<String> cb = new JComboBox<>();
        cb.setFont(new Font(FONT_FAMILY, Font.PLAIN, 12));
        cb.setBackground(Color.WHITE);
        cb.setForeground(mainGradientStart);
        cb.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(mainGradientStart, 1, true),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
        return cb;
    }

    private JTextArea styledTextArea() {
        JTextArea ta = new JTextArea(3, 24);
        ta.setFont(new Font(FONT_FAMILY, Font.PLAIN, 13));
        ta.setBackground(panelBgColor);
        ta.setForeground(mainGradientStart);
        ta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(mainGradientStart, 1, true),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setEditable(false);
        return ta;
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
        b.setBorder(BorderFactory.createEmptyBorder(7, 24, 7, 24));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                b.setForeground(Color.WHITE);
                b.repaint();
            }
            // mouseExited rimosso per evitare identicità con mouseEntered (S4144)
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

    private void cercaPrenotazione() {
        String codice = codiceInserimentoTextField.getText().trim();
        if (codice.isEmpty()) {
            messaggioTextArea.setText("Inserisci un codice prenotazione.");
            setCampiPrenotazioneAbilitati(false);
            return;
        }

        prenotazioneCorrente = controller.cercaPrenotazione(codice);
        if (prenotazioneCorrente != null) {
            nomeTextField.setText(prenotazioneCorrente.getDatiPasseggero().getNome());
            cognomeTextField.setText(prenotazioneCorrente.getDatiPasseggero().getCognome());
            emailTextField.setText(prenotazioneCorrente.getDatiPasseggero().getEmail());
            voloTextField.setText(prenotazioneCorrente.getVolo().getCodiceUnivoco());
            statoVoloComboBox.setSelectedItem(prenotazioneCorrente.getStato().name());

            codiceInserimentoTextField.setEditable(false);

            messaggioTextArea.setText("Prenotazione trovata!\nPuoi modificarla oppure annullarla.");
            setCampiPrenotazioneAbilitati(true);
        } else {
            messaggioTextArea.setText("Prenotazione non trovata.");
            pulisciCampiPrenotazione();
            setCampiPrenotazioneAbilitati(false);
        }
    }

    private void salvaModifiche() {
        if (prenotazioneCorrente == null) {
            messaggioTextArea.setText("Nessuna prenotazione da modificare.");
            return;
        }

        int scelta = JOptionPane.showConfirmDialog(
                panelCercaModificaPrenotazione,
                "Vuoi salvare le modifiche a questa prenotazione?",
                "Conferma salvataggio",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (scelta != JOptionPane.YES_OPTION) {
            return; // l’utente ha annullato
        }

        prenotazioneCorrente.getDatiPasseggero().setNome(nomeTextField.getText());
        prenotazioneCorrente.getDatiPasseggero().setCognome(cognomeTextField.getText());
        prenotazioneCorrente.getDatiPasseggero().setEmail(emailTextField.getText());

        String codiceVolo = voloTextField.getText().trim();
        Volo volo = controller.cercaVolo(codiceVolo);
        if (volo == null) {
            messaggioTextArea.setText("Volo non trovato.");
            return;
        }
        prenotazioneCorrente.setVolo(volo);

        String statoString = (String) statoVoloComboBox.getSelectedItem();
        try {
            StatoPrenotazione stato = StatoPrenotazione.valueOf(statoString.toUpperCase());
            prenotazioneCorrente.setStato(stato);
        } catch (IllegalArgumentException _) {
            messaggioTextArea.setText("Stato non valido.");
            return;
        }

        boolean successo = controller.salvaPrenotazione(prenotazioneCorrente);
        if (successo) {
            messaggioTextArea.setText("Modifiche salvate con successo!");
            pulisciCampiPrenotazione();
            setCampiPrenotazioneAbilitati(false);
            codiceInserimentoTextField.setText("");
            codiceInserimentoTextField.setEditable(true);
        } else {
            messaggioTextArea.setText("Errore nel salvataggio delle modifiche.");
        }

        aggiornaListaPrenotazioni();
    }

    private void annullaPrenotazione() {
        if (prenotazioneCorrente == null) {
            messaggioTextArea.setText("Nessuna prenotazione da annullare.");
            return;
        }

        int scelta = JOptionPane.showConfirmDialog(
                panelCercaModificaPrenotazione,
                "Sei sicuro di voler annullare PERMANENTEMENTE la prenotazione?",
                "Conferma annullamento",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (scelta != JOptionPane.YES_OPTION) return;

        boolean successo = controller.annullaPrenotazione(prenotazioneCorrente);

        if (successo) {
            messaggioTextArea.setText("Prenotazione eliminata dal database!");
            // Rimuovi dal modello subito
            if (listaModel != null) {
                listaModel.removeElement(prenotazioneCorrente.getNumBiglietto());
            }
            pulisciCampiPrenotazione();
            setCampiPrenotazioneAbilitati(false);
            codiceInserimentoTextField.setEditable(true);
            prenotazioneCorrente = null;
        } else {
            messaggioTextArea.setText("Errore nell'annullamento della prenotazione.");
        }

        aggiornaListaPrenotazioni();
    }

    private void aggiornaListaPrenotazioni() {
        if (listaModel == null) {
            // La lista non è presente (utente senza prenotazioni all’avvio): nulla da aggiornare
            return;
        }
        listaModel.clear();
        List<Prenotazione> prenotazioniUtente = controller.getPrenotazioniUtente((UtenteGenerico) utente);
        if (prenotazioniUtente != null) {
            for (Prenotazione p : prenotazioniUtente) {
                listaModel.addElement(p.getNumBiglietto());
            }
        }
    }

    private void setCampiPrenotazioneAbilitati(boolean abilitati) {
        nomeTextField.setEnabled(abilitati);
        cognomeTextField.setEnabled(abilitati);
        emailTextField.setEnabled(abilitati);
        voloTextField.setEnabled(false);
        statoVoloComboBox.setEnabled(false);
        salvaModificheButton.setEnabled(abilitati);
        annullaPrenotazioneButton.setEnabled(abilitati);
    }

    private void pulisciCampiPrenotazione() {
        nomeTextField.setText("");
        cognomeTextField.setText("");
        emailTextField.setText("");
        voloTextField.setText("");
        if (statoVoloComboBox.getItemCount() > 0) {
            statoVoloComboBox.setSelectedIndex(0);
        }
    }

    public JPanel getPanel() {
        return panelCercaModificaPrenotazione;
    }
}