package gui;

import controller.Controller;
import javax.swing.*;

/**
 * Classe che gestisce l'interfaccia grafica per la registrazione
 * di un nuovo utente nell'applicazione.
 */
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

    /**
     * Costruttore che inizializza l'interfaccia grafica per la registrazione.
     *
     * @param controller il controller dell'applicazione, usato per gestire
     *                   la logica lato business durante la registrazione.
     */
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

    /**
     * Restituisce il pannello principale della schermata di registrazione.
     *
     * @return il pannello Swing che rappresenta il form di registrazione
     */
    public JPanel getRegistrazionePanel() {
        return registrazionePanel;
    }
}