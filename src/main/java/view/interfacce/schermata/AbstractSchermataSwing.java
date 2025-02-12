package view.interfacce.schermata;

import model.casella.Casella;
import view.interfacce.elementoGrafico.ElementoGrafico;
import view.swing.casellaView.CasellaGraficaSwing;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractSchermataSwing implements Schermata{
    protected JFrame frame;
    protected JLayeredPane panel;
    protected List<CasellaGraficaSwing> caselleGrafiche = new ArrayList<>();
    protected Map<Integer, ElementoGrafico> elementiSpeciali = new HashMap<>();

    @Override
    public void mostraTabellaGrafica(List<Casella> caselle) {
        pulisciVista();
        for (Casella casella : caselle) {
            CasellaGraficaSwing casellaGrafica = new CasellaGraficaSwing(casella);
            caselleGrafiche.add(casellaGrafica);
            panel.add(casellaGrafica, JLayeredPane.DEFAULT_LAYER);
        }
        refresh();
    }

    @Override
    public void mostraElementoGrafico(ElementoGrafico elemento) {
        if (elemento instanceof JComponent) {
            panel.add((JComponent)elemento, JLayeredPane.DRAG_LAYER);
            elementiSpeciali.put(elemento.hashCode(), elemento);
        }
    }

    @Override
    public void mostraMessaggio(String messaggio, String titolo, int tipoMessaggio) {
        JOptionPane.showMessageDialog(frame, messaggio, titolo, tipoMessaggio);
    }


    public void pulisciVista() {
        panel.removeAll();
        caselleGrafiche.clear();
        elementiSpeciali.clear();
    }

    @Override
    public void refresh() {
        panel.revalidate();
        panel.repaint();
    }



}
