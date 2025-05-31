package gui;

import javax.swing.*;
import controller.Controller;

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
            }

            if(email.equals("marcorossi@gmail.com") || password.equals("12345")) {
                new AreaPersonaleAmmGUI(controller);
            }

        });
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