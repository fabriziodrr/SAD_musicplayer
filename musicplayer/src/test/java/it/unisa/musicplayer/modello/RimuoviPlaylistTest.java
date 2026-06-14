package it.unisa.musicplayer.modello;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RimuoviPlaylistTest {

    private CatalogoPlaylist catalogoPlaylist;
    private Catalogo catalogo;

    @BeforeEach
    void setUp() {
        // Reset singleton prima di ogni test
        CatalogoPlaylist.getInstance().svuota();
        Catalogo.getInstance().svuota();
        catalogoPlaylist = CatalogoPlaylist.getInstance();
        catalogo = Catalogo.getInstance();
    }

    // AC1 + AC2 - Conferma eliminazione: la playlist viene rimossa
    @Test
    void testRimuoviPlaylist_playlistEsistente() {
        Playlist p = new Playlist("Rock");
        catalogoPlaylist.aggiungiPlaylist(p);

        catalogoPlaylist.rimuoviPlaylist("Rock");

        assertFalse(catalogoPlaylist.contiene("Rock"));
        assertEquals(0, catalogoPlaylist.getSize());
    }

    // AC3 - Annulla eliminazione: la playlist rimane
    @Test
    void testRimuoviPlaylist_annullata_playlistRimane() {
        Playlist p = new Playlist("Rock");
        catalogoPlaylist.aggiungiPlaylist(p);

        // Simuliamo l'annullamento non chiamando rimuoviPlaylist
        assertTrue(catalogoPlaylist.contiene("Rock"));
        assertEquals(1, catalogoPlaylist.getSize());
    }

    // AC4 - Le tracce rimangono nel catalogo generale dopo eliminazione playlist
    @Test
    void testRimuoviPlaylist_tracceRimangonoNelCatalogo() {
        Traccia t1 = new Traccia("1", "Bohemian Rhapsody", "Queen", "5:55", "Rock", 1975);
        Traccia t2 = new Traccia("2", "Hotel California", "Eagles", "6:30", "Rock", 1977);
        catalogo.aggiungiTraccia(t1);
        catalogo.aggiungiTraccia(t2);

        Playlist p = new Playlist("Rock");
        p.aggiungiTraccia(t1);
        p.aggiungiTraccia(t2);
        catalogoPlaylist.aggiungiPlaylist(p);

        catalogoPlaylist.rimuoviPlaylist("Rock");

        assertFalse(catalogoPlaylist.contiene("Rock"));
        assertTrue(catalogo.contiene(t1.getTitolo(), t1.getAutore()));
        assertTrue(catalogo.contiene(t2.getTitolo(), t2.getAutore()));
    }

    // AC5 - Nessuna playlist selezionata: rimuoviPlaylist con nome inesistente non lancia eccezioni
    @Test
    void testRimuoviPlaylist_nomeInesistente_nessunErrore() {
        assertDoesNotThrow(() -> catalogoPlaylist.rimuoviPlaylist("PlaylistCheNonEsiste"));
        assertEquals(0, catalogoPlaylist.getSize());
    }

    // AC6 - Catalogo vuoto dopo eliminazione unica playlist
    @Test
    void testRimuoviPlaylist_catalogoVuotoDopoEliminazione() {
        Playlist p = new Playlist("Unica");
        catalogoPlaylist.aggiungiPlaylist(p);
        assertEquals(1, catalogoPlaylist.getSize());

        catalogoPlaylist.rimuoviPlaylist("Unica");

        assertEquals(0, catalogoPlaylist.getSize());
        assertFalse(catalogoPlaylist.contiene("Unica"));
    }

    // AC2 - Eliminazione con nome case-insensitive
    @Test
    void testRimuoviPlaylist_caseInsensitive() {
        Playlist p = new Playlist("Rock");
        catalogoPlaylist.aggiungiPlaylist(p);

        catalogoPlaylist.rimuoviPlaylist("ROCK");

        assertFalse(catalogoPlaylist.contiene("Rock"));
    }

    // AC4 - Le tracce non vengono rimosse dalle altre playlist
    @Test
    void testRimuoviPlaylist_tracceRimangonoInAltrePlaylist() {
        Traccia t1 = new Traccia("1", "Bohemian Rhapsody", "Queen", "5:55", "Rock", 1975);
        catalogo.aggiungiTraccia(t1);

        Playlist p1 = new Playlist("Rock");
        p1.aggiungiTraccia(t1);
        Playlist p2 = new Playlist("Preferiti");
        p2.aggiungiTraccia(t1);

        catalogoPlaylist.aggiungiPlaylist(p1);
        catalogoPlaylist.aggiungiPlaylist(p2);

        catalogoPlaylist.rimuoviPlaylist("Rock");

        assertTrue(catalogoPlaylist.contiene("Preferiti"));
        assertTrue(p2.getTracce().contains(t1));
    }
}