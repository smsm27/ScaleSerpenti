package model.tabella;

import model.casella.Casella;
import model.casella.CasellaFlyweight;
import tools.SaveLoadTabella;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Modello che gestisce i dati e la logica della tabella di gioco.
 * Parte del pattern MVP, contiene tutta la logica di business relativa
 * alla tabella e alle caselle.
 */
public class TabellaModel {
    private List<Casella> caselle = new ArrayList<>();
    private Map<Integer, Casella> oggettiSpeciali = new HashMap<>();
    private CasellaFlyweight casellaFlyweight;

    /**
     * Crea una nuova tabella con le dimensioni e caratteristiche specificate
     */
    public void creaNuovaTabella(int dimensione, int dimX, int dimY, String immagineCasella) {
        // Crea il flyweight per le caselle
        casellaFlyweight = new CasellaFlyweight(dimX, dimY, immagineCasella);

        // Usa TabellaFactory per creare il tabellone
        TabellaFactory factory = new TabellaFactory(casellaFlyweight);
        caselle = factory.creaTabellone(dimensione);

        // Pulisce gli oggetti speciali
        oggettiSpeciali.clear();
    }

    /**
     * Aggiunge una casella speciale (serpente o scala) alla tabella
     * @return true se l'operazione ha avuto successo, false altrimenti
     */
    public boolean aggiungiCasellaSpeciale(String tipo, int partenza, int destinazione) {
        // Verifica validità degli indici
        if (!validaIndici(partenza, destinazione)) {
            return false;
        }

        // Verifica se esiste già una casella speciale in quella posizione
        if (oggettiSpeciali.containsKey(partenza)) {
            return false;
        }

        // Verifica la validità del tipo e della direzione
        if (!validaTipoOggettoComplesso(tipo, partenza, destinazione)) {
            return false;
        }

        // Recupera le caselle coinvolte
        Casella casellaPartenza = caselle.get(partenza);
        Casella casellaDestinazione = caselle.get(destinazione);

        // Imposta lo stato delle caselle
        casellaPartenza.setDestinazione(casellaDestinazione);
        casellaPartenza.setCasellaState(getTipoStateComplesso(tipo));
        casellaDestinazione.setCasellaState(getTipoStateComplesso("fine_" + tipo));

        // Registra le caselle speciali
        oggettiSpeciali.put(partenza, casellaPartenza);
        oggettiSpeciali.put(destinazione, casellaDestinazione);

        return true;
    }

    /**
     * Salva lo stato corrente della tabella
     * @return messaggio di conferma o errore
     */
    public String salvaTabella(String nomeFile) {
        return SaveLoadTabella.salvaTabella(nomeFile, caselle);
    }

    /**
     * Carica una tabella da file
     * @return true se il caricamento ha avuto successo
     */
    public boolean caricaTabella(String nomeFile) {
        try {
            var stato = SaveLoadTabella.caricaStato(nomeFile);
            caselle = stato.getCaselle();
            oggettiSpeciali = stato.getOggettiSpeciali();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifica se una casella è speciale
     */
    public boolean isCasellaSpeciale(int indice) {
        return oggettiSpeciali.containsKey(indice);
    }

    /**
     * Verifica se una casella è speciale e se è complesso (piu di una casella)
     */
    public boolean isCasellaSpecialeComplessa(int indice) {
        if(isCasellaSpeciale(indice)){
            return oggettiSpeciali.get(indice).getDestinazione() != null;
        }
        return false;

    }


    /**
     * Ottiene una casella specifica dall'indice
     */
    public Casella getCasella(int indice) {
        if (indice >= 0 && indice < caselle.size()) {
            return caselle.get(indice);
        }
        return null;
    }

    /**
     * Ottiene tutte le caselle della tabella
     */
    public List<Casella> getCaselle() {
        return new ArrayList<>(caselle);
    }

    /**
     * Ottiene tutte le caselle speciali
     */
    public Map<Integer, Casella> getOggettiSpeciali() {
        return new HashMap<>(oggettiSpeciali);
    }

    /**
     * Verifica se gli indici sono validi
     */
    private boolean validaIndici(int partenza, int destinazione) {
        return !caselle.isEmpty() &&
                partenza >= 0 && partenza < caselle.size() &&
                destinazione >= 0 && destinazione < caselle.size();
    }

    /**
     * Verifica se il tipo di oggetto è valido per la direzione indicata
     */
    private boolean validaTipoOggettoComplesso(String tipo, int partenza, int destinazione) {
        if (tipo.equalsIgnoreCase("serpente") || tipo.equalsIgnoreCase("scala")) {
            return (partenza > destinazione && tipo.equalsIgnoreCase("serpente")) ||
                    (destinazione > partenza && tipo.equalsIgnoreCase("scala"));
        }
        return true;
    }

    /**
     * Converte una stringa nel corrispondente stato della casella
     */
    private Casella.CasellaState getTipoStateComplesso(String tipo) {
        return switch (tipo.toLowerCase()) {
            case "serpente" -> Casella.CasellaState.SERPENTE;
            case "scala" -> Casella.CasellaState.SCALA;
            case "fine_serpente" -> Casella.CasellaState.FINE_SERPENTE;
            case "fine_scala" -> Casella.CasellaState.FINE_SCALA;
            case "finale" -> Casella.CasellaState.FINALE;
            default -> Casella.CasellaState.NORMALE;
        };
    }
}

