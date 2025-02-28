package controller.creazione;


import model.tabella.TabellaModel;
import view.interfacce.elementoGrafico.ElementoGrafico;
import view.interfacce.elementoGrafico.ElementoGraficoFactory;
import view.interfacce.schermata.SchermataCreazione;


public class CreazioneTabellaController {
    private SchermataCreazione view;
    private TabellaModel tabella;
    private ElementoGraficoFactory elementoGraficoFactory;

    public CreazioneTabellaController(SchermataCreazione view,ElementoGraficoFactory elementoFactory) {
        this.view = view;
        this.tabella = new TabellaModel();
        this.elementoGraficoFactory = elementoFactory;
    }

    public void creaNuovaTabella(int numCaselle, int dimX, int dimY,  String casellaPath) {
        tabella.creaNuovaTabella(numCaselle, dimX,dimY,casellaPath );
        view.pulisciVista();
        view.mostraTabella(tabella);
    }

    public void aggiungiCasellaSpeciale(String tipo, int partenza, int destinazione) {

        boolean risultato = tabella.aggiungiCasellaSpeciale(tipo, partenza, destinazione);
        if (risultato) {
            ElementoGrafico elemento = elementoGraficoFactory.creaElemento(
                    tabella.getCasella(partenza),
                    tabella.getCasella(destinazione)
            );
            view.mostraElementoGrafico(elemento);
            view.refresh();
        } else {
            view.mostraMessaggio("Impossibile aggiungere casella speciale", "Errore", 0);
        }
    }

    public String salvaTabella(String nomeFile) {
        return tabella.salvaTabella(nomeFile);
    }

    public void caricaTabella(String nomeFile) {
        tabella.caricaTabella(nomeFile);
        view.mostraTabella(tabella);

    }
}
