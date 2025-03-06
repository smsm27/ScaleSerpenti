package view.swing;

import model.tabella.TabellaModel;
import view.interfacce.elementoGrafico.ElementoGrafico;
import view.interfacce.schermata.Schermata;

import javax.swing.*;
import java.awt.*;


public class SchermataInizialeSwing {
    private static SchermataInizialeSwing instance;
    private JFrame frame;
    private JPanel panel;

    // Singleton pattern to access the menu from anywhere
    public static SchermataInizialeSwing getInstance() {
        if (instance == null) {
            instance = new SchermataInizialeSwing();
        }
        return instance;
    }

    public void inizializza() {
        initComponents();
        setupEventHandlers();
        frame.setVisible(true);
    }



    public void mostraMenu() {
        if (frame != null) {
            frame.setVisible(true);
        } else {
            inizializza();
        }
    }

    private void initComponents() {
        frame = new JFrame("Serpi e Scale - Menu Principale");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null); // Centra la finestra

        panel = new JPanel(new GridBagLayout());
        frame.add(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titolo = new JLabel("Serpi e Scale", JLabel.CENTER);
        titolo.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titolo, gbc);

        JButton creaButton = new JButton("Crea Tabella");
        JButton giocaButton = new JButton("Gioca");
        JButton esciButton = new JButton("Esci");

        panel.add(creaButton, gbc);
        panel.add(giocaButton, gbc);
        panel.add(esciButton, gbc);
    }

    private void setupEventHandlers() {
        // Trova i bottoni nel panel
        for (Component component : panel.getComponents()) {
            if (component instanceof JButton button) {
                switch (button.getText()) {
                    case "Crea Tabella" -> button.addActionListener(e -> avviaSchermataCreazione());
                    case "Gioca" -> button.addActionListener(e -> scegliModalitaGioco());
                    case "Esci" -> button.addActionListener(e -> System.exit(0));
                }
            }
        }
    }

    private void avviaSchermataCreazione() {
        frame.dispose(); // Chiude la schermata corrente

        SchermataCreazioneSwing schermataCreazione = new SchermataCreazioneSwing();
        schermataCreazione.inizializza();

        // Mostra direttamente il dialogo per creare o caricare una tabella
        boolean nuovaTabella = mostraDialogCreaOCarica();

        if (nuovaTabella) {
            schermataCreazione.dialogSetNewTabella();
        } else {
            schermataCreazione.dialogCaricamento();
        }
    }

    private void scegliModalitaGioco() {
        Object[] options = {"Modalità Manuale", "Modalità Automatica"};
        int scelta = JOptionPane.showOptionDialog(
                frame,
                "Scegli la modalità di gioco:",
                "Modalità Gioco",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        frame.dispose(); // Chiude la schermata corrente

        if (scelta == 0) {
            // Modalità Manuale
            avviaSchermataGiocoManuale();
        } else {
            // Modalità Automatica
            avviaSchermataGiocoAutomatica();
        }
    }

    private void avviaSchermataGiocoManuale() {
        // Utilizziamo la factory per creare l'istanza di gioco in modalità manuale
        SchermataGiocaFactory.createSchermataGioca(SchermataGiocaFactory.GameMode.MANUAL).inizializza();
    }

    private void avviaSchermataGiocoAutomatica() {
        // Utilizziamo la factory per creare l'istanza di gioco in modalità automatica
        SchermataGiocaFactory.createSchermataGioca(SchermataGiocaFactory.GameMode.AUTOMATIC).inizializza();
    }

    private boolean mostraDialogCreaOCarica() {
        Object[] options = {"Crea Nuova Tabella", "Carica Tabella Esistente"};
        int scelta = JOptionPane.showOptionDialog(
                null,
                "Cosa desideri fare?",
                "Modalità Creazione",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        return scelta == 0; // true per creare nuova tabella, false per caricare
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SchermataInizialeSwing schermata = new SchermataInizialeSwing();
            schermata.inizializza();
        });
    }
}