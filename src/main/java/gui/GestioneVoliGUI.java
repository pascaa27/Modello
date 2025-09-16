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
    private JButton rimuoviVoloButton;
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

        if(rimuoviVoloButton != null) {
            rimuoviVoloButton.addActionListener(e -> rimuoviVolo());
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
        String orarioStimato = safeText(orarioStimatoTextField);
        String otherAirport = safeText(altroAeroportoTextField).toUpperCase();
        String gate = safeText(gateTextField);

        StatoVolo stato = (StatoVolo) statoVoloComboBox.getSelectedItem();
        String direzione = arrivoRadioButton.isSelected() ? "in arrivo" : "in partenza"; // così matcha la logica di valida()

        // 🔎 Richiamo la validazione
        String errore = valida(codice, compagnia, data, otherAirport, orarioPrevisto, orarioStimato, gate, direzione, stato);
        if(errore != null) {
            JOptionPane.showMessageDialog(
                    gestioneVoliPanel,
                    errore,
                    "Errore inserimento volo",
                    JOptionPane.ERROR_MESSAGE
            );
            return; // blocca inserimento
        }

        // Se il campo è veramente null (binding errato nel form)
        if(gateTextField == null) {
            JOptionPane.showMessageDialog(gestioneVoliPanel,
                    "Errore: il campo GATE non è collegato nel form.",
                    "Configurazione form", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Se tutto ok → inserisci
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
        pulisci(); // reset campi dopo inserimento
        areaAmmGUI.aggiornaTabellaOrario();
    }

    private void rimuoviVolo() {
        String codice = safeText(codiceUnivocoTextField);

        if(codice.isEmpty()) {
            JOptionPane.showMessageDialog(
                    gestioneVoliPanel,
                    "Inserisci il codice univoco del volo da rimuovere.",
                    "Errore rimozione",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        int conferma = JOptionPane.showConfirmDialog(
                gestioneVoliPanel,
                "Sei sicuro di voler rimuovere il volo con codice " + codice + "?",
                "Conferma rimozione",
                JOptionPane.YES_NO_OPTION
        );

        if(conferma == JOptionPane.YES_OPTION) {
            boolean successo = controller.rimuoviVolo(codice); // 🔎 Assicurati che esista questo metodo nel Controller

            if(successo) {
                JOptionPane.showMessageDialog(
                        gestioneVoliPanel,
                        "Volo rimosso con successo!",
                        "Rimozione completata",
                        JOptionPane.INFORMATION_MESSAGE
                );
                pulisci();
                areaAmmGUI.aggiornaTabellaOrario();
            } else {
                JOptionPane.showMessageDialog(
                        gestioneVoliPanel,
                        "Nessun volo trovato con codice " + codice,
                        "Errore rimozione",
                        JOptionPane.ERROR_MESSAGE
                );
            }
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

        if(codice.isEmpty()) return "Codice univoco volo obbligatorio.";

        if(compagnia.isEmpty()) return "Compagnia obbligatoria.";

        if(!Pattern.matches("\\d{4}-\\d{2}-\\d{2}", data))
            return "Data deve essere nel formato yyyy-MM-dd.";

        if(!Pattern.matches("[A-Za-z]{3}", otherAirport))
            return "Altro aeroporto deve essere un codice IATA (3 lettere).";

        if(otherAirport.equalsIgnoreCase(AEROPORTO_LOCALE))
            return "Altro aeroporto deve essere diverso da " + AEROPORTO_LOCALE + ".";

        if(orarioPrevisto == null || !Pattern.matches("\\d{2}:\\d{2}", orarioPrevisto))
            return "Orario previsto (HH:mm) obbligatorio.";

        if(orarioStimato == null || !Pattern.matches("\\d{2}:\\d{2}", orarioStimato))
            return "Orario stimato (HH:mm) obbligatorio.";

        if(gate == null || gate.isEmpty())
            return "Il campo Gate è obbligatorio.";

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