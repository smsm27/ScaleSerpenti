package view.swing;


import lombok.extern.log4j.Log4j2;
import model.casella.Posizione;
import model.giocatore.Giocatore;
import model.gioco.GiocoModel;
import model.gioco.mediator.MediatorImpl;
import tools.Colori;
import view.interfacce.schermata.AbstractSchermataSwing;
import view.interfacce.schermata.DialogoGioco;
import view.interfacce.schermata.SchermataGioco;
import view.swing.GiocatoreSwing.GiocatoreGraficaSwing;


import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.io.File;

import java.util.*;
import java.util.List;

@Log4j2

public class SchermataGiocaSwing extends AbstractSchermataSwing implements SchermataGioco, DialogoGioco {
    private JButton lanciaDadoButton;
    private GameInfoSwing gameInfoPanel;
    private Colori colori;
    private MediatorImpl mediator=new MediatorImpl();
    private Map<Color, GiocatoreGraficaSwing> giocatoriGrafici = new HashMap<>();
    private GiocatoreGraficaSwing giocatoreCurr;


    public void mostraGiocatori(List<Giocatore> giocatori){
        for (Giocatore g : giocatori) {
            GiocatoreGraficaSwing giocatore = new GiocatoreGraficaSwing(g);
            giocatore.setBounds((int)g.getPosizione().getX(),
                    (int)g.getPosizione().getY(),
                    30, 30); // Imposta dimensioni e posizione
            giocatoriGrafici.put(g.getColor(),giocatore);
            log.info(giocatoriGrafici.toString());
            panel.add(giocatore, JLayeredPane.MODAL_LAYER );
        }
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.LIGHT_GRAY); // Per renderlo visibile durante il debug

        // Crea il bottone per lanciare il dado
        lanciaDadoButton = new JButton("Lancia Dado");
        lanciaDadoButton.addActionListener(e -> {

            if (mediator != null && giocatoreCurr != null) {
                mediator.notifyDiceRoll();


            }
        });

        // Aggiungi il bottone al pannello dei bottoni
        buttonPanel.add(lanciaDadoButton);

        // Assicurati che tutto sia visibile
        lanciaDadoButton.setVisible(true);
        buttonPanel.setVisible(true);

        // Debug
        System.out.println("Button visible: " + lanciaDadoButton.isVisible());
        System.out.println("Button panel visible: " + buttonPanel.isVisible());

