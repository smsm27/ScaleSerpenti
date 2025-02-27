package view.interfacce.schermata;

import model.giocatore.Giocatore;
import model.tabella.TabellaModel;
import view.interfacce.elementoGrafico.ElementoGrafico;

import java.util.List;


public interface Schermata {
    void mostraGiocatori(List<Giocatore> giocatori);
    void mostraTabellaGrafica(TabellaModel grafica);
    void mostraElementoGrafico(ElementoGrafico elemento);
    void mostraMessaggio(String messaggio, String titolo, int tipoMessaggio);
    void refresh();
    void inizializza();
}
