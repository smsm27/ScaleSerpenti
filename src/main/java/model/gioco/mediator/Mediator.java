package model.gioco.mediator;


import model.giocatore.Giocatore;
import model.gioco.GiocoModel;
import view.interfacce.schermata.SchermataGioco;

import java.util.List;

public interface Mediator {


    void start();
    void notifyDiceRoll();
    void notifyPlayerMove( );
    void notifyTurnChange();
    void registerView(SchermataGioco view);
    void registerGameManager(GiocoModel gameModel);
    void notifyPlayerStop();
}
