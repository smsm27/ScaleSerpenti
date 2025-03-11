import controller.gioco.Mediator;
import controller.gioco.MediatorImpl;
import model.casella.Posizione;
import model.giocatore.Giocatore;
import model.gioco.AbstractGiocoModel;
import model.gioco.GiocoBaseModel;
import model.gioco.GiocoDoppioDado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import view.swing.schermate.gioco.SchermataGiocaSwingManuale;
import view.swing.schermate.logPartita.GameInfoSwing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
@EnabledIfEnvironmentVariable(named = "HEADLESS", matches = "false")
class GiocoBaseModelTest {
    private TestableSchermataGioco testableSchermataGioco;
    private TestableGiocoBaseModel giocoModel;


    // Classe di test che estende GiocoBaseModel per controllare il lancio del dado
    static class TestableGiocoBaseModel extends GiocoDoppioDado {
        private int risultatoPredefinito = -1;

        public TestableGiocoBaseModel(String nomeMappa, List<Giocatore> giocatoriNonOrdinati) {
            super(nomeMappa, giocatoriNonOrdinati);
        }

        // Metodo per impostare il risultato del lancio
        public void setRisultatoDado(int risultato) {
            this.risultatoPredefinito = risultato;
        }

        // Override del metodo lanciaDado per usare il risultato predefinito
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

            setStatoTurno(StatoTurno.MOVIMENTO);

            // Usa il risultato predefinito se impostato, altrimenti comportamento normale
            if (risultatoPredefinito != -1) {
                risultato = risultatoPredefinito;
            } else {
                risultato = random.nextInt(6) + 1;
            }
            setStatoTurno(StatoTurno.MOVIMENTO);
            notifyListeners();

            // Se esce 12 (solo possibile con doppio dado) il giocatore può lanciare di nuovo
            if (risultato == 12) {
                setRisultatoDado(6);
                setStatoTurno(StatoTurno.IN_ATTESA_LANCIO);
            } else {
                setStatoTurno(StatoTurno.FINE_TURNO);
            }

            return risultato;
        }
    }
    static class TestableSchermataGioco extends SchermataGiocaSwingManuale {
        TestableGiocoBaseModel giocoModel;

        public Component getComponentePrincipale() {
            // Restituisci il componente root della schermata
            return this.frame.getContentPane().getParent(); // Modifica in base alla struttura reale
        }

        @Override
        public String getNomeTabella() {
            return "tabella_standard"; // Nome mappa valido
        }

        @Override
        protected void handleGiocatoreCambio() {
            extracted();
            this.lanciaDadoButton.setEnabled(true);
        }

        private void extracted() {
            JPanel inputPanel = new JPanel();
            inputPanel.setLayout(new GridLayout(2, 2, 5, 5));

            JTextField nomeField = new JTextField(10);
            inputPanel.add(new JLabel("valore dado: "));
            inputPanel.add(nomeField);

            int result = JOptionPane.showConfirmDialog(frame,
                    inputPanel,
                    "Inserisci valore dado",
                    JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                giocoModel.setRisultatoDado(Integer.parseInt(nomeField.getText()));
            }
        }

        @Override
        public List<Giocatore> getInfoPlayers() {
            // Restituisci una lista di giocatori fake
            return List.of(new Giocatore("Giocatore1", Color.WHITE), new Giocatore("Giocatore2", Color.red));
        }

        @Override
        public void inizializza() {
            initComponents();

            String nomeMappa = getNomeTabella();


            if ( nomeMappa==null || nomeMappa.isEmpty()) {
                gestisciErrore();
                return;
            }

            List<Giocatore> giocatori = getInfoPlayers();

            if( giocatori.isEmpty()) {
                gestisciErrore();
                return;
            }

            giocoModel = new TestableGiocoBaseModel(nomeMappa, giocatori);

            mediator.registerGameManager(giocoModel);
            mediator.registerView(this);

            gameInfoPanel.setGiocoModel(giocoModel);
            giocoModel.addListener(gameInfoPanel);



            mediator.start();
        }

        public AbstractGiocoModel getGiocoModel() {
            return giocoModel;
        }
    }

    @BeforeEach
    void setUp() {

        testableSchermataGioco=new TestableSchermataGioco();
        testableSchermataGioco.inizializza();
        giocoModel = (TestableGiocoBaseModel) testableSchermataGioco.getGiocoModel();
        testableSchermataGioco.extracted();
    }


    @Test
    void testGiocoInterattivo() {
        // Ottieni il JFrame dalla schermata (supponendo che la superclasse lo gestisca)
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(testableSchermataGioco.getComponentePrincipale());

        if (frame == null) {
            throw new IllegalStateException("Nessun JFrame associato alla schermata!");
        }

        // Configura il frame
        frame.setTitle("Test Interattivo");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Aggiungi listener per tracciare la chiusura
        final CountDownLatch latch = new CountDownLatch(1);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                latch.countDown();
            }
        });

        // Mostra il frame
        SwingUtilities.invokeLater(() -> {
            frame.setVisible(true);
            frame.toFront();
        });

        // Attendi la chiusura
        try {
            latch.await(); // Blocca finché la finestra non viene chiusa
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}