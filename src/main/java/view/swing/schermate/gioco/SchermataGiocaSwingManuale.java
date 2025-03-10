package view.swing.schermate.gioco;


import lombok.extern.log4j.Log4j2;
import model.casella.Posizione;
import model.gioco.AbstractGiocoModel;
import view.swing.schermate.astrazioni.AbstractSchermataGiocaSwing;
import view.interfacce.schermata.SchermataGioco;


import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;

import java.util.List;

@Log4j2

public class SchermataGiocaSwingManuale extends AbstractSchermataGiocaSwing implements SchermataGioco {
    private JButton lanciaDadoButton;

    @Override
    protected JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.LIGHT_GRAY);

        // Create "roll dice" button
        lanciaDadoButton = new JButton("Lancia Dado");
        lanciaDadoButton.addActionListener(e -> {
            if (mediator != null && giocatoreCurr != null) {
                mediator.notifyDiceRoll();
            }
        });

        buttonPanel.add(lanciaDadoButton);
        lanciaDadoButton.setVisible(true);
        buttonPanel.setVisible(true);

        return buttonPanel;
    }

    @Override
    protected void initModeSpecific(AbstractGiocoModel giocoModel) {}

    @Override
    protected void handleGiocatoreCambio() {
        lanciaDadoButton.setEnabled(true);
    }

    @Override
    public void mostraRisultatoDado(int risultato) {
        int ris =JOptionPane.showOptionDialog(panel,
                "Giocatore: " + this.giocatoreCurr.getGiocatore().getNome()  + "\n"+
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
                mediator.verificaStatoGiocatore();
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










}
