package view.swing.elementoSpecialeView;

import model.casella.Casella;
import view.interfacce.elementoGrafico.ElementoGrafico;
import view.interfacce.elementoGrafico.ElementoGraficoFactory;
import view.swing.casellaView.CasellaGraficaSwing;

public class ElementoGraficoSwingFactory implements ElementoGraficoFactory {

    public ElementoGrafico creaElemento(Casella partenza, Casella destinazione) {
        CasellaGraficaSwing partenzaSwing = new CasellaGraficaSwing(partenza);
        CasellaGraficaSwing destinazioneSwing = new CasellaGraficaSwing(destinazione);
        return switch (partenza.getCasellaState()) {
            case SERPENTE -> new SerpenteSwing(partenzaSwing, destinazioneSwing);
            case SCALA -> new ScalaSwing(partenzaSwing, destinazioneSwing);
            case FINALE -> new FinaleSwing(partenzaSwing);
            default -> throw new IllegalArgumentException("Tipo non supportato: " + partenza.getCasellaState());
        };
    }
}
