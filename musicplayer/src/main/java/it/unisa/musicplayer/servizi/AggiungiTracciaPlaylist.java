package it.unisa.musicplayer.servizi;

import java.util.Objects;

import it.unisa.musicplayer.modello.Playlist;
import it.unisa.musicplayer.modello.Traccia;

public class AggiungiTracciaPlaylist extends Operazione {

    private final Playlist playlist;
    private final Traccia traccia;

    public AggiungiTracciaPlaylist(Playlist playlist, Traccia traccia) {
        this.playlist = Objects.requireNonNull(playlist, "La playlist non può essere null");
        this.traccia = Objects.requireNonNull(traccia, "La traccia non può essere null");
    }

    @Override
    public void esegui() {
        playlist.aggiungiTraccia(traccia);
    }

    @Override
    public void annulla() {
        playlist.rimuoviTraccia(traccia);
    }

    @Override
    public String getDescrizione() {
        return "Aggiunta traccia alla playlist " + playlist.getNome() + ": " + traccia.getTitolo();
    }
}
