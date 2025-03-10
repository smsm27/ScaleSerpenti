package view.interfacce.elementoGrafico;

import java.awt.*;
import java.io.IOException;

public interface ElementoGrafico {
    void disegna(Graphics g);
    void caricaImmagine() throws IOException;
    void aggiornaPosizione();
}
