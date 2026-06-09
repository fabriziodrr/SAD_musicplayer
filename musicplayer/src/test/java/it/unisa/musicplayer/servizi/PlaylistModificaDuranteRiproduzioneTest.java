package it.unisa.musicplayer.servizi;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unisa.musicplayer.modello.Playlist;
import it.unisa.musicplayer.modello.Traccia;

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
        lettore.aggiornaCodeTracce(new ArrayList<>(playlist.getTracce()));
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

    @Test
    void task1202AggiuntaTracciaAllaPlaylistCorrenteVieneAccodataNelLettore() {
        lettore.play();
        playlist.aggiungiTraccia(traccia3);
        lettore.aggiungiTracciaInCoda(traccia3);
        assertEquals(List.of(traccia1, traccia2, traccia3), lettore.getCoda());
        assertEquals(StatoLettore.PLAYING, lettore.getStato());
        assertEquals(traccia1, lettore.getTracciaCorrente());
    }

    @Test
    void task1202AggiuntaTracciaDuplicataNonDuplicaCodaLettore() {
        lettore.play();
        playlist.aggiungiTraccia(traccia2);
        lettore.aggiungiTracciaInCoda(traccia2);
        assertEquals(List.of(traccia1, traccia2), lettore.getCoda());
        assertEquals(traccia1, lettore.getTracciaCorrente());
    }

    @Test
    void task1203RimozioneTracciaNonCorrenteDurantePlaying() {
        playlist.aggiungiTraccia(traccia3);
        lettore.aggiornaCodeTracce(new ArrayList<>(playlist.getTracce()));
        lettore.play();
        playlist.rimuoviTraccia(traccia2);
        lettore.rimuoviTracciaDallaCoda(traccia2);
        assertEquals(List.of(traccia1, traccia3), lettore.getCoda());
        assertEquals(StatoLettore.PLAYING, lettore.getStato());
        assertEquals(traccia1, lettore.getTracciaCorrente());
    }

    @Test
    void task1203RimozioneTracciaNonCorrenteNonVieneRiprodottaDopoSkip() {
        playlist.aggiungiTraccia(traccia3);
        lettore.aggiornaCodeTracce(new ArrayList<>(playlist.getTracce()));
        lettore.play();
        playlist.rimuoviTraccia(traccia2);
        lettore.rimuoviTracciaDallaCoda(traccia2);
        lettore.skip();
        assertEquals(traccia3, lettore.getTracciaCorrente());
        assertEquals(StatoLettore.PLAYING, lettore.getStato());
    }

    @Test
    void task1203RimozioneUnicaTracciaCorrenteFermaIlLettore() {
        playlist.rimuoviTraccia(traccia2);
        lettore.aggiornaCodeTracce(new ArrayList<>(playlist.getTracce()));
        lettore.play();
        playlist.rimuoviTraccia(traccia1);
        lettore.rimuoviTracciaDallaCoda(traccia1);
        assertTrue(lettore.getCoda().isEmpty());
        assertNull(lettore.getTracciaCorrente());
        assertEquals(StatoLettore.STOPPED, lettore.getStato());
    }

    @Test
    void task1203RimozioneTracciaCorrentePassaAllaSuccessiva() {
        playlist.aggiungiTraccia(traccia3);
        lettore.aggiornaCodeTracce(new ArrayList<>(playlist.getTracce()));
        lettore.play();
        playlist.rimuoviTraccia(traccia1);
        lettore.rimuoviTracciaDallaCoda(traccia1);
        assertEquals(List.of(traccia2, traccia3), lettore.getCoda());
        assertEquals(StatoLettore.PLAYING, lettore.getStato());
        assertEquals(traccia2, lettore.getTracciaCorrente());
    }

    @Test
    void task1203RimozioneTracciaCorrenteDaCatalogoPassaAllaSuccessiva() {
        playlist.aggiungiTraccia(traccia3);
        lettore.aggiornaCodeTracce(new ArrayList<>(playlist.getTracce()));
        lettore.play();
        lettore.rimuoviTracciaDallaCoda(traccia1);
        playlist.rimuoviTraccia(traccia1);
        assertEquals(List.of(traccia2, traccia3), lettore.getCoda());
        assertEquals(StatoLettore.PLAYING, lettore.getStato());
        assertEquals(traccia2, lettore.getTracciaCorrente());
    }

    @Test
    void task1203RimozioneTracciaDurantePausedSincronizzata() {
        playlist.aggiungiTraccia(traccia3);
        lettore.aggiornaCodeTracce(new ArrayList<>(playlist.getTracce()));
        lettore.play();
        lettore.pausa();
        playlist.rimuoviTraccia(traccia2);
        lettore.rimuoviTracciaDallaCoda(traccia2);
        assertEquals(List.of(traccia1, traccia3), lettore.getCoda());
        assertEquals(StatoLettore.PAUSED, lettore.getStato());
        assertEquals(traccia1, lettore.getTracciaCorrente());
    }

    @Test
    void task1203RimozioneTracciaCorrenteDurantePausedPassaAllaSuccessiva() {
        playlist.aggiungiTraccia(traccia3);
        lettore.aggiornaCodeTracce(new ArrayList<>(playlist.getTracce()));
        lettore.play();
        lettore.pausa();
        playlist.rimuoviTraccia(traccia1);
        lettore.rimuoviTracciaDallaCoda(traccia1);
        assertEquals(List.of(traccia2, traccia3), lettore.getCoda());
        assertEquals(StatoLettore.PAUSED, lettore.getStato());
        assertEquals(traccia2, lettore.getTracciaCorrente());
    }

    private Traccia creaTraccia(String titolo) {
        return new Traccia(UUID.randomUUID().toString(), titolo, "Autore", "3:00", "Pop", 2024);
    }
}
