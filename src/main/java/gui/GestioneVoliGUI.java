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
    private static final String AEROPORTO_LOCALE = "NAP";

    public GestioneVoliGUI(Controller controller) {
        this.controller = controller;

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
        boolean isArrivo = arrivoRadioButton.isSelected();
        if(gateTextField != null) {
            gateTextField.setEnabled(!isArrivo);
            if(isArrivo) gateTextField.setText("");
        }
    }

    private void aggiungiVolo() {
        String codice = safeText(codiceUnivocoTextField);
        String compagnia = safeText(compagniaTextField);
        String data = safeText(dataTextField);
        String otherAirport = safeText(altroAeroportoTextField).toUpperCase();
        if(!otherAirport.isEmpty()) {
            altroAeroportoTextField.setText(otherAirport);
        }

        String orarioPrevisto = orarioPrevistoTextField != null ? safeText(orarioPrevistoTextField) : null;
        String orarioStimato = orarioStimatoTextField != null ? safeText(orarioStimatoTextField) : null;
        if(orarioStimato != null && orarioStimato.isEmpty()) orarioStimato = null;

        String gate = gateTextField != null ? safeText(gateTextField) : null;
        if(gate != null && gate.isEmpty()) gate = null;

        String direzione = arrivoRadioButton.isSelected() ? "A" : "P";
        StatoVolo stato = statoVoloComboBox != null ? (StatoVolo) statoVoloComboBox.getSelectedItem() : null;

        if(statoVoloComboBox == null) {
            JOptionPane.showMessageDialog(gestioneVoliPanel,
                    "Il componente statoVoloComboBox non è stato associato nel form.",
                    "Configurazione incompleta", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String err = valida(codice, compagnia, data, otherAirport,
                orarioPrevisto, orarioStimato, gate, direzione, stato);
        if(err != null) {
            JOptionPane.showMessageDialog(gestioneVoliPanel, err, "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            controller.aggiungiVolo(codice, compagnia, data, orarioPrevisto, stato, direzione, otherAirport);
            JOptionPane.showMessageDialog(gestioneVoliPanel, "Volo aggiunto con successo!");
            pulisci();
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(gestioneVoliPanel,
                    "Errore inserimento: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
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