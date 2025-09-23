package gui;

import javax.swing.*;
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
    private JTextField passwordTextField;

    private Controller controller;
    private static final String ADMIN_EMAIL = "a";
    private static final String ADMIN_PASS = "a";

    // DAO per salvataggio su DB di datipasseggeri
    private final DatiPasseggeroDAO datiPasseggeroDAO;

    public LoginGUI(Controller controller, Amministratore amministratore) {
        this.controller = controller;
        this.datiPasseggeroDAO = new DatiPasseggeroDAOPostgres();

        accediButton.addActionListener(e -> {
            String email = emailTextField.getText().trim();
            String password = passwordTextField.getText().trim();

            // Admin hardcoded
            if (email.equals(ADMIN_EMAIL) && password.equals(ADMIN_PASS)) {
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(loginPanel);
                frame.dispose();

                JFrame nuovoFrame = new JFrame("Area Personale Amministratore");
                nuovoFrame.setContentPane(new AreaPersonaleAmmGUI(controller, amministratore).getAreaPersonaleAmmPanel());
                nuovoFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                nuovoFrame.pack();
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
                nuovoFrame.pack();
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
            String password = passwordTextField.getText().trim();

            if (nome.isEmpty() || cognome.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Compila tutti i campi per la registrazione!");
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

    public JPanel getLoginPanel() {
        return loginPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Controller controller = new Controller();
            Amministratore amministratore = new Amministratore("a", "a", "a", "a");
            JFrame frame = new JFrame("Area Login - Aeroporto");
            frame.setContentPane(new LoginGUI(controller, amministratore).getLoginPanel());
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}