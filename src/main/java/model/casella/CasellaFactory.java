package model.casella;

import lombok.Getter;


@Getter
public class CasellaFactory {
    private final CasellaFlyweight casellaFlyweight;


    public CasellaFactory(CasellaFlyweight casellaFlyweight) {
        this.casellaFlyweight = casellaFlyweight;

    }
    public Casella createCasella(int indice, Posizione point, String tipo) {
        Casella casella = new Casella(casellaFlyweight, indice, point);
        switch(tipo.toLowerCase()) {
            case "serpente":
                casella.setCasellaState(Casella.CasellaState.SERPENTE);
                break;
            case "scala":
                casella.setCasellaState(Casella.CasellaState.SCALA);
                break;
            case "finale":
                casella.setCasellaState(Casella.CasellaState.FINALE);
                break;
            default:
                casella.setCasellaState(Casella.CasellaState.NORMALE);
        }
        return casella;
    }
}
