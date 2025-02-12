package tools;

import lombok.Getter;
import lombok.Setter;
import model.casella.Casella;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class TabellaStatoCaricata {
    private final List<Casella> caselle;
    private final Map<Integer, Casella> oggettiSpeciali;

    public TabellaStatoCaricata(List<Casella> caselle, Map<Integer, Casella> oggettiSpeciali) {
        this.caselle = caselle;
        this.oggettiSpeciali = oggettiSpeciali;
    }
}
