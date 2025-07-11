package gui;

import javax.swing.*;
import controller.Controller;
import model.Amministratore;

public class RegistrazioneAmmGUI {
    private JPanel registrazioneAmmPanel;
    private JTextField nomeAmmTextField;
    private JTextField cognomeAmmTextField;
    private JTextField emailAmmTextField;
    private JTextField passwordAmmTextField;
    private JButton registratiAmmButton;
    private Controller controller;

    public RegistrazioneAmmGUI(Controller controller) {
        this.controller = controller;

        registratiAmmButton.addActionListener(e -> {
            String nome = nomeAmmTextField.getText();
            String cognome = cognomeAmmTextField.getText();
            String email = emailAmmTextField.getText();
            String password = passwordAmmTextField.getText();

            if(nome.isEmpty() || cognome.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Tutti i campi devono essere compilati.", "Errore", JOptionPane.ERROR_MESSAGE);
            }

            if(nome.equals("Marco") && cognome.equals("Rossi") && email.equals("marcorossi@gmail.com") && password.equals("12345")) {
                Amministratore amministratore = new Amministratore(email, password, "Marco", "Rossi");
                JFrame frame = new JFrame("Area Personale Amministratore");
                frame.setContentPane(new AreaPersonaleAmmGUI(controller, amministratore).getAreaPersonaleAmmPanel());
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(registrazioneAmmPanel, "Credenziali errate.");
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Controller controller = new Controller();
            JFrame frame = new JFrame("Registrazione Amministratore");
            frame.setContentPane(new RegistrazioneAmmGUI(controller).registrazioneAmmPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}