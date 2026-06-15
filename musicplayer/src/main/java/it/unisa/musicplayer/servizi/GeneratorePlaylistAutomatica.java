package it.unisa.musicplayer.servizi;

import it.unisa.musicplayer.modello.Catalogo;
import it.unisa.musicplayer.modello.CatalogoPlaylist;
import it.unisa.musicplayer.modello.Playlist;
import it.unisa.musicplayer.modello.Tag;
import it.unisa.musicplayer.modello.Traccia;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GeneratorePlaylistAutomatica {

    // Filtri

    public List<Traccia> filtraPerGenere(
            Catalogo catalogo,
            String genere
    ) {
        if (catalogo == null) {
            throw new IllegalArgumentException(
                    "Il catalogo non pu+¦ essere null"
            );
        }

        if (genere == null || genere.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Il genere non pu+¦ essere null o vuoto"
            );
        }

        return catalogo.getTracce()
                .stream()
                .filter(traccia ->
                        traccia.getGenere()
                                .equalsIgnoreCase(genere.trim())
                )
                .collect(Collectors.toList());
    }

    public List<Traccia> filtraPerAnno(
            Catalogo catalogo,
            int anno
    ) {
        if (catalogo == null) {
            throw new IllegalArgumentException(
                    "Il catalogo non pu+¦ essere null"
            );
        }

        if (anno <= 0) {
            throw new IllegalArgumentException(
                    "L'anno deve essere un valore positivo"
            );
        }

        return catalogo.getTracce()
                .stream()
                .filter(traccia -> traccia.getAnno() == anno)
                .collect(Collectors.toList());
    }

    public List<Traccia> filtraPerTag(
            Catalogo catalogo,
            Tag tag
    ) {
        if (catalogo == null) {
            throw new IllegalArgumentException(
                    "Il catalogo non pu+¦ essere null"
            );
        }

        if (tag == null) {
            throw new IllegalArgumentException(
                    "Il tag non pu+¦ essere null"
            );
        }

        return catalogo.getTracce()
                .stream()
                .filter(traccia -> traccia.hasTag(tag))
                .collect(Collectors.toList());
    }

    // Generatori

    public Playlist generaPerGenere(
            Catalogo catalogo,
            String genere
    ) {
        Playlist playlist = new Playlist(
                nomePlaylistAutomatica(genere)
        );

        filtraPerGenere(catalogo, genere)
                .forEach(playlist::aggiungiTraccia);

        return playlist;
    }

    public Playlist generaPerAnno(
            Catalogo catalogo,
            int anno
    ) {
        Playlist playlist = new Playlist(
                nomePlaylistAutomatica(anno)
        );

        filtraPerAnno(catalogo, anno)
                .forEach(playlist::aggiungiTraccia);

        return playlist;
    }

    public Playlist generaPerTag(
            Catalogo catalogo,
            Tag tag
    ) {
        Playlist playlist = new Playlist(
                nomePlaylistPerTag(tag)
        );

        filtraPerTag(catalogo, tag)
                .forEach(playlist::aggiungiTraccia);

        return playlist;
    }

    // Sincronizzazione playlist automatiche per tag - US-18

    /**
     * Aggiorna tutte le playlist automatiche per tag gi+á esistenti.
     *
     * Una playlist mancante non viene creata automaticamente:
     * la creazione rimane conseguenza della scelta dell'utente.
     *
     * Se una playlist automatica non ha pi+¦ tracce corrispondenti,
     * viene rimossa dal CatalogoPlaylist.
     *
     * @return numero di playlist modificate o eliminate
     */
    public int sincronizzaPlaylistAutomatichePerTag(
            Catalogo catalogo,
            CatalogoPlaylist catalogoPlaylist
    ) {
        if (catalogo == null) {
            throw new IllegalArgumentException(
                    "Il catalogo non pu+¦ essere null"
            );
        }

        if (catalogoPlaylist == null) {
            throw new IllegalArgumentException(
                    "Il catalogo playlist non pu+¦ essere null"
            );
        }

        int numeroPlaylistModificate = 0;

        for (Tag tag : Tag.values()) {
            boolean modificata =
                    sincronizzaPlaylistPerTagSenzaSalvare(
                            catalogo,
                            catalogoPlaylist,
                            tag
                    );

            if (modificata) {
                numeroPlaylistModificate++;
            }
        }

        if (numeroPlaylistModificate > 0) {
            catalogoPlaylist.eseguiSalvataggioAutomatico();
        }

        return numeroPlaylistModificate;
    }

    /**
     * Sincronizza la playlist automatica relativa a un solo tag.
     *
     * @return true se la playlist +¿ stata aggiornata o eliminata
     */
    public boolean sincronizzaPlaylistPerTag(
            Catalogo catalogo,
            CatalogoPlaylist catalogoPlaylist,
            Tag tag
    ) {
        if (catalogo == null) {
            throw new IllegalArgumentException(
                    "Il catalogo non pu+¦ essere null"
            );
        }

        if (catalogoPlaylist == null) {
            throw new IllegalArgumentException(
                    "Il catalogo playlist non pu+¦ essere null"
            );
        }

        if (tag == null) {
            throw new IllegalArgumentException(
                    "Il tag non pu+¦ essere null"
            );
        }

        boolean modificata =
                sincronizzaPlaylistPerTagSenzaSalvare(
                        catalogo,
                        catalogoPlaylist,
                        tag
                );

        if (modificata) {
            catalogoPlaylist.eseguiSalvataggioAutomatico();
        }

        return modificata;
    }

    private boolean sincronizzaPlaylistPerTagSenzaSalvare(
            Catalogo catalogo,
            CatalogoPlaylist catalogoPlaylist,
            Tag tag
    ) {
        String nomePlaylist = nomePlaylistPerTag(tag);

        Optional<Playlist> playlistEsistente =
                catalogoPlaylist.getPlaylists()
                        .stream()
                        .filter(playlist ->
                                playlist.getNome()
                                        .equalsIgnoreCase(nomePlaylist)
                        )
                        .findFirst();

        /*
         * Se l'utente non ha mai generato la playlist,
         * la sincronizzazione non deve crearla.
         */
        if (playlistEsistente.isEmpty()) {
            return false;
        }

        Playlist playlist = playlistEsistente.get();

        List<Traccia> tracceCorrette =
                filtraPerTag(catalogo, tag);

        /*
         * AC2: se nessuna traccia possiede pi+¦ il tag,
         * la playlist automatica viene eliminata.
         */
        if (tracceCorrette.isEmpty()) {
            catalogoPlaylist.rimuoviPlaylist(
                    playlist.getNome()
            );

            return true;
        }

        /*
         * Nessun aggiornamento necessario.
         */
        if (stessoContenuto(
                playlist.getTracce(),
                tracceCorrette
        )) {
            return false;
        }

        /*
         * Ricostruisce il contenuto utilizzando gli oggetti
         * attualmente presenti nel catalogo.
         *
         * Questo +¿ importante perch+® modificaTraccia()
         * sostituisce il vecchio oggetto Traccia con uno nuovo.
         */
        List<Traccia> vecchieTracce =
                new ArrayList<>(playlist.getTracce());

        for (Traccia traccia : vecchieTracce) {
            playlist.rimuoviTraccia(traccia);
        }

        tracceCorrette.forEach(
                playlist::aggiungiTraccia
        );

        return true;
    }

    private boolean stessoContenuto(
            List<Traccia> traccePlaylist,
            List<Traccia> tracceCorrette
    ) {
        if (traccePlaylist.size()
                != tracceCorrette.size()) {
            return false;
        }

        for (int i = 0;
             i < traccePlaylist.size();
             i++) {

            Traccia presente =
                    traccePlaylist.get(i);

            Traccia corretta =
                    tracceCorrette.get(i);

            /*
             * Si controlla anche l'identit+á dell'oggetto:
             * dopo una modifica, la traccia pu+¦ avere lo stesso ID
             * ma essere una nuova istanza.
             */
            if (presente != corretta) {
                return false;
            }
        }

        return true;
    }

    // Utilit+á

    public String nomePlaylistAutomatica(
            Object criterio
    ) {
        if (criterio == null) {
            throw new IllegalArgumentException(
                    "Il criterio non pu+¦ essere null"
            );
        }

        String valore =
                criterio.toString().trim();

        if (valore.isEmpty()) {
            throw new IllegalArgumentException(
                    "Il criterio non pu+¦ essere vuoto"
            );
        }

        return "Auto - " + valore;
    }

    public String nomePlaylistPerTag(Tag tag) {
        if (tag == null) {
            throw new IllegalArgumentException(
                    "Il tag non pu+¦ essere null"
            );
        }

        return nomePlaylistAutomatica(
                nomeTagLeggibile(tag)
        );
    }

    private String nomeTagLeggibile(Tag tag) {
        return switch (tag) {
            case FAVOURITE -> "Preferiti";
            case EXPLICIT -> "Espliciti";
            case NEW_RELEASE -> "Nuove Uscite";
        };
    }
}
