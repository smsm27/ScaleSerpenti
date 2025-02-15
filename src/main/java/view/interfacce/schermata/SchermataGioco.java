package view.interfacce.schermata;

import model.casella.Casella;
import model.casella.Posizione;
import model.giocatore.Giocatore;

import java.util.List;

public interface SchermataGioco extends Schermata {
    void mostraTabellaGrafica(List<Casella> caselle);
    void spostaGiocatore( Posizione posizione);
    void setGiocatoreCorrente(Giocatore giocatore);
    void animaMossa( List<Posizione> posizioniIntermedie);
    void mostraVincitore();
    void mostraRisultatoDado(int risultato);
    List<Giocatore> getInfoPlayers();
}
