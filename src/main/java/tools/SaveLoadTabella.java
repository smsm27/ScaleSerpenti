package tools;


import lombok.extern.log4j.Log4j2;
import model.casella.Casella;


import java.io.*;
import java.util.List;


@Log4j2
public class SaveLoadTabella {

    private static final String PREDEFINED_TABLES_PATH = "/save/";
    private static final String USER_SAVE_DIR = "SerpiEScale/save";

    public static String salvaTabella(String nomeFile, List<Casella> tabella) {
        String userHome = System.getProperty("user.home");
        if (tabella.isEmpty())throw new IllegalStateException("tabella vuota");

        try {
            // Crea directory se non esiste
            File saveDir = new File(userHome, USER_SAVE_DIR);
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

    /**
     * Carica una tabella, cercando prima nelle tabelle dell'utente
     * e poi nelle tabelle predefinite delle resources
     */
    public static TabellaStato caricaStato(String nomeFile) {
        // Prima prova a caricare dalla cartella dell'utente
        TabellaStato stato = caricaStatoDaCartella(nomeFile);

        // Se non trovato, prova a caricare dalle tabelle predefinite
        if (stato == null) {
            stato = caricaStatoDaResources(nomeFile);
        }

        return stato;
    }

    /**
     * Carica una tabella dalla cartella dell'utente
     * */
    private static TabellaStato caricaStatoDaCartella(String nomeFile) {
        String userHome = System.getProperty("user.home");
        File save = new File(userHome, USER_SAVE_DIR + "/" + nomeFile + ".ser");

        if (!save.exists()) {
            return null;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(save))) {
            TabellaStato stato = (TabellaStato) in.readObject();

            log.info("seivuoto? " + stato.getCaselle().isEmpty() +
                    " primo elemento: " + stato.getCaselle().getFirst().getIndice());

            if (stato.getCaselle().getFirst().getImmagine() != null) {
                stato.getCaselle().getFirst().getCasellaFlyweight().caricaImmagine();
            }

            stato.ricostruisciOggettiSpeciali();

            return stato;
        } catch (Exception e) {
            log.error("Errore nel caricamento da cartella: ", e);
            return null;
        }
    }

    /**
     * Carica una tabella predefinita dalle resources del progetto
     */
    private static TabellaStato caricaStatoDaResources(String nomeFile) {
        try {
            String resourcePath = PREDEFINED_TABLES_PATH + nomeFile + ".ser";
            InputStream inputStream = SaveLoadTabella.class.getResourceAsStream(resourcePath);

            if (inputStream == null) {
                log.warn("Tabella predefinita non trovata: " + resourcePath);
                return null;
            }

            try (ObjectInputStream in = new ObjectInputStream(inputStream)) {
                TabellaStato stato = (TabellaStato) in.readObject();

                log.info("Tabella caricata da resources: " + nomeFile);

                if (stato.getCaselle().getFirst().getImmagine() != null) {
                    stato.getCaselle().getFirst().getCasellaFlyweight().caricaImmagine();
                }

                stato.ricostruisciOggettiSpeciali();

                return stato;
            }
        } catch (Exception e) {
            log.error("Errore nel caricamento da resources: ", e);
            return null;
        }
    }

    public static boolean esisteTabellaPredefinta(String nomeFile) {
        String resourcePath = PREDEFINED_TABLES_PATH + nomeFile + ".ser";
        log.info(PREDEFINED_TABLES_PATH + nomeFile + ".ser");
        return SaveLoadTabella.class.getResourceAsStream(resourcePath) != null;
    }


}
