package controller.gioco;


import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import model.casella.Posizione;
import model.gioco.AbstractGiocoModel;
import view.interfacce.schermata.SchermataGioco;

import java.util.List;

@Log4j2
public class MediatorImpl implements Mediator {

    private SchermataGioco view;
    private AbstractGiocoModel giocoModel;
    @Getter
    private int risultato;


    /**
     * start notificato da giocoModel dopo la creazione della tabella
     */

    public void verificaStatoGiocatore(){
        switch (giocoModel.getStatoTurno()){
            case IN_ATTESA_LANCIO -> notifyDiceRoll();
            case FINE_TURNO -> notifyPlayerStop();
        }
    }


    @Override
    public void start() {
        if( view == null || giocoModel == null)
            throw new NullPointerException();

        view.mostraTabella(giocoModel.getTabellaModel());
        view.mostraGiocatori(giocoModel.getGiocatori());
        view.setGiocatoreCorrente(giocoModel.getGiocatoreCorrente() );

    }

    /**
     * Notificato da schermata (tramite Bottone)
     */

    @Override
    public void notifyDiceRoll() {
        // Gestisci il lancio dei dadi
        this.risultato= giocoModel.lanciaDado();
        view.mostraRisultatoDado(risultato);
    }


    /**
     * Notificato da schermata
     */
    @Override
    public void notifyPlayerMove() {
        // Muovi il giocatore verso la destinazione
        System.out.println("Sto per animare:");
        List<Posizione> percorso = giocoModel.calcolaPercorso(risultato);

        view.animaMossa(percorso);
    }

    @Override
    public void notifyTurnChange() {
        giocoModel.prossimoTurno();
        view.setGiocatoreCorrente(giocoModel.getGiocatoreCorrente());

    }

    @Override
    public void notifyPlayerStop() {
        if(giocoModel.verificaVincitore()){
            view.mostraVincitore();
        }else{
            giocoModel.giocatoreFermo();
            log.info("Sto cambiando il turno");
            notifyTurnChange();
        }
    }


    @Override
    public void registerView(SchermataGioco view) {
        this.view = view;
    }

    @Override
    public void registerGameManager(AbstractGiocoModel giocoModel) {
        this.giocoModel= giocoModel;


    }




}
