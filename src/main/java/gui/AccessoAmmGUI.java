package gui;

import javax.swing.*;
import controller.Controller;
import model.Amministratore;

public class AccessoAmmGUI {
    private JPanel accessoAmmPanel;
    private JTextField emailAmmTextField;
    private JTextField passwordAmmTextField;
    private JButton accediAmmButton;
    private Controller controller;

    public AccessoAmmGUI(Controller controller) {
        this.controller = controller;

        accediAmmButton.addActionListener(e -> {
            String email = emailAmmTextField.getText();
            String password = passwordAmmTextField.getText();

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(accessoAmmPanel, "Email e password non compilate.");
                return;
            }

            if(email.equals("marcorossi@gmail.com") || password.equals("12345")) {
                Amministratore amministratore = new Amministratore(email, password, "Marco", "Rossi");
                JFrame frame = new JFrame("Area Personale Amministratore");
                frame.setContentPane(new AreaPersonaleAmmGUI(controller, amministratore).getAreaPersonaleAmmPanel());
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(accessoAmmPanel, "Credenziali errate.");
            }
        });
    }

    public JPanel getAccessoAmmPanel() {
        return accessoAmmPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Controller controller = new Controller();
            JFrame frame = new JFrame("Accesso amministratore");
            frame.setContentPane(new AccessoAmmGUI(controller).accessoAmmPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}