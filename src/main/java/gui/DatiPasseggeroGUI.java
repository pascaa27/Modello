package gui;

import javax.swing.*;
import java.awt.*;

/**
 * Classe che gestisce l'interfaccia grafica per l'inserimento dei dati di un passeggero.
 */
public class DatiPasseggeroGUI {
    private static final String FONT_FAMILY = "Segoe UI";

    private JPanel panelDatiPasseggero;
    private JTextField nomeTextField;
    private JTextField cognomeTextField;
    private JTextField codiceFiscaleTextField;
    private JTextField emailTextField;         // nuovo campo email
    private JButton salvaDatiPasseggeroButton;

    private String nomeInserito;
    private String cognomeInserito;
    private String codiceFiscaleInserito;
    private String emailInserita;               // nuova variabile

    // Palette colori
    private final Color mainGradientStart = new Color(30, 87, 153);
    private final Color mainGradientEnd   = new Color(125, 185, 232);
    private final Color panelBgColor      = new Color(245, 249, 255);
    private final Color buttonColor       = new Color(60, 130, 200);
    private final Color buttonHoverColor  = new Color(30, 87, 153);

    /**
     * Costruttore: crea l'interfaccia grafica dei dati del passeggero.
     *
     * @param parentDialog Dialog genitore da chiudere dopo il salvataggio dei dati;
     *                     se nullo, chiude la finestra contenente il pannello.
     */
    public DatiPasseggeroGUI(JDialog parentDialog) {
        // Gradient panel
        panelDatiPasseggero = new JPanel() {
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
        panelDatiPasseggero.setLayout(new GridBagLayout());

        // Campi e label
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(14, 18, 8, 18);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0;
        panelDatiPasseggero.add(styledLabelWhite("Nome:"), gbc);
        gbc.gridx = 1;
        nomeTextField = styledTextFieldWhite("");
        panelDatiPasseggero.add(nomeTextField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panelDatiPasseggero.add(styledLabelWhite("Cognome:"), gbc);
        gbc.gridx = 1;
        cognomeTextField = styledTextFieldWhite("");
        panelDatiPasseggero.add(cognomeTextField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panelDatiPasseggero.add(styledLabelWhite("Codice Fiscale:"), gbc);
        gbc.gridx = 1;
        codiceFiscaleTextField = styledTextFieldWhite("");
        panelDatiPasseggero.add(codiceFiscaleTextField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panelDatiPasseggero.add(styledLabelWhite("Email:"), gbc);
        gbc.gridx = 1;
        emailTextField = styledTextFieldWhite("");
        panelDatiPasseggero.add(emailTextField, gbc);

        // Bottone salva
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        gbc.insets = new Insets(18, 18, 16, 18);
        JPanel bottonePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottonePanel.setOpaque(false);
        salvaDatiPasseggeroButton = gradientButton("Salva Dati Passeggero");
        bottonePanel.add(salvaDatiPasseggeroButton);
        panelDatiPasseggero.add(bottonePanel, gbc);

        salvaDatiPasseggeroButton.addActionListener(e -> {
            nomeInserito = nomeTextField.getText().trim();
            cognomeInserito = cognomeTextField.getText().trim();
            codiceFiscaleInserito = codiceFiscaleTextField.getText().trim();
            emailInserita = emailTextField.getText().trim();

            if(nomeInserito.isEmpty() ||
                    cognomeInserito.isEmpty() ||
                    codiceFiscaleInserito.isEmpty()) {
                JOptionPane.showMessageDialog(parentDialog,
                        "I campi nome, cognome e codice fiscale sono obbligatori.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(parentDialog, "Dati passeggero salvati.");

            if(parentDialog != null) {
                parentDialog.dispose();
            } else {
                Window w = SwingUtilities.getWindowAncestor(panelDatiPasseggero);
                if(w != null) {
                    w.dispose();
                }
            }
        });
    }

    // Stile label e campo
    /**
     * Crea un'etichetta con testo bianco e font personalizzato.
     *
     * @param text Testo da visualizzare
     * @return JLabel formattata
     */
    private JLabel styledLabelWhite(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font(FONT_FAMILY, Font.BOLD, 14));
        l.setForeground(Color.WHITE);
        return l;
    }

    /**
     * Crea un campo di testo stilizzato con sfondo chiaro e bordo personalizzato.
     *
     * @param text Testo iniziale del campo
     * @return JTextField formattato
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
     * Crea un pulsante con effetto gradiente e stile personalizzato.
     *
     * @param text Testo da visualizzare sul pulsante
     * @return JButton con gradiente e bordo arrotondato
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
            // mouseExited rimosso per evitare implementazione identica a mouseEntered
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
     * Restituisce il pannello principale contenente l'interfaccia.
     *
     * @return JPanel principale della GUI
     */
    public JPanel getPanel() { return panelDatiPasseggero; }

    /**
     * Restituisce il nome inserito dall'utente.
     *
     * @return String contenente il nome
     */
    public String getNomeInserito() { return nomeInserito; }

    /**
     * Restituisce il cognome inserito dall'utente.
     *
     * @return String contenente il cognome
     */
    public String getCognomeInserito() { return cognomeInserito; }

    /**
     * Restituisce il codice fiscale inserito dall'utente.
     *
     * @return String contenente il codice fiscale
     */
    public String getCodiceFiscaleInserito() { return codiceFiscaleInserito; }

    /**
     * Restituisce l'email inserita dall'utente.
     *
     * @return String contenente l'email
     */
    public String getEmailInserita() { return emailInserita; }
}