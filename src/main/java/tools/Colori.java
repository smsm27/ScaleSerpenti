package tools;

import lombok.Getter;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@Getter
public class Colori {
    private Map<String, Color> colori;

    public Colori() {
        colori = new HashMap<String, Color>();
        colori.put("red", new Color(255, 0, 0));
        colori.put("green", new Color(0, 255, 0));
        colori.put("blue", new Color(0, 0, 255));
        colori.put("yellow", new Color(255, 255, 0));
    }

}
