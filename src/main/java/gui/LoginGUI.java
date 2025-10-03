package gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;
import controller.Controller;
import model.Amministratore;
import model.UtenteGenerico;
import model.AreaPersonale;
import model.DatiPasseggero;
import dao.DatiPasseggeroDAO;
import dao.postgres.DatiPasseggeroDAOPostgres;

/**
 * Gestisce l'interfaccia grafica per l'autenticazione (login e registrazione)
 * degli utenti dell'applicazione Aeroporto.
 */
public class LoginGUI {
    private static final String FONT_FAMILY = "Segoe UI";
    private static final String BTN_SHOW = "Show";
    private static final String BTN_HIDE = "Hide";

    private JPanel loginPanel;
    private JButton accediButton;
    private JButton registratiButton;
    private JTextField nomeTextField;
    private JTextField cognomeTextField1;
    private JTextField emailTextField;
    private JButton showHideButton;
    private JPasswordField passwordField;
    private Controller controller;
    private static final String ADMIN_EMAIL = "pasqualepisano@gmail.com";
    private static final String ADMIN_PASS = "30L";

    // Palette colori
    private final Color mainGradientStart = new Color(30, 87, 153);
    private final Color mainGradientEnd   = new Color(125, 185, 232);
    private final Color panelBgColor      = new Color(245, 249, 255);
    private final Color buttonColor       = new Color(60, 130, 200);
    private final Color buttonHoverColor  = new Color(30, 87, 153);

    // DAO per salvataggio su DB di datipasseggeri
    private final DatiPasseggeroDAO datiPasseggeroDAO;

    private boolean passwordVisible = false;

    /**
     * Costruisce la finestra di login e inizializza i componenti grafici.
     *
     * @param controller      controller principale che gestisce la logica dell'applicazione
     * @param amministratore  oggetto amministratore da usare per l'area riservata admin
     */
    public LoginGUI(Controller controller, Amministratore amministratore) {
        this.controller = controller;
        this.datiPasseggeroDAO = new DatiPasseggeroDAOPostgres();
        initUI(amministratore);
    }

    /**
     * Inizializza i componenti grafici della schermata di login,
     * includendo campi di input, password, pulsanti e listener.
     *
     * @param amministratore istanza di amministratore per l'accesso admin
     */
    private void initUI(Amministratore amministratore) {
        loginPanel = createGradientPanel();
        loginPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = baseGbc();

        addFormFields(gbc);
        addPasswordRow(gbc);
        addButtonsRow(gbc);

        attachListeners(amministratore);
    }

    /**
     * Crea un pannello con sfondo a gradiente blu.
     *
     * @return pannello con gradiente
     */
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

