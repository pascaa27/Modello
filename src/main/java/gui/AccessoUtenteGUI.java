package gui;

import controller.Controller;

import javax.swing.*;

public class AccessoUtenteGUI {
    private JTextField emailtextField;
    private JTextField passwordtextField;
    private JButton ACCEDIButton;
    private JPanel accessoPanel;
    private Controller controller;
    private String emailinserita;
    private String passwordinserita;


    public AccessoUtenteGUI(Controller controller) {
        this.controller = controller;
        emailinserita= emailtextField.getText().trim();
        passwordinserita= passwordtextField.getText().trim();

        ACCEDIButton.addActionListener(e -> {

            if(emailinserita.isEmpty() || passwordinserita.isEmpty()) {
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
