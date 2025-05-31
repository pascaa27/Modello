package gui;

import javax.swing.*;
import controller.Controller;

public class LoginGUI {
    private JPanel loginPanel;
    private JButton accediUtenteButton;
    private JButton registratiUtenteButton;
    private JButton accediAmmButton;
    private JButton registratiAmmButton;
    private Controller controller;

    public LoginGUI(Controller controller) {
        this.controller = controller;

        accediUtenteButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Inserire le credenziali per l'accesso utente.");
            new AccessoUtenteGUI(controller, "utente");
        });

        registratiUtenteButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Inserire le credenziali per la registrazione utente.");
            new RegistrazioneUtenteGUI(controller, "utente");
        });

        accediAmmButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Inserire le credenziali per l'accesso amministratore.");
            new AccessoUtenteGUI(controller, "amministratore");
        });

        registratiAmmButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Inserire le credenziali per la registrazione amministratore.");
            new RegistrazioneUtenteGUI(controller, "amministratore");
        });
    }

    public JPanel getLoginPanel() {
        return loginPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Controller controller = new Controller();
            JFrame frame = new JFrame("Area Login - Aeroporto");
            frame.setContentPane(new LoginGUI(controller).getLoginPanel());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}