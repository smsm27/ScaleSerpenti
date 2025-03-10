package view.swing.casellaView;

import model.casella.Casella;
import view.interfacce.casellaGrafica.CasellaGrafica;


import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URI;
import java.net.URL;



public class CasellaGraficaSwing extends JPanel implements CasellaGrafica {
    protected Casella casella;
    protected JLabel numeroLabel; // Etichetta per il numero della casella
    protected Color c;

    public CasellaGraficaSwing(Casella casella, Color color) {

        this.casella = casella;
        this.c= color;
        setBounds((int) casella.getPosizione().getX(), (int) casella.getPosizione().getY(), (int) casella.getLarghezza(), (int) casella.getAltezza());
        setLayout(null);
        // Aggiungi il numero come JLabel
        setNumeroLabel();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (casella.getImmagine() != null) {
            g.drawImage(casella.getImmagine(), 0, 0, (int)casella.getLarghezza(), (int)casella.getAltezza(), this);
        } else {
            g.setColor(c);
            g.fillRect(0, 0, (int)casella.getLarghezza(), (int)casella.getAltezza());
        }
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, (int)casella.getLarghezza() - 1, (int)casella.getAltezza() - 1);
    }

    @Override
    public void setNumeroLabel(){
        numeroLabel = new JLabel(String.valueOf(casella.getIndice()), SwingConstants.CENTER);
        numeroLabel.setBounds(0, 0, 20, 20); // Occupa tutta la casella
        numeroLabel.setForeground(Color.BLACK); // Colore del testo
        numeroLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Font personalizzato
        add(numeroLabel);
    }

    @Override
    public Casella getCasella() {
        return casella;
    }

    @Override
    public void update() {
        repaint();
    }


}
