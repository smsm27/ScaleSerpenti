package view.interfacce.schermata;

import model.casella.Casella;
import model.tabella.Tabella;
import model.tabella.TabellaModel;
import tools.Colori;
import view.interfacce.elementoGrafico.ElementoGrafico;
import view.swing.SchermataInizialeSwing;
import view.swing.casellaView.CasellaGraficaSwing;
import view.swing.elementoSpecialeView.ElementoGraficoSwingFactory;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public abstract class AbstractSchermataSwing implements Schermata{
    protected JFrame frame;
    protected JLayeredPane panel;
    protected List<CasellaGraficaSwing> caselleGrafiche = new ArrayList<>();
    protected Map<Integer, ElementoGrafico> elementiSpeciali = new HashMap<>();
    protected ElementoGraficoSwingFactory elementoGraficoSwingFactory = new ElementoGraficoSwingFactory();

    protected void aggiungiMenuTornaMenu(JMenuBar menuBar) {
        JMenu menuFile = null;

        // Check if there's already a "File" menu
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            if (menuBar.getMenu(i).getText().equals("File")) {
                menuFile = menuBar.getMenu(i);
                break;
            }
        }

        // If no "File" menu exists, create one
        if (menuFile == null) {
            menuFile = new JMenu("File");
            menuBar.add(menuFile);
        }

        // Add the "Back to Menu" menu item
        JMenuItem tornaMenuItem = new JMenuItem("Torna al Menu Principale");
        tornaMenuItem.addActionListener(e -> tornaMenuPrincipale());
        menuFile.add(tornaMenuItem);
    }

    protected void tornaMenuPrincipale() {
        int option = JOptionPane.showConfirmDialog(frame,
                "Vuoi tornare al menu principale?\nLe modifiche non salvate andranno perse.",
                "Conferma", JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            frame.dispose();
            SchermataInizialeSwing.getInstance().mostraMenu();
        }
    }

    @Override
    public void mostraTabellaGrafica(TabellaModel tabellaModel) {
        pulisciVista();
        Color[] colori={Color.yellow, Color.white, Color.darkGray, Color.blue};
        int i=0;
        for (Casella casella : tabellaModel.getCaselle()) {
            i= new Random().nextInt(colori.length);
            CasellaGraficaSwing casellaGrafica = new CasellaGraficaSwing(casella, colori[1]);
            caselleGrafiche.add(casellaGrafica);
            panel.add(casellaGrafica, JLayeredPane.DEFAULT_LAYER);

        }
        for(Map.Entry<Integer, Casella> entry : tabellaModel.getOggettiSpeciali().entrySet()) {
            ElementoGrafico elementoGrafico = null;
            if(tabellaModel.isCasellaSpecialeComplessa(entry.getKey())) {
                elementoGrafico = elementoGraficoSwingFactory.getElemento(
                        caselleGrafiche.get(entry.getKey()), caselleGrafiche.get(entry.getValue().getDestinazione().getIndice())
                );
            }
            mostraElementoGrafico(elementoGrafico);
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
