package it.unisa.musicplayer.modello;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;
import java.util.ArrayList;
import java.util.Optional;
import it.unisa.musicplayer.servizi.GestoreFile;

/**
 * Catalogo delle tracce musicali dell'applicazione.
 * Nome della classe allineato all'UML (CatalogoTracce)[cite: 17, 18, 19].
 */
public class Catalogo {

    // ── Singleton ─────────────────────────────────────────────────────────────

    private static Catalogo instance;

    /** Costruttore privato: attiva l'ascoltatore di salvataggio automatico */
    private Catalogo() {
        this.tracce = FXCollections.observableArrayList();

        // AUTOMAZIONE TOTALE: Qualsiasi modifica alla lista salva le canzoni sul Desktop
        this.tracce.addListener((ListChangeListener<Traccia>) change -> {
            eseguiSalvataggioAutomatico();
        });
    }

    public static Catalogo getInstance() {
        if (instance == null) {
            instance = new Catalogo();
        }
        return instance;
    }

    // ── Stato ─────────────────────────────────────────────────────────────────

    private final ObservableList<Traccia> tracce;

    /**
     * Confeziona le canzoni correnti dentro il Wrapper e lancia l'esportazione.
     */
    public void eseguiSalvataggioAutomatico() {
        DatiApplicazione pacchettoDati = DatiApplicazione.costruisci(
            new ArrayList<>(this.tracce),
            new ArrayList<>(CatalogoPlaylist.getInstance().getPlaylists())
        );
        GestoreFile.esporta(pacchettoDati);
    }

    //Carica i dati dal file JSON all'avvio dell'applicazione.
    public void caricaDaFile() {
        DatiApplicazione dati = GestoreFile.importa();
        if (dati.getCanzoni() != null && !dati.getCanzoni().isEmpty()) {
            tracce.addAll(dati.getCanzoni());
        }
    }

    // ── Getter ────────────────────────────────────────────────────────────────

    public ObservableList<Traccia> getTracce() {
        return tracce;
    }

    // ── CRUD (Focalizzato su Aggiunta e Modifica) ─────────────────────────────

    /**
     * FLUSSO DI AGGIUNTA
     * Aggiunge una traccia al catalogo verificando preventivamente i duplicati.
     */
    public void aggiungiTraccia(Traccia t) {
        if (t == null) {
            throw new IllegalArgumentException("La traccia non può essere null");
        }
        // Utilizza il metodo esisteDuplicato come richiesto dall'UML [cite: 73]
        if (contiene(t.getTitolo(), t.getAutore())) {
            throw new IllegalArgumentException(
                "Esiste già una traccia con titolo '" + t.getTitolo() +
                "' e autore '" + t.getAutore() + "'");
        }
        tracce.add(t);
        // Il salvataggio JSON parte da solo grazie al listener nel costruttore!
    }

    /**
     * FLUSSO DI MODIFICA
     * Sostituisce una traccia controllando se i nuovi dati generano un duplicato nel catalogo[cite: 71].
     */
    public void modificaTraccia(Traccia vecchia, Traccia nuova) {
        if (vecchia == null || nuova == null) {
            throw new IllegalArgumentException("Le tracce non possono essere null");
        }
        
        int indice = tracce.indexOf(vecchia);
        if (indice == -1) {
            throw new IllegalArgumentException("La traccia da modificare non è presente nel catalogo");
        }
        
        // CONTROLLO INTELIGENTE: Applica la verifica solo se l'utente ha cambiato Titolo o Autore
        if (!vecchia.getTitolo().equalsIgnoreCase(nuova.getTitolo()) || 
            !vecchia.getAutore().equalsIgnoreCase(nuova.getAutore())) {
            
            // Se la nuova combinazione esiste già in un altro brano, blocca e lancia l'errore
            if (contiene(nuova.getTitolo(), nuova.getAutore())) {
                throw new IllegalArgumentException(
                    "Impossibile modificare: esiste già un'altra traccia con titolo '" + nuova.getTitolo() +
                    "' e autore '" + nuova.getAutore() + "'");
            }
        }
        
        tracce.set(indice, nuova);
        // Il salvataggio JSON parte da solo grazie al listener nel costruttore!
    }

    /**
     * FLUSSO DI RIMOZIONE
     */
    public void rimuoviTraccia(Traccia t) {
        if (t == null) {
            throw new IllegalArgumentException("La traccia da rimuovere non può essere null");
        }

        if (!tracce.contains(t)) {
            throw new IllegalArgumentException("La traccia da rimuovere non è presente nel catalogo");
        }

        CatalogoPlaylist.getInstance().rimuoviTracciaDaTutte(t);
        tracce.remove(t);
    }

    // ── Ricerca e Controllo UML ───────────────────────────────────────────────

    public Optional<Traccia> cercaPerId(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Optional.empty();
        }
        return tracce.stream()
                     .filter(t -> t.getId().equals(id))
                     .findFirst();
    }

    /**
     * Specifica esatta del metodo presente nel diagramma UML (Metodo: esisteDuplicato) [cite: 73]
     */
    public boolean contiene(String titolo, String autore) {
        for (Traccia t : tracce) {
            if (t.getTitolo().equalsIgnoreCase(titolo) &&
                t.getAutore().equalsIgnoreCase(autore)) {
                return true;
            }
        }
        return false;
    }
    
    // ── Utilità ───────────────────────────────────────────────────────────────

    public int getSize() {
        return tracce.size();
    }
    
    public void svuota() {
        tracce.clear();
    }

    @Override
    public String toString() {
        return "CatalogoTracce{tracce=" + tracce.size() + "}";
    }
}