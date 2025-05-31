package gui;

import controller.Controller;

import javax.swing.*;

public class RegistrazioneGUI {
    private JTextField NometextField;
    private JTextField CognometextField;
    private JTextField EmailtextField;
    private JTextField PasswordtextField;
    private JButton REGISTRATIButton;
    private Controller controller;
    private String nomeinserito;
    private String cognomeinserito;
    private String emailinserita;
    private String passwordinserita;
    private String ruolo;


    public RegistrazioneGUI(Controller controller, String ruolo) {
        this.controller = controller;
        this.ruolo = ruolo;
        nomeinserito= NometextField.getText().trim();
        cognomeinserito= CognometextField.getText().trim();
        emailinserita= EmailtextField.getText().trim();
        passwordinserita= PasswordtextField.getText().trim();

        if(ruolo.equals("utente")) {



        }else if(ruolo.equals("amminitratore")) {



        }

        REGISTRATIButton.addActionListener(e -> {

            if(nomeinserito.isEmpty() || cognomeinserito.isEmpty() || emailinserita.isEmpty() || passwordinserita.isEmpty()) {}
                JOptionPane.showMessageDialog(null, "Inserisci le credenziali");
                new RegistrazioneGUI(controller);
        });


        public static void main(String[] args) {


        }


    }




}
