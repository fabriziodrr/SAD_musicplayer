package it.unisa.musicplayer.servizi;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unisa.musicplayer.modello.Traccia;

class LettoreTest {

    private Lettore lettore;
    private Traccia traccia1;
    private Traccia traccia2;
    private Traccia traccia3;

    @BeforeEach
    void setUp() {
        lettore = new Lettore();
        lettore.setModalita(new Sequenziale());
        traccia1 = new Traccia(UUID.randomUUID().toString(), "Bohemian Rhapsody", "Queen", "5:55", "Rock", 1975);
        traccia2 = new Traccia(UUID.randomUUID().toString(), "Hotel California", "Eagles", "6:30", "Rock", 1977);
        traccia3 = new Traccia(UUID.randomUUID().toString(), "Stairway to Heaven", "Led Zeppelin", "8:02", "Rock", 1971);
    }

    @AfterEach
    void tearDown() {
        lettore = null;
    }

    // ── Transizioni di stato ──────────────────────────────────────────────────

    @Test
    void testStatoInizialeEStopped() {
        assertEquals(StatoLettore.STOPPED, lettore.getStato());
    }

    @Test
    void testStoppedToPlaying() {
        List<Traccia> coda = new ArrayList<>();
        coda.add(traccia1);
        lettore.aggiornaCodeTracce(coda);
        lettore.play();
        assertEquals(StatoLettore.PLAYING, lettore.getStato());
    }

    @Test
    void testPlayingToPaused() {
        List<Traccia> coda = new ArrayList<>();
        coda.add(traccia1);
        lettore.aggiornaCodeTracce(coda);
        lettore.play();
        lettore.pausa();
        assertEquals(StatoLettore.PAUSED, lettore.getStato());
    }

    @Test
    void testPausedToPlaying() {
        List<Traccia> coda = new ArrayList<>();
        coda.add(traccia1);
        lettore.aggiornaCodeTracce(coda);
        lettore.play();
        lettore.pausa();
        lettore.play();
        assertEquals(StatoLettore.PLAYING, lettore.getStato());
    }

    @Test
    void testPlayingToStopped() {
        List<Traccia> coda = new ArrayList<>();
        coda.add(traccia1);
        lettore.aggiornaCodeTracce(coda);
        lettore.play();
        lettore.stop();
        assertEquals(StatoLettore.STOPPED, lettore.getStato());
    }

    @Test
    void testPausaNonAttivaSeNonInPlaying() {
        List<Traccia> coda = new ArrayList<>();
        coda.add(traccia1);
        lettore.aggiornaCodeTracce(coda);
        lettore.pausa();
        assertEquals(StatoLettore.STOPPED, lettore.getStato());
    }

    // ── Gestione Queue ────────────────────────────────────────────────────────

    @Test
    void testSkipAvanzaTraccia() {
        List<Traccia> coda = new ArrayList<>();
        coda.add(traccia1);
        coda.add(traccia2);
        coda.add(traccia3);
        lettore.aggiornaCodeTracce(coda);
        lettore.play();
        lettore.skip();
        assertEquals(traccia2, lettore.getTracciaCorrente());
    }

    @Test
    void testSkipUltimaTracciaInSequenzialeTerminaRiproduzione() {
        List<Traccia> coda = new ArrayList<>();
        coda.add(traccia1);

        lettore.aggiornaCodeTracce(coda);
        lettore.play();
        lettore.skip();

        assertNull(lettore.getTracciaCorrente());
        assertEquals(StatoLettore.STOPPED, lettore.getStato());
    }

    @Test
    void testSkipDueVolte() {
        List<Traccia> coda = new ArrayList<>();
        coda.add(traccia1);
        coda.add(traccia2);
        coda.add(traccia3);
        lettore.aggiornaCodeTracce(coda);
        lettore.play();
        lettore.skip();
        lettore.skip();
        assertEquals(traccia3, lettore.getTracciaCorrente());
    }

    @Test
    void testAggiornaCodeTracceImpostaTracciaCorrente() {
        List<Traccia> coda = new ArrayList<>();
        coda.add(traccia1);
        coda.add(traccia2);
        lettore.aggiornaCodeTracce(coda);
        assertEquals(traccia1, lettore.getTracciaCorrente());
    }

