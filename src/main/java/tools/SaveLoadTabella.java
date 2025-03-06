package tools;


import lombok.extern.log4j.Log4j2;
import model.casella.Casella;


import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class SaveLoadTabella {

    public static String salvaTabella(String nomeFile, List<Casella> tabella) {
        String userHome = System.getProperty("user.home");
        if (tabella.isEmpty())throw new IllegalStateException("tabella vuota");

        try {
            // Crea directory se non esiste
            File saveDir = new File(userHome, "SerpiEScale/save");
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }

            // Crea oggetto per salvare lo stato
            TabellaStato stato = new TabellaStato();
            log.info("é vuota ?"+tabella.isEmpty());
            stato.setCaselle(tabella);

            // Salva su file
            File saveFile = new File(saveDir, nomeFile + ".ser");
            ObjectOutputStream out = new ObjectOutputStream(
                    new FileOutputStream( saveFile ));
            out.writeObject(stato);
            out.close();

            return "Tabella salvata con successo!";
        } catch (IOException ex) {
            throw new IllegalStateException("Tabella non salvata");
        }
    }

    public static TabellaStatoCaricata caricaStato(String nomeFile) throws IOException, ClassNotFoundException {
        String userHome = System.getProperty("user.home");
        File save = new File(userHome, "SerpiEScale/save/"+ nomeFile +".ser");
        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(save))) {

            tools.TabellaStato stato = (TabellaStato) in.readObject();
            log.info("seivuoto? " +stato.getCaselle().isEmpty() + " primo elemnto: " + stato.getCaselle().getFirst().getIndice());
            List<Casella> caselle = stato.getCaselle();
            if(stato.getCaselle().getFirst().getImmagine()!=null){
                stato.getCaselle().getFirst().getCasellaFlyweight().caricaImmagine();
            }

            Map<Integer, Casella> oggettiSpeciali = new HashMap<>();

            // Mappatura degli oggetti speciali
            for (Casella casella : caselle) {
                log.info(casella.getPosizione().getX() + " " + casella.getPosizione().getY());
                if (casella.getCasellaState() != Casella.CasellaState.NORMALE) {
                    if (casella.getCasellaState() == Casella.CasellaState.SCALA || casella.getCasellaState() == Casella.CasellaState.SERPENTE) {
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
        } catch (Exception e){
            log.error( String.valueOf(e));
            return null;
        }


    }

}
