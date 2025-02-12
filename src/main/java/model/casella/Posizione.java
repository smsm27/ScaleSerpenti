package model.casella;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Posizione implements Serializable {
    double x;
    double y;
    public Posizione(double x, double y) {
        this.setLocation(x, y);
    }
    public void setLocation(double x, double y){
        this.x = x;
        this.y = y;
    }

    public String toString(){
        return "Posizione [x=" + x + ", y=" + y + "]";
    }
}
