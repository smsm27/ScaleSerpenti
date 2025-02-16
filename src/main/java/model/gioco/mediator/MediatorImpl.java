package model.gioco.mediator;


import controller.gioco.GiocoPresenter;
import model.casella.Posizione;
import model.giocatore.Giocatore;
import view.interfacce.schermata.SchermataGioco;

import java.util.List;

public class MediatorImpl implements Mediator {

    private SchermataGioco view;
    private GiocoPresenter giocoPresenter;

    /**
     *
     *
     */

    @Override
    public void create( String nomeFile) {
        giocoPresenter.setTabella( nomeFile);
    }

    public void addPlayers(){
        List<Giocatore> giocatori=view.getInfoPlayers();
        giocoPresenter.setGiocatori(giocatori);
    }
    /**
     * start notificato da giocoPresenter dopo la creazione della tabella
     */

    @Override
    public void start() {
        if( view == null || giocoPresenter == null)
            throw new NullPointerException();


        view.setGiocatoreCorrente(giocoPresenter.getGiocatoreCorrente() );

//        // Imposta lo stato iniziale del turno
//        MediatorImpl.setStatoTurno(MediatorImpl.StatoTurno.IN_ATTESA_LANCIO);

    }

    /**
     * Notificato da schermata (tramite Bottone)
     */

    @Override
    public void notifyDiceRoll() {
        // Gestisci il lancio dei dadi
        int risultato= giocoPresenter.lanciaDado();
        view.mostraRisultatoDado(risultato);
    }


    /**
     * Notificato da schermata
     */
    @Override
    public void notifyPlayerMove() {
        // Muovi il giocatore verso la destinazione
        List<Posizione> percorso = giocoPresenter.muoviGiocatore();
        view.animaMossa( percorso);

    }

    @Override
    public void notifyTurnChange() {
        view.setGiocatoreCorrente(giocoPresenter.getGiocatoreCorrente());
    }

    @Override
    public void notifyPlayerStop() {
        if(giocoPresenter.fineGioco()){
            view.mostraVincitore();
        }else{
            giocoPresenter.cambiaGiocatore();
        }
    }


    @Override
    public void registerView(SchermataGioco view) {
        this.view = view;
    }

    @Override

    public void registerGameManager(GiocoPresenter giocoPresenter) {
        this.giocoPresenter = giocoPresenter;
    }




}
