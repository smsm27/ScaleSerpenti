package view.swing;



import lombok.extern.log4j.Log4j2;
import model.giocatore.Giocatore;
import model.gioco.giocoManager.AbstractGiocoModel;
import model.gioco.giocoManager.GiocoBaseModel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class GameInfoSwing extends JPanel implements GiocoBaseModel.GiocoListener {
    private JTextArea logArea;
    private JScrollPane scrollPane;
    private JLabel currentPlayerLabel;
    private JLabel playerPositionLabel;
    private JPanel allPlayersPanel;
    private List<JLabel> playerLabels;
    private AbstractGiocoModel giocoModel;

    public GameInfoSwing() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Informazioni"));
        setBackground(Color.WHITE);

        // Inizializza la lista di label per i giocatori
        playerLabels = new ArrayList<>();

        // Crea il pannello superiore per le info del giocatore corrente
        JPanel currentPlayerPanel = new JPanel();
        currentPlayerPanel.setLayout(new BoxLayout(currentPlayerPanel, BoxLayout.Y_AXIS));
        currentPlayerPanel.setBorder(BorderFactory.createTitledBorder("Giocatore Corrente"));
        currentPlayerPanel.setBackground(Color.WHITE);

        currentPlayerLabel = new JLabel("Giocatore: -");
        playerPositionLabel = new JLabel("Posizione: -");

        currentPlayerPanel.add(currentPlayerLabel);
        currentPlayerPanel.add(playerPositionLabel);

        // Crea il pannello per tutti i giocatori
        allPlayersPanel = new JPanel();
        allPlayersPanel.setLayout(new BoxLayout(allPlayersPanel, BoxLayout.Y_AXIS));
        allPlayersPanel.setBorder(BorderFactory.createTitledBorder("Tutti i Giocatori"));
        allPlayersPanel.setBackground(Color.WHITE);

        // Pannello contenitore per le informazioni
        JPanel infoContainer = new JPanel();
        infoContainer.setLayout(new BoxLayout(infoContainer, BoxLayout.Y_AXIS));
        infoContainer.add(currentPlayerPanel);
        infoContainer.add(allPlayersPanel);
        infoContainer.setBackground(Color.WHITE);

        // Aggiunge uno ScrollPane per le informazioni dei giocatori
        JScrollPane infoScrollPane = new JScrollPane(infoContainer);
        infoScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        infoScrollPane.setPreferredSize(new Dimension(250, 150));

        // Crea l'area di log
        logArea = new JTextArea(15, 20);
        logArea.setEditable(false);
        logArea.setFont(new Font("SansSerif", Font.PLAIN, 12)); // Imposta un font leggibile

        scrollPane = new JScrollPane(logArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Aggiungi i componenti al pannello
        add(infoScrollPane, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setPreferredSize(new Dimension(300, 300));

        log.info("GameInfoSwing inizializzato");
    }

    /**
     * Configura il pannello con il modello di gioco
     * @param giocoModel Il modello di gioco
     */
    public void setGiocoModel(AbstractGiocoModel giocoModel) {
        this.giocoModel = giocoModel;

        // Aggiorna i label dei giocatori
        updatePlayersPanel();

        // Imposta le informazioni del giocatore corrente
        setCurrentPlayerInfo(giocoModel.getGiocatoreCorrente());

        log.info("GameInfoSwing configurato con il modello di gioco");
    }

    /**
     * Aggiorna il pannello con le informazioni di tutti i giocatori
     */
    private void updatePlayersPanel() {
        allPlayersPanel.removeAll();
        playerLabels.clear();

        if (giocoModel != null && giocoModel.getGiocatori() != null) {
            for (Giocatore giocatore : giocoModel.getGiocatori()) {
                JLabel playerLabel = new JLabel(giocatore.getNome() + " - Posizione: " + giocatore.getIndiceCurr());
                playerLabels.add(playerLabel);
                allPlayersPanel.add(playerLabel);
            }
        }

        allPlayersPanel.revalidate();
        allPlayersPanel.repaint();

        log.debug("Pannello giocatori aggiornato");
    }

    /**
     * Aggiorna le informazioni del giocatore corrente
     * @param giocatore Il giocatore corrente
     */
    public void setCurrentPlayerInfo(Giocatore giocatore) {
        if (giocatore != null) {
            currentPlayerLabel.setText("Giocatore: " + giocatore.getNome());
            playerPositionLabel.setText("Posizione: " + giocatore.getIndiceCurr());

            // Aggiorna anche il pannello di tutti i giocatori
            updateAllPlayersInfo();

            log.info("Informazioni giocatore corrente aggiornate: " + giocatore.getNome());
        }
    }

    /**
     * Aggiorna le informazioni di tutti i giocatori
     */
    public void updateAllPlayersInfo() {
        if (giocoModel != null && giocoModel.getGiocatori() != null) {
            List<Giocatore> giocatori = giocoModel.getGiocatori();
            for (int i = 0; i < giocatori.size() && i < playerLabels.size(); i++) {
                Giocatore giocatore = giocatori.get(i);
                JLabel label = playerLabels.get(i);

                // Evidenzia il giocatore corrente in grassetto
                if (giocatore == giocoModel.getGiocatoreCorrente()) {
                    label.setText("<html><b>" + giocatore.getNome() + " - Posizione: " + giocatore.getIndiceCurr() + "</b></html>");
                } else {
                    label.setText(giocatore.getNome() + " - Posizione: " + giocatore.getIndiceCurr());
                }
            }

            log.debug("Informazioni di tutti i giocatori aggiornate");
        }
    }

    /**
     * Aggiunge un messaggio all'area di log
     * @param message Il messaggio da aggiungere
     */
    public void appendToLog(String message) {
        logArea.append(message + "\n");

        // Scorri automaticamente in fondo
        logArea.setCaretPosition(logArea.getDocument().getLength());

        log.debug("Messaggio aggiunto al log: " + message);
    }

    /**
     * Pulisce l'area di log
     */
    public void clearLog() {
        logArea.setText("");
        log.debug("Log cancellato");
    }

    /**
     * Metodo per aggiornare la UI quando il modello cambia
     */
    private void updateUI(GiocoBaseModel.StatoTurno statoTurno) {
        // Aggiorna le informazioni del giocatore corrente
        setCurrentPlayerInfo(giocoModel.getGiocatoreCorrente());

        // Aggiorna le informazioni di tutti i giocatori
        updateAllPlayersInfo();

        // Aggiunge un messaggio al log a seconda dello stato del turno
        switch (statoTurno) {
            case IN_ATTESA_LANCIO:
                appendToLog("Turno di " + giocoModel.getGiocatoreCorrente().getNome() + " - in attesa di lancio");
                break;
            case MOVIMENTO:
                appendToLog(giocoModel.getGiocatoreCorrente().getNome() + " ha fatto:" + giocoModel.getRisultato());
                break;
            case AZIONE_CASELLA:
                appendToLog("Esecuzione azione casella...");
                break;
            case FINE_TURNO:
                appendToLog("Fine turno di " + giocoModel.getGiocatoreCorrente().getNome());
                break;
            case GIOCO_FINITO:
                appendToLog("GIOCO FINITO! Vincitore: " + giocoModel.getGiocatoreCorrente().getNome());
                break;
        }

        log.info("GameInfoSwing aggiornato - Stato: " + statoTurno);
    }

    @Override
    public void onGiocoUpdated(GiocoBaseModel.StatoTurno statoTurno) {
        updateUI(statoTurno);
    }
}
