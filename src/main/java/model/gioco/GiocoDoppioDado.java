package model.gioco;

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
        if(getStatoTurno() == StatoTurno.FINE_TURNO) {
            return 0;
        }

        // Verifica se il giocatore è a sei o meno caselle dalla fine
        Giocatore giocatoreCorrente = getGiocatoreCorrente();
        int indiceAttuale = giocatoreCorrente.getIndiceCurr();
        int indiceFine = tabellaModel.getCaselle().size() - 1;
        int distanzaDallaFine = indiceFine - indiceAttuale;

        // Se il giocatore è a sei o meno caselle dalla fine, usa un solo dado (1-6)
        if (distanzaDallaFine <= 6) {
            risultato = random.nextInt(6) + 1;
        } else {
            // Altrimenti usa il doppio dado (1-12)
            risultato = random.nextInt(12) + 1;
        }

        setStatoTurno(StatoTurno.MOVIMENTO);
        notifyListeners();

        // Se esce 12 (solo possibile con doppio dado) il giocatore può lanciare di nuovo
        if (risultato == 12) {
            setStatoTurno(StatoTurno.IN_ATTESA_LANCIO);
        } else {
            setStatoTurno(StatoTurno.FINE_TURNO);
        }

        return risultato;
    }

    @Override
    public List<Posizione> calcolaPercorso(int spostamento) {
        List<Posizione> percorso = new ArrayList<>();
        Giocatore giocatoreCorrente = getGiocatoreCorrente();
        int indiceAttuale = giocatoreCorrente.getIndiceCurr();

        // Ottieni la lista delle caselle e il relativo indice finale
        List<Casella> caselle = tabellaModel.getCaselle();
        int indiceFine = caselle.size() - 1;

        // Calcola l'indice potenziale prima di considerare il rimbalzo
        int nuovoIndicePotenziale = indiceAttuale + spostamento;
        int nuovoIndice = nuovoIndicePotenziale;

        boolean rimbalzo = false;

        // Gestione del superamento della fine
        if (nuovoIndicePotenziale > indiceFine) {
            // Calcola la quantità di caselle in eccesso
            int eccesso = nuovoIndicePotenziale - indiceFine;
            // La nuova posizione sarà: fine - eccesso
            nuovoIndice = indiceFine - eccesso;
            rimbalzo = true;
        }

        // Verifica che l'indice non sia negativo (caso estremo)
        if (nuovoIndice < 0) {
            nuovoIndice = 0;
        }

        // Genera il percorso della pedina
        if (!rimbalzo) {
            // Caso normale: movimento in avanti
            for (int i = indiceAttuale + 1; i <= nuovoIndice && i < caselle.size(); i++) {
                percorso.add(caselle.get(i).getPosizione());
            }
        } else {
            // Caso di rimbalzo: avanza fino alla fine e poi torna indietro

            // Prima avanza fino alla fine
            for (int i = indiceAttuale + 1; i <= indiceFine; i++) {
                percorso.add(caselle.get(i).getPosizione());
            }

            // Poi retrocede fino alla posizione finale
            for (int i = indiceFine - 1; i >= nuovoIndice && i >= 0; i--) {
                percorso.add(caselle.get(i).getPosizione());
            }
        }

        // Gestione di eventuali scale o serpenti usando il TabellaModel
        if (nuovoIndice >= 0 && nuovoIndice < caselle.size() && tabellaModel.isCasellaComplessa(nuovoIndice)) {
            Casella casellaDiArrivo = tabellaModel.getCasella(nuovoIndice);
            Casella casellaDestinazione = casellaDiArrivo.getDestinazione();

            if (casellaDestinazione != null) {
                int indiceFinaleCasella = caselle.indexOf(casellaDestinazione);

                if (indiceFinaleCasella >= 0) {
                    percorso.add(casellaDestinazione.getPosizione());
                    nuovoIndice = indiceFinaleCasella;
                }
            }
        }

        // Aggiorna la posizione del giocatore con controlli di sicurezza
        if (nuovoIndice >= 0 && nuovoIndice < caselle.size()) {
            giocatoreCorrente.setPosizione(tabellaModel.getCasella(nuovoIndice).getPosizione());
            giocatoreCorrente.setIndiceCurr(nuovoIndice);
        }

        return percorso;
    }
}
