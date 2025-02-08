package view.swing;

import controller.creazione.CreazioneTabellaController;
import model.casella.Casella;
import view.interfacce.schermata.AbstractSchermataSwing;
import view.interfacce.schermata.Dialogo;
import view.interfacce.schermata.SchermataCreazione;
import view.swing.casellaView.CasellaGraficaSwing;
import view.swing.elementoSpecialeView.ElementoGraficoSwingFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;


public class SchermataCreazioneSwing extends AbstractSchermataSwing implements SchermataCreazione, Dialogo {
    private CreazioneTabellaController controller;

    @Override
    public void mostraTabellaGrafica(List<Casella> caselle) {
        pulisciVista();
        for (Casella casella : caselle) {
            CasellaGraficaSwing casellaGrafica = new CasellaGraficaSwing(casella);
            caselleGrafiche.add(casellaGrafica);
            panel.add(casellaGrafica, JLayeredPane.DEFAULT_LAYER);
        }
        refresh();
    }

    @Override
    public void rimuoviElementoGrafico(int indice) {
        //TODO
    }

    @Override
    public void inizializza() {
        initComponents();
        setupEventHandlers();
        controller = new CreazioneTabellaController(this,new ElementoGraficoSwingFactory());
    }

    /**
     * Inizializzo menu:
     * Gioca
     * addSerpente
     * addScala
     * salva
     * carica
     */
    protected void initComponents() {
        frame = new JFrame("Gestione Gioco da Tavolo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Opzioni");

        JMenuItem creaMappaItem = new JMenuItem("Crea Mappa");
        JMenuItem giocaItem = new JMenuItem("Gioca");
        JMenuItem addSerpenteItem = new JMenuItem("Add Serpente");
        JMenuItem addScalaItem = new JMenuItem("Add Scala");
        JMenuItem salvaItem = new JMenuItem("Salva Tabella");
        JMenuItem caricaItem = new JMenuItem("Carica Tabella");

        menu.add(creaMappaItem);
        menu.add(giocaItem);
        menu.add(addSerpenteItem);
        menu.add(addScalaItem);
        menu.add(salvaItem);
        menu.add(caricaItem);
        menuBar.add(menu);

        frame.setJMenuBar(menuBar);

        panel = new JLayeredPane();
        frame.add(panel);
        frame.setVisible(true);
    }

    /**
     * Associo metodo a elementi del menu
     */
    protected void setupEventHandlers() {
        JMenu menu = frame.getJMenuBar().getMenu(0); // Ottiene il menu "Opzioni"

        for (Component component : menu.getMenuComponents()) {
            if (component instanceof JMenuItem menuItem) {
                switch (menuItem.getText()) {
                    case "Crea Mappa" -> menuItem.addActionListener(e -> dialogSetNewTabella());
                    case "Add Serpente" -> menuItem.addActionListener(e -> dialogAddElementoSpeciale("serpente"));
                    case "Add Scala" -> menuItem.addActionListener(e -> dialogAddElementoSpeciale("scala"));
                    case "Salva Tabella" -> menuItem.addActionListener(e -> dialogGestisciSalvataggio());
                    case "Carica Tabella" -> menuItem.addActionListener(e -> dialogCaricamento());
                }
            }
        }
    }

