package model.casella;

import lombok.Getter;
import lombok.Setter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class CasellaFlyweight implements Serializable {

    private double larghezza, altezza;
    private String imageURL;
    private transient BufferedImage immagine;

    public CasellaFlyweight(double larghezza, double altezza, String imageURL) {
        this.larghezza = larghezza;
        this.altezza = altezza;
        this.imageURL = imageURL;
        if(imageURL!=null && !imageURL.isEmpty()){
            caricaImmagine();
        }
    }

    public void caricaImmagine() {
        try {
            File file = new File(imageURL);
            if (file.exists()) {
                this.immagine = ImageIO.read(file);
            }
        } catch (IOException e) {
            System.err.println("Errore nel caricamento dell'immagine: " + e.getMessage());
        }
    }
}
