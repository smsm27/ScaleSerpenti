package view.swing;

import view.interfacce.schermata.Schermata;

public class SchermataGiocaFactory {


        public enum GameMode {
            MANUAL,
            AUTOMATIC,
            START
        }

        public static Schermata createSchermataGioca(GameMode mode) {
            return switch (mode) {
                case AUTOMATIC -> new SchermataGiocaAutomaticoSwing();
                case MANUAL -> new SchermataGiocaSwingManuale();

                default -> null;
            };
        }
}

