package it.unisa.musicplayer.modello;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlaylistRiordinoTest {

    private Playlist playlist;
    private Traccia t1;
    private Traccia t2;
    private Traccia t3;

    @BeforeEach
    void setUp() {
        playlist = new Playlist("Test");
        t1 = new Traccia("1", "Canzone A", "Artista A", "3:00", "Pop", 2020);
        t2 = new Traccia("2", "Canzone B", "Artista B", "4:00", "Rock", 2021);
        t3 = new Traccia("3", "Canzone C", "Artista C", "5:00", "Jazz", 2022);
        playlist.aggiungiTraccia(t1);
        playlist.aggiungiTraccia(t2);
        playlist.aggiungiTraccia(t3);
    }

    // spostaSu - primo elemento: non fa nulla
    @Test
    void testSpostaSu_primoElemento_nonCambia() {
        playlist.spostaSu(t1);
        assertEquals(t1, playlist.getTracce().get(0));
        assertEquals(t2, playlist.getTracce().get(1));
        assertEquals(t3, playlist.getTracce().get(2));
    }

    // spostaSu - elemento centrale: sale di una posizione
    @Test
    void testSpostaSu_elementoCentrale() {
        playlist.spostaSu(t2);
        assertEquals(t2, playlist.getTracce().get(0));
        assertEquals(t1, playlist.getTracce().get(1));
        assertEquals(t3, playlist.getTracce().get(2));
    }

    // spostaSu - ultimo elemento: sale di una posizione
    @Test
    void testSpostaSu_ultimoElemento() {
        playlist.spostaSu(t3);
        assertEquals(t1, playlist.getTracce().get(0));
        assertEquals(t3, playlist.getTracce().get(1));
        assertEquals(t2, playlist.getTracce().get(2));
    }

    // spostaGiu - ultimo elemento: non fa nulla
    @Test
    void testSpostaGiu_ultimoElemento_nonCambia() {
        playlist.spostaGiu(t3);
        assertEquals(t1, playlist.getTracce().get(0));
        assertEquals(t2, playlist.getTracce().get(1));
        assertEquals(t3, playlist.getTracce().get(2));
    }

    // spostaGiu - elemento centrale: scende di una posizione
    @Test
    void testSpostaGiu_elementoCentrale() {
        playlist.spostaGiu(t2);
        assertEquals(t1, playlist.getTracce().get(0));
        assertEquals(t3, playlist.getTracce().get(1));
        assertEquals(t2, playlist.getTracce().get(2));
    }

    // spostaGiu - primo elemento: scende di una posizione
    @Test
    void testSpostaGiu_primoElemento() {
        playlist.spostaGiu(t1);
        assertEquals(t2, playlist.getTracce().get(0));
        assertEquals(t1, playlist.getTracce().get(1));
        assertEquals(t3, playlist.getTracce().get(2));
    }

    // spostaSu - traccia non presente: non lancia eccezioni
    @Test
    void testSpostaSu_tracciaAssente_nessunErrore() {
        Traccia tEstranea = new Traccia("99", "Estranea", "X", "1:00", "Pop", 2000);
        assertDoesNotThrow(() -> playlist.spostaSu(tEstranea));
        // L'ordine non cambia
        assertEquals(t1, playlist.getTracce().get(0));
        assertEquals(t2, playlist.getTracce().get(1));
        assertEquals(t3, playlist.getTracce().get(2));
    }

    // spostaGiu - traccia non presente: non lancia eccezioni
    @Test
    void testSpostaGiu_tracciaAssente_nessunErrore() {
        Traccia tEstranea = new Traccia("99", "Estranea", "X", "1:00", "Pop", 2000);
        assertDoesNotThrow(() -> playlist.spostaGiu(tEstranea));
        // L'ordine non cambia
        assertEquals(t1, playlist.getTracce().get(0));
        assertEquals(t2, playlist.getTracce().get(1));
        assertEquals(t3, playlist.getTracce().get(2));
    }

    // playlist con un solo elemento: spostaSu e spostaGiu non fanno nulla
    @Test
    void testSpostaSu_playlistConUnElemento_nonCambia() {
        Playlist p = new Playlist("Singola");
        p.aggiungiTraccia(t1);
        p.spostaSu(t1);
        assertEquals(t1, p.getTracce().get(0));
        assertEquals(1, p.getNumeroTracce());
    }

    @Test
    void testSpostaGiu_playlistConUnElemento_nonCambia() {
        Playlist p = new Playlist("Singola");
        p.aggiungiTraccia(t1);
        p.spostaGiu(t1);
        assertEquals(t1, p.getTracce().get(0));
        assertEquals(1, p.getNumeroTracce());
    }
}