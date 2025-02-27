package model.gioco;

import lombok.Getter;
import lombok.Setter;
import model.casella.Posizione;
import model.giocatore.Giocatore;
import model.gioco.mediator.MediatorImpl;
import model.tabella.TabellaModel;
import model.casella.Casella;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GiocoModel {



    public enum StatoTurno {
        IN_ATTESA_LANCIO,   // In attesa che il giocatore lanci i dadi
        MOVIMENTO,          // Giocatore sta muovendo il suo pedone
        AZIONE_CASELLA,     // Gestione delle azioni speciali della casella
        FINE_TURNO,// Turno completato, pronto per passare al prossimo giocatore
        GIOCO_FINITO
    }

    // Interfaccia per i listeners
    public interface GiocoListener {
        void onGiocoUpdated(StatoTurno statoTurno);
    }

    @Getter
    private List<Giocatore> giocatori;
    @Getter
    @Setter
    private TabellaModel tabellaModel;
    private int giocatoreCorrenteIndex;
    private Random random;

    @Getter
    @Setter
    private StatoTurno statoTurno;
    @Getter
    int risultato;

    private List<Posizione> posizioni;
    private List<GiocoListener> listeners = new ArrayList<>();

    // Metodi per gestire i listeners
    public void addListener(GiocoListener listener) {
        listeners.add(listener);
    }

    public void removeListener(GiocoListener listener) {
        listeners.remove(listener);
    }

    // Metodo per notificare i listeners
    private void notifyListeners() {
        for (GiocoListener listener : listeners) {
            listener.onGiocoUpdated(statoTurno);
        }
    }

    public void giocatoreFermo() {
        setStatoTurno(StatoTurno.FINE_TURNO);
        notifyListeners();
    }



    public GiocoModel(String nomeMappa,List<Giocatore> giocatoriNonOrdinati) {
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

    public int lanciaDado() {
        setStatoTurno(StatoTurno.MOVIMENTO);
        risultato = random.nextInt(6) + 1;
        notifyListeners();
        // Genera un numero casuale tra 1 e 6
        return risultato;
    }

    public List<Posizione> calcolaPercorso(int spostamento) {
        List<Posizione> percorso = new ArrayList<>();
        Giocatore giocatoreCorrente = getGiocatoreCorrente();
        int indiceAttuale = giocatoreCorrente.getIndiceCurr();
        int nuovoIndice = indiceAttuale + spostamento;

        List<Casella> caselle = tabellaModel.getCaselle();

        // Verifica che la nuova posizione non superi la fine della tabella
        if (nuovoIndice >= caselle.size()) {
            nuovoIndice = caselle.size() - 1;
        }

        // Genera il percorso della pedina
        for (int i = indiceAttuale + 1; i <= nuovoIndice; i++) {
            percorso.add(caselle.get(i).getPosizione());
        }

        // Gestione di eventuali scale o serpenti usando il TabellaModel
        if (tabellaModel.isCasellaSpecialeComplessa(nuovoIndice)) {
            setStatoTurno(StatoTurno.AZIONE_CASELLA);
            Casella casellaDiArrivo = tabellaModel.getCasella(nuovoIndice);
            Casella casellaDestinazione = casellaDiArrivo.getDestinazione();
            int indiceFinaleCasella = caselle.indexOf(casellaDestinazione);

            percorso.add(casellaDestinazione.getPosizione());
            nuovoIndice = indiceFinaleCasella;
        }

        // Aggiorna la posizione del giocatore
        giocatoreCorrente.setPosizione(tabellaModel.getCasella(nuovoIndice).getPosizione());
        giocatoreCorrente.setIndiceCurr(nuovoIndice);

        return percorso;
    }

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

}
