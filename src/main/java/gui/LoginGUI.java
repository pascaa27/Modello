package gui;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import controller.Controller;
import model.Amministratore;
import model.UtenteGenerico;
import model.AreaPersonale;
import model.DatiPasseggero;
import implementazioneDAO.DatiPasseggeroDAO;
import implementazioneDAO.implementazionePostgresDAO.DatiPasseggeroDAOPostgres;

public class LoginGUI {
    private JPanel loginPanel;
    private JButton accediButton;
    private JButton registratiButton;
    private JTextField nomeTextField;
    private JTextField cognomeTextField1;
    private JTextField emailTextField;
    private JTextField passwordTextField; // legacy, non usata più per input
    private JButton showHideButton;
    private JPasswordField passwordField;
    private Controller controller;
    private static final String ADMIN_EMAIL = "a";
    private static final String ADMIN_PASS = "a";

    // Palette colori
    private final Color mainGradientStart = new Color(30, 87, 153);
    private final Color mainGradientEnd   = new Color(125, 185, 232);
    private final Color panelBgColor      = new Color(245, 249, 255);
    private final Color buttonColor       = new Color(60, 130, 200);
    private final Color buttonHoverColor  = new Color(30, 87, 153);

    // DAO per salvataggio su DB di datipasseggeri
    private final DatiPasseggeroDAO datiPasseggeroDAO;

    private boolean passwordVisible = false;

