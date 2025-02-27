package view.swing.GiocatoreSwing;

import model.casella.Posizione;

import model.giocatore.Giocatore;
import view.interfacce.GiocatoreGrafico.GiocatoreGrafico;

import javax.swing.*;
import java.awt.*;

public class GiocatoreGraficaSwing extends JPanel implements GiocatoreGrafico {
    private Giocatore giocatore;


    public GiocatoreGraficaSwing(Giocatore giocatore) {
        this.giocatore = giocatore;
        setOpaque(false); // Per rendere trasparente lo sfondo
        setSize(20, 20); // Dimensione fissa per la pedina
        setLocation((int)giocatore.getPosizione().getX(), (int)giocatore.getPosizione().getY());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Rende il disegno più smooth
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Imposta il colore della pedina
        g2d.setColor(giocatore.getColor());

        // Disegna un cerchio che riempie il componente
        g2d.fillOval(0, 0, getWidth(), getHeight());
    }

    @Override
    public void spostaA(Posizione destinazione) {
        try {
            //Tronco cifre dopo la virgola
            // Aggiorno coordinate della casella
            int nuovaX = (int)destinazione.getX();
            int nuovaY = (int)destinazione.getY();

            //Aggiorno posione logica nel manager
            // Aggiorno posizione grafica del componente
            setLocation(nuovaX, nuovaY);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Inserire valori numerici validi.");
        }
    }

    @Override
    public Giocatore getGiocatore() {
        return giocatore;
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
