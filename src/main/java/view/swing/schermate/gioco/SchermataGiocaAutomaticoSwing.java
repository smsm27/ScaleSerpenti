package view.swing.schermate.gioco;

import lombok.extern.log4j.Log4j2;
import model.casella.Posizione;
import model.gioco.AbstractGiocoModel;
import view.swing.schermate.astrazioni.AbstractSchermataGiocaSwing;

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
        if(!startButton.isEnabled()){
        String message = "Turno Giocatore: " + this.giocatoreCurr.getGiocatore().getNome();

        // Creo un JDialog personalizzato senza bottoni
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(panel), "Cambio turno", false);
        JLabel label = new JLabel(message, JLabel.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        dialog.add(label);
        dialog.pack();
        dialog.setLocationRelativeTo(panel);

        Timer timer = new Timer(2000, e -> {
            dialog.dispose();
            refresh();
            mediator.notifyDiceRoll(); // Chiama il mediator dopo la chiusura del dialogo
        });
        timer.setRepeats(false);
        timer.start();

        dialog.setVisible(true);}
    }

    @Override
    public void mostraRisultatoDado(int risultato) {
        log.info("Giocatore: {} ha fatto: {}", giocatoreCurr.getGiocatore().getNome(), risultato);

        // Creo il messaggio
        String message = "Giocatore: " + this.giocatoreCurr.getGiocatore().getNome() + "\n" +
                "Hai fatto: " + risultato;

        // Mostro il dialogo in modo non bloccante
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(panel), "Risultato dado", false);
        JLabel label = new JLabel("<html>" + message.replace("\n", "<br>") + "</html>", JLabel.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        dialog.add(label);
        dialog.pack();
        dialog.setLocationRelativeTo(panel);

        // Timer per chiudere automaticamente il dialogo
        Timer timer = new Timer(3000, e -> {
            dialog.dispose();
            refresh();
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
            startButton.setEnabled(false);
            handleGiocatoreCambio();
            pauseButton.setEnabled(true);
        });

        // Create pause button
        pauseButton = new JButton("Pausa");
        pauseButton.setEnabled(false);
        pauseButton.addActionListener(e -> {
            if (isPaused) {
                isPaused = false;
                pauseButton.setText("Pausa");
            }
            pauseButton.setEnabled(false);
            startButton.setEnabled(true);
        });

        buttonPanel.add(startButton);
        buttonPanel.add(pauseButton);

        return buttonPanel;
    }

    @Override
    protected void initModeSpecific(AbstractGiocoModel giocoModel) {}
}
