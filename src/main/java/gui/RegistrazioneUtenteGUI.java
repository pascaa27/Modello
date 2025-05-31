package gui;

import controller.Controller;

import javax.swing.*;

public class RegistrazioneUtenteGUI {
    private JTextField nometextField;
    private JTextField cognometextField;
    private JTextField emailtextField;
    private JTextField passwordtextField;
    private JButton REGISTRATIButton;
    private JPanel registrazionePanel;
    private Controller controller;
    private String nomeinserito;
    private String cognomeinserito;
    private String emailinserita;
    private String passwordinserita;


    public RegistrazioneUtenteGUI(Controller controller) {
        this.controller = controller;
        nomeinserito= nometextField.getText().trim();
        cognomeinserito= cognometextField.getText().trim();
        emailinserita= emailtextField.getText().trim();
        passwordinserita= passwordtextField.getText().trim();


        REGISTRATIButton.addActionListener(e -> {

            if(nomeinserito.isEmpty() || cognomeinserito.isEmpty() || emailinserita.isEmpty() || passwordinserita.isEmpty()) {}
                JOptionPane.showMessageDialog(null, "Inserisci le credenziali");
                new RegistrazioneUtenteGUI(controller);
        });


    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Controller controller = new Controller();
            JFrame frame = new JFrame("Registrazione utente");
            frame.setContentPane(new RegistrazioneUtenteGUI(controller).registrazionePanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

    }




}
