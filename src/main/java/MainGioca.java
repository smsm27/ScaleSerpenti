import tools.Colori;
import view.interfacce.schermata.SchermataGioco;
import view.swing.SchermataGiocaSwing;

public class MainGioca {
    public static void main(String[] args) {
        SchermataGioco schermataGioco =new SchermataGiocaSwing();
        schermataGioco.inizializza();
    }
}
