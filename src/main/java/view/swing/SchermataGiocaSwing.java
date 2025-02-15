package view.swing;

import model.casella.Posizione;
import model.giocatore.Giocatore;
import model.gioco.mediator.MediatorImpl;
import tools.Colori;
import view.interfacce.schermata.AbstractSchermataSwing;
import view.interfacce.schermata.DialogoGioco;
import view.interfacce.schermata.SchermataGioco;
import view.swing.GiocatoreSwing.GiocatoreGraficaSwing;


import javax.swing.*;
import java.awt.*;
import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchermataGiocaSwing extends AbstractSchermataSwing implements SchermataGioco, DialogoGioco {
    private JButton lanciaDadoButton;
    private final Colori colori;
    private MediatorImpl mediator=new MediatorImpl();
    private Map<Color, GiocatoreGraficaSwing> giocatoriGrafici = new HashMap<>();
    private GiocatoreGraficaSwing giocatoreCurr;


    public SchermataGiocaSwing(Colori colori) {
        this.colori = colori;
    }

    @Override
    public void mostraRisultatoDado(int risultato) {
        int ris =JOptionPane.showOptionDialog(panel,
                "Giocatore: " + giocatoreCurr +
                        "Hai fatto: " + risultato,
                "Lancio",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new Object[]{"OK"},
                "OK");
        if(risultato == JOptionPane.OK_OPTION) {
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
        lanciaDadoButton.setEnabled(true);
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
                mediator.notifyPlayerStop();
            }
        });
        timer.start();
        mediator.notifyPlayerStop();
    }


    @Override
    public void mostraVincitore() {
        int option = JOptionPane.showOptionDialog(panel,
                "Hai vinto: " + giocatoreCurr.getName(),
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
        String nomeMappa=getNomeMappa();
        if (nomeMappa != null) {
            mediator.create( nomeMappa);
        }

    }

    private void initComponents(){
        frame = new JFrame("Serpi e Scale");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        // Crea il pannello principale
        panel = new JLayeredPane();

        // Crea il bottone per lanciare il dado
        lanciaDadoButton = new JButton("Lancia Dado");
        lanciaDadoButton.setBounds(650, 700, 120, 40);
        lanciaDadoButton.addActionListener(e -> {
            if (mediator != null && giocatoreCurr != null) {
                mediator.notifyDiceRoll();
            }
        });

        // Aggiungi il bottone al pannello
        panel.add(lanciaDadoButton);
        frame.add(panel);
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
            Giocatore g = new Giocatore(nome, caselleGrafiche.getFirst().getCasella().getPosizione(), colori.getColori().get(coloreSelezionato));
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

        String scelta = (String) JOptionPane.showInputDialog(
                frame,
                "Seleziona una mappa da caricare:",
                "Carica Mappa",
                JOptionPane.QUESTION_MESSAGE,
                null,
                mapNames,
                mapNames[0]
        );
        return scelta;
    }


}
