package view.interfacce.schermata;

import model.casella.Casella;
import model.casella.Posizione;
import model.giocatore.Giocatore;

import java.util.List;

public interface SchermataGioco extends Schermata {
    List<Giocatore> getInfoPlayers();
    void mostraGiocatori(List<Giocatore> giocatori);
    void spostaGiocatore( Posizione posizione);
    void setGiocatoreCorrente(Giocatore giocatore);
    void mostraRisultatoDado(int risultato);
    void animaMossa( List<Posizione> posizioniIntermedie);
    void mostraVincitore();

}