    public LoginGUI(Controller controller, Amministratore amministratore) {
        this.controller = controller;
        this.datiPasseggeroDAO = new DatiPasseggeroDAOPostgres();

        // Gradient panel
        loginPanel = new JPanel() {
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
        loginPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(14, 18, 8, 18);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Campi
        gbc.gridx = 0; gbc.gridy = 0;
        loginPanel.add(styledLabelWhite("Nome:"), gbc);
        gbc.gridx = 1;
        nomeTextField = styledTextFieldWhite("");
        loginPanel.add(nomeTextField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        loginPanel.add(styledLabelWhite("Cognome:"), gbc);
        gbc.gridx = 1;
        cognomeTextField1 = styledTextFieldWhite("");
        loginPanel.add(cognomeTextField1, gbc);

        gbc.gridx = 0; gbc.gridy++;
        loginPanel.add(styledLabelWhite("Email:"), gbc);
        gbc.gridx = 1;
        emailTextField = styledTextFieldWhite("");
        loginPanel.add(emailTextField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        loginPanel.add(styledLabelWhite("Password:"), gbc);
        gbc.gridx = 1;
        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.setOpaque(false);
        passwordField = styledPasswordField();
        passwordPanel.add(passwordField, BorderLayout.CENTER);

        // PATCH: bottone show/hide SENZA gradientButton UI custom!
        showHideButton = new JButton("Show");
        showHideButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        showHideButton.setForeground(Color.WHITE);
        showHideButton.setBackground(buttonColor);
        showHideButton.setFocusPainted(false);
        showHideButton.setBorderPainted(false);
        showHideButton.setContentAreaFilled(true);
        showHideButton.setOpaque(true);
        showHideButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        showHideButton.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));
        showHideButton.setPreferredSize(new Dimension(90, 28));
        showHideButton.setMinimumSize(new Dimension(90, 28));
        showHideButton.setMaximumSize(new Dimension(90, 28));
        showHideButton.addActionListener(e -> togglePasswordVisibility());
        passwordPanel.add(showHideButton, BorderLayout.EAST);

        loginPanel.add(passwordPanel, gbc);

        // Bottoni
        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 18, 10, 18);
        JPanel bottoniPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 4));
        bottoniPanel.setOpaque(false);
        accediButton = gradientButton("Accedi");
        registratiButton = gradientButton("Registrati");
        bottoniPanel.add(accediButton);
        bottoniPanel.add(registratiButton);
        loginPanel.add(bottoniPanel, gbc);

        accediButton.addActionListener(e -> {
            String nome = nomeTextField.getText().trim();
            String cognome = cognomeTextField1.getText().trim();
            String email = emailTextField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            // Controlli:
            if (nome.isEmpty() || cognome.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Compila tutti i campi per l'accesso!");
                return;
            }
            if (!Character.isUpperCase(nome.charAt(0)) || !Character.isUpperCase(cognome.charAt(0))) {
                JOptionPane.showMessageDialog(null, "Nome e Cognome devono iniziare con lettera MAIUSCOLA.");
                return;
            }
            if (!email.equals(email.toLowerCase())) {
                JOptionPane.showMessageDialog(null, "L'email deve essere tutta minuscola.");
                return;
            }

            // Admin hardcoded
            if (email.equals(ADMIN_EMAIL) && password.equals(ADMIN_PASS)) {
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(loginPanel);
                frame.dispose();

                JFrame nuovoFrame = new JFrame("Area Personale Amministratore");
                nuovoFrame.setContentPane(new AreaPersonaleAmmGUI(controller, amministratore).getAreaPersonaleAmmPanel());
                nuovoFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                nuovoFrame.setSize(1100, 750);
                nuovoFrame.setLocationRelativeTo(null);
                nuovoFrame.setVisible(true);
                return;
            }

            // Login persistente: verifica su DB
            DatiPasseggero p = datiPasseggeroDAO.findByEmail(email);
            if (p != null && Objects.equals(p.getPassword(), password)) {
                UtenteGenerico utente = new UtenteGenerico(
                        p.getEmail(),
                        p.getPassword(),
                        p.getNome(),
                        p.getCognome(),
                        new ArrayList<>(),
                        new AreaPersonale()
                );

                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(loginPanel);
                frame.dispose();

                JFrame nuovoFrame = new JFrame("Area Personale Utente");
                nuovoFrame.setContentPane(new AreaPersonaleUtenteGUI(controller, utente).getPanel());
                nuovoFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                nuovoFrame.setSize(1100, 750);
                nuovoFrame.setLocationRelativeTo(null);
                nuovoFrame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Credenziali non valide. Registrati o riprova.");
            }
        });

        registratiButton.addActionListener(e -> {
            String nome = nomeTextField.getText().trim();
            String cognome = cognomeTextField1.getText().trim();
            String email = emailTextField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            // Controlli:
            if (nome.isEmpty() || cognome.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Compila tutti i campi per la registrazione!");
                return;
            }
            if (!Character.isUpperCase(nome.charAt(0)) || !Character.isUpperCase(cognome.charAt(0))) {
                JOptionPane.showMessageDialog(null, "Nome e Cognome devono iniziare con lettera MAIUSCOLA.");
                return;
            }
            if (!email.equals(email.toLowerCase())) {
                JOptionPane.showMessageDialog(null, "L'email deve essere tutta minuscola.");
                return;
            }
            if (email.equals(ADMIN_EMAIL)) {
                JOptionPane.showMessageDialog(null, "Email già registrata!");
                return;
            }
            // Duplicati su DB
            if (datiPasseggeroDAO.findByEmail(email) != null) {
                JOptionPane.showMessageDialog(null, "Email già registrata nel database!");
                return;
            }

            // Inserisci su DB (codiceFiscale non richiesto, passiamo null)
            DatiPasseggero nuovoPasseggero = new DatiPasseggero(nome, cognome, null, email, password);
            boolean inserito = datiPasseggeroDAO.insert(nuovoPasseggero);
            if (!inserito) {
                JOptionPane.showMessageDialog(null, "Errore durante la registrazione nel database. Riprova.");
                return;
            }

            JOptionPane.showMessageDialog(null, "Registrazione avvenuta con successo! Ora puoi accedere.");
        });
    }

    private void togglePasswordVisibility() {
        if (passwordVisible) {
            passwordField.setEchoChar('•');
            showHideButton.setText("Show");
        } else {
            passwordField.setEchoChar((char) 0);
            showHideButton.setText("Hide");
        }
        passwordVisible = !passwordVisible;
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
    private JPasswordField styledPasswordField() {
        JPasswordField pf = new JPasswordField(13);
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pf.setBackground(panelBgColor);
        pf.setForeground(mainGradientStart);
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255,255,255,180), 1, true),
                BorderFactory.createEmptyBorder(5, 14, 5, 14)
        ));
        pf.setCaretColor(mainGradientStart);
        pf.setEchoChar('•');
        return pf;
    }
    // gradientButton rimane invariato per accedi/registrati
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

    public JPanel getLoginPanel() {
        return loginPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Controller controller = new Controller();
            Amministratore amministratore = new Amministratore("a", "a", "A", "A");
            JFrame frame = new JFrame("Area Login - Aeroporto");
            frame.setContentPane(new LoginGUI(controller, amministratore).getLoginPanel());
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            frame.setSize(500, 350);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}