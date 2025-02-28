package view.interfacce.schermata;


import model.tabella.TabellaModel;
import view.interfacce.elementoGrafico.ElementoGrafico;




public interface Schermata {

    void inizializza();
    void mostraTabella(TabellaModel tabella);
    void mostraElementoGrafico(ElementoGrafico elemento);
    void mostraMessaggio(String messaggio, String titolo, int tipoMessaggio);
    void refresh();
    String getNomeTabella();
}
