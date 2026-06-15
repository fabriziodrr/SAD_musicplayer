package it.unisa.musicplayer.servizi;

import it.unisa.musicplayer.modello.Catalogo;
import it.unisa.musicplayer.modello.CatalogoPlaylist;
import it.unisa.musicplayer.modello.Playlist;
import it.unisa.musicplayer.modello.Traccia;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Servizio di sola lettura per il calcolo delle statistiche
 * relative alle tracce e alle playlist.
 */
public class Statistiche {

    /**
     * Restituisce al massimo le prime n tracce ordinate
     * per numero di riproduzioni decrescente.
     */
    public List<Traccia> getTopTracce(Catalogo catalogo, int n) {
        Objects.requireNonNull(
                catalogo,
                "Il catalogo non può essere null"
        );

        if (n <= 0) {
            return List.of();
        }

        return catalogo.getTracce()
                .stream()
                .sorted(
                        Comparator
                                .comparingInt(
                                        Traccia::getContaRiproduzioni
                                )
                                .reversed()
                                .thenComparing(
                                        Traccia::getTitolo,
                                        String.CASE_INSENSITIVE_ORDER
                                )
                                .thenComparing(
                                        Traccia::getAutore,
                                        String.CASE_INSENSITIVE_ORDER
                                )
                )
                .limit(n)
                .toList();
    }

    /**
     * Restituisce al massimo le prime n playlist ordinate
     * per numero di riproduzioni decrescente.
     */
    public List<Playlist> getTopPlaylist(
            CatalogoPlaylist catalogoPlaylist,
            int n
    ) {
        Objects.requireNonNull(
                catalogoPlaylist,
                "Il catalogo delle playlist non può essere null"
        );

        if (n <= 0) {
            return List.of();
        }

        return catalogoPlaylist.getPlaylists()
                .stream()
                .sorted(
                        Comparator
                                .comparingInt(
                                        Playlist::getContaRiproduzioni
                                )
                                .reversed()
                                .thenComparing(
                                        Playlist::getNome,
                                        String.CASE_INSENSITIVE_ORDER
                                )
                )
                .limit(n)
                .toList();
    }

    public int contaTracce(Catalogo catalogo) {
        Objects.requireNonNull(
                catalogo,
                "Il catalogo non può essere null"
        );

        return catalogo.getTracce().size();
    }

    public int contaPlaylist(
            CatalogoPlaylist catalogoPlaylist
    ) {
        Objects.requireNonNull(
                catalogoPlaylist,
                "Il catalogo delle playlist non può essere null"
        );

        return catalogoPlaylist.getPlaylists().size();
    }

    public Optional<Traccia> getTracciaPiuRiprodotta(
            Catalogo catalogo
    ) {
        return getTopTracce(catalogo, 1)
                .stream()
                .findFirst();
    }

    public Optional<Playlist> getPlaylistPiuRiprodotta(
            CatalogoPlaylist catalogoPlaylist
    ) {
        return getTopPlaylist(catalogoPlaylist, 1)
                .stream()
                .findFirst();
    }
}
