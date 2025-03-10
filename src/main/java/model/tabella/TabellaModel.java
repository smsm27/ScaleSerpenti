package model.tabella;

import model.casella.Casella;
import model.casella.CasellaFlyweight;
import tools.SaveLoadTabella;
import tools.TabellaStato;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class TabellaModel {
    private List<Casella> caselle = new ArrayList<>();
    private Map<Integer, Casella> caselleSpeciali = new HashMap<>();

    /**
     * Crea una nuova tabella con le dimensioni e caratteristiche specificate
     */
    public void creaNuovaTabella(int dimensione, int dimX, int dimY, String immagineCasella) {
        // Crea il flyweight per le caselle
        CasellaFlyweight casellaFlyweight = new CasellaFlyweight(dimX, dimY, immagineCasella);

        // Usa TabellaFactory per creare il tabellone
        TabellaFactory factory = new TabellaFactory(casellaFlyweight);
        caselle = factory.creaTabellone(dimensione);

        // Pulisce gli oggetti speciali
        caselleSpeciali.clear();
        caselleSpeciali.put(caselle.getLast().getIndice(),caselle.getLast());
    }


    public boolean aggiungiCasellaSpeciale(String tipo, int partenza){

        // Verifica se esiste già una casella speciale in quella posizione
        if (caselleSpeciali.containsKey(partenza)) {
            return false;
        }
        Casella casellaPartenza = caselle.get(partenza);
        caselleSpeciali.put(partenza, casellaPartenza);
        return true;
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
        if (caselleSpeciali.containsKey(partenza)) {
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
        casellaPartenza.setCasellaState(getTipoCasella(tipo));
        casellaDestinazione.setCasellaState(getTipoCasella("fine_" + tipo));

        // Registra le caselle speciali
        caselleSpeciali.put(partenza, casellaPartenza);
        caselleSpeciali.put(destinazione, casellaDestinazione);

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

     * Utilizzare SaveLoadTabella mi garandisce:
     *      - L'utilizzo di TabellaStato che garantisce:
     *         - Il caricamento OggettiSpeciali con distinzione tra oggetti Complessi(occupano 2 caselle(Scale/Serpenti)) e Speciali (occupano 1 casella(finale))
     *      - il caricamento di singola immagine casella in memoria, riferimento per tutte le caselle
     *
     */
    public boolean caricaTabella(String nomeFile) {
        try {
            TabellaStato stato = SaveLoadTabella.caricaStato(nomeFile);
            if (stato != null) {
                caselle = stato.getCaselle();
                caselleSpeciali = stato.getOggettiSpeciali();
                return true;
            }
            return false;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifica se una casella è speciale
     */
    public boolean isCasellaSpeciale(int indice) {
        return caselleSpeciali.containsKey(indice);
    }

    /**
     * Verifica se una casella è speciale e se è complessa (piu di una casella)
     */
    public boolean isCasellaComplessa(int indice) {
        if(isCasellaSpeciale(indice)){
            return caselleSpeciali.get(indice).getDestinazione() != null;
        }
        return false;

    }

    public boolean isPartOfCasellaComplessa(int indice) {
        Casella casella = caselleSpeciali.get(indice);
        return casella.getCasellaState() == Casella.CasellaState.FINE_SERPENTE ||
                casella.getCasellaState() == Casella.CasellaState.FINE_SCALA;
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
    public Map<Integer, Casella> getCaselleSpeciali() {
        return new HashMap<>(caselleSpeciali);
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
    private Casella.CasellaState getTipoCasella(String tipo) {
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

