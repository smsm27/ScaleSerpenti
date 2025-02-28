package view.swing.elementoSpecialeView;

import lombok.Getter;
import view.interfacce.elementoGrafico.ElementoGrafico;
import view.swing.casellaView.CasellaGraficaSwing;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

public class SerpenteSwing extends JPanel implements ElementoGrafico {
    @Getter
    private final CasellaGraficaSwing partenza;
    @Getter
    private final CasellaGraficaSwing destinazione;
    private BufferedImage texture;
    private BufferedImage testaImage;
    private BufferedImage codaImage;

    // Immagini colorate
    private BufferedImage textureColorata;
    private BufferedImage testaColorata;
    private BufferedImage codaColorata;

    // Proprietà per controllare l'aspetto del serpente
    private final float spessoreCorpo = 5.0f;  // Ridotto a 5 pixel
    private final float scalaTesta = 0.6f;    // Riduce la testa al 60%
    private final float scalaCoda = 0.6f;     // Riduce la coda al 60%
    private final float curvatura = 0.4f;     // Controlla quanto è curvo il serpente (0-1)

    // Colore del serpente
    private Color coloreSerpenteRandom;

    public SerpenteSwing(CasellaGraficaSwing partenza, CasellaGraficaSwing destinazione) {
        this.partenza = partenza;
        this.destinazione = destinazione;

        setOpaque(false);
        setBackground(new Color(0, 0, 0, 0));

        // Genera un colore casuale per il serpente
        generaColoreRandom();

        caricaImmagine();
        aggiornaPosizione();
        aggiungiListener();
    }

    /**
     * Genera un colore casuale per il serpente, evitando colori troppo chiari
     */
    private void generaColoreRandom() {
        Random random = new Random();
        // Limitiamo la luminosità per evitare colori troppo chiari
        float h = random.nextFloat(); // Tonalità (0-1)
        float s = 0.7f + random.nextFloat() * 0.3f; // Saturazione (0.7-1.0)
        float b = 0.5f + random.nextFloat() * 0.5f; // Luminosità (0.5-1.0)

        coloreSerpenteRandom = Color.getHSBColor(h, s, b);
    }

    @Override
    public void caricaImmagine() {
        try {
            texture = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/img/serpenteBody.png")));
            testaImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/img/serpenteTp.png")));
            codaImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/img/serpenteC.png")));

            // Crea versioni colorate delle immagini
            coloraImmagini();
        } catch (IOException e) {
            System.err.println("Errore nel caricamento delle immagini: " + e.getMessage());
        }
    }

    /**
     * Colora le immagini del serpente con il colore casuale
     */
    private void coloraImmagini() {
        if (texture == null || testaImage == null || codaImage == null) return;

        // Colora la texture del corpo
        textureColorata = applicaTintaImmagine(texture, coloreSerpenteRandom);

        // Colora la testa
        testaColorata = applicaTintaImmagine(testaImage, coloreSerpenteRandom);

        // Colora la coda
        codaColorata = applicaTintaImmagine(codaImage, coloreSerpenteRandom);
    }

