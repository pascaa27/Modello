package gui;

import controller.Controller;
import model.Prenotazione;
import model.StatoPrenotazione;
import model.UtenteGenerico;
import model.Volo;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

public class EffettuaPrenotazioneGUI {
    private JPanel effettuaPrenotazionePanel;
    private JTextField nomeTextField;
    private JTextField cognomeTextField;
    private JTextField codiceFiscaleTextField;
    private JTextField emailTextField;
    private JComboBox<Integer> annoInizioComboBox;
    private JComboBox<Integer> meseInizioComboBox;
    private JComboBox<Integer> giornoInizioComboBox;
    private JComboBox<Integer> annoFineComboBox;
    private JComboBox<Integer> meseFineComboBox;
    private JComboBox<Integer> giornoFineComboBox;
    private JTextField aeroportoDestinazioneTextField;
    private JButton prenotaButton;
    private JButton annullaButton;
    private Controller controller;
    private UtenteGenerico utente;

    public EffettuaPrenotazioneGUI(Controller controller, UtenteGenerico utente) {
        this.controller = controller;
        this.utente = utente;

        // Pulsanti: listener immediati (non toccano le combo)
        prenotaButton.addActionListener(e -> effettuaPrenotazione());
        annullaButton.addActionListener(e -> {
            JFrame f = (JFrame) SwingUtilities.getWindowAncestor(effettuaPrenotazionePanel);
            if (f != null) f.dispose();
        });

        // Popola e aggiusta le combo *dopo* l'inizializzazione della UI generata dal .form
        SwingUtilities.invokeLater(() -> {
            // debug: verifica binding
            if (annoInizioComboBox == null || meseInizioComboBox == null || giornoInizioComboBox == null
                    || annoFineComboBox == null || meseFineComboBox == null || giornoFineComboBox == null) {
                System.err.println("EFF: uno o più componenti JComboBox NON sono stati collegati dal .form. Controlla i nomi di binding.");
                // esci: non provare a chiamare addItem su oggetti null
                return;
            }

            // anni (se vuoti)
            if (annoInizioComboBox.getItemCount() == 0) {
                for (int anno = 2025; anno <= 2035; anno++) {
                    annoInizioComboBox.addItem(anno);
                    annoFineComboBox.addItem(anno);
                }
            }

            // mesi (se vuoti)
            if (meseInizioComboBox.getItemCount() == 0) {
                for (int m = 1; m <= 12; m++) {
                    meseInizioComboBox.addItem(m);
                    meseFineComboBox.addItem(m);
                }
            }

            // listeners per aggiornare i giorni quando cambia mese/anno
            ActionListener aggiornaGiorniInizio = e -> aggiornaGiorni(annoInizioComboBox, meseInizioComboBox, giornoInizioComboBox);
            ActionListener aggiornaGiorniFine  = e -> aggiornaGiorni(annoFineComboBox, meseFineComboBox, giornoFineComboBox);

            annoInizioComboBox.addActionListener(aggiornaGiorniInizio);
            meseInizioComboBox.addActionListener(aggiornaGiorniInizio);
            annoFineComboBox.addActionListener(aggiornaGiorniFine);
            meseFineComboBox.addActionListener(aggiornaGiorniFine);

            // Inizializza i giorni in base alla selezione corrente
            aggiornaGiorni(annoInizioComboBox, meseInizioComboBox, giornoInizioComboBox);
            aggiornaGiorni(annoFineComboBox, meseFineComboBox, giornoFineComboBox);
        });
    }