    /**
     * Imposta i vincoli di base del {@link GridBagLayout}.
     *
     * @return oggetto {@link GridBagConstraints} preconfigurato
     */
    private GridBagConstraints baseGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(14, 18, 8, 18);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        return gbc;
    }

    /**
     * Aggiunge i campi di input Nome, Cognome ed Email al form di login.
     *
     * @param gbc vincoli di posizionamento GridBag
     */
    private void addFormFields(GridBagConstraints gbc) {
        // Nome
        loginPanel.add(styledLabelWhite("Nome:"), gbc);
        gbc.gridx = 1;
        nomeTextField = styledTextFieldWhite("");
        loginPanel.add(nomeTextField, gbc);

        // Cognome
        gbc.gridx = 0; gbc.gridy++;
        loginPanel.add(styledLabelWhite("Cognome:"), gbc);
        gbc.gridx = 1;
        cognomeTextField1 = styledTextFieldWhite("");
        loginPanel.add(cognomeTextField1, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy++;
        loginPanel.add(styledLabelWhite("Email:"), gbc);
        gbc.gridx = 1;
        emailTextField = styledTextFieldWhite("");
        loginPanel.add(emailTextField, gbc);
    }

    /**
     * Aggiunge la riga per l'inserimento della password,
     * con campo {@link JPasswordField} e bottone show/hide.
     *
     * @param gbc vincoli di posizionamento GridBag
     */
    private void addPasswordRow(GridBagConstraints gbc) {
        gbc.gridx = 0; gbc.gridy++;
        loginPanel.add(styledLabelWhite("Password:"), gbc);
        gbc.gridx = 1;

        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.setOpaque(false);

        passwordField = styledPasswordField();
        passwordPanel.add(passwordField, BorderLayout.CENTER);

        // Bottone show/hide semplice (no UI custom)
        showHideButton = new JButton(BTN_SHOW);
        showHideButton.setFont(new Font(FONT_FAMILY, Font.BOLD, 14));
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
    }

    /**
     * Aggiunge i pulsanti "Accedi" e "Registrati" alla GUI.
     *
     * @param gbc vincoli di posizionamento GridBag
     */
    private void addButtonsRow(GridBagConstraints gbc) {
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
    }

    /**
     * Collega i listener ai pulsanti di login e registrazione.
     *
     * @param amministratore istanza amministratore per l'accesso admin
     */
    private void attachListeners(Amministratore amministratore) {
        accediButton.addActionListener(e -> doLogin(amministratore));
        registratiButton.addActionListener(e -> doRegister());
    }

    /**
     * Esegue il login con i dati inseriti dall'utente.
     *
     * @param amministratore istanza amministratore per accesso privilegiato
     */
    private void doLogin(Amministratore amministratore) {
        String nome = nomeTextField.getText().trim();
        String cognome = cognomeTextField1.getText().trim();
        String email = emailTextField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // Controlli base
        if(nome.isEmpty() || cognome.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Compila tutti i campi per l'accesso!");
            return;
        }
        if(!Character.isUpperCase(nome.charAt(0)) || !Character.isUpperCase(cognome.charAt(0))) {
            JOptionPane.showMessageDialog(null, "Nome e Cognome devono iniziare con lettera maiuscola.");
            return;
        }
        if(!email.equals(email.toLowerCase())) {
            JOptionPane.showMessageDialog(null, "L'email deve essere tutta minuscola.");
            return;
        }

        // Admin hardcoded
        if(email.equals(ADMIN_EMAIL) && password.equals(ADMIN_PASS)) {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(loginPanel);
            if(frame != null) frame.dispose();

            JFrame nuovoFrame = new JFrame("Area Personale Amministratore");
            nuovoFrame.setContentPane(new AreaPersonaleAmmGUI(controller, amministratore).getAreaPersonaleAmmPanel());
            nuovoFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            nuovoFrame.setSize(1100, 750);
            nuovoFrame.setLocationRelativeTo(null);
            nuovoFrame.setVisible(true);
            return;
        }

        // Login persistente su DB
        DatiPasseggero p = datiPasseggeroDAO.findByEmail(email);
        if(p != null && Objects.equals(p.getPassword(), password)) {
            UtenteGenerico utente = new UtenteGenerico(
                    p.getEmail(),
                    p.getPassword(),
                    p.getNome(),
                    p.getCognome(),
                    new ArrayList<>(),
                    new AreaPersonale()
            );

            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(loginPanel);
            if(frame != null) frame.dispose();

            JFrame nuovoFrame = new JFrame("Area Personale Utente");
            nuovoFrame.setContentPane(new AreaPersonaleUtenteGUI(controller, utente).getPanel());
            nuovoFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            nuovoFrame.setSize(1100, 750);
            nuovoFrame.setLocationRelativeTo(null);
            nuovoFrame.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Credenziali non valide. Registrati o riprova.");
        }
    }

    /**
     * Esegue la registrazione di un nuovo utente.
     */
    private void doRegister() {
        String nome = nomeTextField.getText().trim();
        String cognome = cognomeTextField1.getText().trim();
        String email = emailTextField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if(nome.isEmpty() || cognome.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Compila tutti i campi per la registrazione!");
            return;
        }
        if(!Character.isUpperCase(nome.charAt(0)) || !Character.isUpperCase(cognome.charAt(0))) {
            JOptionPane.showMessageDialog(null, "Nome e Cognome devono iniziare con lettera MAIUSCOLA.");
            return;
        }
        if(!email.equals(email.toLowerCase())) {
            JOptionPane.showMessageDialog(null, "L'email deve essere tutta minuscola.");
            return;
        }
        if(email.equals(ADMIN_EMAIL)) {
            JOptionPane.showMessageDialog(null, "Email già registrata!");
            return;
        }
        if(datiPasseggeroDAO.findByEmail(email) != null) {
            JOptionPane.showMessageDialog(null, "Email già registrata nel database!");
            return;
        }

        DatiPasseggero nuovoPasseggero = new DatiPasseggero(nome, cognome, null, email, password);
        boolean inserito = datiPasseggeroDAO.insert(nuovoPasseggero);
        if(!inserito) {
            JOptionPane.showMessageDialog(null, "Errore durante la registrazione nel database. Riprova.");
            return;
        }

        JOptionPane.showMessageDialog(null, "Registrazione avvenuta con successo! Ora puoi accedere.");
    }

    /**
     * Alterna la visibilità del campo password tra nascosto e visibile.
     * Aggiorna anche il testo del bottone show/hide.
     */
    private void togglePasswordVisibility() {
        if(passwordVisible) {
            passwordField.setEchoChar('•');
            showHideButton.setText(BTN_SHOW);
        } else {
            passwordField.setEchoChar((char) 0);
            showHideButton.setText(BTN_HIDE);
        }
        passwordVisible = !passwordVisible;
    }

    /**
     * Crea una label bianca con font bold.
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
     * Crea un campo di testo con stile personalizzato (sfondo chiaro, bordo arrotondato).
     *
     * @param text testo iniziale
     * @return campo di testo stilizzato
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
     * Crea un campo password con stile personalizzato.
     *
     * @return password field stilizzato
     */
    private JPasswordField styledPasswordField() {
        JPasswordField pf = new JPasswordField(13);
        pf.setFont(new Font(FONT_FAMILY, Font.PLAIN, 14));
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

    /**
     * Crea un pulsante con sfondo gradiente e bordi arrotondati.
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

    /**
     * Restituisce il pannello principale della schermata di login.
     *
     * @return pannello login
     */
    public JPanel getLoginPanel() {
        return loginPanel;
    }

    /**
     * Metodo main di test per lanciare la schermata di login in autonomia.
     *
     * @param args argomenti da linea di comando (non utilizzati)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Controller controller = new Controller();
            Amministratore amministratore = new Amministratore("pasqualepisano@gmail.com", "30L", "Pasquale", "Pisano");
            JFrame frame = new JFrame("Area Login - Aeroporto");
            frame.setContentPane(new LoginGUI(controller, amministratore).getLoginPanel());
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(500, 350);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}