    /**
     * Applica una tinta a un'immagine
     * @param originale Immagine originale
     * @param colore Colore da applicare
     * @return Immagine con tinta applicata
     */
    private BufferedImage applicaTintaImmagine(BufferedImage originale, Color colore) {
        BufferedImage colorata = new BufferedImage(
                originale.getWidth(),
                originale.getHeight(),
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = colorata.createGraphics();
        g2.drawImage(originale, 0, 0, null);

        // Applica un filtro di colore preservando l'alpha
        for (int y = 0; y < colorata.getHeight(); y++) {
            for (int x = 0; x < colorata.getWidth(); x++) {
                int pixel = originale.getRGB(x, y);
                int alpha = (pixel >> 24) & 0xFF;

                // Se il pixel non è trasparente
                if (alpha > 0) {
                    int r = (pixel >> 16) & 0xFF;
                    int g = (pixel >> 8) & 0xFF;
                    int b = pixel & 0xFF;

                    // Calcola luminosità (grayscale)
                    float luminosita = (r * 0.299f + g * 0.587f + b * 0.114f) / 255.0f;

                    // Applica il colore mantenendo la luminosità originale
                    int newR = Math.min(255, (int)(colore.getRed() * luminosita));
                    int newG = Math.min(255, (int)(colore.getGreen() * luminosita));
                    int newB = Math.min(255, (int)(colore.getBlue() * luminosita));

                    // Assembla il nuovo pixel
                    int newPixel = (alpha << 24) | (newR << 16) | (newG << 8) | newB;
                    colorata.setRGB(x, y, newPixel);
                }
            }
        }

        g2.dispose();
        return colorata;
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
        // Calcola i limiti del rettangolo che contiene l'intero serpente
        int padding = 30; // Aggiungi padding per spazio per curvatura
        int x = Math.min(partenza.getX(), destinazione.getX()) - padding;
        int y = Math.min(partenza.getY(), destinazione.getY()) - padding;
        int width = Math.abs(partenza.getX() - destinazione.getX()) + partenza.getWidth() + padding * 2;
        int height = Math.abs(partenza.getY() - destinazione.getY()) + partenza.getHeight() + padding * 2;

        setBounds(x, y, width, height);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // Punto di partenza (testa)
        Point2D start = new Point2D.Double(
                partenza.getX() - getX() + partenza.getWidth() / 2,
                partenza.getY() - getY() + partenza.getHeight() / 2
        );

        // Punto di arrivo (coda)
        Point2D end = new Point2D.Double(
                destinazione.getX() - getX() + destinazione.getWidth() / 2,
                destinazione.getY() - getY() + destinazione.getHeight() / 2
        );

        // Calcola la distanza e l'angolo di base
        double distanzaX = end.getX() - start.getX();
        double distanzaY = end.getY() - start.getY();
        double lunghezza = Math.sqrt(distanzaX * distanzaX + distanzaY * distanzaY);
        double angoloBase = Math.atan2(distanzaY, distanzaX);

        // Crea un percorso con più punti per simulare una funzione seno
        Path2D.Double path = new Path2D.Double();

        // Numero di segmenti per la curva seno
        int segmenti = 30;

        // Ampiezza della curva seno
        double ampiezza = lunghezza * curvatura * 0.8;

        // Frequenza - controlla quante "onde" ci sono
        double frequenza = 1.0; // Un'onda completa

        // Array per memorizzare i punti del percorso
        Point2D.Double[] pathPoints = new Point2D.Double[segmenti + 1];

        // Calcola tutti i punti del percorso
        for (int i = 0; i <= segmenti; i++) {
            double t = (double) i / segmenti;

            // Calcola la posizione lungo la linea diretta
            double x = start.getX() + distanzaX * t;
            double y = start.getY() + distanzaY * t;

            // Calcola lo spostamento perpendicolare basato sulla funzione seno
            double sinValue = Math.sin(t * Math.PI * 2 * frequenza);
            double offsetX = Math.sin(angoloBase) * sinValue * ampiezza;
            double offsetY = -Math.cos(angoloBase) * sinValue * ampiezza;

            // Memorizza il punto
            pathPoints[i] = new Point2D.Double(x + offsetX, y + offsetY);

            // Aggiungi il punto al percorso
            if (i == 0) {
                path.moveTo(x + offsetX, y + offsetY);
            } else {
                path.lineTo(x + offsetX, y + offsetY);
            }
        }

        // Disegna il corpo del serpente usando solo il colore
        // Utilizziamo direttamente il colore casuale generato
        g2.setStroke(new BasicStroke(spessoreCorpo, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(coloreSerpenteRandom);
        g2.draw(path);

        // Calcola l'angolo della testa basato sulla direzione del primo segmento
        double angoloTesta;
        if (segmenti >= 1) {
            angoloTesta = Math.atan2(
                    pathPoints[1].y - pathPoints[0].y,
                    pathPoints[1].x - pathPoints[0].x
            );
        } else {
            angoloTesta = angoloBase;
        }

        // Calcola l'angolo della coda basato sulla direzione dell'ultimo segmento
        double angoloCoda;
        if (segmenti >= 1) {
            angoloCoda = Math.atan2(
                    pathPoints[segmenti].y - pathPoints[segmenti-1].y,
                    pathPoints[segmenti].x - pathPoints[segmenti-1].x
            );
        } else {
            angoloCoda = angoloBase;
        }

        // Disegna la testa colorata
        if (testaColorata != null) {
            AffineTransform oldTransform = g2.getTransform();
            g2.translate(pathPoints[0].x, pathPoints[0].y);
            g2.rotate(angoloTesta);
            int testaWidth = (int)(testaColorata.getWidth() * scalaTesta);
            int testaHeight = (int)(testaColorata.getHeight() * scalaTesta);
            g2.drawImage(testaColorata, -testaWidth/2, -testaHeight/2, testaWidth, testaHeight, null);
            g2.setTransform(oldTransform);
        }

        // Disegna la coda colorata
        if (codaColorata != null) {
            AffineTransform oldTransform = g2.getTransform();
            g2.translate(pathPoints[segmenti].x, pathPoints[segmenti].y);
            g2.rotate(angoloCoda + Math.PI); // Ruota di 180° per la coda
            int codaWidth = (int)(codaColorata.getWidth() * scalaCoda);
            int codaHeight = (int)(codaColorata.getHeight() * scalaCoda);
            g2.drawImage(codaColorata, -codaWidth/2, -codaHeight/2, codaWidth, codaHeight, null);
            g2.setTransform(oldTransform);
        }
    }



    @Override
    public void disegna(Graphics g) {
        this.paintComponent(g);
    }
}