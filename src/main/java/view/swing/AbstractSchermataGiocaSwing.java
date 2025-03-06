package view.swing;

import model.casella.Posizione;
import model.giocatore.Giocatore;
import model.gioco.giocoManager.AbstractGiocoModel;
import model.gioco.giocoManager.GiocoDoppioDado;
import model.gioco.mediator.Mediator;
import model.gioco.mediator.MediatorImpl;
import tools.Colori;
import view.astrazioni.schermata.AbstractSchermataSwing;
import view.interfacce.schermata.SchermataGioco;
import view.swing.GiocatoreSwing.GiocatoreGraficaSwing;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractSchermataGiocaSwing extends AbstractSchermataSwing implements SchermataGioco {
    protected GameInfoSwing gameInfoPanel;
    protected Colori colori;
    protected Mediator mediator = new MediatorImpl();
    protected Map<Color, GiocatoreGraficaSwing> giocatoriGrafici = new HashMap<>();
    protected GiocatoreGraficaSwing giocatoreCurr;


    @Override
    public List<Giocatore> getInfoPlayers() {
        // Chiedi il numero di giocatori
        JTextField nGiocatoriField = new JTextField("4");
        Object[] messaggio = {"Numero di giocatori", nGiocatoriField};
        int opzione = JOptionPane.showConfirmDialog(frame, messaggio, "Configura Giocatori", JOptionPane.OK_CANCEL_OPTION);

        if (opzione != JOptionPane.OK_OPTION) {
            return List.of(); // Se annulla il numero di giocatori, restituisci lista vuota
        }

        try {
            int numeroGiocatori = Integer.parseInt(nGiocatoriField.getText());
            if (numeroGiocatori < 1 || numeroGiocatori > 4) {
                JOptionPane.showMessageDialog(frame, "Numero di giocatori non valido, deve essere tra 1 e 4", "Errore", JOptionPane.ERROR_MESSAGE);
                return List.of();
            }

            List<Giocatore> giocatori = new ArrayList<>();
            while (giocatori.size() < numeroGiocatori) {
                Giocatore giocatore = getInfoPlayer();
                if (giocatore != null) {
                    giocatori.add(giocatore); // Aggiungi solo se valido
                } else {
                    // L'utente ha annullato l'inserimento di un giocatore
                    if (giocatori.size() > 0) {
                        // Se ci sono già giocatori, chiedi se proseguire
                        int choice = JOptionPane.showConfirmDialog(frame,
                                "Vuoi proseguire con " + giocatori.size() + " giocatori?",
                                "Conferma",
                                JOptionPane.YES_NO_OPTION);
                        if (choice == JOptionPane.YES_OPTION) {
                            break; // Procedi con i giocatori attuali
                        }
                        // Se dice "No", continua a chiedere per il prossimo giocatore
                    } else {
                        // Nessun giocatore ancora aggiunto
                        JOptionPane.showMessageDialog(frame, "Devi inserire almeno un giocatore.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                        // Continua il ciclo per chiedere di nuovo
                    }
                }
            }
            return giocatori;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Inserire un numero valido!", "Errore", JOptionPane.ERROR_MESSAGE);
            return List.of();
        }
    }

    private Giocatore getInfoPlayer() {

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 2, 5, 5));

        JTextField nomeField = new JTextField(10);
        inputPanel.add(new JLabel("Nome: "));
        inputPanel.add(nomeField);

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
    public void mostraGiocatori(List<Giocatore> giocatori) {
        for (Giocatore g : giocatori) {
            GiocatoreGraficaSwing giocatore = new GiocatoreGraficaSwing(g);
            giocatore.setBounds((int)g.getPosizione().getX(),
                    (int)g.getPosizione().getY(),
                    30, 30);
            giocatoriGrafici.put(g.getColor(), giocatore);
            panel.add(giocatore, JLayeredPane.MODAL_LAYER);
        }
    }

    @Override
    public void spostaGiocatore(Posizione posizione) {
        giocatoreCurr.spostaA(posizione);
        panel.repaint();
    }

    @Override
    public void setGiocatoreCorrente(Giocatore giocatore) {
        giocatoreCurr = giocatoriGrafici.get(giocatore.getColor());
        handleGiocatoreCambio();
        refresh();
    }

    protected abstract void handleGiocatoreCambio();

    @Override
    public abstract void mostraRisultatoDado(int risultato);

    @Override
    public abstract void animaMossa(List<Posizione> posizioniIntermedie);


    @Override
    public abstract void mostraVincitore();



    @Override
    public void inizializza() {
        initComponents();

        String nomeMappa = getNomeTabella();


        if ( nomeMappa==null || nomeMappa.isEmpty()) {
            gestisciErrore();
            return;
        }

        List<Giocatore> giocatori = getInfoPlayers();

        if( giocatori.isEmpty()) {
            gestisciErrore();
            return;
        }

        AbstractGiocoModel giocoModel = new GiocoDoppioDado(nomeMappa, giocatori);

        mediator.registerGameManager(giocoModel);
        mediator.registerView(this);

        gameInfoPanel.setGiocoModel(giocoModel);
        giocoModel.addListener(gameInfoPanel);

        // Hook for mode-specific initialization
        initModeSpecific(giocoModel);

        mediator.start();
    }

    protected void initComponents() {
        frame = new JFrame("Serpi e Scale");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        colori = new Colori();
        JMenuBar menuBar = new JMenuBar();
        aggiungiTornaMenu(menuBar);
        frame.setJMenuBar(menuBar);

        // Create main panel
        panel = new JLayeredPane();
        frame.add(panel, BorderLayout.CENTER);

        // Create panels
        JPanel buttonPanel = createButtonPanel();
        gameInfoPanel = new GameInfoSwing();
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.add(gameInfoPanel, BorderLayout.NORTH);

        frame.add(panel);
        frame.setVisible(true);
    }

    protected abstract JPanel createButtonPanel();

    protected abstract void initModeSpecific(AbstractGiocoModel giocoModel);

}
