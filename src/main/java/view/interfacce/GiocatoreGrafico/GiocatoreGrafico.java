package view.interfacce.GiocatoreGrafico;

import model.casella.Posizione;
import model.giocatore.Giocatore;
import view.interfacce.elementoGrafico.ElementoGrafico;

public interface GiocatoreGrafico extends ElementoGrafico {
    void spostaA(Posizione posizione);
    Giocatore getGiocatore();
}