        return buttonPanel;
    }

    @Override
    public void mostraRisultatoDado(int risultato) {
        int ris =JOptionPane.showOptionDialog(panel,
                "Giocatore: " + this.giocatoreCurr.getGiocatore().getNome()  +
                        "Hai fatto: " + risultato,
                "Lancio",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new Object[]{"OK"},
                "OK");
        if(ris == JOptionPane.OK_OPTION) {

            log.info("Sto per muovermi");
            mediator.notifyPlayerMove();
        }

    }

    @Override
    public void spostaGiocatore( Posizione posizione) {
        giocatoreCurr.spostaA(posizione);
        panel.repaint();
    }

    @Override
    public void setGiocatoreCorrente(Giocatore giocatore) {
        giocatoreCurr = giocatoriGrafici.get(giocatore.getColor());
        log.info("ecco giocatorecurr: {}", giocatoreCurr.getName());
        lanciaDadoButton.setEnabled(true);
        refresh();
    }

    @Override
    public void animaMossa( List<Posizione> posizioni) {
        lanciaDadoButton.setEnabled(false);
        Timer timer = new Timer(500, null);
        final int[] currentStep = {0};

        timer.addActionListener(e -> {
            if (currentStep[0] < posizioni.size()) {
                spostaGiocatore( posizioni.get(currentStep[0]));
                currentStep[0]++;
            } else {
                timer.stop();
                log.info("mi sto fermando");
                mediator.notifyPlayerStop();
            }
        });
        timer.start();

    }


    @Override
    public void mostraVincitore() {
        int option = JOptionPane.showOptionDialog(panel,
                "Hai vinto: " + giocatoreCurr.getGiocatore().getNome(),
                "Lancio",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new Object[]{"OK"},
                "OK");
        //notifica su ok
        if (option == 0) {

        }
    }

    @Override
    public void inizializza() {

        initComponents();
        colori = new Colori();
        String nomeMappa=getNomeMappa();
        List<Giocatore> giocatori=getInfoPlayers();

        GiocoModel giocoModel= new GiocoModel(nomeMappa,giocatori);



        mediator.registerGameManager(giocoModel);
        mediator.registerView(this);

        gameInfoPanel.setGiocoModel(giocoModel);
        //Inizializzo il Listener
        giocoModel.addListener(gameInfoPanel);


        mediator.start();


    }

    private void initComponents(){

        frame = new JFrame("Serpi e Scale");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);

        JMenuBar menuBar = new JMenuBar();
        aggiungiMenuTornaMenu(menuBar);
        frame.setJMenuBar(menuBar);

        // Crea il pannello principale
        panel = new JLayeredPane();
        frame.add(panel, BorderLayout.CENTER);

        //Creazione Pannelli
        JPanel buttonPanel = createButtonPanel();
        gameInfoPanel = new GameInfoSwing();
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.add(gameInfoPanel, BorderLayout.NORTH);


        frame.add(panel);
        frame.setVisible(true);
    }

    @Override
    public List<Giocatore> getInfoPlayers() {
        JTextField nGiocatoriField = new JTextField("4");
        Object[] messaggio = {
                "Numero di giocatori", nGiocatoriField
        };
        int opzione = JOptionPane.showConfirmDialog(
                frame,
                messaggio,
                "configura Giocatori",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (opzione == JOptionPane.OK_OPTION) {
            try {
                int numeroGiocatori = Integer.parseInt(nGiocatoriField.getText());
                if (numeroGiocatori < 0 || numeroGiocatori > 4) {
                    JOptionPane.showMessageDialog(panel, "Numero di Giocatori non valido, Max 4");
                } else {
                    List<Giocatore> giocatori = new ArrayList<Giocatore>();
                    for (int i = 0; i < numeroGiocatori; i++) {
                        Giocatore giocatore = getInfoPlayer();
                        giocatori.add(giocatore);
                    }


                    return giocatori;

                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Inserire valori validi!", "Errore", JOptionPane.ERROR_MESSAGE);
            }
        }

        return List.of();
    }

    private Giocatore getInfoPlayer() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 2, 5, 5));

        // Campo nome
        JTextField nomeField = new JTextField(10);
        inputPanel.add(new JLabel("Nome: "));
        inputPanel.add(nomeField);

        // Menu a tendina per i colori
        String[] nomiColori = new String[colori.getColori().size()];
        int i = 0;
        for (String s : colori.getColori().keySet()) {
            nomiColori[i] = s;
            i++;
        }
        JComboBox<String> coloreCombo = new JComboBox<>(nomiColori);
        inputPanel.add(new JLabel("Colore: "));
        inputPanel.add(coloreCombo);

        int result = JOptionPane.showConfirmDialog(frame,
                inputPanel,
                "Inserisci i dati del giocatore",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String nome = nomeField.getText();
            String coloreSelezionato = (String) coloreCombo.getSelectedItem();
            Giocatore g = new Giocatore(nome, colori.getColori().get(coloreSelezionato));

            colori.getColori().remove(coloreSelezionato);
            return g;
        }

        return null;
    }

    @Override
    public String getNomeMappa() {
        File directory = new File("src/main/save");
        if (!directory.exists() || !directory.isDirectory()) {
            JOptionPane.showMessageDialog(frame, "Nessuna mappa salvata trovata!",
                    "Errore", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".ser"));
        if (files == null || files.length == 0) {
            JOptionPane.showMessageDialog(frame, "Nessuna mappa salvata trovata!",
                    "Errore", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        String[] mapNames = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            mapNames[i] = files[i].getName().replace(".ser", "");
        }

        return (String) JOptionPane.showInputDialog(
                frame,
                "Seleziona una mappa da caricare:",
                "Carica Mappa",
                JOptionPane.QUESTION_MESSAGE,
                null,
                mapNames,
                mapNames[0]
        );
    }


}
