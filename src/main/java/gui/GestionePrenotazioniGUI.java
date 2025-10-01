package gui;

import javax.swing.*;
import java.awt.*;
import controller.Controller;
import model.*;

/**
 * Classe che gestisce l'interfaccia grafica per la gestione delle prenotazioni.
 */
public class GestionePrenotazioniGUI {
    private static final String FONT_FAMILY = "Segoe UI";
    private static final String MSG_ERRORE_TITLE = "Errore";

    private JPanel panelPrenotazione;
    private JTextField numeroPrenotazioneTextField;
    private JTextField postoTextField;
    private JComboBox<StatoPrenotazione> statoPrenotazioneComboBox;
    private JButton aggiungiPrenotazioneButton;
    private JTextField numeroVoloTextField;
    private JButton rimuoviPrenotazioneButton;
    private Controller controller;
    private final AreaPersonaleAmmGUI areaPersonaleAmmGUI;

    // Palette colori
    private final Color mainGradientStart = new Color(30, 87, 153);
    private final Color mainGradientEnd   = new Color(125, 185, 232);
    private final Color panelBgColor      = new Color(245, 249, 255);
    private final Color buttonColor       = new Color(60, 130, 200);
    private final Color buttonHoverColor  = new Color(30, 87, 153);

    /**
     * Costruttore: crea l'interfaccia per la gestione delle prenotazioni.
     * Inizializza i campi e imposta i listener per aggiungere o rimuovere prenotazioni.
     *
     * @param controller Controller per la gestione logica
     * @param utente Utente corrente (utilizzato per creare o recuperare un utente generico)
     * @param areaPersonaleAmmGUI Riferimento all'interfaccia amministratore
     */
    public GestionePrenotazioniGUI(Controller controller, Utente utente, AreaPersonaleAmmGUI areaPersonaleAmmGUI) {
        this.controller = controller;
        this.areaPersonaleAmmGUI = areaPersonaleAmmGUI;

        // Gradient panel
        panelPrenotazione = new JPanel() {
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
        panelPrenotazione.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(14, 18, 8, 18);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Numero prenotazione
        gbc.gridx = 0; gbc.gridy = 0;
        panelPrenotazione.add(styledLabelWhite("Numero Prenotazione:"), gbc);
        gbc.gridx = 1;
        numeroPrenotazioneTextField = styledTextFieldWhite("");
        panelPrenotazione.add(numeroPrenotazioneTextField, gbc);

        // Numero volo
        gbc.gridx = 0; gbc.gridy++;
        panelPrenotazione.add(styledLabelWhite("Numero Volo:"), gbc);
        gbc.gridx = 1;
        numeroVoloTextField = styledTextFieldWhite("");
        panelPrenotazione.add(numeroVoloTextField, gbc);

        // Posto assegnato
        gbc.gridx = 0; gbc.gridy++;
        panelPrenotazione.add(styledLabelWhite("Posto Assegnato:"), gbc);
        gbc.gridx = 1;
        postoTextField = styledTextFieldWhite("");
        panelPrenotazione.add(postoTextField, gbc);

        // Stato Prenotazione
        gbc.gridx = 0; gbc.gridy++;
        panelPrenotazione.add(styledLabelWhite("Stato Prenotazione:"), gbc);
        gbc.gridx = 1;
        statoPrenotazioneComboBox = styledComboBoxStato(StatoPrenotazione.values());
        panelPrenotazione.add(statoPrenotazioneComboBox, gbc);

        // Bottoni
        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 18, 10, 18);
        JPanel bottoniPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 4));
        bottoniPanel.setOpaque(false);
        aggiungiPrenotazioneButton = gradientButton("Aggiungi Prenotazione");
        rimuoviPrenotazioneButton = gradientButton("Rimuovi Prenotazione");
        bottoniPanel.add(aggiungiPrenotazioneButton);
        bottoniPanel.add(rimuoviPrenotazioneButton);
        panelPrenotazione.add(bottoniPanel, gbc);

        // Ricava/crea l'utente generico solo localmente (nessun field 'utente')
        UtenteGenerico utenteGenerico;
        if(controller.getUtenteByEmail(utente.getNomeUtente()) == null) {
            utenteGenerico = controller.creaUtenteGenerico(utente.getNomeUtente());
        } else {
            utenteGenerico = controller.getUtenteByEmail(utente.getNomeUtente());
        }

        aggiungiPrenotazioneButton.addActionListener(e -> {
            String numeroBiglietto = numeroPrenotazioneTextField.getText().trim();
            String posto = postoTextField.getText().trim();
            String numeroVolo = numeroVoloTextField.getText().trim();

            if(numeroBiglietto.isEmpty() || posto.isEmpty() || numeroVolo.isEmpty()) {
                JOptionPane.showMessageDialog(panelPrenotazione,
                        "Compila prima tutti i campi obbligatori.",
                        MSG_ERRORE_TITLE,
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(panelPrenotazione, "Per effettuare la prenotazione, inserire i seguenti dati:");
            JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(panelPrenotazione), "Inserisci dati passeggero", true);
            DatiPasseggeroGUI datiGUI = new DatiPasseggeroGUI(dialog);
            dialog.setContentPane(datiGUI.getPanel());
            dialog.pack();
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);

            String nome = datiGUI.getNomeInserito();
            String cognome = datiGUI.getCognomeInserito();
            String codiceFiscale = datiGUI.getCodiceFiscaleInserito();
            String email = datiGUI.getEmailInserita();

            if(nome == null || cognome == null || codiceFiscale == null || email == null ||
                    nome.isEmpty() || cognome.isEmpty() || codiceFiscale.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(panelPrenotazione,
                        "Compila correttamente tutti i dati del passeggero (inclusa l'email).",
                        MSG_ERRORE_TITLE,
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            StatoPrenotazione stato = (StatoPrenotazione) statoPrenotazioneComboBox.getSelectedItem();
            Volo volo = controller.getVoloByCodice(numeroVolo);

            if(volo == null) {
                JOptionPane.showMessageDialog(panelPrenotazione,
                        "Il volo con codice " + numeroVolo + " non esiste.",
                        MSG_ERRORE_TITLE,
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Controller.PrenotazioneInput input = Controller.PrenotazioneInput.of(
                    new Controller.PrenotazioneBase(numeroBiglietto, posto, stato),
                    new Controller.VoloRef(numeroVolo, utenteGenerico),
                    new Controller.PasseggeroInfo(nome, cognome, codiceFiscale, email)
            );

            Prenotazione pren = controller.aggiungiPrenotazione(input);


            areaPersonaleAmmGUI.aggiornaTabellaPasseggeri();

            JOptionPane.showMessageDialog(panelPrenotazione, "Prenotazione completata con successo!");

            numeroPrenotazioneTextField.setText("");
            postoTextField.setText("");
            numeroVoloTextField.setText("");
            statoPrenotazioneComboBox.setSelectedIndex(0);
        });

        rimuoviPrenotazioneButton.addActionListener(e -> rimuoviPrenotazione());
    }

    /**
     * Rimuove una prenotazione in base al numero inserito.
     * Mostra finestre di conferma o errore tramite {@link JOptionPane}.
     * Aggiorna la tabella passeggeri in {@link AreaPersonaleAmmGUI}.
     */
    private void rimuoviPrenotazione() {
        String numeroBiglietto = numeroPrenotazioneTextField.getText().trim();

        if(numeroBiglietto.isEmpty()) {
            JOptionPane.showMessageDialog(panelPrenotazione,
                    "Inserisci il numero della prenotazione da rimuovere.",
                    MSG_ERRORE_TITLE,
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean rimosso = controller.rimuoviPrenotazione(numeroBiglietto);

        if(rimosso) {
            JOptionPane.showMessageDialog(panelPrenotazione,
                    "Prenotazione rimossa con successo!");
            areaPersonaleAmmGUI.aggiornaTabellaPasseggeri();
            numeroPrenotazioneTextField.setText("");
            postoTextField.setText("");
            numeroVoloTextField.setText("");
            statoPrenotazioneComboBox.setSelectedIndex(0);
        } else {
            JOptionPane.showMessageDialog(panelPrenotazione,
                    "Nessuna prenotazione trovata con numero " + numeroBiglietto,
                    MSG_ERRORE_TITLE,
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Stile componenti ---
    /**
     * Crea un'etichetta stilizzata con testo bianco e font in grassetto.
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
     * Crea un campo di testo con sfondo chiaro e bordi personalizzati.
     *
     * @param text Testo iniziale del campo
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
     * Crea una {@link JComboBox} stilizzata contenente gli stati di prenotazione.
     *
     * @param items Array di stati {@link StatoPrenotazione} da inserire
     * @return JComboBox personalizzata
     */
    private JComboBox<StatoPrenotazione> styledComboBoxStato(StatoPrenotazione[] items) {
        JComboBox<StatoPrenotazione> cb = new JComboBox<>();
        cb.setFont(new Font(FONT_FAMILY, Font.PLAIN, 13));
        cb.setBackground(Color.WHITE);
        cb.setForeground(mainGradientStart);
        cb.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(mainGradientStart, 1, true),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
        for(StatoPrenotazione s : items) cb.addItem(s);
        return cb;
    }

    /**
     * Crea un pulsante con stile gradiente personalizzato.
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
     * Restituisce il pannello principale della GUI.
     *
     * @return JPanel con l'interfaccia di gestione prenotazioni
     */
    public JPanel getPanelPrenotazione() {
        return panelPrenotazione;
    }
}