package view.astrazioni.schermata;

import lombok.extern.log4j.Log4j2;
import model.casella.Casella;
import model.tabella.TabellaModel;
import view.interfacce.elementoGrafico.ElementoGrafico;
import view.interfacce.schermata.Schermata;
import view.swing.SchermataInizialeSwing;
import view.swing.casellaView.CasellaGraficaSwing;
import view.swing.elementoSpecialeView.ElementoGraficoSwingFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

@Log4j2
public abstract class AbstractSchermataSwing implements Schermata {
    protected JFrame frame;
    protected JLayeredPane panel;
    protected List<CasellaGraficaSwing> caselleGrafiche = new ArrayList<>();
    protected Map<Integer, ElementoGrafico> elementiGrafici = new HashMap<>();
    protected ElementoGraficoSwingFactory elementoGraficoSwingFactory = new ElementoGraficoSwingFactory();

    protected void aggiungiTornaMenu(JMenuBar menuBar) {
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
    public void mostraTabella(TabellaModel tabellaModel) {
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
            elementiGrafici.put(elemento.hashCode(), elemento);
        }
    }

    @Override
    public void mostraMessaggio(String messaggio, String titolo, int tipoMessaggio) {
        JOptionPane.showMessageDialog(frame, messaggio, titolo, tipoMessaggio);
    }


    public void pulisciVista() {
        panel.removeAll();
        caselleGrafiche.clear();
        elementiGrafici.clear();
    }

    @Override
    public void refresh() {
        panel.revalidate();
        panel.repaint();
    }

    @Override
    public String getNomeTabella() {
        String userHome = System.getProperty("user.home");
        File directory = new File(userHome, "SerpiEScale/save");
        if (!directory.exists() || !directory.isDirectory()) {
            JOptionPane.showMessageDialog(frame, "Nessuna mappa salvata trovata!",
                    "Errore", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".ser"));
        if (files == null || files.length == 0) {
            JOptionPane.showMessageDialog(frame, "Nessuna mappa salvata trovata!",
                    "Errore", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        String[] mapNames = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            log.info(files[i].getName());
            mapNames[i] = files[i].getName().replace(".ser", "");

        }


        return (String) JOptionPane.showInputDialog(
                frame,
                "Seleziona una mappa da caricare:",
                "Carica Mappa",
                JOptionPane.QUESTION_MESSAGE,
                null,
                mapNames,
                mapNames[0]
        );
    }

}