    private void aggiornaGiorni(JComboBox<Integer> annoCombo, JComboBox<Integer> meseCombo, JComboBox<Integer> giornoCombo) {
        if (annoCombo == null || meseCombo == null || giornoCombo == null) return;
        Object oAnno = annoCombo.getSelectedItem();
        Object oMese = meseCombo.getSelectedItem();
        if (oAnno == null || oMese == null) return;

        int anno = (int) oAnno;
        int mese = (int) oMese;
        int giorniNelMese = YearMonth.of(anno, mese).lengthOfMonth();

        // conserva selezione precedente se possibile
        Integer precedente = (Integer) giornoCombo.getSelectedItem();

        giornoCombo.removeAllItems();
        for (int g = 1; g <= giorniNelMese; g++) {
            giornoCombo.addItem(g);
        }
        if (precedente != null && precedente <= giorniNelMese) {
            giornoCombo.setSelectedItem(precedente);
        } else {
            giornoCombo.setSelectedIndex(0); // primo giorno
        }
    }

    private void effettuaPrenotazione() {
        String nome = nomeTextField.getText().trim();
        String cognome = cognomeTextField.getText().trim();
        String codiceFiscale = codiceFiscaleTextField.getText().trim();
        String email = emailTextField.getText().trim();
        String destinazione = aeroportoDestinazioneTextField.getText().trim().toUpperCase();

        // Validazione base
        if (nome.isEmpty() || cognome.isEmpty() || codiceFiscale.isEmpty() || destinazione.isEmpty()) {
            JOptionPane.showMessageDialog(effettuaPrenotazionePanel,
                    "Tutti i campi devono essere compilati.", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!Character.isUpperCase(nome.charAt(0)) || !Character.isUpperCase(cognome.charAt(0))) {
            JOptionPane.showMessageDialog(effettuaPrenotazionePanel,
                    "Nome e Cognome devono iniziare con lettera maiuscola.", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate dataInizio = LocalDate.of(
                (int) annoInizioComboBox.getSelectedItem(),
                (int) meseInizioComboBox.getSelectedItem(),
                (int) giornoInizioComboBox.getSelectedItem()
        );
        LocalDate dataFine = LocalDate.of(
                (int) annoFineComboBox.getSelectedItem(),
                (int) meseFineComboBox.getSelectedItem(),
                (int) giornoFineComboBox.getSelectedItem()
        );

        if (dataFine.isBefore(dataInizio)) {
            JOptionPane.showMessageDialog(effettuaPrenotazionePanel,
                    "La data di fine deve essere successiva o uguale alla data di inizio.", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Cerca il volo corretto per destinazione e data
            Volo volo = controller.cercaVoloPerDestinazioneEData(destinazione, dataInizio.toString());
            if (volo == null) {
                JOptionPane.showMessageDialog(effettuaPrenotazionePanel,
                        "Nessun volo trovato per questa destinazione e data!", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Controllo duplicati per lo stesso utente sullo stesso volo
            if (controller.utenteHaPrenotazionePerVolo(utente.getLogin(), volo.getCodiceUnivoco())) {
                JOptionPane.showMessageDialog(effettuaPrenotazionePanel,
                        "Hai già una prenotazione per questo volo!", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }


            String numeroBiglietto = UUID.randomUUID().toString().substring(0, 8);

            Prenotazione pren = controller.aggiungiPrenotazione(
                    numeroBiglietto,
                    "",
                    StatoPrenotazione.CONFERMATA,
                    volo.getCodiceUnivoco(), // passa il volo giusto
                    utente,
                    nome,
                    cognome,
                    codiceFiscale,
                    email,
                    null
            );

            if (pren == null) {
                JOptionPane.showMessageDialog(effettuaPrenotazionePanel,
                        "Creazione prenotazione fallita.", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(effettuaPrenotazionePanel,
                    "Prenotazione effettuata con successo!\n" +
                            "Codice prenotazione: " + pren.getNumBiglietto() + "\n" +
                            "Dal " + dataInizio + " al " + dataFine,
                    "Successo",
                    JOptionPane.INFORMATION_MESSAGE);

            if (utente != null) {
                utente.aggiungiCodicePrenotazione(pren.getNumBiglietto());
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(effettuaPrenotazionePanel,
                    "Errore durante la prenotazione: " + ex.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }


    public JPanel getPanel() {
        return effettuaPrenotazionePanel;
    }
}