    @Override
    public void dialogCaricamento() {
        File directory = new File("src/main/save");
        if (!directory.exists() || !directory.isDirectory()) {
            mostraMessaggio("Nessuna mappa salvata trovata!",
                    "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".ser"));
        if (files == null || files.length == 0) {
            mostraMessaggio("Nessuna mappa salvata trovata!",
                    "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] mapNames = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            mapNames[i] = files[i].getName().replace(".ser", "");
        }

        String scelta = (String) JOptionPane.showInputDialog(
                frame,
                "Seleziona una mappa da caricare:",
                "Carica Mappa",
                JOptionPane.QUESTION_MESSAGE,
                null,
                mapNames,
                mapNames[0]
        );

        if (scelta != null) {
            controller.caricaTabella(scelta);
        }
    }

    @Override
    public void dialogGestisciSalvataggio() {
        String nomeFile = JOptionPane.showInputDialog(frame,
                "Inserisci il nome del file per salvare la tabella:");
        if (nomeFile != null && !nomeFile.trim().isEmpty()) {
            try {
                String mes = controller.salvaTabella(nomeFile);
                mostraMessaggio(mes, "Successo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                mostraMessaggio(ex.getMessage(), "Tabella non salvata", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    @Override
    public void dialogAddElementoSpeciale(String tipo) {
        JTextField partenzaField = new JTextField();
        JTextField destinazioneField = new JTextField();

        Object[] message = {
                "Indice casella partenza:", partenzaField,
                "Indice casella destinazione:", destinazioneField
        };

        int option = JOptionPane.showConfirmDialog(frame, message,
                "Crea " + tipo, JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                int partenza = Integer.parseInt(partenzaField.getText());
                int destinazione = Integer.parseInt(destinazioneField.getText());

                if ((partenza > destinazione && tipo.equals("serpente")) ||
                        (destinazione > partenza && tipo.equals("scala"))) {
                    controller.aggiungiCasellaSpeciale(tipo, partenza, destinazione);
                } else {
                    mostraMessaggio("L'indice non corrisponde, è un/a: " + tipo,
                            "Errore", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                mostraMessaggio("Inserire valori numerici validi",
                        "Errore", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void dialogSetNewTabella() {
        // Mostra una finestra di dialogo per configurare la tabella
        JTextField numeroCaselleField = new JTextField("10");

        JTextField dimXField = new JTextField("50");
        JTextField dimYField = new JTextField("50");

        // Variabili per memorizzare i percorsi delle immagini
        final String[] sfondoTabellone = {null};
        final String[] immagineCasella = {null};

        // Pulsanti per scegliere le immagini
        JButton sfondoButton = new JButton("Scegli immagine sfondo");
        JButton casellaButton = new JButton("Scegli immagine casella");

        // Label per mostrare i percorsi selezionati
        JLabel sfondoLabel = new JLabel("Nessuna immagine selezionata");
        JLabel casellaLabel = new JLabel("Nessuna immagine selezionata");

        // Configurazione dei listener per i pulsanti
        sfondoButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "Immagini", "jpg", "jpeg", "png", "gif"));
            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                sfondoTabellone[0] = fileChooser.getSelectedFile().getPath();
                sfondoLabel.setText(fileChooser.getSelectedFile().getName());
            }
        });

        casellaButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "Immagini", "jpg", "jpeg", "png", "gif"));
            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                immagineCasella[0] = fileChooser.getSelectedFile().getPath();
                casellaLabel.setText(fileChooser.getSelectedFile().getName());
            }
        });

        // Pannelli per organizzare i componenti di selezione delle immagini
        JPanel sfondoPanel = new JPanel(new BorderLayout());
        sfondoPanel.add(sfondoButton, BorderLayout.WEST);
        sfondoPanel.add(sfondoLabel, BorderLayout.CENTER);

        JPanel casellaPanel = new JPanel(new BorderLayout());
        casellaPanel.add(casellaButton, BorderLayout.WEST);
        casellaPanel.add(casellaLabel, BorderLayout.CENTER);

        Object[] messaggio = {
                "Numero di righe:", numeroCaselleField,
                "Dimensione casella X:", dimXField,
                "Dimensione casella Y:", dimYField,
                "Sfondo Tabellone:", sfondoPanel,
                "Immagine Casella:", casellaPanel
        };

        int opzione = JOptionPane.showConfirmDialog(
                frame,
                messaggio,
                "Configura Tabella",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (opzione == JOptionPane.OK_OPTION) {
            try {
                int numCaselle = Integer.parseInt(numeroCaselleField.getText());
                int dimX = Integer.parseInt(dimXField.getText());
                int dimY = Integer.parseInt(dimYField.getText());

                controller.creaNuovaTabella(numCaselle,dimX,dimY,sfondoTabellone[0],immagineCasella[0]);

            } catch (NumberFormatException ex) {
                mostraMessaggio("Inserire valori validi!", "Errore", JOptionPane.ERROR_MESSAGE);
            }
        }

    }
}
