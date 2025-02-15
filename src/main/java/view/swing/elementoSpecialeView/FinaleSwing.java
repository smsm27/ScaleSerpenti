package view.swing.elementoSpecialeView;


import lombok.Getter;
import view.interfacce.elementoGrafico.ElementoGrafico;
import view.swing.casellaView.CasellaGraficaSwing;

import javax.swing.*;
import java.awt.*;

public class FinaleSwing extends JPanel implements ElementoGrafico {
    @Getter
    private final CasellaGraficaSwing partenza;

    public FinaleSwing(CasellaGraficaSwing partenza) {
        this.partenza = partenza;


        setOpaque(false);
        setBackground(new Color(200, 130, 120, 0));

        caricaImmagine();
        aggiornaPosizione();
        aggiungiListener();
    }

    @Override
    public void disegna(Graphics g) {
        this.paintComponent(g);
    }

    @Override
    public void caricaImmagine() {

    }

    @Override
    public void aggiungiListener() {

    }

    @Override
    public void aggiornaPosizione() {

    }




}
