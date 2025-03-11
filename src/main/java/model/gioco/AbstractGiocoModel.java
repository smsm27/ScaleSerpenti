package model.gioco;

import lombok.Getter;
import lombok.Setter;
import model.casella.Casella;
import model.casella.Posizione;
import model.giocatore.Giocatore;
import model.tabella.TabellaModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class AbstractGiocoModel {

    public enum StatoTurno {
        IN_ATTESA_LANCIO,   // In attesa che il giocatore lanci i dadi
        MOVIMENTO,          // Giocatore sta muovendo il suo pedone
        AZIONE_CASELLA,     // Gestione delle azioni speciali della casella
        FINE_TURNO,// Turno completato, pronto per passare al prossimo giocatore
        GIOCO_FINITO
    }

    // Interfaccia per i listeners
    public interface GiocoListener {
        void onGiocoUpdated();
    }

    @Getter
    protected List<Giocatore> giocatori;
    @Getter
    @Setter
    protected TabellaModel tabellaModel;
    protected int giocatoreCorrenteIndex;
    protected Random random;
    @Getter
    @Setter
    protected StatoTurno statoTurno;
    @Getter
    protected int risultato;



    private List<GiocoListener> listeners = new ArrayList<>();

    // Metodi per gestire i listeners
    public void addListener(GiocoListener listener) {
        listeners.add(listener);
    }

    public void removeListener(GiocoListener listener) {
        listeners.remove(listener);
    }

    // Metodo per notificare i listeners
    protected void notifyListeners() {
        for (GiocoListener listener : listeners) {
            listener.onGiocoUpdated();
        }
    }

    public void giocatoreFermo() {
        setStatoTurno(StatoTurno.FINE_TURNO);
        notifyListeners();
    }

    public AbstractGiocoModel(String nomeMappa, List<Giocatore> giocatoriNonOrdinati) {
        this.random = new Random();
        this.giocatoreCorrenteIndex = 0;
        this.tabellaModel = new TabellaModel();
        tabellaModel.caricaTabella(nomeMappa);
        this.giocatori=determinaOrdineGiocatori(giocatoriNonOrdinati);
        for(Giocatore g : giocatori){
            g.setIndiceCurr(0);
            g.setPosizione(tabellaModel.getCaselle().getFirst().getPosizione());
        }
        this.statoTurno = StatoTurno.IN_ATTESA_LANCIO;
        notifyListeners();
    }

    public Giocatore getGiocatoreCorrente() {
        return giocatori.get(giocatoreCorrenteIndex);
    }

    public abstract int lanciaDado();

    public abstract List<Posizione> calcolaPercorso(int spostamento);

    public boolean verificaVincitore() {
        Giocatore giocatoreCorrente = getGiocatoreCorrente();
        int posizione = giocatoreCorrente.getIndiceCurr();
        List<Casella> caselle = tabellaModel.getCaselle();
        if (posizione == caselle.size() - 1) {
            setStatoTurno(StatoTurno.GIOCO_FINITO);
            notifyListeners();
        }

        // Verifica se il giocatore è arrivato all'ultima casella
        return posizione == caselle.size() - 1;
    }

    public void prossimoTurno() {
        giocatoreCorrenteIndex = (giocatoreCorrenteIndex + 1) % giocatori.size();
        setStatoTurno(StatoTurno.IN_ATTESA_LANCIO);
        notifyListeners();
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

    public StatoTurno getStato(){
        return statoTurno;
    }

}
