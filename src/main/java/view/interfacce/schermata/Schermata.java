package view.interfacce.schermata;

import model.casella.Casella;
import view.interfacce.elementoGrafico.ElementoGrafico;

import java.util.List;

public interface Schermata {
    void mostraTabellaGrafica(List<Casella> caselle);
    void mostraElementoGrafico(ElementoGrafico elemento);
    void mostraMessaggio(String messaggio, String titolo, int tipoMessaggio);
    void refresh();
    void inizializza();
}
