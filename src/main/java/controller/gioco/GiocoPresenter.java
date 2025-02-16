package controller.gioco;

import model.casella.Casella;
import model.casella.Posizione;
import model.giocatore.Giocatore;
import model.gioco.GiocoModel;
import model.gioco.mediator.MediatorImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GiocoPresenter {
    private final MediatorImpl mediator;
    private final GiocoModel giocoModel;
    private List<Posizione> percorso;

    public GiocoPresenter(MediatorImpl mediator) {
        this.mediator= mediator;
        this.giocoModel = new GiocoModel();
    }

//    public void iniziaNuovaPartita(List<Giocatore> giocatori, String  nomeTabella) {
//        List<Giocatore> giocatoriOrdinati = determinaOrdineGiocatori(giocatori);
//        giocoModel.inizializzaTabella(nomeTabella);
//        mediator.start();
//    }

    public void setTabella(String  nomeTabella){
        giocoModel.inizializzaTabella(nomeTabella);
        mediator.addPlayers();
    }

    public void setGiocatori(List<Giocatore> giocatori){
        List<Giocatore> giocatoriOrdinati=determinaOrdineGiocatori(giocatori);
        giocoModel.inizializzaGiocatori(giocatoriOrdinati);
        mediator.start();
    }

    private List<Giocatore> determinaOrdineGiocatori(List<Giocatore> giocatori) {
        List<Giocatore> ordineGiocatori = new ArrayList<>(giocatori);
        Random random = new Random();

        // Fisher-Yates shuffle
        for (int i = ordineGiocatori.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            // Scambia gli elementi
            Giocatore temp = ordineGiocatori.get(i);
            ordineGiocatori.set(i, ordineGiocatori.get(j));
            ordineGiocatori.set(j, temp);
        }
        return ordineGiocatori;
    }

    public int lanciaDado() {
        int risultato = giocoModel.lanciaDado();
        // Calcola il movimento
        List<Posizione> percorso = giocoModel.calcolaPercorso(risultato);
        return risultato;
    }

    public List<Posizione> muoviGiocatore(){
        return percorso;
    }

    public boolean fineGioco() {
        // Verifica vincitore
        return giocoModel.verificaVincitore();
    }


    public Giocatore getGiocatoreCorrente() {
        return giocoModel.getGiocatoreCorrente();
    }

    public List<Casella> getCaselle() {
        return giocoModel.getCaselle();
    }

    public void cambiaGiocatore() {
        giocoModel.prossimoTurno();
        mediator.notifyTurnChange();
    }
}
