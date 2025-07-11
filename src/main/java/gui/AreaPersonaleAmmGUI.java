package gui;

import javax.swing.*;
import controller.Controller;
import model.Amministratore;

import java.awt.*;


public class AreaPersonaleAmmGUI {
    private JTextField nomeTextField;
    private JTextField cognomeTextField;
    private JTextField emailTextField;
    private JPanel areaPersonaleAmmPanel;
    private JButton mostraPasswordButton;
    private JPasswordField passwordField;
    private boolean passwordVisibile = false;

    public AreaPersonaleAmmGUI(Controller controller, Amministratore amministratore) {
        areaPersonaleAmmPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        areaPersonaleAmmPanel.add(new JLabel("Nome:"));
        nomeTextField = new JTextField(amministratore.getNomeAdmin());
        nomeTextField.setEditable(false);
        areaPersonaleAmmPanel.add(nomeTextField);

        areaPersonaleAmmPanel.add(new JLabel("Cognome:"));
        cognomeTextField = new JTextField(amministratore.getCognome());
        cognomeTextField.setEditable(false);
        areaPersonaleAmmPanel.add(cognomeTextField);

        areaPersonaleAmmPanel.add(new JLabel("Email:"));
        emailTextField = new JTextField(amministratore.getLogin());
        emailTextField.setEditable(false);
        areaPersonaleAmmPanel.add(emailTextField);

        areaPersonaleAmmPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(amministratore.getPassword());
        passwordField.setEditable(false);
        passwordField.setEchoChar('•');

// Bottone piccolo solo icona o emoji
        mostraPasswordButton = new JButton("Show");
        mostraPasswordButton.setPreferredSize(new Dimension(60, 28));
        mostraPasswordButton.setFocusPainted(false);
        mostraPasswordButton.setMargin(new Insets(2,2,2,2));
        mostraPasswordButton.setBorderPainted(false);
        mostraPasswordButton.addActionListener(e -> mostraNascondiPassword());

// Pannello orizzontale per password e bottone
        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        passwordPanel.add(mostraPasswordButton, BorderLayout.EAST);

// Aggiungi il pannello orizzontale alla griglia principale
        areaPersonaleAmmPanel.add(passwordPanel);
    }

    private void mostraNascondiPassword() {
        if(passwordVisibile) {
            passwordField.setEchoChar('•');
            mostraPasswordButton.setText("Show");
        } else {
            passwordField.setEchoChar((char) 0);
            mostraPasswordButton.setText("Hide");
        }
        passwordVisibile = !passwordVisibile;
    }

    public JPanel getAreaPersonaleAmmPanel() {
        return areaPersonaleAmmPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Controller controller = new Controller();
            Amministratore amm = new Amministratore("marcorossi@gmail.com", "12345", "Marco", "Rossi");
            JFrame frame = new JFrame("Area Personale Amministratore");
            frame.setContentPane(new AreaPersonaleAmmGUI(controller, amm).getAreaPersonaleAmmPanel());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}