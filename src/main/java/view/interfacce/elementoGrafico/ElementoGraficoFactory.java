package view.interfacce.elementoGrafico;

import model.casella.Casella;

public interface ElementoGraficoFactory {
    ElementoGrafico creaElemento(Casella partenza, Casella destinazione);
}
