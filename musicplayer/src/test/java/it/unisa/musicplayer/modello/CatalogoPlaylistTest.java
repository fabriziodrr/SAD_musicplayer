package it.unisa.musicplayer.modello;

import javafx.collections.ObservableList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CatalogoPlaylistTest {

    private CatalogoPlaylist catalogoPlaylist;

    @BeforeEach
    void setUp() {
        catalogoPlaylist = CatalogoPlaylist.getInstance();
        catalogoPlaylist.svuota();
        Catalogo.getInstance().svuota();
    }

    @AfterEach
    void tearDown() {
        catalogoPlaylist.svuota();
        Catalogo.getInstance().svuota();
    }

    @Test
    void testGetInstanceRestituisceStessaIstanza() {
        CatalogoPlaylist altra = CatalogoPlaylist.getInstance();
        assertSame(catalogoPlaylist, altra);
    }

    @Test
    void testGetPlaylistsRestituisceListaNonVuotaDopoAggiunta() {
        Playlist p = new Playlist("Rock Classics");
        catalogoPlaylist.aggiungiPlaylist(p);

        ObservableList<Playlist> lista = catalogoPlaylist.getPlaylists();
        assertNotNull(lista);
        assertEquals(1, lista.size());
        assertEquals("Rock Classics", lista.get(0).getNome());
    }

    @Test
    void testGetPlaylistsAllAvvioRestituisceListaVuota() {
        ObservableList<Playlist> lista = catalogoPlaylist.getPlaylists();
        assertNotNull(lista);
        assertTrue(lista.isEmpty());
    }

    @Test
    void testContieneRestituisceTruePerPlaylistEsistente() {
        catalogoPlaylist.aggiungiPlaylist(new Playlist("Chill Vibes"));
        assertTrue(catalogoPlaylist.contiene("Chill Vibes"));
    }

    @Test
    void testContieneCaseInsensitive() {
        catalogoPlaylist.aggiungiPlaylist(new Playlist("Chill Vibes"));
        assertTrue(catalogoPlaylist.contiene("chill vibes"));
        assertTrue(catalogoPlaylist.contiene("CHILL VIBES"));
    }

    @Test
    void testContieneRestituisceFalsePerPlaylistInesistente() {
        assertFalse(catalogoPlaylist.contiene("Inesistente"));
    }

    @Test
    void testAggiungiPlaylistLanciaEccezionePerDuplicato() {
        catalogoPlaylist.aggiungiPlaylist(new Playlist("Rock"));
        Playlist duplicato = new Playlist("Rock");
        assertThrows(IllegalArgumentException.class, () -> catalogoPlaylist.aggiungiPlaylist(duplicato));
    }

    @Test
    void testAggiungiPlaylistCaseInsensitive() {
        catalogoPlaylist.aggiungiPlaylist(new Playlist("Rock"));
        Playlist altroCase = new Playlist("rock");
        assertThrows(IllegalArgumentException.class, () -> catalogoPlaylist.aggiungiPlaylist(altroCase));
    }

    @Test
    void testRimuoviPlaylist() {
        catalogoPlaylist.aggiungiPlaylist(new Playlist("Rock"));
        catalogoPlaylist.aggiungiPlaylist(new Playlist("Jazz"));
        assertEquals(2, catalogoPlaylist.getSize());

        catalogoPlaylist.rimuoviPlaylist("Rock");
        assertEquals(1, catalogoPlaylist.getSize());
        assertFalse(catalogoPlaylist.contiene("Rock"));
        assertTrue(catalogoPlaylist.contiene("Jazz"));
    }

    @Test
    void testSvuotaRimuoveTutteLePlaylist() {
        catalogoPlaylist.aggiungiPlaylist(new Playlist("A"));
        catalogoPlaylist.aggiungiPlaylist(new Playlist("B"));
        assertEquals(2, catalogoPlaylist.getSize());

        catalogoPlaylist.svuota();
        assertEquals(0, catalogoPlaylist.getSize());
        assertTrue(catalogoPlaylist.getPlaylists().isEmpty());
    }


    @Test
    void testRimuoviTracciaDaTutteRimuoveLaTracciaDaTutteLePlaylist() {
        Traccia traccia = new Traccia(UUID.randomUUID().toString(), "Song X", "Artist X", "3:10", "Pop", 2020);
        Traccia altraTraccia = new Traccia(UUID.randomUUID().toString(), "Song Y", "Artist Y", "4:20", "Rock", 2021);

        Playlist playlist1 = new Playlist("Playlist A");
        Playlist playlist2 = new Playlist("Playlist B");

        playlist1.aggiungiTraccia(traccia);
        playlist1.aggiungiTraccia(altraTraccia);
        playlist2.aggiungiTraccia(traccia);

        catalogoPlaylist.aggiungiPlaylist(playlist1);
        catalogoPlaylist.aggiungiPlaylist(playlist2);

        catalogoPlaylist.rimuoviTracciaDaTutte(traccia);

        assertFalse(playlist1.getTracce().contains(traccia));
        assertFalse(playlist2.getTracce().contains(traccia));

        assertFalse(playlist1.getIdTracce().contains(traccia.getId()));
        assertFalse(playlist2.getIdTracce().contains(traccia.getId()));

        assertTrue(playlist1.getTracce().contains(altraTraccia));
    }

    @Test
    void testRimuoviTracciaDaTutteConNullLanciaEccezione() {
        assertThrows(IllegalArgumentException.class, () -> catalogoPlaylist.rimuoviTracciaDaTutte(null));
    }

}
