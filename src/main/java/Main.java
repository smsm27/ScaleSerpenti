



import view.swing.SchermataInizialeSwing;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SchermataInizialeSwing.getInstance().inizializza();
        });
    }
}
