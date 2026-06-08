package it.unisa.musicplayer.servizi;

import it.unisa.musicplayer.modello.Catalogo;
import it.unisa.musicplayer.modello.CatalogoPlaylist;
import it.unisa.musicplayer.modello.Playlist;
import it.unisa.musicplayer.modello.Traccia;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GeneratorePlaylistAutomaticaTest {

    private Catalogo catalogo;
    private GeneratorePlaylistAutomatica generatore;

    @BeforeEach
    void setUp() {
        catalogo = Catalogo.getInstance();
        catalogo.svuota();
        CatalogoPlaylist.getInstance().svuota();
        generatore = new GeneratorePlaylistAutomatica();
    }

    @AfterEach
    void tearDown() {
        catalogo.svuota();
        CatalogoPlaylist.getInstance().svuota();
    }

    private Traccia traccia(String titolo, String autore, String genere, int anno) {
        return new Traccia(UUID.randomUUID().toString(), titolo, autore, "3:00", genere, anno);
    }

    // ── filtraPerGenere ────────────────────────────────────────────────────────

    @Test
    void testFiltraPerGenereRestituisceTracceDelGenere() {
        catalogo.aggiungiTraccia(traccia("Song A", "Artist A", "Rock", 2000));
        catalogo.aggiungiTraccia(traccia("Song B", "Artist B", "Pop", 2001));
        catalogo.aggiungiTraccia(traccia("Song C", "Artist C", "Rock", 2002));

        List<Traccia> risultato = generatore.filtraPerGenere(catalogo, "Rock");

        assertEquals(2, risultato.size());
        assertTrue(risultato.stream().allMatch(t -> t.getGenere().equalsIgnoreCase("Rock")));
    }

    @Test
    void testFiltraPerGenereCaseInsensitive() {
        catalogo.aggiungiTraccia(traccia("Song A", "Artist A", "Rock", 2000));

        List<Traccia> risultato = generatore.filtraPerGenere(catalogo, "rock");

        assertEquals(1, risultato.size());
    }

    @Test
    void testFiltraPerGenereRestituisceListaVuotaSeNessunBranoCorresp() {
        catalogo.aggiungiTraccia(traccia("Song A", "Artist A", "Pop", 2000));

        List<Traccia> risultato = generatore.filtraPerGenere(catalogo, "Jazz");

        assertTrue(risultato.isEmpty());
    }

    @Test
    void testFiltraPerGenereRestituisceListaVuotaSuCatalogoVuoto() {
        assertTrue(generatore.filtraPerGenere(catalogo, "Rock").isEmpty());
    }

    @Test
    void testFiltraPerGenereLanciaEccezioneSeCatalogoNull() {
        assertThrows(IllegalArgumentException.class,
                () -> generatore.filtraPerGenere(null, "Rock"));
    }

    @Test
    void testFiltraPerGenereLanciaEccezioneSeGenereNull() {
        assertThrows(IllegalArgumentException.class,
                () -> generatore.filtraPerGenere(catalogo, null));
    }

    @Test
    void testFiltraPerGenereLanciaEccezioneSeGenereVuoto() {
        assertThrows(IllegalArgumentException.class,
                () -> generatore.filtraPerGenere(catalogo, "  "));
    }

    // ── filtraPerAnno ──────────────────────────────────────────────────────────

    @Test
    void testFiltraPerAnnoRestituisceTracceConAnnoCorretto() {
        catalogo.aggiungiTraccia(traccia("Song A", "Artist A", "Rock", 2020));
        catalogo.aggiungiTraccia(traccia("Song B", "Artist B", "Pop", 2021));
        catalogo.aggiungiTraccia(traccia("Song C", "Artist C", "Jazz", 2020));

        List<Traccia> risultato = generatore.filtraPerAnno(catalogo, 2020);

        assertEquals(2, risultato.size());
        assertTrue(risultato.stream().allMatch(t -> t.getAnno() == 2020));
    }

    @Test
    void testFiltraPerAnnoRestituisceListaVuotaSeNessunBranoCorresp() {
        catalogo.aggiungiTraccia(traccia("Song A", "Artist A", "Rock", 2020));

        List<Traccia> risultato = generatore.filtraPerAnno(catalogo, 1999);

        assertTrue(risultato.isEmpty());
    }

    @Test
    void testFiltraPerAnnoRestituisceListaVuotaSuCatalogoVuoto() {
        assertTrue(generatore.filtraPerAnno(catalogo, 2020).isEmpty());
    }

    @Test
    void testFiltraPerAnnoLanciaEccezioneSeCatalogoNull() {
        assertThrows(IllegalArgumentException.class,
                () -> generatore.filtraPerAnno(null, 2020));
    }

    @Test
    void testFiltraPerAnnoLanciaEccezioneSeAnnoZero() {
        assertThrows(IllegalArgumentException.class,
                () -> generatore.filtraPerAnno(catalogo, 0));
    }

    @Test
    void testFiltraPerAnnoLanciaEccezioneSeAnnoNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> generatore.filtraPerAnno(catalogo, -1));
    }

    // ── generaPerGenere ────────────────────────────────────────────────────────

    @Test
    void testGeneraPerGenereCreaNomeCorretto() {
        catalogo.aggiungiTraccia(traccia("Song A", "Artist A", "Rock", 2000));

        Playlist p = generatore.generaPerGenere(catalogo, "Rock");

        assertEquals("Auto - Rock", p.getNome());
    }

    @Test
    void testGeneraPerGenereContieneSoloTracceDelGenere() {
        Traccia rock1 = traccia("Song A", "Artist A", "Rock", 2000);
        Traccia rock2 = traccia("Song C", "Artist C", "Rock", 2002);
        catalogo.aggiungiTraccia(rock1);
        catalogo.aggiungiTraccia(traccia("Song B", "Artist B", "Pop", 2001));
        catalogo.aggiungiTraccia(rock2);

        Playlist p = generatore.generaPerGenere(catalogo, "Rock");

        assertEquals(2, p.getNumeroTracce());
        assertTrue(p.getTracce().contains(rock1));
        assertTrue(p.getTracce().contains(rock2));
    }

    @Test
    void testGeneraPerGenerePlaylistVuotaSeNessunBranoCorresp() {
        catalogo.aggiungiTraccia(traccia("Song A", "Artist A", "Pop", 2000));

        Playlist p = generatore.generaPerGenere(catalogo, "Jazz");

        assertEquals(0, p.getNumeroTracce());
    }

    @Test
    void testGeneraPerGenereNonModificaIlCatalogo() {
        catalogo.aggiungiTraccia(traccia("Song A", "Artist A", "Rock", 2000));
        int sizePrima = catalogo.getSize();

        generatore.generaPerGenere(catalogo, "Rock");

        assertEquals(sizePrima, catalogo.getSize());
    }

    // ── generaPerAnno ──────────────────────────────────────────────────────────

    @Test
    void testGeneraPerAnnoCreaNomeCorretto() {
        catalogo.aggiungiTraccia(traccia("Song A", "Artist A", "Rock", 2020));

        Playlist p = generatore.generaPerAnno(catalogo, 2020);

        assertEquals("Auto - 2020", p.getNome());
    }

    @Test
    void testGeneraPerAnnoContieneSoloTracceConAnnoCorretto() {
        Traccia t2020a = traccia("Song A", "Artist A", "Rock", 2020);
        Traccia t2020b = traccia("Song C", "Artist C", "Jazz", 2020);
        catalogo.aggiungiTraccia(t2020a);
        catalogo.aggiungiTraccia(traccia("Song B", "Artist B", "Pop", 2021));
        catalogo.aggiungiTraccia(t2020b);

        Playlist p = generatore.generaPerAnno(catalogo, 2020);

        assertEquals(2, p.getNumeroTracce());
        assertTrue(p.getTracce().contains(t2020a));
        assertTrue(p.getTracce().contains(t2020b));
    }

    @Test
    void testGeneraPerAnnoPlaylistVuotaSeNessunBranoCorresp() {
        catalogo.aggiungiTraccia(traccia("Song A", "Artist A", "Rock", 2020));

        Playlist p = generatore.generaPerAnno(catalogo, 1999);

        assertEquals(0, p.getNumeroTracce());
    }

    @Test
    void testGeneraPerAnnoNonModificaIlCatalogo() {
        catalogo.aggiungiTraccia(traccia("Song A", "Artist A", "Rock", 2020));
        int sizePrima = catalogo.getSize();

        generatore.generaPerAnno(catalogo, 2020);

        assertEquals(sizePrima, catalogo.getSize());
    }

    // ── nomePlaylistAutomatica ─────────────────────────────────────────────────

    @Test
    void testNomePlaylistAutomaticaConStringa() {
        assertEquals("Auto - Rock", generatore.nomePlaylistAutomatica("Rock"));
    }

    @Test
    void testNomePlaylistAutomaticaConAnno() {
        assertEquals("Auto - 2020", generatore.nomePlaylistAutomatica(2020));
    }

    @Test
    void testNomePlaylistAutomaticaLanciaEccezioneSeNull() {
        assertThrows(IllegalArgumentException.class,
                () -> generatore.nomePlaylistAutomatica(null));
    }
}