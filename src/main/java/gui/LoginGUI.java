package gui;

import javax.swing.*;
import controller.Controller;

public class LoginGUI {
    private JPanel loginPanel;
    private JButton accediUtenteButton;
    private JButton registratiUtenteButton;
    private JButton accediAmmButton;
    private Controller controller;

    public LoginGUI(Controller controller) {
        this.controller = controller;

        accediUtenteButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Inserire le credenziali per l'accesso utente.");
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(loginPanel); // Ottieni il frame corrente (finestra login)
            frame.dispose(); // Chiudi la finestra corrente

            JFrame nuovoFrame = new JFrame("Accesso Utente");
            nuovoFrame.setContentPane(new AccessoUtenteGUI(controller).getAccessoPanel()); // getPanel() deve restituire il JPanel della nuova GUI
            nuovoFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            nuovoFrame.pack();
            nuovoFrame.setLocationRelativeTo(null);
            nuovoFrame.setVisible(true);
        });

        registratiUtenteButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Inserire le credenziali per la registrazione utente.");
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(loginPanel);
            frame.dispose();

            JFrame nuovoFrame = new JFrame("Registrazione Utente");
            nuovoFrame.setContentPane(new RegistrazioneUtenteGUI(controller).getRegistrazionePanel());
            nuovoFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            nuovoFrame.pack();
            nuovoFrame.setLocationRelativeTo(null);
            nuovoFrame.setVisible(true);
        });

        accediAmmButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Inserire le credenziali per l'accesso amministratore.");
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(loginPanel);
            frame.dispose();

            JFrame nuovoFrame = new JFrame("Accesso Amministratore");
            nuovoFrame.setContentPane(new AccessoAmmGUI(controller).getAccessoAmmPanel());
            nuovoFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            nuovoFrame.pack();
            nuovoFrame.setLocationRelativeTo(null);
            nuovoFrame.setVisible(true);
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
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}