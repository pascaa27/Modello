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

/**
 * Classe che gestisce l'interfaccia grafica per l'effettuazione di una prenotazione.
 */
public class EffettuaPrenotazioneGUI {
    private static final String FONT_FAMILY = "Segoe UI";
    private static final String MSG_ERRORE_TITLE = "Errore";
    private static final String MSG_SUCCESSO_TITLE = "Successo";

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

    /**
     * Costruttore: crea l'interfaccia per effettuare una prenotazione.
     * Inizializza tutti i campi di testo, le combo per le date e i bottoni,
     * aggiunge i listener e popola le combo con gli anni, mesi e giorni disponibili.
     *
     * @param controller Controller per gestire la logica applicativa
     * @param utente Utente che sta effettuando la prenotazione
     */
    public EffettuaPrenotazioneGUI(Controller controller, UtenteGenerico utente) {
        this.controller = controller;
        this.utente = utente;

        // Gradient panel
        effettuaPrenotazionePanel = new JPanel() {
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
            if(f != null) f.dispose();
        });

        // Popola e aggiusta le combo *dopo* l'inizializzazione
        SwingUtilities.invokeLater(() -> {
            if(annoInizioComboBox.getItemCount() == 0) {
                for (int anno = 2025; anno <= 2035; anno++) {
                    annoInizioComboBox.addItem(anno);
                    annoFineComboBox.addItem(anno);
                }
            }
            if(meseInizioComboBox.getItemCount() == 0) {
                for(int m = 1; m <= 12; m++) {
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

    /**
     * Aggiorna i giorni del mese nella ComboBox in base all'anno e mese selezionati.
     *
     * @param annoCombo ComboBox anno
     * @param meseCombo ComboBox mese
     * @param giornoCombo ComboBox giorno da aggiornare
     */
    private void aggiornaGiorni(JComboBox<Integer> annoCombo, JComboBox<Integer> meseCombo, JComboBox<Integer> giornoCombo) {
        if(annoCombo == null || meseCombo == null || giornoCombo == null) return;
        Object oAnno = annoCombo.getSelectedItem();
        Object oMese = meseCombo.getSelectedItem();
        if(oAnno == null || oMese == null) return;

        int anno = (int) oAnno;
        int mese = (int) oMese;
        int giorniNelMese = YearMonth.of(anno, mese).lengthOfMonth();

        Integer precedente = (Integer) giornoCombo.getSelectedItem();

        giornoCombo.removeAllItems();
        for(int g = 1; g <= giorniNelMese; g++) {
            giornoCombo.addItem(g);
        }
        if(precedente != null && precedente <= giorniNelMese) {
            giornoCombo.setSelectedItem(precedente);
        } else {
            giornoCombo.setSelectedIndex(0);
        }
    }

    /**
     * Effettua la prenotazione: legge i dati dalla GUI, valida i campi,
     * verifica la disponibilità del volo e crea una nuova prenotazione tramite il controller.
     * Visualizza messaggi di conferma o errore.
     */
    private void effettuaPrenotazione() {
        String nome = nomeTextField.getText().trim();
        String cognome = cognomeTextField.getText().trim();
        String codiceFiscale = codiceFiscaleTextField.getText().trim();
        String email = emailTextField.getText().trim();
        String destinazione = aeroportoDestinazioneTextField.getText().trim().toUpperCase();

        String err = validateDatiBase(nome, cognome, codiceFiscale, destinazione);
        if(err != null) {
            showError(err);
            return;
        }

        LocalDate dataInizio = buildDate(annoInizioComboBox, meseInizioComboBox, giornoInizioComboBox);
        LocalDate dataFine   = buildDate(annoFineComboBox, meseFineComboBox, giornoFineComboBox);

        err = validateDateRange(dataInizio, dataFine);
        if(err != null) {
            showError(err);
            return;
        }

        try {
            Volo volo = controller.cercaVoloPerDestinazioneEData(destinazione, dataInizio.toString());
            if(volo == null) {
                showError("Nessun volo trovato per questa destinazione e data!");
                return;
            }

            err = validateUserEligibility(email, volo);
            if(err != null) {
                showError(err);
                return;
            }

            String numeroBiglietto = generateTicket();

            Controller.PrenotazioneInput input = Controller.PrenotazioneInput.of(
                    new Controller.PrenotazioneBase(numeroBiglietto, "", StatoPrenotazione.CONFERMATA),
                    new Controller.VoloRef(volo.getCodiceUnivoco(), utente),
                    new Controller.PasseggeroInfo(nome, cognome, codiceFiscale, email)
            );

            Prenotazione pren = controller.aggiungiPrenotazione(input);

            if(pren == null) {
                showError("Creazione prenotazione fallita.");
                return;
            }

            showInfo(
                    "Prenotazione effettuata con successo!\n" +
                            "Codice prenotazione: " + pren.getNumBiglietto() + "\n" +
                            "Dal " + dataInizio + " al " + dataFine
            );

            if(utente != null) {
                utente.aggiungiCodicePrenotazione(pren.getNumBiglietto());
            }

        } catch(Exception ex) {
            showError("Errore durante la prenotazione: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Valida i dati base del passeggero e destinazione.
     *
     * @param nome Nome del passeggero
     * @param cognome Cognome del passeggero
     * @param codiceFiscale Codice fiscale del passeggero
     * @param destinazione Aeroporto di destinazione
     * @return Stringa con messaggio di errore, o null se valido
     */
    private String validateDatiBase(String nome, String cognome, String codiceFiscale, String destinazione) {
        if(nome.isEmpty() || cognome.isEmpty() || codiceFiscale.isEmpty() || destinazione.isEmpty()) {
            return "Tutti i campi devono essere compilati.";
        }
        if(!Character.isUpperCase(nome.charAt(0)) || !Character.isUpperCase(cognome.charAt(0))) {
            return "Nome e Cognome devono iniziare con lettera maiuscola.";
        }
        return null;
    }

    /**
     * Costruisce una data a partire dalle ComboBox di anno, mese e giorno.
     *
     * @param anno ComboBox anno
     * @param mese ComboBox mese
     * @param giorno ComboBox giorno
     * @return LocalDate corrispondente alla selezione
     */
    private LocalDate buildDate(JComboBox<Integer> anno, JComboBox<Integer> mese, JComboBox<Integer> giorno) {
        return LocalDate.of(
                (int) anno.getSelectedItem(),
                (int) mese.getSelectedItem(),
                (int) giorno.getSelectedItem()
        );
    }

    /**
     * Valida l'intervallo di date selezionato.
     *
     * @param inizio Data di inizio viaggio
     * @param fine Data di fine viaggio
     * @return Stringa con messaggio di errore, o null se valido
     */
    private String validateDateRange(LocalDate inizio, LocalDate fine) {
        if(fine.isBefore(inizio)) {
            return "La data di fine deve essere successiva o uguale alla data di inizio.";
        }
        return null;
    }

    /**
     * Verifica se l'utente può prenotare il volo selezionato.
     *
     * @param email Email dell'utente
     * @param volo Volo selezionato
     * @return Stringa con messaggio di errore, o null se valido
     */
    private String validateUserEligibility(String email, Volo volo) {
        if(utente != null && utente.isRegistrato()) {
            boolean giaPrenotato = controller.utenteHaPrenotazionePerVolo(utente.getLogin(), volo.getCodiceUnivoco());
            if(giaPrenotato) {
                return "Hai già una prenotazione per questo volo!";
            }
        } else {
            if(!controller.emailRegistrata(email)) {
                return "Devi registrarti prima di prenotare con questa email.";
            }
        }
        return null;
    }

    /**
     * Genera un numero di biglietto univoco per la prenotazione.
     *
     * @return String con numero biglietto
     */
    private String generateTicket() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Mostra un messaggio di errore all'utente tramite JOptionPane.
     *
     * @param msg Messaggio di errore da visualizzare
     */
    private void showError(String msg) {
        JOptionPane.showMessageDialog(effettuaPrenotazionePanel, msg, MSG_ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Mostra un messaggio informativo all'utente tramite JOptionPane.
     *
     * @param msg Messaggio informativo da visualizzare
     */
    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(effettuaPrenotazionePanel, msg, MSG_SUCCESSO_TITLE, JOptionPane.INFORMATION_MESSAGE);
    }

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
     * Crea una ComboBox stilizzata per numeri interi.
     *
     * @return JComboBox<Integer> stilizzata
     */
    private JComboBox<Integer> styledComboBoxInt() {
        JComboBox<Integer> cb = new JComboBox<>();
        cb.setFont(new Font(FONT_FAMILY, Font.PLAIN, 13));
        cb.setBackground(Color.WHITE);
        cb.setForeground(mainGradientStart);
        cb.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(mainGradientStart, 1, true),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
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
            // mouseExited rimosso: altrimenti identico a mouseEntered (S4144)
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
     * @return JPanel contenente tutti i componenti dell'interfaccia
     */
    public JPanel getPanel() {
        return effettuaPrenotazionePanel;
    }
}