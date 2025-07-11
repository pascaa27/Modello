package gui;

import controller.Controller;

import javax.swing.*;

public class AccessoUtenteGUI {
    private JTextField emailTextField;
    private JTextField passwordTextField;
    private JButton accediButton;
    private JPanel accessoPanel;
    private Controller controller;
    private String emailInserita;
    private String passwordInserita;


    public AccessoUtenteGUI(Controller controller) {
        this.controller = controller;
        emailInserita= emailTextField.getText().trim();
        passwordInserita= passwordTextField.getText().trim();

        accediButton.addActionListener(e -> {

            if(emailInserita.isEmpty() || passwordInserita.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Inserisci le credenziali");
                new AccessoUtenteGUI(controller);
            }
        });

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
           Controller controller = new Controller();
           JFrame frame = new JFrame("Accesso Utente");
           frame.setContentPane(new AccessoUtenteGUI(controller).accessoPanel);
           frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
           frame.pack();
           frame.setLocationRelativeTo(null);
           frame.setVisible(true);
        });
    }



}