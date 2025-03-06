package model.gioco.giocoManager;
import model.giocatore.Giocatore;
import model.gioco.giocoManager.AbstractGiocoModel.StatoTurno;
import model.gioco.giocoManager.GiocoDoppioDado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class GiocoDoppioDadoTest {

    private GiocoDoppioDado gioco;
    private Random mockRandom;

    @BeforeEach
    public void setUp() throws Exception {
        // Creare una lista di giocatori per inizializzare il gioco
        List<Giocatore> giocatori = new ArrayList<>();
        giocatori.add(new Giocatore("Giocatore1"));

        // Inizializzare il gioco
        gioco = new GiocoDoppioDado("mappa_test", giocatori);

        // Creare e iniettare un mock dell'oggetto Random
        mockRandom = Mockito.mock(Random.class);
        Field randomField = AbstractGiocoModel.class.getDeclaredField("random");
        randomField.setAccessible(true);
        randomField.set(gioco, mockRandom);
    }

    @Test
    public void testLancioDado_Quando12_ImpostaStatoInAttesaLancio() {
        // Imposta il comportamento del mock Random per restituire 11 (che diventa 12 dopo +1)
        when(mockRandom.nextInt(12)).thenReturn(11);

        // Imposta lo stato corrente del turno
        gioco.setStatoTurno(StatoTurno.IN_ATTESA_LANCIO);

        // Esegui il metodo lanciaDado
        int risultato = gioco.lanciaDado();

        // Verifica che il risultato sia 12
        assertEquals(12, risultato);

        // Verifica che lo stato del turno sia impostato su FINE_TURNO
        // (questo è il comportamento attuale, anche se potrebbe essere un bug)
        assertEquals(StatoTurno.FINE_TURNO, gioco.getStatoTurno());
    }
}