package controller.gioco;

import model.gioco.AbstractGiocoModel;
import view.interfacce.schermata.SchermataGioco;


public interface Mediator {


    void start();
    void notifyDiceRoll();
    void notifyPlayerMove( );
    void notifyTurnChange();
    void verificaStatoGiocatore();
    void registerView(SchermataGioco view);
    void registerGameManager(AbstractGiocoModel gameModel);
    void notifyPlayerStop();
}
