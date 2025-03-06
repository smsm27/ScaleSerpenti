package model.gioco.giocoManager;

import model.casella.Casella;
import model.casella.Posizione;
import model.giocatore.Giocatore;

import java.util.ArrayList;
import java.util.List;

public class GiocoDoppioDado extends AbstractGiocoModel{


    public GiocoDoppioDado(String nomeMappa, List<Giocatore> giocatoriNonOrdinati) {
        super(nomeMappa, giocatoriNonOrdinati);
    }

    @Override
    public int lanciaDado() {
        if(getStatoTurno()==StatoTurno.FINE_TURNO){
            return 0;
        }
        setStatoTurno(StatoTurno.MOVIMENTO);
        risultato = random.nextInt(12) + 1;
        if (risultato == 12){
            setStatoTurno(StatoTurno.IN_ATTESA_LANCIO);
        }else{
            setStatoTurno(StatoTurno.FINE_TURNO);
        }
        return risultato;
    }

    @Override
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
        if (tabellaModel.isCasellaComplessa(nuovoIndice)) {
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
}
