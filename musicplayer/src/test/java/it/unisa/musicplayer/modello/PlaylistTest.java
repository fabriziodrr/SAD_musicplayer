package it.unisa.musicplayer.modello;

import org.junit.jupiter.api.Test;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PlaylistTest {

    @Test
    void testCostruttoreConNomeValido() {
        Playlist p = new Playlist("Rock Classics");
        assertEquals("Rock Classics", p.getNome());
        assertNotNull(p.getTracce());
        assertTrue(p.getTracce().isEmpty());
    }

    @Test
    void testCostruttoreLanciaEccezionePerNomeNull() {
        assertThrows(IllegalArgumentException.class, () -> new Playlist(null));
    }

    @Test
    void testCostruttoreLanciaEccezionePerNomeVuoto() {
        assertThrows(IllegalArgumentException.class, () -> new Playlist(""));
        assertThrows(IllegalArgumentException.class, () -> new Playlist("   "));
    }

    @Test
    void testSetNomeValido() {
        Playlist p = new Playlist("Old");
        p.setNome("New");
        assertEquals("New", p.getNome());
    }

    @Test
    void testAggiungiTraccia() {
        Playlist p = new Playlist("Test");
        Traccia t = new Traccia(UUID.randomUUID().toString(), "Song", "Artist", "3:45", "Rock", 2024);
        p.aggiungiTraccia(t);

        assertEquals(1, p.getTracce().size());
        assertEquals("Song", p.getTracce().get(0).getTitolo());
        assertEquals(1, p.getIdTracce().size());
    }

    @Test
    void testAggiungiTracciaNoDuplicati() {
        Playlist p = new Playlist("Test");
        Traccia t = new Traccia(UUID.randomUUID().toString(), "Song", "Artist", "3:45", "Rock", 2024);
        p.aggiungiTraccia(t);
        p.aggiungiTraccia(t);

        assertEquals(1, p.getTracce().size());
    }

    @Test
    void testRimuoviTraccia() {
        Playlist p = new Playlist("Test");
        Traccia t1 = new Traccia(UUID.randomUUID().toString(), "A", "B", "1:00", "C", 2000);
        Traccia t2 = new Traccia(UUID.randomUUID().toString(), "D", "E", "2:00", "F", 2001);
        p.aggiungiTraccia(t1);
        p.aggiungiTraccia(t2);
        assertEquals(2, p.getTracce().size());

        p.rimuoviTraccia(t1);
        assertEquals(1, p.getTracce().size());
        assertEquals("D", p.getTracce().get(0).getTitolo());
    }

    @Test
    void testRisolviRiferimenti() {
        Catalogo catalogo = Catalogo.getInstance();
        catalogo.svuota();

        Traccia t1 = new Traccia(UUID.randomUUID().toString(), "Song1", "Artist1", "3:00", "Pop", 2020);
        Traccia t2 = new Traccia(UUID.randomUUID().toString(), "Song2", "Artist2", "4:00", "Rock", 2021);
        catalogo.aggiungiTraccia(t1);
        catalogo.aggiungiTraccia(t2);

        Playlist p = new Playlist("Test");
        p.setIdTracce(java.util.Arrays.asList(t1.getId(), t2.getId()));
        p.risolviRiferimenti(catalogo);

        assertEquals(2, p.getTracce().size());
        assertSame(t1, p.getTracce().get(0));
        assertSame(t2, p.getTracce().get(1));
    }

    @Test
    void testEqualsCaseInsensitive() {
        Playlist p1 = new Playlist("Rock");
        Playlist p2 = new Playlist("rock");
        assertEquals(p1, p2);
    }

    @Test
    void testGetNumeroTracce() {
        Playlist p = new Playlist("Test");
        assertEquals(0, p.getNumeroTracce());

        Traccia t = new Traccia(UUID.randomUUID().toString(), "Song", "Artist", "3:45", "Rock", 2024);
        p.aggiungiTraccia(t);
        assertEquals(1, p.getNumeroTracce());
    }

    @Test
    void testUS07AggiungiTracciaAllaPlaylist() {
        Playlist playlist = new Playlist("Playlist Test");
        Traccia traccia = new Traccia(
                java.util.UUID.randomUUID().toString(),
                "Titolo Test",
                "Autore Test",
                "3:30",
                "Pop",
                2024
        );

        playlist.aggiungiTraccia(traccia);

        assertEquals(1, playlist.getNumeroTracce());
        assertTrue(playlist.getTracce().contains(traccia));
        assertTrue(playlist.getIdTracce().contains(traccia.getId()));
    }

    @Test
    void testUS07StessaTracciaInPlaylistDiverse() {
        Playlist playlist1 = new Playlist("Playlist 1");
        Playlist playlist2 = new Playlist("Playlist 2");

        Traccia traccia = new Traccia(
                java.util.UUID.randomUUID().toString(),
                "Brano Condiviso",
                "Autore",
                "4:00",
                "Rock",
                2023
        );

        playlist1.aggiungiTraccia(traccia);
        playlist2.aggiungiTraccia(traccia);

        assertEquals(1, playlist1.getNumeroTracce());
        assertEquals(1, playlist2.getNumeroTracce());

        assertTrue(playlist1.getTracce().contains(traccia));
        assertTrue(playlist2.getTracce().contains(traccia));
    }

    @Test
    void testUS07AggiungiTracciaNullLanciaEccezione() {
        Playlist playlist = new Playlist("Playlist Test");

        assertThrows(NullPointerException.class, () -> playlist.aggiungiTraccia(null));
    }

    @Test
    void testContatoreRiproduzioniInizialmenteZero() {
        Playlist playlist = new Playlist("Playlist Test");

        assertEquals(0, playlist.getContaRiproduzioni());
    }

    @Test
    void testIncrementaRiproduzioniPlaylist() {
        Playlist playlist = new Playlist("Playlist Test");

        playlist.incrementaRiproduzioni();

        assertEquals(1, playlist.getContaRiproduzioni());
    }

    @Test
    void testIncrementaRiproduzioniPlaylistPiuVolte() {
        Playlist playlist = new Playlist("Playlist Test");

        playlist.incrementaRiproduzioni();
        playlist.incrementaRiproduzioni();
        playlist.incrementaRiproduzioni();

        assertEquals(3, playlist.getContaRiproduzioni());
    }

}
