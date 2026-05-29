package it.unisa.musicplayer.modello;

import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CatalogoTest {

    private Catalogo catalogo;

    @BeforeEach
    void setUp() {
        catalogo = Catalogo.getInstance();
        catalogo.svuota();
        CatalogoPlaylist.getInstance().svuota();
    }

    @AfterEach
    void tearDown() {
        catalogo.svuota();
        CatalogoPlaylist.getInstance().svuota();
    }

    @Test
    void testGetInstanceRestituisceStessaIstanza() {
        Catalogo altra = Catalogo.getInstance();
        assertSame(catalogo, altra);
    }

    @Test
    void testGetTracceRestituisceListaNonVuotaDopoAggiunta() {
        Traccia t = new Traccia(UUID.randomUUID().toString(), "Bohemian Rhapsody", "Queen", "5:55", "Rock", 1975);
        catalogo.aggiungiTraccia(t);

        ObservableList<Traccia> tracce = catalogo.getTracce();
        assertNotNull(tracce);
        assertEquals(1, tracce.size());
        assertEquals("Bohemian Rhapsody", tracce.get(0).getTitolo());
        assertEquals("Queen", tracce.get(0).getAutore());
    }

    @Test
    void testGetTracceAllAvvioRestituisceListaVuota() {
        ObservableList<Traccia> tracce = catalogo.getTracce();
        assertNotNull(tracce);
        assertTrue(tracce.isEmpty());
    }

    @Test
    void testContieneRestituisceTruePerTracciaEsistente() {
        Traccia t = new Traccia(UUID.randomUUID().toString(), "Imagine", "John Lennon", "3:07", "Pop", 1971);
        catalogo.aggiungiTraccia(t);
        assertTrue(catalogo.contiene("Imagine", "John Lennon"));
    }

    @Test
    void testContieneCaseInsensitive() {
        Traccia t = new Traccia(UUID.randomUUID().toString(), "Imagine", "John Lennon", "3:07", "Pop", 1971);
        catalogo.aggiungiTraccia(t);
        assertTrue(catalogo.contiene("imagine", "john lennon"));
        assertTrue(catalogo.contiene("IMAGINE", "JOHN LENNON"));
    }

    @Test
    void testContieneRestituisceFalsePerTracciaInesistente() {
        assertFalse(catalogo.contiene("Inesistente", "Nessuno"));
    }

    @Test
    void testAggiungiTracciaLanciaEccezionePerDuplicato() {
        Traccia t1 = new Traccia(UUID.randomUUID().toString(), "Song", "Artist", "3:00", "Pop", 2020);
        catalogo.aggiungiTraccia(t1);

        Traccia t2 = new Traccia(UUID.randomUUID().toString(), "Song", "Artist", "4:00", "Rock", 2021);
        assertThrows(IllegalArgumentException.class, () -> catalogo.aggiungiTraccia(t2));
    }

    @Test
    void testGetSizeRestituisceDimensioneCorretta() {
        assertEquals(0, catalogo.getSize());
        catalogo.aggiungiTraccia(new Traccia(UUID.randomUUID().toString(), "A", "B", "1:00", "C", 2000));
        assertEquals(1, catalogo.getSize());
        catalogo.aggiungiTraccia(new Traccia(UUID.randomUUID().toString(), "D", "E", "2:00", "F", 2001));
        assertEquals(2, catalogo.getSize());
    }

    @Test
    void testSvuotaRimuoveTutteLeTracce() {
        catalogo.aggiungiTraccia(new Traccia(UUID.randomUUID().toString(), "A", "B", "1:00", "C", 2000));
        catalogo.aggiungiTraccia(new Traccia(UUID.randomUUID().toString(), "D", "E", "2:00", "F", 2001));
        assertEquals(2, catalogo.getSize());

        catalogo.svuota();
        assertEquals(0, catalogo.getSize());
        assertTrue(catalogo.getTracce().isEmpty());
    }

    @Test
    void testCaricaDaFileSenzaFileNonCrasha() {
        assertDoesNotThrow(() -> catalogo.caricaDaFile());
    }


    @Test
    void testRimuoviTracciaEliminaDalCatalogo() {
        Traccia traccia = new Traccia(UUID.randomUUID().toString(), "Song A", "Artist A", "3:00", "Pop", 2020);

        catalogo.aggiungiTraccia(traccia);
        assertEquals(1, catalogo.getSize());

        catalogo.rimuoviTraccia(traccia);

        assertEquals(0, catalogo.getSize());
        assertFalse(catalogo.getTracce().contains(traccia));
    }

    @Test
    void testRimuoviTracciaNullLanciaEccezione() {
        assertThrows(IllegalArgumentException.class, () -> catalogo.rimuoviTraccia(null));
    }

    @Test
    void testRimuoviTracciaNonPresenteLanciaEccezione() {
        Traccia traccia = new Traccia(UUID.randomUUID().toString(), "Song B", "Artist B", "4:00", "Rock", 2021);

        assertThrows(IllegalArgumentException.class, () -> catalogo.rimuoviTraccia(traccia));
    }

    @Test
    void testRimuoviTracciaEliminaAncheDaTutteLePlaylist() {
        CatalogoPlaylist catalogoPlaylist = CatalogoPlaylist.getInstance();

        Traccia tracciaDaRimuovere = new Traccia(UUID.randomUUID().toString(), "Song C", "Artist C", "3:30", "Pop", 2022);
        Traccia altraTraccia = new Traccia(UUID.randomUUID().toString(), "Song D", "Artist D", "2:45", "Jazz", 2023);

        Playlist playlist1 = new Playlist("Playlist 1");
        Playlist playlist2 = new Playlist("Playlist 2");

        catalogo.aggiungiTraccia(tracciaDaRimuovere);
        catalogo.aggiungiTraccia(altraTraccia);

        playlist1.aggiungiTraccia(tracciaDaRimuovere);
        playlist1.aggiungiTraccia(altraTraccia);
        playlist2.aggiungiTraccia(tracciaDaRimuovere);

        catalogoPlaylist.aggiungiPlaylist(playlist1);
        catalogoPlaylist.aggiungiPlaylist(playlist2);

        catalogo.rimuoviTraccia(tracciaDaRimuovere);

        assertFalse(catalogo.getTracce().contains(tracciaDaRimuovere));

        assertFalse(playlist1.getTracce().contains(tracciaDaRimuovere));
        assertFalse(playlist2.getTracce().contains(tracciaDaRimuovere));

        assertFalse(playlist1.getIdTracce().contains(tracciaDaRimuovere.getId()));
        assertFalse(playlist2.getIdTracce().contains(tracciaDaRimuovere.getId()));

        assertTrue(playlist1.getTracce().contains(altraTraccia));
    }
}
