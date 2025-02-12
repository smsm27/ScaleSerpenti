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

    public List<Casella> creaTabellone(int numeroCaselle){
        List<Casella> tabellone = new ArrayList<>();
        int totalRows = (numeroCaselle - 1) / nElementiRiga;
        for (int i = 0; i < numeroCaselle; i++) {
            int row = i / nElementiRiga;  // Righe partendo da 0 (dall'alto)
            int col = i % nElementiRiga;  // Colonna corrente

            // Inverte le righe per partire dal basso
            double y = 50 + (totalRows - row) * casellaFlyweight.getAltezza();
            double x = 50 + col * casellaFlyweight.getLarghezza();

            if(i==numeroCaselle-1){
                tabellone.add(casellaFactory.createCasella(
                        i,
                        new Posizione(x, y),
                        "finale"
                ));
            }else{
                tabellone.add(casellaFactory.createCasella(
                        i,
                        new Posizione(x, y),
                        "normale"
                ));
            }


        }

        return tabellone;

    }
}
