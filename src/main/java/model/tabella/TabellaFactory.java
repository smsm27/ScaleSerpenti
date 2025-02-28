package model.tabella;


import model.casella.Casella;
import model.casella.CasellaFactory;
import model.casella.CasellaFlyweight;
import model.casella.Posizione;

import java.util.ArrayList;
import java.util.List;

public class TabellaFactory {
    private final int nElementiRiga=10;
    private final CasellaFactory casellaFactory;
    private final CasellaFlyweight casellaFlyweight;

    public TabellaFactory(CasellaFlyweight casellaFlyweight) {
        this.casellaFlyweight = casellaFlyweight;
        casellaFactory=new CasellaFactory(casellaFlyweight);
    }


    public List<Casella> creaTabellone(int numeroCaselle) {
    List<Casella> tabellone = new ArrayList<>();
    int totalRows = (numeroCaselle - 1) / nElementiRiga;

    for (int i = 0; i < numeroCaselle; i++) {
        int row = i / nElementiRiga;  // Riga corrente
        int col;

        // Determina la colonna in base alla riga (pari o dispari)
        if (row % 2 == 0) {
            // Righe pari (0, 2, 4...): da sinistra a destra
            col = i % nElementiRiga;
        } else {
            // Righe dispari (1, 3, 5...): da destra a sinistra
            col = nElementiRiga - 1 - (i % nElementiRiga);
        }

        // Inverte le righe per partire dal basso
        double y = 50 + (totalRows - row) * casellaFlyweight.getAltezza();
        double x = 50 + col * casellaFlyweight.getLarghezza();

        if (i == numeroCaselle - 1) {
            tabellone.add(casellaFactory.createCasella(
                    i,
                    new Posizione(x, y),
                    "finale"
            ));
        } else {
            tabellone.add(casellaFactory.createCasella(
                    i,
                    new Posizione(x, y),
                    "normale"
            ));
        }
    }

    return tabellone;  // Ho aggiunto il return che mancava
}

//    public List<Casella> creaTabellone(int numeroCaselle){
//        List<Casella> tabellone = new ArrayList<>();
//        int totalRows = (numeroCaselle - 1) / nElementiRiga;
//        for (int i = 0; i < numeroCaselle; i++) {
//            int row = i / nElementiRiga;  // Righe partendo da 0 (dall'alto)
//            int col = i % nElementiRiga;  // Colonna corrente
//
//            // Inverte le righe per partire dal basso
//            double y = 50 + (totalRows - row) * casellaFlyweight.getAltezza();
//            double x = 50 + col * casellaFlyweight.getLarghezza();
//
//            if(i==numeroCaselle-1){
//                tabellone.add(casellaFactory.createCasella(
//                        i,
//                        new Posizione(x, y),
//                        "finale"
//                ));
//            }else{
//                tabellone.add(casellaFactory.createCasella(
//                        i,
//                        new Posizione(x, y),
//                        "normale"
//                ));
//            }
//
//
//        }
//
//        return tabellone;
//
//    }
}
