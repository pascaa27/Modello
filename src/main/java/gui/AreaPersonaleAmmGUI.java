package gui;

import javax.swing.*;
import controller.Controller;
import model.Amministratore;
import model.UtenteGenerico;

import java.awt.*;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class AreaPersonaleAmmGUI {
    private JTextField nomeTextField;
    private JTextField cognomeTextField;
    private JTextField emailTextField;
    private JPanel areaPersonaleAmmPanel;
    private JButton mostraPasswordButton;
    private Controller controller;
    private JPasswordField passwordField;
    private JTabbedPane tabbedPane1;
    private JTextField numeroVoloTextField;
    private JTextField compagniaTextField;
    private JComboBox<String> statoComboBox;
    private JTextField dataTextField;
    private JTable risultatiRicercaTable;
    private JTextField nomePasseggeroTextField;
    private JTextField cognomePasseggeroTextField;
    private JTextField numeroVoloPasseggeroTextField;
    private JTextField numeroPrenotazionePasseggeroTextField;
    private JButton cercaVoloButton;
    private JButton cercaPasseggeroButton;
    private JTable risultatiRicercaPasseggeroable;
    private JTextField codiceBagaglioTextField;
    private JTextField numeroVoloBagaglioTextField;
    private JTextField nomePasseggeroBagaglioTextField;
    private JComboBox<String> statoBagaglioComboBox;
    private JButton cercaBagaglioButton;
    private JTable risultatiRicercaBagaglioTable;
    private JButton gestioneVoliButton;
    private JButton gestionePrenotazioniButton;
    private JButton gestioneGateButton;
    private boolean passwordVisibile = false;
    private TabellaOrarioGUI tabellaOrarioGUI;

    public AreaPersonaleAmmGUI(Controller controller, Amministratore amministratore) {
        this.controller = controller;

        // Pannello principale
        areaPersonaleAmmPanel = new JPanel();
        areaPersonaleAmmPanel.setLayout(new BoxLayout(areaPersonaleAmmPanel, BoxLayout.Y_AXIS));

        // Dati amministratore
        JPanel datiPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        datiPanel.add(new JLabel("Nome:"));
        nomeTextField = new JTextField(amministratore.getNomeUtente());
        nomeTextField.setEditable(false);
        datiPanel.add(nomeTextField);

        datiPanel.add(new JLabel("Cognome:"));
        cognomeTextField = new JTextField(amministratore.getCognomeUtente());
        cognomeTextField.setEditable(false);
        datiPanel.add(cognomeTextField);

        datiPanel.add(new JLabel("Email:"));
        emailTextField = new JTextField(amministratore.getLogin());
        emailTextField.setEditable(false);
        datiPanel.add(emailTextField);

        datiPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(amministratore.getPassword());
        passwordField.setEditable(false);
        passwordField.setEchoChar('•');

        mostraPasswordButton = new JButton("Show");
        mostraPasswordButton.setPreferredSize(new Dimension(60, 28));
        mostraPasswordButton.setFocusPainted(false);
        mostraPasswordButton.setMargin(new Insets(2, 2, 2, 2));
        mostraPasswordButton.setBorderPainted(false);
        mostraPasswordButton.addActionListener(e -> mostraNascondiPassword());

        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        passwordPanel.add(mostraPasswordButton, BorderLayout.EAST);
        datiPanel.add(passwordPanel);

        areaPersonaleAmmPanel.add(datiPanel);

        // TabbedPane con i 3 pannelli
        tabbedPane1 = new JTabbedPane();
        tabbedPane1.addTab("Voli", creaTabVoli());
        tabbedPane1.addTab("Passeggeri", creaTabPasseggeri());
        tabbedPane1.addTab("Bagagli", creaTabBagagli());

        areaPersonaleAmmPanel.add(tabbedPane1);

        JPanel bottoniPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        gestioneVoliButton = new JButton("Gestione Voli");
        gestionePrenotazioniButton = new JButton("Gestione Prenotazioni");
        gestioneGateButton = new JButton("Gestione Gate");
        bottoniPanel.add(gestioneVoliButton);
        bottoniPanel.add(gestionePrenotazioniButton);
        bottoniPanel.add(gestioneGateButton);

        areaPersonaleAmmPanel.add(bottoniPanel);

        gestioneVoliButton.addActionListener(e -> {
            GestioneVoli gestioneVoli = new GestioneVoli(controller);
            JFrame frame = new JFrame("Gestione Voli");
            frame.setContentPane(gestioneVoli.getPanelDatiVolo());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        gestionePrenotazioniButton.addActionListener(e -> {
            GestionePrenotazioni gestionePrenotazioni = new GestionePrenotazioni(controller, amministratore);
            JFrame frame = new JFrame("Gestione Prenotazioni");
            frame.setContentPane(gestionePrenotazioni.getPanelPrenotazione());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        gestioneGateButton.addActionListener(e -> {
            GestioneGateGUI gestioneGate = new GestioneGateGUI(controller);
            JFrame frame = new JFrame("Gestione Gate");
            frame.setContentPane(gestioneGate.getPanelGate());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    // Tab Voli
    private JPanel creaTabVoli() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel ricercaPanel = new JPanel(new GridLayout(2, 5, 8, 8));
        ricercaPanel.add(new JLabel("Numero volo:"));
        numeroVoloTextField = new JTextField(10);
        ricercaPanel.add(numeroVoloTextField);

        ricercaPanel.add(new JLabel("Compagnia:"));
        compagniaTextField = new JTextField(10);
        ricercaPanel.add(compagniaTextField);

        ricercaPanel.add(new JLabel("Stato:"));
        statoComboBox = new JComboBox<>(new String[]{"", "In arrivo", "In partenza", "Ritardato", "Cancellato"});
        ricercaPanel.add(statoComboBox);

        ricercaPanel.add(new JLabel("Data:"));
        dataTextField = new JTextField(10);
        ricercaPanel.add(dataTextField);

        panel.add(ricercaPanel);

        cercaVoloButton = new JButton("Cerca");
        cercaVoloButton.addActionListener(e -> ricercaVoli());
        panel.add(cercaVoloButton);

        // Integri TabellaOrarioGUI
        tabellaOrarioGUI = new TabellaOrarioGUI(controller);
        panel.add(tabellaOrarioGUI.getPanel());

        return panel;
    }

    // Tab Passeggeri
    private JPanel creaTabPasseggeri() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel ricercaPanel = new JPanel(new GridLayout(2, 4, 8, 8));
        ricercaPanel.add(new JLabel("Nome:"));
        nomePasseggeroTextField = new JTextField(10);
        ricercaPanel.add(nomePasseggeroTextField);

        ricercaPanel.add(new JLabel("Cognome:"));
        cognomePasseggeroTextField = new JTextField(10);
        ricercaPanel.add(cognomePasseggeroTextField);

        ricercaPanel.add(new JLabel("Numero volo:"));
        numeroVoloPasseggeroTextField = new JTextField(10);
        ricercaPanel.add(numeroVoloPasseggeroTextField);

        ricercaPanel.add(new JLabel("Numero prenotazione:"));
        numeroPrenotazionePasseggeroTextField = new JTextField(10);
        ricercaPanel.add(numeroPrenotazionePasseggeroTextField);

        cercaPasseggeroButton = new JButton("Cerca");
        cercaPasseggeroButton.addActionListener(e -> ricercaPasseggeri());
        panel.add(ricercaPanel);
        panel.add(cercaPasseggeroButton);

        risultatiRicercaPasseggeroable = new JTable();
        JScrollPane scrollPane = new JScrollPane(risultatiRicercaPasseggeroable);
        panel.add(scrollPane);

        return panel;
    }

    // Tab Bagagli
    private JPanel creaTabBagagli() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel ricercaPanel = new JPanel(new GridLayout(2, 4, 8, 8));
        ricercaPanel.add(new JLabel("Codice bagaglio:"));
        codiceBagaglioTextField = new JTextField(10);
        ricercaPanel.add(codiceBagaglioTextField);


        ricercaPanel.add(new JLabel("Stato:"));
        statoBagaglioComboBox = new JComboBox<>(new String[]{"", "Caricato", "Smarrito", "Trovato"});
        ricercaPanel.add(statoBagaglioComboBox);

        cercaBagaglioButton = new JButton("Cerca");
        cercaBagaglioButton.addActionListener(e -> ricercaBagagli());
        panel.add(ricercaPanel);
        panel.add(cercaBagaglioButton);

        risultatiRicercaBagaglioTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(risultatiRicercaBagaglioTable);
        panel.add(scrollPane);

        return panel;
    }

    // Azioni di ricerca (da collegare al Controller)
    private void ricercaVoli() {
        String numeroVolo = numeroVoloTextField.getText().trim();
        if (numeroVolo.isEmpty()) numeroVolo = null;

        String compagnia = compagniaTextField.getText().trim();
        if (compagnia.isEmpty()) compagnia = null;

        String stato = (String) statoComboBox.getSelectedItem();
        if (stato != null && stato.isBlank()) stato = null;

        String data = dataTextField.getText().trim();
        if (data.isEmpty()) data = null;

        // Controller ritorna List<Object[]>
        List<Object[]> risultati = controller.ricercaVoli(numeroVolo, compagnia, stato, data);

        // Aggiorna la tabella orario
        tabellaOrarioGUI.aggiornaVoli(risultati);
    }

    private void ricercaPasseggeri() {
        String nome = nomePasseggeroTextField.getText();
        String cognome = cognomePasseggeroTextField.getText();
        String numeroVolo = numeroVoloPasseggeroTextField.getText();
        String numeroPrenotazione = numeroPrenotazionePasseggeroTextField.getText();

        List<Object[]> risultati = controller.ricercaPasseggeri(nome, cognome, numeroVolo, numeroPrenotazione);

        DefaultTableModel model = new DefaultTableModel(new String[]{"Nome", "Cognome", "Numero Volo", "Numero Prenotazione"}, 0);
        for (Object[] r : risultati) {
            model.addRow(r);
        }
        risultatiRicercaPasseggeroable.setModel(model);
    }

    private void ricercaBagagli() {
        String codiceBagaglio = codiceBagaglioTextField.getText();
        String stato = (String) statoBagaglioComboBox.getSelectedItem();

        List<Object[]> risultati = controller.ricercaBagagli(codiceBagaglio, stato);

        DefaultTableModel model = new DefaultTableModel(new String[]{"Codice Bagaglio", "Stato"}, 0);
        for (Object[] r : risultati) {
            model.addRow(r);
        }
        risultatiRicercaBagaglioTable.setModel(model);
    }

    private void mostraNascondiPassword() {
        if (passwordVisibile) {
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
}