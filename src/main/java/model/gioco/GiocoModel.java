package model.gioco;

import model.casella.Posizione;
import model.giocatore.Giocatore;
import model.tabella.TabellaModel;
import model.casella.Casella;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GiocoModel {
    private List<Giocatore> giocatori;
    private TabellaModel tabellaModel;
    private int giocatoreCorrenteIndex;
    private Random random;

    public GiocoModel() {
        this.random = new Random();
        this.giocatoreCorrenteIndex = 0;
        this.tabellaModel = new TabellaModel();
    }

    /**
     * Inizializza il gioco caricando una tabella esistente
     */
    public boolean inizializzaTabella( String nomeFile) {
        return tabellaModel.caricaTabella(nomeFile);

    }

    public boolean inizializzaGiocatori(List<Giocatore> giocatori) {
            this.giocatori = giocatori;
            this.giocatoreCorrenteIndex = 0;

            // Posiziona tutti i giocatori sulla casella iniziale
            for (Giocatore giocatore : giocatori) {
                giocatore.setPosizione(tabellaModel.getCaselle().getFirst().getPosizione());
                giocatore.setIndiceCurr(0);
            }
            return true;
    }


    public Giocatore getGiocatoreCorrente() {
        return giocatori.get(giocatoreCorrenteIndex);
    }

    public int lanciaDado() {
        // Genera un numero casuale tra 1 e 6
        return random.nextInt(6) + 1;
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

        // Verifica se il giocatore è arrivato all'ultima casella
        return posizione == caselle.size() - 1;
    }

    public void prossimoTurno() {
        giocatoreCorrenteIndex = (giocatoreCorrenteIndex + 1) % giocatori.size();
    }

    public List<Casella> getCaselle() {
        return tabellaModel.getCaselle();
    }


}
