package model.casella;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CasellaFlyweight implements Serializable {
    private double larghezza, altezza;
    private String imageURL;

    public CasellaFlyweight(double larghezza, double altezza, String imageURL) {
        this.larghezza = larghezza;
        this.altezza = altezza;
        this.imageURL = imageURL;
    }
}
