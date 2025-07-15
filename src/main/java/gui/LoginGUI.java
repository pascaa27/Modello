package gui;

import javax.swing.*;
import java.util.*;
import controller.Controller;
import model.Amministratore;
import model.Utente;

public class LoginGUI {
    private JPanel loginPanel;
    private JButton accediButton;
    private JButton registratiButton;
    private JTextField nomeTextField;
    private JTextField cognomeTextField1;
    private JTextField emailTextField;
    private JTextField passwordTextField;
    private Controller controller;

    private static final String ADMIN_EMAIL = "admin@aeroporto.it";
    private static final String ADMIN_PASS = "12345";

    private List<Utente> utentiRegistrati = new ArrayList<>();

    public LoginGUI(Controller controller, Amministratore amministratore) {
        this.controller = controller;

        accediButton.addActionListener(e -> {
            String email = emailTextField.getText().trim();
            String password = passwordTextField.getText().trim();

            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(loginPanel);
            frame.dispose();

            if (email.equals(ADMIN_EMAIL) && password.equals(ADMIN_PASS)) {
                // Area amministratore
                JFrame nuovoFrame = new JFrame("Area Personale Amministratore");
                nuovoFrame.setContentPane(new AreaPersonaleAmmGUI(controller, amministratore).getAreaPersonaleAmmPanel());
                nuovoFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                nuovoFrame.pack();
                nuovoFrame.setLocationRelativeTo(null);
                nuovoFrame.setVisible(true);
            } else {
                // Cerca utente tra i registrati
                Utente utenteTrovato = null;
                for (Utente u : utentiRegistrati) {
                    if (u.getLogin().equals(email) && u.getPassword().equals(password)) {
                        utenteTrovato = u;
                        break;
                    }
                }
                if (utenteTrovato != null) {
                    JFrame nuovoFrame = new JFrame("Area Personale Utente");
                    nuovoFrame.setContentPane(new AreaPersonaleUtenteGUI(controller, utenteTrovato).getPanel());
                    nuovoFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                    nuovoFrame.pack();
                    nuovoFrame.setLocationRelativeTo(null);
                    nuovoFrame.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Credenziali non valide. Registrati o riprova.");
                    frame.setVisible(true); // Torna al login
                }
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

            boolean esiste = false;
            for (Utente u : utentiRegistrati) {
                if (u.getLogin().equals(email)) {
                    esiste = true;
                    break;
                }
            }
            if (esiste || email.equals(ADMIN_EMAIL)) {
                JOptionPane.showMessageDialog(null, "Email già registrata!");
                return;
            }

            // Costruttore corretto
            Utente nuovoUtente = new Utente(email, password, nome, cognome);
            utentiRegistrati.add(nuovoUtente);
            JOptionPane.showMessageDialog(null, "Registrazione avvenuta con successo! Ora puoi accedere.");
        });
    }

    public JPanel getLoginPanel() {
        return loginPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Controller controller = new Controller();
            Amministratore amministratore = new Amministratore(
                    "admin@aeroporto.it", "12345", "Mario", "Rossi"
            );
            JFrame frame = new JFrame("Area Login - Aeroporto");
            frame.setContentPane(new LoginGUI(controller, amministratore).getLoginPanel());
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

}