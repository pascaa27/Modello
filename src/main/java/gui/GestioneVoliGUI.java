package gui;

import controller.Controller;
import model.StatoVolo;

import javax.swing.*;
import java.util.regex.Pattern;

public class GestioneVoliGUI {

    private JPanel gestioneVoliPanel;
    private JTextField codiceUnivocoTextField;
    private JTextField compagniaTextField;
    private JTextField dataTextField;
    private JTextField altroAeroportoTextField;
    private JTextField orarioPrevistoTextField;
    private JTextField orarioStimatoTextField;
    private JTextField gateTextField;
    private JRadioButton arrivoRadioButton;
    private JRadioButton partenzaRadioButton;
    private JComboBox<StatoVolo> statoVoloComboBox;
    private JButton aggiungiVoloButton;
    private final Controller controller;
    private final AreaPersonaleAmmGUI areaAmmGUI;
    private static final String AEROPORTO_LOCALE = "NAP";

    public GestioneVoliGUI(Controller controller, AreaPersonaleAmmGUI areaAmmGUI) {
        this.controller = controller;
        this.areaAmmGUI = areaAmmGUI;

        // Gruppo radio
        ButtonGroup direzioneGroup = new ButtonGroup();
        direzioneGroup.add(arrivoRadioButton);
        direzioneGroup.add(partenzaRadioButton);
        setDirezioneDefault();

        // Popola combo stato se presente
        if(statoVoloComboBox != null) {
            statoVoloComboBox.removeAllItems();
            for(StatoVolo sv : StatoVolo.values()) {
                statoVoloComboBox.addItem(sv);
            }
        }

        // Listener direzione
        arrivoRadioButton.addActionListener(e -> toggleDirezione());
        partenzaRadioButton.addActionListener(e -> toggleDirezione());
        toggleDirezione();

        //Pulsante aggiungi
        if(aggiungiVoloButton != null) {
            aggiungiVoloButton.addActionListener(e -> aggiungiVolo());
        }

    }

    private void setDirezioneDefault() {
        if(partenzaRadioButton != null) {
            partenzaRadioButton.setSelected(true);
        }
    }

    private void toggleDirezione() {
        if(gateTextField != null) {
            gateTextField.setEnabled(true);
        }
    }

    private void aggiungiVolo() {
        String codice = safeText(codiceUnivocoTextField);
        String compagnia = safeText(compagniaTextField);
        String data = safeText(dataTextField);
        String orarioPrevisto = safeText(orarioPrevistoTextField);
        String otherAirport = safeText(altroAeroportoTextField).toUpperCase();
        String gate = safeText(gateTextField); // safeText gestisce gateTextField==null

        StatoVolo stato = (StatoVolo) statoVoloComboBox.getSelectedItem();

        String direzione = arrivoRadioButton.isSelected() ? "in arrivo" : "in partenza";

        // DEBUG per capire cosa viene passato
        //System.out.println("DEBUG aggiungiVolo: codice=" + codice + " gate='" + gate + "' direzione=" + direzione);

        // Se il campo è veramente null (non inizializzato nel form), avvisa l'utente/lo sviluppatore
        if (gateTextField == null) {
            JOptionPane.showMessageDialog(gestioneVoliPanel,
                    "Errore: il campo GATE non è collegato nel form (variabile gateTextField è null). Controlla il binding nel GUI builder.",
                    "Configurazione form", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Se vuoi impedire aggiunte senza gate per partenze in imbarco, lascia la validazione già presente.
        // Alla fine chiami il controller (assicurati che la firma del controller includa 'gate')
        controller.aggiungiVolo(
                codice,
                compagnia,
                data,
                orarioPrevisto,
                stato,
                direzione,
                otherAirport,
                gate
        );

        JOptionPane.showMessageDialog(gestioneVoliPanel, "Volo aggiunto con successo!");

        // 👇 aggiorna la tabella orario dell’area amministratore
        areaAmmGUI.aggiornaTabellaOrario();
    }


    private String valida(String codice,
                          String compagnia,
                          String data,
                          String otherAirport,
                          String orarioPrevisto,
                          String orarioStimato,
                          String gate,
                          String direzione,
                          StatoVolo stato) {

        if(codice.isEmpty()) return "Codice volo obbligatorio.";

        if(compagnia.isEmpty()) return "Compagnia obbligatoria.";

        if(!Pattern.matches("\\d{4}-\\d{2}-\\d{2}", data))
            return "Data deve essere nel formato yyyy-MM-dd.";

        if(!Pattern.matches("[A-Za-z]{3}", otherAirport))
            return "Altro aeroporto deve essere un codice IATA (3 lettere).";

        if(otherAirport.equalsIgnoreCase(AEROPORTO_LOCALE))
            return "Altro aeroporto deve essere diverso da " + AEROPORTO_LOCALE + ".";

        if(orarioPrevisto == null || !Pattern.matches("\\d{2}:\\d{2}", orarioPrevisto))
            return "Orario previsto (HH:mm) obbligatorio.";

        if(orarioStimato != null && !orarioStimato.isEmpty() && !Pattern.matches("\\d{2}:\\d{2}", orarioStimato))
            return "Orario stimato deve essere HH:mm oppure vuoto.";

        if("P".equals(direzione) && stato == StatoVolo.IMBARCO && (gate == null || gate.isEmpty()))
            return "Gate obbligatorio per una partenza in IMBARCO.";

        return null;
    }

    private void pulisci() {
        codiceUnivocoTextField.setText("");
        compagniaTextField.setText("");
        dataTextField.setText("");
        altroAeroportoTextField.setText("");

        if(orarioPrevistoTextField != null) orarioPrevistoTextField.setText("");
        if(orarioStimatoTextField != null) orarioStimatoTextField.setText("");
        if(gateTextField != null) gateTextField.setText("");
        if(statoVoloComboBox != null) statoVoloComboBox.setSelectedIndex(0);

        partenzaRadioButton.setSelected(true);
        toggleDirezione();
    }

    private String safeText(JTextField f) {
        return f == null ? "" : f.getText().trim();
    }

    public JPanel getPanelDatiVolo() {
        return gestioneVoliPanel;
    }
}