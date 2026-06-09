package it.unisa.musicplayer.modello;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlaylistRimozioneTest {

    private Playlist playlist;
    private Traccia traccia1;
    private Traccia traccia2;
    private Catalogo catalogo;

    @BeforeEach
    void setUp() {
        catalogo = Catalogo.getInstance();
        catalogo.svuota();
        playlist = new Playlist("Test Playlist");
        traccia1 = new Traccia(UUID.randomUUID().toString(), "Bohemian Rhapsody", "Queen", "5:55", "Rock", 1975);
        traccia2 = new Traccia(UUID.randomUUID().toString(), "Hotel California", "Eagles", "6:30", "Rock", 1977);
        catalogo.aggiungiTraccia(traccia1);
        catalogo.aggiungiTraccia(traccia2);
        playlist.aggiungiTraccia(traccia1);
        playlist.aggiungiTraccia(traccia2);
    }

    @AfterEach
    void tearDown() {
        catalogo.svuota();
    }

    @Test
    void testRimuoviTracciaRimuoveSoloDallaPlaylist() {
        playlist.rimuoviTraccia(traccia1);
        assertFalse(playlist.getTracce().contains(traccia1));
        assertTrue(catalogo.getTracce().contains(traccia1));
    }

    @Test
    void testRimuoviTracciaAltraNonRimossa() {
        playlist.rimuoviTraccia(traccia1);
        assertTrue(playlist.getTracce().contains(traccia2));
    }

    @Test
    void testRimuoviTutteLeTracce() {
        playlist.rimuoviTraccia(traccia1);
        playlist.rimuoviTraccia(traccia2);
        assertTrue(playlist.getTracce().isEmpty());
        assertTrue(catalogo.getTracce().contains(traccia1));
        assertTrue(catalogo.getTracce().contains(traccia2));
    }

    @Test
    void testRimuoviTracciaNull() {
        assertThrows(Exception.class, () -> playlist.rimuoviTraccia(null));
    }

    @Test
    void testRimuoviTracciaNonPresente() {
        Traccia tracciaEsterna = new Traccia(UUID.randomUUID().toString(), "Test", "Artista", "3:00", "Pop", 2020);
        assertDoesNotThrow(() -> playlist.rimuoviTraccia(tracciaEsterna));
        assertTrue(playlist.getTracce().contains(traccia1));
        assertTrue(playlist.getTracce().contains(traccia2));
    }

    @Test
    void testDimensionePlaylistDopoRimozione() {
        assertEquals(2, playlist.getNumeroTracce());
        playlist.rimuoviTraccia(traccia1);
        assertEquals(1, playlist.getNumeroTracce());
    }
}