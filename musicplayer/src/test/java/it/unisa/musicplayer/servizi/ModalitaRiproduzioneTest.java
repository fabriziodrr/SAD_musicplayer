package it.unisa.musicplayer.servizi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


import java.util.List;
import java.util.UUID;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unisa.musicplayer.modello.Traccia;

class ModalitaRiproduzioneTest {

    private Traccia traccia1;
    private Traccia traccia2;
    private Traccia traccia3;

    @BeforeEach
    void setUp() {
        traccia1 = new Traccia(
                UUID.randomUUID().toString(),
                "Traccia 1",
                "Artista 1",
                "3:00",
                "Rock",
                2020
        );

        traccia2 = new Traccia(
                UUID.randomUUID().toString(),
                "Traccia 2",
                "Artista 2",
                "4:00",
                "Pop",
                2021
        );

        traccia3 = new Traccia(
                UUID.randomUUID().toString(),
                "Traccia 3",
                "Artista 3",
                "5:00",
                "Jazz",
                2022
        );
    }

    // ── Modalità Sequenziale ────────────────────────────────────────────────

    @Test
    void testSequenzialeListaVuotaRestituisceNull() {
        ModalitaRiproduzione modalita = new Sequenziale();

        Traccia risultato = modalita.prossimaTraccia(List.of(), null);

        assertNull(risultato);
    }

    @Test
    void testSequenzialeConUnaTracciaTerminaAllaFine() {
        ModalitaRiproduzione modalita = new Sequenziale();
        List<Traccia> tracce = List.of(traccia1);

        Traccia risultato = modalita.prossimaTraccia(tracce, traccia1);

        assertNull(risultato);
    }

    @Test
    void testSequenzialeRestituisceLaTracciaSuccessiva() {
        ModalitaRiproduzione modalita = new Sequenziale();
        List<Traccia> tracce = List.of(traccia1, traccia2, traccia3);

        assertEquals(
                traccia2,
                modalita.prossimaTraccia(tracce, traccia1)
        );

        assertEquals(
                traccia3,
                modalita.prossimaTraccia(tracce, traccia2)
        );
    }

    @Test
    void testSequenzialeUltimaTracciaRestituisceNull() {
        ModalitaRiproduzione modalita = new Sequenziale();
        List<Traccia> tracce = List.of(traccia1, traccia2, traccia3);

        Traccia risultato = modalita.prossimaTraccia(tracce, traccia3);

        assertNull(risultato);
    }

    @Test
    void testSequenzialeSenzaTracciaCorrenteParteDallaPrima() {
        ModalitaRiproduzione modalita = new Sequenziale();
        List<Traccia> tracce = List.of(traccia1, traccia2, traccia3);

        Traccia risultato = modalita.prossimaTraccia(tracce, null);

        assertEquals(traccia1, risultato);
    }

    // Modalità Loop

    @Test
    void testLoopListaVuotaRestituisceNull() {
        ModalitaRiproduzione modalita = new Loop();

        Traccia risultato = modalita.prossimaTraccia(List.of(), null);

        assertNull(risultato);
    }

    @Test
    void testLoopConUnaTracciaRiproduceLaStessaTraccia() {
        ModalitaRiproduzione modalita = new Loop();
        List<Traccia> tracce = List.of(traccia1);

        Traccia risultato = modalita.prossimaTraccia(tracce, traccia1);

        assertEquals(traccia1, risultato);
    }

    @Test
    void testLoopRestituisceLaTracciaSuccessiva() {
        ModalitaRiproduzione modalita = new Loop();
        List<Traccia> tracce = List.of(traccia1, traccia2, traccia3);

        assertEquals(
                traccia2,
                modalita.prossimaTraccia(tracce, traccia1)
        );

        assertEquals(
                traccia3,
                modalita.prossimaTraccia(tracce, traccia2)
        );
    }

    @Test
    void testLoopDopoUltimaTracciaRiparteDallaPrima() {
        ModalitaRiproduzione modalita = new Loop();
        List<Traccia> tracce = List.of(traccia1, traccia2, traccia3);

        Traccia risultato = modalita.prossimaTraccia(tracce, traccia3);

        assertEquals(traccia1, risultato);
    }

    @Test
    void testLoopSenzaTracciaCorrenteParteDallaPrima() {
        ModalitaRiproduzione modalita = new Loop();
        List<Traccia> tracce = List.of(traccia1, traccia2, traccia3);

        Traccia risultato = modalita.prossimaTraccia(tracce, null);

        assertEquals(traccia1, risultato);
    }

    // Modalità Shuffle

    @Test
    void testShuffleListaVuotaRestituisceNull() {
        ModalitaRiproduzione modalita = new Shuffle();

        Traccia risultato = modalita.prossimaTraccia(List.of(), null);

        assertNull(risultato);
    }

    @Test
    void testShuffleConUnaTracciaRestituisceLaStessaTraccia() {
        ModalitaRiproduzione modalita = new Shuffle();
        List<Traccia> tracce = List.of(traccia1);

        Traccia risultato = modalita.prossimaTraccia(tracce, traccia1);

        assertEquals(traccia1, risultato);
    }

    @Test
    void testShuffleConPiuTracceNonRipeteLaCorrente() {
        ModalitaRiproduzione modalita = new Shuffle();
        List<Traccia> tracce = List.of(traccia1, traccia2, traccia3);

        Traccia corrente = traccia1;

        /*
         * Ripetiamo il controllo più volte perché l'ordine
         * è casuale, ma la traccia consecutiva non deve
         * mai essere uguale a quella corrente.
         */
        for (int i = 0; i < 30; i++) {
            Traccia prossima = modalita.prossimaTraccia(tracce, corrente);

            assertNotNull(prossima);
            assertTrue(tracce.contains(prossima));
            assertNotEquals(corrente, prossima);

            corrente = prossima;
        }
    }

    @Test
    void testShuffleNonRipeteTraccePrimaDiCompletareIlCiclo() {
        Shuffle modalita = new Shuffle();
        List<Traccia> tracce = List.of(traccia1, traccia2, traccia3);

        Traccia seconda = modalita.prossimaTraccia(tracce, traccia1);
        Traccia terza = modalita.prossimaTraccia(tracce, seconda);

        Set<Traccia> tracceRiprodotte = new HashSet<>();
        tracceRiprodotte.add(traccia1);
        tracceRiprodotte.add(seconda);
        tracceRiprodotte.add(terza);

        assertEquals(3, tracceRiprodotte.size());
    }

    @Test
    void testShuffleSenzaTracciaCorrenteRestituisceUnaTracciaDellaLista() {
        ModalitaRiproduzione modalita = new Shuffle();
        List<Traccia> tracce = List.of(traccia1, traccia2, traccia3);

        Traccia risultato = modalita.prossimaTraccia(tracce, null);

        assertNotNull(risultato);
        assertTrue(tracce.contains(risultato));
    }

}