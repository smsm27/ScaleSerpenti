package view.interfacce.elementoGrafico;

import model.casella.Casella;
import view.interfacce.casellaGrafica.CasellaGrafica;

public interface ElementoGraficoFactory<T extends CasellaGrafica> {
    ElementoGrafico creaElemento(Casella partenza, Casella destinazione);
    ElementoGrafico creaElemento(Casella partenza);
    ElementoGrafico getElemento(T partenza, T destinazione);
}
