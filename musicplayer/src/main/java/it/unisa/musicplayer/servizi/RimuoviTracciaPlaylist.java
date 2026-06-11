package it.unisa.musicplayer.servizi;

import java.util.Objects;

import it.unisa.musicplayer.modello.Playlist;
import it.unisa.musicplayer.modello.Traccia;

public class RimuoviTracciaPlaylist extends Operazione {

    private final Playlist playlist;
    private final Traccia traccia;
    private int indiceOriginale = -1;

    public RimuoviTracciaPlaylist(Playlist playlist, Traccia traccia) {
        this.playlist = Objects.requireNonNull(playlist, "La playlist non può essere null");
        this.traccia = Objects.requireNonNull(traccia, "La traccia non può essere null");
    }

    @Override
    public void esegui() {
        indiceOriginale = playlist.getTracce().indexOf(traccia);
        playlist.rimuoviTraccia(traccia);
    }

    @Override
    public void annulla() {
        if (indiceOriginale >= 0) {
            playlist.inserisciTraccia(traccia, indiceOriginale);
        }
    }

    @Override
    public String getDescrizione() {
        return "Rimozione traccia dalla playlist " + playlist.getNome() + ": " + traccia.getTitolo();
    }
}
