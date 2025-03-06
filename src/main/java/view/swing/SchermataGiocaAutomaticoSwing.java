package view.swing;

import lombok.extern.log4j.Log4j2;
import model.casella.Posizione;
import model.gioco.giocoManager.AbstractGiocoModel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

@Log4j2
public class SchermataGiocaAutomaticoSwing extends AbstractSchermataGiocaSwing {

    private JButton startButton;
    private JButton pauseButton;

    private boolean isPaused = false;



    @Override
    protected void handleGiocatoreCambio() {
        String message = "Turno Giocatore: " + this.giocatoreCurr.getGiocatore().getNome();

        JOptionPane pane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = pane.createDialog(panel, "Cambio turno");
        dialog.setModal(false);

        Timer timer = new Timer(2000, e -> {
            dialog.dispose();
            mediator.notifyDiceRoll(); // Chiama il mediator dopo la chiusura del dialogo
        });
        timer.setRepeats(false);
        timer.start();

        dialog.setVisible(true);
    }

    @Override
    public void mostraRisultatoDado(int risultato) {
        log.info("Giocatore: {} ha fatto: {}", giocatoreCurr.getGiocatore().getNome(), risultato);

        // Creo il messaggio
        String message = "Giocatore: " + this.giocatoreCurr.getGiocatore().getNome() + "\n" +
                "Hai fatto: " + risultato;

        // Mostro il dialogo in modo non bloccante
        JOptionPane pane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = pane.createDialog(panel, "Risultato dado");
        dialog.setModal(false); // Non bloccante

        // Timer per chiudere automaticamente il dialogo
        Timer timer = new Timer(3000, e -> {
            dialog.dispose();
            mediator.notifyPlayerMove(); // Chiama il mediator dopo che il dialogo si chiude
        });
        timer.setRepeats(false);
        timer.start();

        dialog.setVisible(true);
    }

    @Override
    public void animaMossa(List<Posizione> posizioniIntermedie) {
        Timer timer = new Timer(500, null);
        final int[] currentStep = {0};

        timer.addActionListener(e -> {
            if (currentStep[0] < posizioniIntermedie.size()) {
                spostaGiocatore(posizioniIntermedie.get(currentStep[0]));
                currentStep[0]++;
            } else {
                timer.stop();
                log.info("mi sto fermando");
                mediator.verificaStatoGiocatore();
            }
        });
        timer.start();
    }

    @Override
    public void mostraVincitore() {


        isPaused = true;
        pauseButton.setEnabled(false);

        // Show winner
        JOptionPane.showOptionDialog(panel,
                "Vittoria! Giocatore: " + giocatoreCurr.getGiocatore().getNome(),
                "Fine Partita",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new Object[]{"OK"},
                "OK");
    }

    @Override
    protected JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.LIGHT_GRAY);

        // Create start button
        startButton = new JButton("Avvia Partita");
        startButton.addActionListener(e -> {
            isPaused = false;
            handleGiocatoreCambio();
            startButton.setEnabled(false);
            pauseButton.setEnabled(true);
        });

        // Create pause button
        pauseButton = new JButton("Pausa");
        pauseButton.setEnabled(false);
        pauseButton.addActionListener(e -> {
            if (isPaused) {
                isPaused = false;
                pauseButton.setText("Pausa");
            } else {
                isPaused = true;
                pauseButton.setText("Riprendi");
            }
        });

        buttonPanel.add(startButton);
        buttonPanel.add(pauseButton);

        return buttonPanel;
    }

    @Override
    protected void initModeSpecific(AbstractGiocoModel giocoModel) {
        if (mediator != null && giocatoreCurr != null && !isPaused) {
                mediator.notifyDiceRoll();
        }

    }
}
