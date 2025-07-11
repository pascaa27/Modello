package gui;

import javax.swing.*;
import controller.Controller;
import model.Utente;

import java.awt.*;

public class AreaPersonaleUtenteGUI {
    private JTextField nomeUtenteTextField;
    private JTextField cognomeUtenteTextField;
    private JButton tabellaOrarioButton;
    private JButton cercaModificaButton;
    private JTextField emailUtenteTextField;
    private JPanel areaPersonaleUtentePanel;
    private Controller controller;

    public AreaPersonaleUtenteGUI(Controller controller, Utente utente) {
        this.controller = controller;

        areaPersonaleUtentePanel = new JPanel(new GridLayout(5, 2, 10, 10));

        areaPersonaleUtentePanel.add(new JLabel("Nome:"));
        nomeUtenteTextField = new JTextField(utente.getNomeUtente());
        nomeUtenteTextField.setEditable(false);
        areaPersonaleUtentePanel.add(nomeUtenteTextField);

        areaPersonaleUtentePanel.add(new JLabel("Cognome:"));
        cognomeUtenteTextField = new JTextField(utente.getCognomeUtente());
        cognomeUtenteTextField.setEditable(false);
        areaPersonaleUtentePanel.add(cognomeUtenteTextField);

        areaPersonaleUtentePanel.add(new JLabel("Email:"));
        emailUtenteTextField = new JTextField(utente.getLogin());
        emailUtenteTextField.setEditable(false);
        areaPersonaleUtentePanel.add(emailUtenteTextField);

        tabellaOrarioButton = new JButton("Tabella Orario");
        cercaModificaButton = new JButton("Cerca/Modifica Prenotazione");
        areaPersonaleUtentePanel.add(tabellaOrarioButton);
        areaPersonaleUtentePanel.add(cercaModificaButton);

        // Listener per Tabella Orario
        tabellaOrarioButton.addActionListener(e -> apriTabellaOrario());

        // Listener per Cerca/Modifica Prenotazione
        cercaModificaButton.addActionListener(e -> apriCercaModificaPrenotazione());
    }

    // Metodo che apre la GUI TabellaOrario
    private void apriTabellaOrario() {
        JFrame frame = new JFrame("Tabella Orario");
        frame.setContentPane(new TabellaOrarioGUI(controller).getPanel());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Metodo che apre la GUI Cerca/Modifica Prenotazione
    private void apriCercaModificaPrenotazione() {
        JFrame frame = new JFrame("Cerca/Modifica Prenotazione");
        frame.setContentPane(new CercaModificaPrenotazioneGUI(controller).getPanel());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public JPanel getPanel() {
        return areaPersonaleUtentePanel;
    }

}

