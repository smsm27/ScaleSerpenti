package view.swing.schermate.gioco;

import view.interfacce.schermata.Schermata;

public class SchermataGiocaFactory {


        public enum GameMode {
            MANUAL,
            AUTOMATIC,
        }

        public static Schermata createSchermataGioca(GameMode mode) {
            return switch (mode) {
                case AUTOMATIC -> new SchermataGiocaAutomaticoSwing();
                case MANUAL -> new SchermataGiocaSwingManuale();

                default -> null;
            };
        }
}

