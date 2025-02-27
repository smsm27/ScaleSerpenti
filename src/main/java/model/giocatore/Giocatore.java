package model.giocatore;

import lombok.Getter;
import lombok.Setter;
import model.casella.Posizione;


import java.awt.*;

@Setter
@Getter
public class Giocatore {
    private String nome;
    private Posizione posizione;
    private int indiceCurr;
    private GiocatoreStato stato;
    private Color color;


    public Giocatore(String nome, Color color) {
        this.nome = nome;
        this.color = color;
        this.stato= GiocatoreStato.ATTIVO;
        this.indiceCurr=0;
    }


    public enum GiocatoreStato {
        FERMO,
        ATTIVO,
        IN_ATTESA
    }

    public void muoviGiocatore(Posizione destinazione ){
        this.posizione=destinazione;
    }

}
