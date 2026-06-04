package it.unisa.musicplayer.servizi;

import it.unisa.musicplayer.modello.Playlist;
import it.unisa.musicplayer.modello.Traccia;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlaylistModificaDuranteRiproduzioneTest {

    private Lettore lettore;
    private Playlist playlist;
    private Traccia traccia1;
    private Traccia traccia2;
    private Traccia traccia3;

    @BeforeEach
    void setUp() {
        lettore = new Lettore();
        lettore.setModalita(new Sequenziale());

        playlist = new Playlist("Playlist Sprint 2");
        traccia1 = creaTraccia("Traccia 1");
        traccia2 = creaTraccia("Traccia 2");
        traccia3 = creaTraccia("Traccia 3");

        playlist.aggiungiTraccia(traccia1);
        playlist.aggiungiTraccia(traccia2);
        lettore.aggiornaCodeTracce(List.of(traccia1, traccia2));
    }

    @Test
    void task1201AggiungiTracciaDurantePlaying() {
        lettore.play();

        assertDoesNotThrow(() -> playlist.aggiungiTraccia(traccia3));

        assertTrue(playlist.getTracce().contains(traccia3));
        assertEquals(StatoLettore.PLAYING, lettore.getStato());
        assertEquals(traccia1, lettore.getTracciaCorrente());
    }

    @Test
    void task1201RimuoviTracciaDurantePlaying() {
        lettore.play();

        assertDoesNotThrow(() -> playlist.rimuoviTraccia(traccia2));

        assertFalse(playlist.getTracce().contains(traccia2));
        assertEquals(StatoLettore.PLAYING, lettore.getStato());
        assertEquals(traccia1, lettore.getTracciaCorrente());
    }

    @Test
    void task1201AggiungiERimuoviTracciaDurantePaused() {
        lettore.play();
        lettore.pausa();

        assertDoesNotThrow(() -> playlist.aggiungiTraccia(traccia3));
        assertDoesNotThrow(() -> playlist.rimuoviTraccia(traccia2));

        assertTrue(playlist.getTracce().contains(traccia3));
        assertFalse(playlist.getTracce().contains(traccia2));
        assertEquals(StatoLettore.PAUSED, lettore.getStato());
        assertEquals(traccia1, lettore.getTracciaCorrente());
    }

    @Test
    void task1201AggiungiERimuoviTracciaDuranteStopped() {
        assertDoesNotThrow(() -> playlist.aggiungiTraccia(traccia3));
        assertDoesNotThrow(() -> playlist.rimuoviTraccia(traccia2));

        assertTrue(playlist.getTracce().contains(traccia3));
        assertFalse(playlist.getTracce().contains(traccia2));
        assertEquals(StatoLettore.STOPPED, lettore.getStato());
        assertEquals(traccia1, lettore.getTracciaCorrente());
    }

    private Traccia creaTraccia(String titolo) {
        return new Traccia(UUID.randomUUID().toString(), titolo, "Autore", "3:00", "Pop", 2024);
    }
}
