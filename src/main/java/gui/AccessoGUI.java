package gui;

import controller.Controller;

import javax.swing.*;

public class AccessoGUI {
    private JTextField EmailtextField;
    private JTextField PasswordtextField;
    private JButton ACCEDIButton;
    private Controller controller;
    private String emailinserita;
    private String passwordinserita;


    public AccessoGUI(Controller controller) {
        this.controller = controller;
        emailinserita= EmailtextField.getText().trim();
        passwordinserita= PasswordtextField.getText().trim();

        ACCEDIButton.addActionListener(e -> {

            if(emailinserita.isEmpty() || passwordinserita.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Inserisci le credenziali");
                new AccessoGUI(controller);
            }
        });

    }



}
