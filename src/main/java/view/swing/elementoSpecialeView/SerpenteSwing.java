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
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
            texture = ImageIO.read(new File("src/main/img/serpenteBody.png"));
            testaImage = ImageIO.read(new File("src/main/img/serpenteTp.png"));
            codaImage = ImageIO.read(new File("src/main/img/serpenteC.png"));

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

        // Calcola i punti di controllo per la curva di Bezier
        double distanzaX = end.getX() - start.getX();
        double distanzaY = end.getY() - start.getY();
        double lunghezza = Math.sqrt(distanzaX * distanzaX + distanzaY * distanzaY);

        // Calcola l'angolo della linea tra inizio e fine
        double angolo = Math.atan2(distanzaY, distanzaX);

        // Determina la direzione della curva (sopra o sotto la linea diretta)
        double offset = lunghezza * curvatura;
        double perpendicolarX = Math.sin(angolo) * offset;
        double perpendicolarY = -Math.cos(angolo) * offset;

        // Punti di controllo per la curva di Bezier
        Point2D ctrl1 = new Point2D.Double(
                start.getX() + distanzaX * 0.3 + perpendicolarX,
                start.getY() + distanzaY * 0.3 + perpendicolarY
        );

        Point2D ctrl2 = new Point2D.Double(
                start.getX() + distanzaX * 0.7 + perpendicolarX,
                start.getY() + distanzaY * 0.7 + perpendicolarY
        );

        // Crea la curva di Bezier
        CubicCurve2D curve = new CubicCurve2D.Double(
                start.getX(), start.getY(),
                ctrl1.getX(), ctrl1.getY(),
                ctrl2.getX(), ctrl2.getY(),
                end.getX(), end.getY()
        );

        // Disegna il corpo del serpente usando la texture colorata
        if (textureColorata != null) {
            g2.setStroke(new BasicStroke(spessoreCorpo, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            TexturePaint texturePaint = new TexturePaint(textureColorata, new Rectangle(0, 0, 20, 10));
            g2.setPaint(texturePaint);
            g2.draw(curve);
        }

        // Calcola angoli per testa e coda
        double angoloTesta = calcolaAngoloTangente(curve, 0.0);
        double angoloCoda = calcolaAngoloTangente(curve, 1.0);

        // Disegna la testa colorata
        if (testaColorata != null) {
            AffineTransform oldTransform = g2.getTransform();
            g2.translate(start.getX(), start.getY());
            g2.rotate(angoloTesta);
            int testaWidth = (int)(testaColorata.getWidth() * scalaTesta);
            int testaHeight = (int)(testaColorata.getHeight() * scalaTesta);
            g2.drawImage(testaColorata, -testaWidth/2, -testaHeight/2, testaWidth, testaHeight, null);
            g2.setTransform(oldTransform);
        }

        // Disegna la coda colorata
        if (codaColorata != null) {
            AffineTransform oldTransform = g2.getTransform();
            g2.translate(end.getX(), end.getY());
            g2.rotate(angoloCoda + Math.PI); // Ruota di 180° per la coda
            int codaWidth = (int)(codaColorata.getWidth() * scalaCoda);
            int codaHeight = (int)(codaColorata.getHeight() * scalaCoda);
            g2.drawImage(codaColorata, -codaWidth/2, -codaHeight/2, codaWidth, codaHeight, null);
            g2.setTransform(oldTransform);
        }
    }

    /**
     * Calcola l'angolo della tangente alla curva nel punto specificato (0-1)
     */
    private double calcolaAngoloTangente(CubicCurve2D curve, double t) {
        // Parametri della curva
        double x0 = curve.getX1();
        double y0 = curve.getY1();
        double x1 = curve.getCtrlX1();
        double y1 = curve.getCtrlY1();
        double x2 = curve.getCtrlX2();
        double y2 = curve.getCtrlY2();
        double x3 = curve.getX2();
        double y3 = curve.getY2();

        // Calcola la derivata della curva di Bezier nel punto t
        double dx = 3 * (1-t)*(1-t) * (x1-x0) + 6 * (1-t)*t * (x2-x1) + 3 * t*t * (x3-x2);
        double dy = 3 * (1-t)*(1-t) * (y1-y0) + 6 * (1-t)*t * (y2-y1) + 3 * t*t * (y3-y2);

        // Restituisce l'angolo della tangente
        return Math.atan2(dy, dx);
    }

    @Override
    public void disegna(Graphics g) {
        this.paintComponent(g);
    }
}