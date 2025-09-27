package gui;

import controller.Controller;
import model.StatoVolo;
import model.Volo;

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

    // NUOVI pulsanti che devi inserire nel .form con questi variable name:
    private JButton cercaCodiceButton;
    private JButton confermaModificheButton;

    private final Controller controller;
    private final AreaPersonaleAmmGUI areaAmmGUI;
    private static final String AEROPORTO_LOCALE = "NAP";

    // Tiene traccia del volo caricato in "modifica"
    private String codiceVoloSelezionato = null;

    public GestioneVoliGUI(Controller controller, AreaPersonaleAmmGUI areaAmmGUI) {
        this.controller = controller;
        this.areaAmmGUI = areaAmmGUI;

        // Gruppo radio
        ButtonGroup direzioneGroup = new ButtonGroup();
        direzioneGroup.add(arrivoRadioButton);
        direzioneGroup.add(partenzaRadioButton);
        setDirezioneDefault();

        // Popola combo stato se presente
        if (statoVoloComboBox != null) {
            statoVoloComboBox.removeAllItems();
            for (StatoVolo sv : StatoVolo.values()) {
                statoVoloComboBox.addItem(sv);
            }
        }

        // Listener direzione
        if (arrivoRadioButton != null) arrivoRadioButton.addActionListener(e -> toggleDirezione());
        if (partenzaRadioButton != null) partenzaRadioButton.addActionListener(e -> toggleDirezione());
        toggleDirezione();

        // Pulsante Aggiungi
        if (aggiungiVoloButton != null) {
            aggiungiVoloButton.addActionListener(e -> aggiungiVolo());
        }

        // Pulsante Rimuovi
        if (rimuoviVoloButton != null) {
            rimuoviVoloButton.addActionListener(e -> rimuoviVolo());
        }

        // NUOVO: Pulsante Cerca (usa Controller.cercaVolo)
        if (cercaCodiceButton != null) {
            cercaCodiceButton.addActionListener(e -> cercaVoloPerCodice());
        }

        // NUOVO: Pulsante Conferma Modifiche (usa Controller.aggiornaVolo(codice, stato, nuovoOrarioPrevisto))
        if (confermaModificheButton != null) {
            confermaModificheButton.addActionListener(e -> confermaModifiche());
            confermaModificheButton.setEnabled(false); // attivo solo dopo una "cerca" riuscita
        }
    }

    private void setDirezioneDefault() {
        if (partenzaRadioButton != null) {
            partenzaRadioButton.setSelected(true);
        }
    }

    private void toggleDirezione() {
        if (gateTextField != null) {
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
        String direzione = arrivoRadioButton.isSelected() ? "in arrivo" : "in partenza";

        String errore = valida(codice, compagnia, data, otherAirport, orarioPrevisto, orarioStimato, gate, direzione, stato);
        if (errore != null) {
            JOptionPane.showMessageDialog(
                    gestioneVoliPanel,
                    errore,
                    "Errore inserimento volo",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (gateTextField == null) {
            JOptionPane.showMessageDialog(gestioneVoliPanel,
                    "Errore: il campo GATE non è collegato nel form.",
                    "Configurazione form", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- GESTIONE ECCEZIONE ---
        try {
            controller.aggiungiVolo(
                    codice,
                    compagnia,
                    data,
                    orarioPrevisto,
                    orarioStimato,
                    stato,
                    direzione,
                    otherAirport,
                    gate
            );
            JOptionPane.showMessageDialog(gestioneVoliPanel, "Volo aggiunto con successo!");
            pulisci();
            areaAmmGUI.aggiornaTabellaOrario();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    gestioneVoliPanel,
                    "Errore: esiste già un volo con questo codice univoco!\nDettaglio: " + ex.getMessage(),
                    "Errore inserimento volo",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void rimuoviVolo() {
        String codice = safeText(codiceUnivocoTextField);

        if (codice.isEmpty()) {
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

        if (conferma == JOptionPane.YES_OPTION) {
            boolean successo = controller.rimuoviVolo(codice);

            if (successo) {
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

    // --- CERCA PER CODICE (usa Controller.cercaVolo) ---
    private void cercaVoloPerCodice() {
        String codice = safeText(codiceUnivocoTextField);
        if (codice.isEmpty()) {
            JOptionPane.showMessageDialog(gestioneVoliPanel,
                    "Inserisci un codice volo da cercare.",
                    "Cerca volo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Volo v = controller.cercaVolo(codice);
            if (v == null) {
                JOptionPane.showMessageDialog(gestioneVoliPanel,
                        "Nessun volo trovato con codice " + codice,
                        "Cerca volo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Popola i campi
            codiceUnivocoTextField.setText(v.getCodiceUnivoco());
            compagniaTextField.setText(v.getCompagniaAerea());
            dataTextField.setText(v.getDataVolo());
            orarioPrevistoTextField.setText(v.getOrarioPrevisto());
            orarioStimatoTextField.setText(v.getOrarioStimato());   // <— IMPORTANTE
            altroAeroportoTextField.setText(v.getAeroporto());
            gateTextField.setText(v.getGate());

            if (statoVoloComboBox != null) {
                StatoVolo st = v.getStato();
                if (st != null) statoVoloComboBox.setSelectedItem(st);
                else if (statoVoloComboBox.getItemCount() > 0) statoVoloComboBox.setSelectedIndex(0);
            }

            String direzione = v.getArrivoPartenza();
            if ("in arrivo".equalsIgnoreCase(direzione)) arrivoRadioButton.setSelected(true);
            else partenzaRadioButton.setSelected(true);
            toggleDirezione();

            setLockCodice(true);
            codiceVoloSelezionato = v.getCodiceUnivoco();
            if (confermaModificheButton != null) confermaModificheButton.setEnabled(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(gestioneVoliPanel,
                    "Errore durante la ricerca: " + ex.getMessage(),
                    "Cerca volo", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- CONFERMA MODIFICHE ---
    // Si allinea alla firma del Controller:
    // public void aggiornaVolo(String codiceUnivoco, StatoVolo nuovoStato, String nuovoOrario)
    // NOTA: nuovoOrario = orarioPrevisto (dato il tuo Controller aggiorna orarioPrevisto)
    private void confermaModifiche() {
        if (codiceVoloSelezionato == null || codiceVoloSelezionato.isEmpty()) {
            JOptionPane.showMessageDialog(gestioneVoliPanel,
                    "Prima cerca e carica un volo da modificare.",
                    "Modifica volo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Leggi tutti i campi
        String compagnia = safeText(compagniaTextField);
        String data = safeText(dataTextField);
        String orarioPrevisto = safeText(orarioPrevistoTextField);
        String orarioStimato = safeText(orarioStimatoTextField); // non usato dal DAO attuale
        String aeroporto = safeText(altroAeroportoTextField).toUpperCase();
        String gate = safeText(gateTextField);
        StatoVolo stato = (StatoVolo) statoVoloComboBox.getSelectedItem();
        String direzione = arrivoRadioButton.isSelected() ? "in arrivo" : "in partenza";

        // Riutilizzo la validazione "completa" che già avevi
        String errore = valida(codiceVoloSelezionato, compagnia, data, aeroporto, orarioPrevisto, orarioStimato, gate, direzione, stato);
        if (errore != null) {
            JOptionPane.showMessageDialog(gestioneVoliPanel, errore, "Errore modifica volo", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            controller.aggiornaVolo(
                    codiceVoloSelezionato,
                    compagnia,
                    data,
                    orarioPrevisto,
                    orarioStimato,
                    stato,
                    direzione,
                    aeroporto,
                    gate
            );
            JOptionPane.showMessageDialog(gestioneVoliPanel, "Volo aggiornato con successo!");
            areaAmmGUI.aggiornaTabellaOrario(); // ricarica i voli nella tabella
            pulisci();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(gestioneVoliPanel,
                    "Aggiornamento non riuscito: " + ex.getMessage(),
                    "Modifica volo", JOptionPane.ERROR_MESSAGE);
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

        if (codice.isEmpty()) return "Codice univoco volo obbligatorio.";

        if (compagnia.isEmpty()) return "Compagnia obbligatoria.";

        if (!Pattern.matches("\\d{4}-\\d{2}-\\d{2}", data))
            return "Data deve essere nel formato yyyy-MM-dd.";

        if (!Pattern.matches("[A-Za-z]{3}", otherAirport))
            return "Altro aeroporto deve essere un codice IATA (3 lettere).";

        if (otherAirport.equalsIgnoreCase(AEROPORTO_LOCALE))
            return "Altro aeroporto deve essere diverso da " + AEROPORTO_LOCALE + ".";

        if (orarioPrevisto == null || !Pattern.matches("\\d{2}:\\d{2}", orarioPrevisto))
            return "Orario previsto (HH:mm) obbligatorio.";

        if (orarioStimato == null || !Pattern.matches("\\d{2}:\\d{2}", orarioStimato))
            return "Orario stimato (HH:mm) obbligatorio.";

        if (gate == null || gate.isEmpty())
            return "Il campo Gate è obbligatorio.";

        return null;
    }

    // Validazione “light” per la modifica (aggiorni solo stato e orario previsto)
    private String validaModifica(String nuovoOrarioPrevisto, StatoVolo nuovoStato) {
        if (nuovoStato == null) return "Seleziona uno stato del volo.";
        if (nuovoOrarioPrevisto == null || !Pattern.matches("\\d{2}:\\d{2}", nuovoOrarioPrevisto))
            return "Orario previsto deve essere nel formato HH:mm.";
        return null;
    }

    private void pulisci() {
        codiceUnivocoTextField.setText("");
        compagniaTextField.setText("");
        dataTextField.setText("");
        altroAeroportoTextField.setText("");

        if (orarioPrevistoTextField != null) orarioPrevistoTextField.setText("");
        if (orarioStimatoTextField != null) orarioStimatoTextField.setText("");
        if (gateTextField != null) gateTextField.setText("");
        if (statoVoloComboBox != null && statoVoloComboBox.getItemCount() > 0) statoVoloComboBox.setSelectedIndex(0);

        partenzaRadioButton.setSelected(true);
        toggleDirezione();

        setLockCodice(false);
        codiceVoloSelezionato = null;

        if (confermaModificheButton != null) confermaModificheButton.setEnabled(false);
    }

    private void setLockCodice(boolean lock) {
        if (codiceUnivocoTextField != null) {
            codiceUnivocoTextField.setEditable(!lock);
        }
        if (cercaCodiceButton != null) {
            cercaCodiceButton.setEnabled(!lock); // opzionale: evita di ricercare mentre modifichi
        }
    }

    private String safeText(JTextField f) {
        return f == null ? "" : f.getText().trim();
    }

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    public JPanel getPanelDatiVolo() {
        return gestioneVoliPanel;
    }
}