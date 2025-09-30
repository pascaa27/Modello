package gui;

import javax.swing.*;
import javax.swing.WindowConstants;
import controller.Controller;
import model.UtenteGenerico;
import java.awt.*;

public class AreaPersonaleUtenteGUI {
    private static final String FONT_FAMILY = "Segoe UI";

    private JTextField nomeUtenteTextField;
    private JTextField cognomeUtenteTextField;
    private JButton tabellaOrarioButton;
    private JButton cercaModificaButton;
    private JTextField emailUtenteTextField;
    private JPanel areaPersonaleUtentePanel;
    private JButton effettuaPrenotazioneButton;
    private Controller controller;
    private UtenteGenerico utente;
    private JFrame prenotazioneFrame;

    // Palette colori
    private final Color mainGradientStart = new Color(30, 87, 153);
    private final Color mainGradientEnd   = new Color(125, 185, 232);
    private final Color panelBgColor      = new Color(245, 249, 255);
    private final Color buttonColor       = new Color(60, 130, 200);
    private final Color buttonHoverColor  = new Color(30, 87, 153);

    public AreaPersonaleUtenteGUI(Controller controller, UtenteGenerico utente) {
        this.controller = controller;
        this.utente = utente;

        // Gradient desktop panel
        areaPersonaleUtentePanel = new JPanel() {
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
        areaPersonaleUtentePanel.setLayout(new BorderLayout(0, 0));

        // Card dati utente
        JPanel datiPanel = new JPanel(new GridBagLayout());
        datiPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 18, 10, 18);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        datiPanel.add(styledLabelWhite("Nome:"), gbc);
        gbc.gridx = 1;
        nomeUtenteTextField = styledTextFieldWhite(utente.getNomeUtente());
        nomeUtenteTextField.setEditable(false);
        datiPanel.add(nomeUtenteTextField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        datiPanel.add(styledLabelWhite("Cognome:"), gbc);
        gbc.gridx = 1;
        cognomeUtenteTextField = styledTextFieldWhite(utente.getCognomeUtente());
        cognomeUtenteTextField.setEditable(false);
        datiPanel.add(cognomeUtenteTextField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        datiPanel.add(styledLabelWhite("Email:"), gbc);
        gbc.gridx = 1;
        emailUtenteTextField = styledTextFieldWhite(utente.getLogin());
        emailUtenteTextField.setEditable(false);
        datiPanel.add(emailUtenteTextField, gbc);

        areaPersonaleUtentePanel.add(datiPanel, BorderLayout.NORTH);

        // Bottoni principali desktop
        JPanel bottoniPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 26, 18));
        bottoniPanel.setOpaque(false);
        tabellaOrarioButton = gradientButton("Tabella Orario");
        effettuaPrenotazioneButton = gradientButton("Effettua Prenotazione");
        cercaModificaButton = gradientButton("Cerca/Modifica Prenotazione");

        bottoniPanel.add(tabellaOrarioButton);
        bottoniPanel.add(effettuaPrenotazioneButton);
        bottoniPanel.add(cercaModificaButton);

        areaPersonaleUtentePanel.add(bottoniPanel, BorderLayout.CENTER);

        // Listeners
        tabellaOrarioButton.addActionListener(e -> apriTabellaOrario());
        effettuaPrenotazioneButton.addActionListener(e -> apriEffettuaPrenotazione());
        cercaModificaButton.addActionListener(e -> apriCercaModificaPrenotazione());
    }

    // Desktop label
    private JLabel styledLabelWhite(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font(FONT_FAMILY, Font.BOLD, 14));
        l.setForeground(Color.WHITE);
        return l;
    }

    // Desktop field
    private JTextField styledTextFieldWhite(String text) {
        JTextField tf = new JTextField(text, 13);
        tf.setFont(new Font(FONT_FAMILY, Font.PLAIN, 14));
        tf.setBackground(panelBgColor);
        tf.setForeground(mainGradientStart);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255,255,255, 180), 1, true),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));
        tf.setCaretColor(mainGradientStart);
        return tf;
    }

    // Desktop button
    private JButton gradientButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font(FONT_FAMILY, Font.BOLD, 14));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Solo padding, no larghezza minima fissa!
        b.setBorder(BorderFactory.createEmptyBorder(7, 32, 7, 32));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                b.setForeground(Color.WHITE);
                b.repaint();
            }
            // mouseExited rimosso per evitare duplicazione con mouseEntered (S4144)
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

    // Metodo che apre la GUI TabellaOrario
    private void apriTabellaOrario() {
        TabellaOrarioGUI gui = new TabellaOrarioGUI(controller);
        JFrame frame = new JFrame("Tabella Orario");
        frame.setContentPane(gui.getPanel());
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(950, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Metodo che apre la GUI EffettuaPrenotazione
    private void apriEffettuaPrenotazione() {
        if (prenotazioneFrame != null) {
            prenotazioneFrame.toFront();
            prenotazioneFrame.requestFocus();
            return;
        }
        prenotazioneFrame = new JFrame("Effettua Prenotazione");
        prenotazioneFrame.setContentPane(new EffettuaPrenotazioneGUI(controller, utente).getPanel());
        prenotazioneFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        prenotazioneFrame.setSize(650, 450);
        prenotazioneFrame.setLocationRelativeTo(null);
        prenotazioneFrame.setVisible(true);

        prenotazioneFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                prenotazioneFrame = null;
            }
        });
    }

    // Metodo che apre la GUI Cerca/Modifica Prenotazione
    private void apriCercaModificaPrenotazione() {
        JFrame frame = new JFrame("Cerca/Modifica Prenotazione");
        frame.setContentPane(
                new CercaModificaPrenotazioneGUI(controller, utente, utente.getUltimoCodicePrenotazione()).getPanel()
        );
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public JPanel getPanel() {
        return areaPersonaleUtentePanel;
    }
}