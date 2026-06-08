package it.unisa.musicplayer.servizi;

import it.unisa.musicplayer.modello.Catalogo;
import it.unisa.musicplayer.modello.Playlist;
import it.unisa.musicplayer.modello.Traccia;

import java.util.List;
import java.util.stream.Collectors;

public class GeneratorePlaylistAutomatica {

    // ── Filtri ────────────────────────────────────────────────────────────────

    public List<Traccia> filtraPerGenere(Catalogo catalogo, String genere) {
        if (catalogo == null) throw new IllegalArgumentException("Il catalogo non può essere null");
        if (genere == null || genere.trim().isEmpty()) throw new IllegalArgumentException("Il genere non può essere null o vuoto");
        return catalogo.getTracce().stream()
                .filter(t -> t.getGenere().equalsIgnoreCase(genere.trim()))
                .collect(Collectors.toList());
    }

    public List<Traccia> filtraPerAnno(Catalogo catalogo, int anno) {
        if (catalogo == null) throw new IllegalArgumentException("Il catalogo non può essere null");
        if (anno <= 0) throw new IllegalArgumentException("L'anno deve essere un valore positivo");
        return catalogo.getTracce().stream()
                .filter(t -> t.getAnno() == anno)
                .collect(Collectors.toList());
    }

    // ── Generatori ────────────────────────────────────────────────────────────

    public Playlist generaPerGenere(Catalogo catalogo, String genere) {
        Playlist playlist = new Playlist(nomePlaylistAutomatica(genere));
        filtraPerGenere(catalogo, genere).forEach(playlist::aggiungiTraccia);
        return playlist;
    }

    public Playlist generaPerAnno(Catalogo catalogo, int anno) {
        Playlist playlist = new Playlist(nomePlaylistAutomatica(anno));
        filtraPerAnno(catalogo, anno).forEach(playlist::aggiungiTraccia);
        return playlist;
    }

    // ── Utilità ───────────────────────────────────────────────────────────────

    public String nomePlaylistAutomatica(Object criterio) {
        if (criterio == null) throw new IllegalArgumentException("Il criterio non può essere null");
        return "Auto - " + criterio.toString().trim();
    }
}