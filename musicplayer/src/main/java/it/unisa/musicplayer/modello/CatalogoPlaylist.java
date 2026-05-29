package it.unisa.musicplayer.modello;

import it.unisa.musicplayer.servizi.GestoreFile;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;
import java.util.ArrayList;

public class CatalogoPlaylist {

    private static CatalogoPlaylist instance;

    private final ObservableList<Playlist> playlists;

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
        DatiApplicazione dati = DatiApplicazione.costruisci(
            new ArrayList<>(Catalogo.getInstance().getTracce()),
            new ArrayList<>(this.playlists)
        );
        GestoreFile.esporta(dati);
    }

    public void caricaDaFile() {
        DatiApplicazione dati = GestoreFile.importa();
        if (dati.getPlaylists() != null) {
            for (Playlist p : dati.getPlaylists()) {
                p.risolviRiferimenti(Catalogo.getInstance());
            }
            playlists.addAll(dati.getPlaylists());
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
