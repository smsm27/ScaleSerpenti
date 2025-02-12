package tools;


import model.casella.Casella;


import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaveLoadTabella {

    public static String salvaTabella(String nomeFile, List<Casella> tabella) {
        if (tabella.isEmpty())throw new IllegalStateException("tabella vuota");

        try {
            // Crea directory se non esiste
            File directory = new File("src/main/save");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Crea oggetto per salvare lo stato
            TabellaStato stato = new TabellaStato();
            stato.setCaselle(tabella);

            // Salva su file
            ObjectOutputStream out = new ObjectOutputStream(
                    new FileOutputStream("src/main/save/" + nomeFile + ".ser"));
            out.writeObject(stato);
            out.close();

            return "Tabella salvata con successo!";
        } catch (IOException ex) {
            throw new IllegalStateException("Tabella non salvata");
        }
    }

    public static TabellaStatoCaricata caricaStato(String nomeFile) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream("src/main/save/" + nomeFile + ".ser"))) {

            tools.TabellaStato stato = (TabellaStato) in.readObject();
            List<Casella> caselle = stato.getCaselle();
            Map<Integer, Casella> oggettiSpeciali = new HashMap<>();

            // Mappatura degli oggetti speciali
            for (Casella casella : caselle) {
                if (casella.getCasellaState() != Casella.CasellaState.NORMALE) {
                    if(casella.getCasellaState() == Casella.CasellaState.SCALA || casella.getCasellaState() == Casella.CasellaState.SERPENTE) {
                        if (casella.getDestinazione().getIndice() == null) {
                            throw new IllegalStateException("Oggetto speciale senza destinazione");
                        }
                    }
                    // Creare l'oggetto speciale appropriato basato sullo stato
                    oggettiSpeciali.put(casella.getIndice(),
                            casella);
                }
            }

            return new TabellaStatoCaricata(caselle, oggettiSpeciali);
        }
    }

}
