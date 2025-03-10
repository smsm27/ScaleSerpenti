package view.swing.elementoSpecialeView;


import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import view.interfacce.elementoGrafico.ElementoGrafico;
import view.swing.casellaView.CasellaGraficaSwing;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.Buffer;
import java.util.Objects;

@Log4j2
public class FinaleSwing extends JPanel implements ElementoGrafico {
    private final CasellaGraficaSwing partenza;
    private BufferedImage immagine;
    public FinaleSwing(CasellaGraficaSwing partenza) {
        this.partenza = partenza;

        // Imposta dimensioni e posizione in base alla casella di partenza
        setBounds(partenza.getBounds());

        // Rendi trasparente lo sfondo
        setOpaque(false);

        // Carica l'immagine
        caricaImmagine();

    }

    @Override
    public void disegna(Graphics g) {
        this.paintComponent(g);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (immagine != null) {
            // Disegna l'immagine adattandola alle dimensioni della casella
            g.drawImage(immagine, 0, 0, getWidth(), getHeight(), this);
        }
    }

    @Override
    public void caricaImmagine()  {
        try {
            immagine = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/img/finale.png")));
            log.info(immagine.getWidth() + "x" + immagine.getHeight());
        } catch (IOException e) {
            System.err.println("Errore nel caricamento delle immagini: " + e.getMessage());
        }
    }

    @Override
    public void aggiornaPosizione() {

    }


}
