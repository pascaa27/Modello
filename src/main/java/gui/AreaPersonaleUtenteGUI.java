package gui;

import javax.swing.*;
import controller.Controller;
import model.UtenteGenerico;
import java.awt.*;

public class AreaPersonaleUtenteGUI {
    private JTextField nomeUtenteTextField;
    private JTextField cognomeUtenteTextField;
    private JButton tabellaOrarioButton;
    private JButton cercaModificaButton;
    private JTextField emailUtenteTextField;
    private JPanel areaPersonaleUtentePanel;
    private JButton effettuaPrenotazioneButton;
    private Controller controller;
    private UtenteGenerico utente;

    public AreaPersonaleUtenteGUI(Controller controller, UtenteGenerico utente) {
        this.controller = controller;
        this.utente = utente;

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
        effettuaPrenotazioneButton = new JButton("Effettua Prenotazione");
        cercaModificaButton = new JButton("Cerca/Modifica Prenotazione");

        areaPersonaleUtentePanel.add(tabellaOrarioButton);
        areaPersonaleUtentePanel.add(effettuaPrenotazioneButton);
        areaPersonaleUtentePanel.add(cercaModificaButton);

        // Listener per Tabella Orario
        tabellaOrarioButton.addActionListener(e -> apriTabellaOrario());

        // Listener per Effettua Prenotazione
        effettuaPrenotazioneButton.addActionListener(e -> apriEffettuaPrenotazione());

        // Listener per Cerca/Modifica Prenotazione
        cercaModificaButton.addActionListener(e -> apriCercaModificaPrenotazione());
    }

    // Metodo che apre la GUI TabellaOrario
    private void apriTabellaOrario() {
        TabellaOrarioGUI gui = new TabellaOrarioGUI(controller); // già carica dati
        JFrame frame = new JFrame("Tabella Orario");
        frame.setContentPane(gui.getPanel());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Metodo che apre la GUI EffettuaPrenotazione
    private void apriEffettuaPrenotazione() {
        JFrame frame = new JFrame("Effettua Prenotazione");
        frame.setContentPane(new EffettuaPrenotazioneGUI(controller, utente).getPanel());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Metodo che apre la GUI Cerca/Modifica Prenotazione
    private void apriCercaModificaPrenotazione() {
        JFrame frame = new JFrame("Cerca/Modifica Prenotazione");
        frame.setContentPane(
                new CercaModificaPrenotazioneGUI(controller, utente, utente.getUltimoCodicePrenotazione()).getPanel()
        );
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public JPanel getPanel() {
        return areaPersonaleUtentePanel;
    }
}