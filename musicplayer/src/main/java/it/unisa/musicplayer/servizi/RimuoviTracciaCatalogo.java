package it.unisa.musicplayer.servizi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.unisa.musicplayer.modello.Catalogo;
import it.unisa.musicplayer.modello.CatalogoPlaylist;
import it.unisa.musicplayer.modello.Playlist;
import it.unisa.musicplayer.modello.Traccia;

public class RimuoviTracciaCatalogo extends Operazione {

    private final Catalogo catalogo;
    private final CatalogoPlaylist catalogoPlaylist;
    private final Traccia traccia;
    private final List<PosizionePlaylist> posizioniPlaylist = new ArrayList<>();
    private int indiceCatalogo = -1;

    public RimuoviTracciaCatalogo(
            Catalogo catalogo,
            CatalogoPlaylist catalogoPlaylist,
            Traccia traccia
    ) {
        this.catalogo = Objects.requireNonNull(catalogo, "Il catalogo non può essere null");
        this.catalogoPlaylist = Objects.requireNonNull(catalogoPlaylist, "Il catalogo playlist non può essere null");
        this.traccia = Objects.requireNonNull(traccia, "La traccia non può essere null");
    }

    @Override
    public void esegui() {
        indiceCatalogo = catalogo.getTracce().indexOf(traccia);
        posizioniPlaylist.clear();

        // Salva le posizioni prima che il catalogo rimuova la traccia dalle playlist.
        for (Playlist playlist : catalogoPlaylist.getPlaylists()) {
            int indicePlaylist = playlist.getTracce().indexOf(traccia);
            if (indicePlaylist >= 0) {
                posizioniPlaylist.add(new PosizionePlaylist(playlist, indicePlaylist));
            }
        }

        catalogo.rimuoviTraccia(traccia);
    }

    @Override
    public void annulla() {
        if (indiceCatalogo >= 0 && !catalogo.getTracce().contains(traccia)) {
            catalogo.inserisciTraccia(traccia, indiceCatalogo);
        }

        for (PosizionePlaylist posizione : posizioniPlaylist) {
            posizione.playlist.inserisciTraccia(traccia, posizione.indice);
        }
    }

    @Override
    public String getDescrizione() {
        return "Rimozione traccia dal catalogo: " + traccia.getTitolo();
    }

    private static class PosizionePlaylist {
        private final Playlist playlist;
        private final int indice;

        private PosizionePlaylist(Playlist playlist, int indice) {
            this.playlist = playlist;
            this.indice = indice;
        }
    }
}
