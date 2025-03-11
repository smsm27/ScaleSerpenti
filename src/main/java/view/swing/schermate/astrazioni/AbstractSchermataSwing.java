package view.swing.schermate.astrazioni;

import lombok.extern.log4j.Log4j2;
import model.casella.Casella;
import model.tabella.TabellaModel;
import tools.SaveLoadTabella;
import view.interfacce.elementoGrafico.ElementoGrafico;
import view.interfacce.schermata.Schermata;
import view.swing.schermate.inziale.SchermataInizialeSwing;
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
        Color[] colori={Color.lightGray, Color.white, Color.red, Color.blue};
        int i=0;
        for (Casella casella : tabellaModel.getCaselle()) {
            //i= new Random().nextInt(colori.length);
            //i=(i+1)%(colori.length);
            CasellaGraficaSwing casellaGrafica = new CasellaGraficaSwing(casella, colori[0]);
            caselleGrafiche.add(casellaGrafica);
            panel.add(casellaGrafica, JLayeredPane.DEFAULT_LAYER);

        }
        for(Map.Entry<Integer, Casella> entry : tabellaModel.getCaselleSpeciali().entrySet()) {
            ElementoGrafico elementoGrafico = null;
            if(tabellaModel.isCasellaComplessa(entry.getKey())) {
                elementoGrafico = elementoGraficoSwingFactory.getElemento(
                        caselleGrafiche.get(entry.getKey()), caselleGrafiche.get(entry.getValue().getDestinazione().getIndice())
                );
            }else{
                if(!tabellaModel.isPartOfCasellaComplessa(entry.getKey())) {
                    log.info("sono casella speciale non complessa "+ entry.getKey()+" "+ entry.getValue().getCasellaState());
                    elementoGrafico = elementoGraficoSwingFactory.getElemento(
                            caselleGrafiche.get(entry.getKey()),null
                    );
                }

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
        if (panel != null) {
            panel.removeAll();
            caselleGrafiche.clear();
            elementiGrafici.clear();
        }
    }

    @Override
    public void refresh() {
        panel.revalidate();
        panel.repaint();
    }

    @Override
    public String getNomeTabella() {
        // Lista per contenere tutti i nomi delle tabelle
        List<String> allTableNames = new ArrayList<>();

        // Ottieni le tabelle salvate dall'utente
        String userHome = System.getProperty("user.home");
        File directory = new File(userHome, "SerpiEScale/save");
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles((dir, name) -> name.endsWith(".ser"));
            if (files != null && files.length > 0) {
                for (File file : files) {
                    allTableNames.add(file.getName().replace(".ser", ""));
                    log.info("Trovata tabella: " + file.getName());
                }
            }
        }

        // Verifica se esiste la tabella predefinita
        String tabellaPredefinita = "tabella_standard";
        if (SaveLoadTabella.esisteTabellaPredefinta(tabellaPredefinita)) {
            // Aggiungi solo se non esiste già una tabella utente con lo stesso nome
            if (!allTableNames.contains(tabellaPredefinita)) {
                allTableNames.add(tabellaPredefinita);
                log.info("Trovata tabella predefinita: " + tabellaPredefinita);
            }
        }

        // Se non ci sono tabelle, mostra un messaggio di errore
        if (allTableNames.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Nessuna mappa trovata!",
                    "Errore", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        // Converti la lista in array per il JOptionPane
        String[] tableNames = allTableNames.toArray(new String[0]);

        // Mostra il dialog per la selezione
        String selectedTable = (String) JOptionPane.showInputDialog(
                frame,
                "Seleziona una mappa da caricare:",
                "Carica Mappa",
                JOptionPane.INFORMATION_MESSAGE,
                null,
                tableNames,
                tableNames[0]
        );

        return selectedTable;
    }

    public void gestisciErrore(){
        // Mostra il messaggio di errore
        JOptionPane.showMessageDialog(frame, "Mappa inesistente/ nessun giocatore inserito", "Errore", JOptionPane.ERROR_MESSAGE);

        // Chiudi il frame corrente
        frame.dispose();

        // Mostra la schermata iniziale
        SchermataInizialeSwing.getInstance().mostraMenu();
    }

}
