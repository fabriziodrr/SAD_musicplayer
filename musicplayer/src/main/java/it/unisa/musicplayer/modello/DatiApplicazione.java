package it.unisa.musicplayer.modello;

import java.util.ArrayList;
import java.util.List;

public class DatiApplicazione {

    private List<Traccia> canzoni;
    private List<Playlist> playlists;

    public DatiApplicazione() {
        this.canzoni = new ArrayList<>();
        this.playlists = new ArrayList<>();
    }

    public DatiApplicazione(List<Traccia> canzoni) {
        this.canzoni = canzoni != null ? canzoni : new ArrayList<>();
        this.playlists = new ArrayList<>();
    }

    public static DatiApplicazione costruisci(List<Traccia> canzoni, List<Playlist> playlists) {
        DatiApplicazione d = new DatiApplicazione();
        d.canzoni = canzoni != null ? canzoni : new ArrayList<>();
        d.playlists = playlists != null ? playlists : new ArrayList<>();
        return d;
    }

    public List<Traccia> getCanzoni() {
        return canzoni;
    }

    public void setCanzoni(List<Traccia> canzoni) {
        this.canzoni = canzoni;
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
    }
}
