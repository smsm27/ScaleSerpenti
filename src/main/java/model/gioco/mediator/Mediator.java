package model.gioco.mediator;

import model.gioco.giocoManager.AbstractGiocoModel;
import model.gioco.giocoManager.GiocoBaseModel;
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
