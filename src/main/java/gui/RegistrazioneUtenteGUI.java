package gui;

import controller.Controller;

import javax.swing.*;

public class RegistrazioneUtenteGUI {
    private JTextField nomeTextField;
    private JTextField cognomeTextField;
    private JTextField emailTextField;
    private JTextField passwordTextField;
    private JButton registratiButton;
    private JPanel registrazionePanel;
    private Controller controller;
    private String nomeinserito;
    private String cognomeinserito;
    private String emailinserita;
    private String passwordinserita;


    public RegistrazioneUtenteGUI(Controller controller) {
        this.controller = controller;
        nomeinserito= nomeTextField.getText().trim();
        cognomeinserito= cognomeTextField.getText().trim();
        emailinserita= emailTextField.getText().trim();
        passwordinserita= passwordTextField.getText().trim();


        registratiButton.addActionListener(e -> {

            if(nomeinserito.isEmpty() || cognomeinserito.isEmpty() || emailinserita.isEmpty() || passwordinserita.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Inserisci le credenziali");
                new RegistrazioneUtenteGUI(controller);
            }
        });
    }

    public JPanel getRegistrazionePanel() {
        return registrazionePanel;
    }
}