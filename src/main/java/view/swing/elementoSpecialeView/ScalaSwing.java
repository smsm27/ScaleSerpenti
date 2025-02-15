package view.swing.elementoSpecialeView;


import lombok.Getter;
import view.interfacce.elementoGrafico.ElementoGrafico;
import view.swing.casellaView.CasellaGraficaSwing;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Getter
public class ScalaSwing extends JPanel implements ElementoGrafico {
    @Getter
    private final CasellaGraficaSwing partenza;
    @Getter
    private final CasellaGraficaSwing destinazione;
    private BufferedImage scala;

    public ScalaSwing(CasellaGraficaSwing partenza, CasellaGraficaSwing destinazione) {
        this.partenza = partenza;
        this.destinazione = destinazione;

        setOpaque(false);
        setBackground(new Color(0, 0, 0, 0));

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
        try {
            scala = ImageIO.read(new File("src/main/img/scala.png"));

        } catch (IOException e) {
            System.err.println("Errore nel caricamento delle immagini: " + e.getMessage());
        }
    }

    @Override
    public void aggiungiListener() {
        partenza.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(java.awt.event.ComponentEvent e) {
                aggiornaPosizione();
            }
        });

        destinazione.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(java.awt.event.ComponentEvent e) {
                aggiornaPosizione();
            }
        });
    }

    @Override
    public void aggiornaPosizione() {
        // Calcola la posizione e dimensione del pannello per coprire entrambe le caselle
        int x = Math.min(partenza.getX(), destinazione.getX());
        int y = Math.min(partenza.getY(), destinazione.getY());
        int width = Math.abs(partenza.getX() - destinazione.getX()) + partenza.getWidth();
        int height = Math.abs(partenza.getY() - destinazione.getY()) + partenza.getHeight();

        setBounds(x, y, width, height);
        repaint(); // Ridisegna il serpente
    }


    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Centro delle caselle
        int startX = partenza.getX() - getX() + partenza.getWidth() / 2;
        int startY = partenza.getY() - getY() + partenza.getHeight() / 2;
        int endX = destinazione.getX() - getX() + destinazione.getWidth() / 2;
        int endY = destinazione.getY() - getY() + destinazione.getHeight() / 2;

        // Vettore e angolo
        int dx = endX - startX;
        int dy = endY - startY;
        double angle = Math.atan2(dy, dx);
        double length = Math.sqrt(dx * dx + dy * dy);

        // Salva il contesto grafico
        AffineTransform oldTransform = g2.getTransform();

        // Sposta e ruota per disegnare la scala
        g2.translate(startX, startY);
        g2.rotate(angle);

        // Disegna i montanti della scala
        int larghezzaScala = 20;
        g2.setStroke(new BasicStroke(3));
        g2.setColor(new Color(139, 69, 19)); // Marrone per il legno
        g2.drawLine(0, -larghezzaScala/2, (int)length, -larghezzaScala/2);
        g2.drawLine(0, larghezzaScala/2, (int)length, larghezzaScala/2);

        // Disegna i gradini
        int numeroGradini = (int)(length / 20);
        for(int i = 1; i < numeroGradini; i++) {
            int posX = (int)(i * length / numeroGradini);
            g2.drawLine(posX, -larghezzaScala/2, posX, larghezzaScala/2);
        }

        // Ripristina il contesto grafico
        g2.setTransform(oldTransform);
    }


}
