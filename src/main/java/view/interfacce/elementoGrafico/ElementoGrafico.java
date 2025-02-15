package view.interfacce.elementoGrafico;

import java.awt.*;

public interface ElementoGrafico {
    void disegna(Graphics g);
    Rectangle getBounds();
    void caricaImmagine();
    void aggiungiListener();
    void aggiornaPosizione();
}
