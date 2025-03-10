package tools;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import model.casella.Casella;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Getter
@Setter
public class TabellaStato implements Serializable {
    private List<Casella> caselle = new ArrayList<>();
    private Map<Integer, Casella> oggettiSpeciali = new HashMap<>();

    // Metodi per ricostruire la mappa degli oggetti speciali durante il caricamento
    public void ricostruisciOggettiSpeciali() {
        oggettiSpeciali.clear();
        for (Casella casella : caselle) {
            log.info("{} {}", casella.getPosizione().getX(), casella.getPosizione().getY());
            if (casella.getCasellaState() != Casella.CasellaState.NORMALE) {
                if (casella.getCasellaState() == Casella.CasellaState.SCALA || casella.getCasellaState() == Casella.CasellaState.SERPENTE) {
                    if (casella.getDestinazione().getIndice() == null) {
                        throw new IllegalStateException("Oggetto speciale senza destinazione");
                    }
                }
                // Creare l'oggetto speciale appropriato basato sullo stato
                oggettiSpeciali.put(casella.getIndice(),
                        casella);
            }
        }
    }
}