package view.swing.elementoSpecialeView;

import model.casella.Casella;
import view.interfacce.casellaGrafica.CasellaGrafica;
import view.interfacce.elementoGrafico.ElementoGrafico;
import view.interfacce.elementoGrafico.ElementoGraficoFactory;
import view.swing.casellaView.CasellaGraficaSwing;

import java.awt.*;

public class ElementoGraficoSwingFactory implements ElementoGraficoFactory<CasellaGraficaSwing> {

    public ElementoGrafico creaElemento(Casella partenza, Casella destinazione) {
        CasellaGraficaSwing partenzaSwing = new CasellaGraficaSwing(partenza, Color.red);
        CasellaGraficaSwing destinazioneSwing = new CasellaGraficaSwing(destinazione, Color.RED);
        return switch (partenza.getCasellaState()) {
            case SERPENTE -> new SerpenteSwing(partenzaSwing, destinazioneSwing);
            case SCALA -> new ScalaSwing(partenzaSwing, destinazioneSwing);
            case FINALE -> new FinaleSwing(partenzaSwing);
            default -> throw new IllegalArgumentException("Tipo non supportato: " + partenza.getCasellaState());
        };
    }

    @Override
    public ElementoGrafico creaElemento(Casella partenza) {
        CasellaGraficaSwing partenzaSwing = new CasellaGraficaSwing(partenza, Color.BLUE);
        return switch (partenza.getCasellaState()){
            case FINALE -> new FinaleSwing(partenzaSwing);
            default -> throw new IllegalArgumentException("Tipo non supportato: " + partenza.getCasellaState());
        };
    }

    @Override
    public ElementoGrafico getElemento( CasellaGraficaSwing partenzaSwing, CasellaGraficaSwing destinazioneSwing) {
        return switch (partenzaSwing.getCasella().getCasellaState()) {
            case SERPENTE -> new SerpenteSwing( partenzaSwing,  destinazioneSwing);
            case SCALA -> new ScalaSwing( partenzaSwing,  destinazioneSwing);
            case FINALE -> new FinaleSwing(partenzaSwing);
            default -> throw new IllegalArgumentException("Tipo non supportato: " + partenzaSwing.getCasella().getCasellaState());
        };
    }
}
