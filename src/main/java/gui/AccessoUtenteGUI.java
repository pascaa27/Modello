package gui;

import controller.Controller;
import model.Utente;

import javax.swing.*;

public class AccessoUtenteGUI {
    private JTextField emailTextField;
    private JTextField passwordTextField;
    private JButton accediButton;
    private JPanel accessoPanel;
    private Controller controller;

    public AccessoUtenteGUI(Controller controller) {
        this.controller = controller;

        accediButton.addActionListener(e -> {
            String emailInserita = emailTextField.getText().trim();
            String passwordInserita = passwordTextField.getText().trim();

            if(emailInserita.isEmpty() || passwordInserita.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Inserisci le credenziali");
                return;
            }

            // Simulazione login: crea un utente finto (sostituisci con controller.login se ce l'hai)
            Utente utente = new Utente(emailInserita, "Mario", "Rossi", passwordInserita);
            // Se credenziali errate, utente = null

            if (utente != null) {
                JFrame frame = new JFrame("Area Personale Utente");
                frame.setContentPane(new AreaPersonaleUtenteGUI(controller, utente).getPanel());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                // Chiudi la finestra di accesso (prendi la finestra dal bottone)
                SwingUtilities.getWindowAncestor(accessoPanel).dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Credenziali non valide!");
            }
        });
    }

    public JPanel getAccessoPanel() {
        return accessoPanel;
    }


}