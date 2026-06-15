package it.unisa.musicplayer.modello;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Alert;
import java.io.IOException;
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
    private boolean caricamentoInCorso = false;

    /**
     * Confeziona le canzoni correnti dentro il Wrapper e lancia l'esportazione.
     * In caso di errore I/O mostra un Alert all'utente.
     */
    public void eseguiSalvataggioAutomatico() {
        if (caricamentoInCorso) return;
        DatiApplicazione pacchettoDati = DatiApplicazione.costruisci(
            new ArrayList<>(this.tracce),
            new ArrayList<>(CatalogoPlaylist.getInstance().getPlaylists())
        );
        try {
            GestoreFile.esporta(pacchettoDati);
        } catch (IOException e) {
            System.err.println("[ERRORE SAVE] Impossibile scrivere il file JSON: " + e.getMessage());
            try {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Errore di salvataggio");
                    alert.setHeaderText("Impossibile salvare i dati");
                    alert.setContentText("Si è verificato un errore durante il salvataggio:\n" + e.getMessage());
                    alert.showAndWait();
                });
            } catch (Exception ignored) { }
        }
    }

    public void caricaDaFile() {
        caricamentoInCorso = true;
        try {
            DatiApplicazione dati = GestoreFile.importa();
            if (dati.getCanzoni() != null && !dati.getCanzoni().isEmpty()) {
                tracce.addAll(dati.getCanzoni());
            }
        } finally {
            caricamentoInCorso = false;
        }
    }

    // ── Getter ────────────────────────────────────────────────────────────────

    public ObservableList<Traccia> getTracce() {
        return tracce;
    }

    /**
     * FLUSSO DI AGGIUNTA
     * Aggiunge una traccia al catalogo verificando preventivamente i duplicati.
     */
    public void aggiungiTraccia(Traccia t) {
        if (t == null) {
            throw new IllegalArgumentException("La traccia non può essere null");
        }

        if (contiene(t.getTitolo(), t.getAutore())) {
            throw new IllegalArgumentException(
                "Esiste già una traccia con titolo '" + t.getTitolo() +
                "' e autore '" + t.getAutore() + "'");
        }
        tracce.add(t);
        aggiornaPlaylistAutomatiche(t);
    }

    public void inserisciTraccia(Traccia t, int indice) {
        if (t == null) {
            throw new IllegalArgumentException("La traccia non può essere null");
        }

        if (contiene(t.getTitolo(), t.getAutore())) {
            throw new IllegalArgumentException(
                "Esiste già una traccia con titolo '" + t.getTitolo() +
                "' e autore '" + t.getAutore() + "'");
        }

        // L'undo deve ripristinare il catalogo senza riattivare le playlist automatiche.
        int indiceSicuro = Math.max(0, Math.min(indice, tracce.size()));
        tracce.add(indiceSicuro, t);
    }

    private void aggiornaPlaylistAutomatiche(Traccia t) {
        final String PREFISSO = "Auto - ";
        for (Playlist p : CatalogoPlaylist.getInstance().getPlaylists()) {
            String nome = p.getNome();
            if (!nome.startsWith(PREFISSO)) continue;
            String criterio = nome.substring(PREFISSO.length());
            try {
                int anno = Integer.parseInt(criterio);
                if (t.getAnno() == anno) p.aggiungiTraccia(t);
            } catch (NumberFormatException e) {
                if (criterio.equalsIgnoreCase(t.getGenere())) p.aggiungiTraccia(t);
            }
        }
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
        
        
        if (!vecchia.getTitolo().equalsIgnoreCase(nuova.getTitolo()) || 
            !vecchia.getAutore().equalsIgnoreCase(nuova.getAutore())) {
            
            
            if (contiene(nuova.getTitolo(), nuova.getAutore())) {
                throw new IllegalArgumentException(
                    "Impossibile modificare: esiste già un'altra traccia con titolo '" + nuova.getTitolo() +
                    "' e autore '" + nuova.getAutore() + "'");
            }
        }
        
        tracce.set(indice, nuova);
        
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
     * Specifica esatta del metodo presente nel diagramma UML (Metodo: esisteDuplicato)
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
