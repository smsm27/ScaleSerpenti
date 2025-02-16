package model.gioco.mediator;


import controller.gioco.GiocoPresenter;

import view.interfacce.schermata.SchermataGioco;

import java.util.List;

public interface Mediator {

    void create( String nomeFile);
    void start();
    void notifyDiceRoll();
    void notifyPlayerMove( );
    void notifyTurnChange();
    void registerView(SchermataGioco view);
    void registerGameManager(GiocoPresenter presenter);

    void notifyPlayerStop();
}