    @Test
    void testAggiornaCodeTracceAzzeraTempoTrascorso() {
        List<Traccia> coda = new ArrayList<>();
        coda.add(traccia1);
        lettore.aggiornaCodeTracce(coda);
        lettore.play();
        lettore.avanzaTempo(10);
        lettore.aggiornaCodeTracce(coda);
        assertEquals(0, lettore.getTempoTrascorso());
    }

    // ── Tempo ─────────────────────────────────────────────────────────────────

    @Test
    void testAvanzaTempoSoloInPlaying() {
        List<Traccia> coda = new ArrayList<>();
        coda.add(traccia1);
        lettore.aggiornaCodeTracce(coda);
        lettore.avanzaTempo(5);
        assertEquals(0, lettore.getTempoTrascorso());
        lettore.play();
        lettore.avanzaTempo(5);
        assertEquals(5, lettore.getTempoTrascorso());
    }

    @Test
    void testTempoNonAvanzaInPausa() {
        List<Traccia> coda = new ArrayList<>();
        coda.add(traccia1);
        lettore.aggiornaCodeTracce(coda);
        lettore.play();
        lettore.avanzaTempo(5);
        lettore.pausa();
        lettore.avanzaTempo(5);
        assertEquals(5, lettore.getTempoTrascorso());
    }

    @Test
    void testTempoNonAvanzaInStopped() {
        List<Traccia> coda = new ArrayList<>();
        coda.add(traccia1);
        lettore.aggiornaCodeTracce(coda);
        lettore.play();
        lettore.avanzaTempo(5);
        lettore.stop();
        lettore.avanzaTempo(5);
        assertEquals(0, lettore.getTempoTrascorso());
    }

    @Test
    void testTempoAzzeratoDopoSkip() {
        List<Traccia> coda = new ArrayList<>();
        coda.add(traccia1);
        coda.add(traccia2);
        lettore.aggiornaCodeTracce(coda);
        lettore.play();
        lettore.avanzaTempo(10);
        lettore.skip();
        assertEquals(0, lettore.getTempoTrascorso());
    }

    @Test
    void testTempoAzzeratoDopoStop() {
        List<Traccia> coda = new ArrayList<>();
        coda.add(traccia1);
        lettore.aggiornaCodeTracce(coda);
        lettore.play();
        lettore.avanzaTempo(30);
        lettore.stop();
        assertEquals(0, lettore.getTempoTrascorso());
    }

    @Test
    void testLettoreConLoopRiparteDallaPrimaTraccia() {
        List<Traccia> coda = List.of(traccia1, traccia2, traccia3);

        lettore.aggiornaCodeTracce(coda);
        lettore.setModalita(new Loop());
        lettore.play();

        lettore.skip();
        assertEquals(traccia2, lettore.getTracciaCorrente());

        lettore.skip();
        assertEquals(traccia3, lettore.getTracciaCorrente());

        lettore.skip();
        assertEquals(traccia1, lettore.getTracciaCorrente());
        assertEquals(StatoLettore.PLAYING, lettore.getStato());
    }

    @Test
    void testLettoreConShuffleNonRipeteConsecutivamenteLaTraccia() {
        List<Traccia> coda = List.of(traccia1, traccia2, traccia3);

        lettore.aggiornaCodeTracce(coda);
        lettore.setModalita(new Shuffle());
        lettore.play();

        Traccia precedente = lettore.getTracciaCorrente();

        for (int i = 0; i < 20; i++) {
            lettore.skip();

            Traccia corrente = lettore.getTracciaCorrente();

            assertNotNull(corrente);
            assertTrue(coda.contains(corrente));
            assertNotEquals(precedente, corrente);

            precedente = corrente;
        }
    }


    @Test
    void testCambioModalitaDuranteLaRiproduzione() {
        List<Traccia> coda = List.of(traccia1, traccia2);

        lettore.aggiornaCodeTracce(coda);
        lettore.setModalita(new Sequenziale());
        lettore.play();

        lettore.skip();
        assertEquals(traccia2, lettore.getTracciaCorrente());

        lettore.setModalita(new Loop());
        lettore.skip();

        assertEquals(traccia1, lettore.getTracciaCorrente());
        assertEquals(StatoLettore.PLAYING, lettore.getStato());
    }


    @Test
    void testSetModalitaNullLanciaEccezione() {
        assertThrows(
                NullPointerException.class,
                () -> lettore.setModalita(null)
        );
    }





}