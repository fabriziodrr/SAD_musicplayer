package it.unisa.musicplayer.modello;

import it.unisa.musicplayer.servizi.GestoreFile;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Alert;
import java.io.IOException;
import java.util.ArrayList;

public class CatalogoPlaylist {

    private static CatalogoPlaylist instance;

    private final ObservableList<Playlist> playlists;
    private boolean caricamentoInCorso = false;

    private CatalogoPlaylist() {
        this.playlists = FXCollections.observableArrayList();
        this.playlists.addListener((ListChangeListener<Playlist>) change -> {
            eseguiSalvataggioAutomatico();
        });
    }

    public static CatalogoPlaylist getInstance() {
        if (instance == null) {
            instance = new CatalogoPlaylist();
        }
        return instance;
    }

    public void eseguiSalvataggioAutomatico() {
        if (caricamentoInCorso) return;
        DatiApplicazione dati = DatiApplicazione.costruisci(
            new ArrayList<>(Catalogo.getInstance().getTracce()),
            new ArrayList<>(this.playlists)
        );
        try {
            GestoreFile.esporta(dati);
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
            if (dati.getPlaylists() != null) {
                for (Playlist p : dati.getPlaylists()) {
                    p.risolviRiferimenti(Catalogo.getInstance());
                }
                playlists.addAll(dati.getPlaylists());
            }
        } finally {
            caricamentoInCorso = false;
        }
    }

    public ObservableList<Playlist> getPlaylists() {
        return playlists;
    }

    public boolean contiene(String nome) {
        for (Playlist p : playlists) {
            if (p.getNome().equalsIgnoreCase(nome)) {
                return true;
            }
        }
        return false;
    }

    public void aggiungiPlaylist(Playlist playlist) {
        if (playlist == null) {
            throw new IllegalArgumentException("La playlist non può essere null");
        }
        if (contiene(playlist.getNome())) {
            throw new IllegalArgumentException(
                "Esiste già una playlist con nome '" + playlist.getNome() + "'");
        }
        playlists.add(playlist);
    }

    public void rimuoviPlaylist(String nome) {
        playlists.removeIf(p -> p.getNome().equalsIgnoreCase(nome));
    }

    public void rimuoviTracciaDaTutte(Traccia traccia) {
        if (traccia == null) {
            throw new IllegalArgumentException("La traccia da rimuovere non può essere null");
        }

        boolean tracciaRimossa = false;

        for (Playlist playlist : playlists) {
            if (playlist.getIdTracce().contains(traccia.getId())) {
                playlist.rimuoviTraccia(traccia);
                tracciaRimossa = true;
            }
        }

        if (tracciaRimossa) {
            eseguiSalvataggioAutomatico();
        }
    }

    public int getSize() {
        return playlists.size();
    }

    public void svuota() {
        playlists.clear();
    }

    @Override
    public String toString() {
        return "CatalogoPlaylist{playlist=" + playlists.size() + "}";
    }
}
