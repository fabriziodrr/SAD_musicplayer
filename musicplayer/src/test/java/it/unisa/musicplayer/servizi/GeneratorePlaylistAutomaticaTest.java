package it.unisa.musicplayer.servizi;

import it.unisa.musicplayer.modello.Catalogo;
import it.unisa.musicplayer.modello.CatalogoPlaylist;
import it.unisa.musicplayer.modello.Playlist;
import it.unisa.musicplayer.modello.Traccia;
import it.unisa.musicplayer.modello.Tag;
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

    // ิ๖วิ๖ว filtraPerGenere ิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖ว

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

    // ิ๖วิ๖ว filtraPerAnno ิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖ว

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

    // ิ๖วิ๖ว generaPerGenere ิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖ว

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

    // ิ๖วิ๖ว generaPerAnno ิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖ว

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

    // ิ๖วิ๖ว nomePlaylistAutomatica ิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖ว

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
    // ิ๖วิ๖ว filtraPerTag - US-18 ิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖ว

    @Test
    void testFiltraPerTagRestituisceSoloTracceConTagRichiesto() {
        Traccia preferita1 =
                traccia("Preferita 1", "Artista 1", "Rock", 2024);

        Traccia preferita2 =
                traccia("Preferita 2", "Artista 2", "Pop", 2023);

        Traccia senzaTag =
                traccia("Senza tag", "Artista 3", "Jazz", 2022);

        preferita1.aggiungiTag(Tag.FAVOURITE);
        preferita2.aggiungiTag(Tag.FAVOURITE);

        catalogo.aggiungiTraccia(preferita1);
        catalogo.aggiungiTraccia(preferita2);
        catalogo.aggiungiTraccia(senzaTag);

        List<Traccia> risultato =
                generatore.filtraPerTag(
                        catalogo,
                        Tag.FAVOURITE
                );

        assertEquals(2, risultato.size());
        assertTrue(risultato.contains(preferita1));
        assertTrue(risultato.contains(preferita2));
        assertFalse(risultato.contains(senzaTag));

        assertTrue(
                risultato.stream()
                        .allMatch(t -> t.hasTag(Tag.FAVOURITE))
        );
    }

    @Test
    void testFiltraPerTagGestisceTracciaConPiuTag() {
        Traccia tracciaMultipla =
                traccia("Multipla", "Artista", "Rock", 2024);

        Traccia soloPreferita =
                traccia("Preferita", "Artista", "Pop", 2023);

        tracciaMultipla.aggiungiTag(Tag.FAVOURITE);
        tracciaMultipla.aggiungiTag(Tag.EXPLICIT);

        soloPreferita.aggiungiTag(Tag.FAVOURITE);

        catalogo.aggiungiTraccia(tracciaMultipla);
        catalogo.aggiungiTraccia(soloPreferita);

        List<Traccia> preferite =
                generatore.filtraPerTag(
                        catalogo,
                        Tag.FAVOURITE
                );

        List<Traccia> esplicite =
                generatore.filtraPerTag(
                        catalogo,
                        Tag.EXPLICIT
                );

        assertEquals(2, preferite.size());
        assertTrue(preferite.contains(tracciaMultipla));
        assertTrue(preferite.contains(soloPreferita));

        assertEquals(1, esplicite.size());
        assertTrue(esplicite.contains(tracciaMultipla));
    }

    @Test
    void testFiltraPerTagRestituisceListaVuotaSeNessunaCorrispondenza() {
        Traccia tracciaSenzaTag =
                traccia("Senza tag", "Artista", "Rock", 2024);

        catalogo.aggiungiTraccia(tracciaSenzaTag);

        List<Traccia> risultato =
                generatore.filtraPerTag(
                        catalogo,
                        Tag.NEW_RELEASE
                );

        assertNotNull(risultato);
        assertTrue(risultato.isEmpty());
    }

    @Test
    void testFiltraPerTagRestituisceListaVuotaConCatalogoVuoto() {
        List<Traccia> risultato =
                generatore.filtraPerTag(
                        catalogo,
                        Tag.FAVOURITE
                );

        assertNotNull(risultato);
        assertTrue(risultato.isEmpty());
    }

    @Test
    void testFiltraPerTagLanciaEccezioneSeCatalogoNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> generatore.filtraPerTag(
                        null,
                        Tag.FAVOURITE
                )
        );
    }

    @Test
    void testFiltraPerTagLanciaEccezioneSeTagNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> generatore.filtraPerTag(
                        catalogo,
                        null
                )
        );
    }

// ิ๖วิ๖ว generaPerTag - US-18 ิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖วิ๖ว

    @Test
    void testGeneraPerTagCreaNomiCorretti() {
        Playlist preferiti =
                generatore.generaPerTag(
                        catalogo,
                        Tag.FAVOURITE
                );

        Playlist espliciti =
                generatore.generaPerTag(
                        catalogo,
                        Tag.EXPLICIT
                );

        Playlist nuoveUscite =
                generatore.generaPerTag(
                        catalogo,
                        Tag.NEW_RELEASE
                );

        assertEquals(
                "Auto - Preferiti",
                preferiti.getNome()
        );

        assertEquals(
                "Auto - Espliciti",
                espliciti.getNome()
        );

        assertEquals(
                "Auto - Nuove Uscite",
                nuoveUscite.getNome()
        );
    }

    @Test
    void testGeneraPerTagContieneSoloTracceCorrispondenti() {
        Traccia preferita =
                traccia("Preferita", "Artista 1", "Rock", 2024);

        Traccia esplicita =
                traccia("Esplicita", "Artista 2", "Pop", 2023);

        Traccia senzaTag =
                traccia("Senza tag", "Artista 3", "Jazz", 2022);

        preferita.aggiungiTag(Tag.FAVOURITE);
        esplicita.aggiungiTag(Tag.EXPLICIT);

        catalogo.aggiungiTraccia(preferita);
        catalogo.aggiungiTraccia(esplicita);
        catalogo.aggiungiTraccia(senzaTag);

        Playlist playlist =
                generatore.generaPerTag(
                        catalogo,
                        Tag.FAVOURITE
                );

        assertEquals(1, playlist.getNumeroTracce());
        assertTrue(playlist.getTracce().contains(preferita));
        assertFalse(playlist.getTracce().contains(esplicita));
        assertFalse(playlist.getTracce().contains(senzaTag));
    }

    @Test
    void testGeneraPerTagRestituiscePlaylistVuotaSeNessunaCorrispondenza() {
        Traccia tracciaSenzaTag =
                traccia("Senza tag", "Artista", "Rock", 2024);

        catalogo.aggiungiTraccia(tracciaSenzaTag);

        Playlist playlist =
                generatore.generaPerTag(
                        catalogo,
                        Tag.NEW_RELEASE
                );

        assertEquals(
                "Auto - Nuove Uscite",
                playlist.getNome()
        );

        assertEquals(0, playlist.getNumeroTracce());
        assertTrue(playlist.getTracce().isEmpty());
    }

    @Test
    void testGeneraPerTagNonModificaIlCatalogo() {
        Traccia preferita =
                traccia("Preferita", "Artista", "Rock", 2024);

        preferita.aggiungiTag(Tag.FAVOURITE);
        catalogo.aggiungiTraccia(preferita);

        int numeroTraccePrima = catalogo.getSize();

        generatore.generaPerTag(
                catalogo,
                Tag.FAVOURITE
        );

        assertEquals(
                numeroTraccePrima,
                catalogo.getSize()
        );

        assertTrue(
                catalogo.getTracce().contains(preferita)
        );
    }

    @Test
    void testGeneraPerTagLanciaEccezioneSeCatalogoNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> generatore.generaPerTag(
                        null,
                        Tag.FAVOURITE
                )
        );
    }

    @Test
    void testGeneraPerTagLanciaEccezioneSeTagNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> generatore.generaPerTag(
                        catalogo,
                        null
                )
        );
    }

    // Sincronizzazione playlist automatiche per tag - US-18

    @Test
    void testSincronizzaRimuoveTracciaDopoRimozioneTag() {
        Traccia prima =
                traccia(
                        "Prima",
                        "Artista 1",
                        "Rock",
                        2024
                );

        Traccia seconda =
                traccia(
                        "Seconda",
                        "Artista 2",
                        "Pop",
                        2025
                );

        prima.aggiungiTag(Tag.FAVOURITE);
        seconda.aggiungiTag(Tag.FAVOURITE);

        catalogo.aggiungiTraccia(prima);
        catalogo.aggiungiTraccia(seconda);

        Playlist playlist =
                generatore.generaPerTag(
                        catalogo,
                        Tag.FAVOURITE
                );

        CatalogoPlaylist.getInstance()
                .aggiungiPlaylist(playlist);

        /*
         * Simula la modifica della prima traccia:
         * stesso ID, ma senza il tag Preferito.
         */
        Traccia primaModificata =
                new Traccia(
                        prima.getId(),
                        prima.getTitolo(),
                        prima.getAutore(),
                        prima.getDurata(),
                        prima.getGenere(),
                        prima.getAnno()
                );

        catalogo.modificaTraccia(
                prima,
                primaModificata
        );

        int modificate =
                generatore
                        .sincronizzaPlaylistAutomatichePerTag(
                                catalogo,
                                CatalogoPlaylist.getInstance()
                        );

        assertEquals(1, modificate);
        assertEquals(1, playlist.getNumeroTracce());

        assertFalse(
                playlist.getTracce()
                        .contains(primaModificata)
        );

        assertTrue(
                playlist.getTracce()
                        .contains(seconda)
        );
    }

    @Test
    void testSincronizzaEliminaPlaylistSeRimaneVuota() {
        Traccia preferita =
                traccia(
                        "Preferita",
                        "Artista",
                        "Rock",
                        2024
                );

        preferita.aggiungiTag(Tag.FAVOURITE);
        catalogo.aggiungiTraccia(preferita);

        Playlist playlist =
                generatore.generaPerTag(
                        catalogo,
                        Tag.FAVOURITE
                );

        CatalogoPlaylist.getInstance()
                .aggiungiPlaylist(playlist);

        Traccia senzaTag =
                new Traccia(
                        preferita.getId(),
                        preferita.getTitolo(),
                        preferita.getAutore(),
                        preferita.getDurata(),
                        preferita.getGenere(),
                        preferita.getAnno()
                );

        catalogo.modificaTraccia(
                preferita,
                senzaTag
        );

        generatore
                .sincronizzaPlaylistAutomatichePerTag(
                        catalogo,
                        CatalogoPlaylist.getInstance()
                );

        assertFalse(
                CatalogoPlaylist.getInstance()
                        .contiene("Auto - Preferiti")
        );
    }

    @Test
    void testSincronizzaAggiungeTracciaDopoAggiuntaTag() {
        Traccia esplicita =
                traccia(
                        "Esplicita",
                        "Artista 1",
                        "Rock",
                        2024
                );

        esplicita.aggiungiTag(Tag.EXPLICIT);
        catalogo.aggiungiTraccia(esplicita);

        Traccia senzaTag =
                traccia(
                        "Seconda",
                        "Artista 2",
                        "Pop",
                        2025
                );

        catalogo.aggiungiTraccia(senzaTag);

        Playlist playlist =
                generatore.generaPerTag(
                        catalogo,
                        Tag.EXPLICIT
                );

        CatalogoPlaylist.getInstance()
                .aggiungiPlaylist(playlist);

        Traccia secondaEsplicita =
                new Traccia(
                        senzaTag.getId(),
                        senzaTag.getTitolo(),
                        senzaTag.getAutore(),
                        senzaTag.getDurata(),
                        senzaTag.getGenere(),
                        senzaTag.getAnno()
                );

        secondaEsplicita.aggiungiTag(
                Tag.EXPLICIT
        );

        catalogo.modificaTraccia(
                senzaTag,
                secondaEsplicita
        );

        generatore
                .sincronizzaPlaylistAutomatichePerTag(
                        catalogo,
                        CatalogoPlaylist.getInstance()
                );

        assertEquals(2, playlist.getNumeroTracce());

        assertTrue(
                playlist.getTracce()
                        .contains(esplicita)
        );

        assertTrue(
                playlist.getTracce()
                        .contains(secondaEsplicita)
        );
    }

    @Test
    void testSincronizzaNonCreaPlaylistMaiGenerata() {
        Traccia preferita =
                traccia(
                        "Preferita",
                        "Artista",
                        "Rock",
                        2024
                );

        preferita.aggiungiTag(Tag.FAVOURITE);
        catalogo.aggiungiTraccia(preferita);

        int modificate =
                generatore
                        .sincronizzaPlaylistAutomatichePerTag(
                                catalogo,
                                CatalogoPlaylist.getInstance()
                        );

        assertEquals(0, modificate);

        assertFalse(
                CatalogoPlaylist.getInstance()
                        .contiene("Auto - Preferiti")
        );
    }

    @Test
    void testSincronizzaNonModificaPlaylistManuale() {
        Traccia traccia =
                traccia(
                        "Brano",
                        "Artista",
                        "Rock",
                        2024
                );

        traccia.aggiungiTag(Tag.NEW_RELEASE);
        catalogo.aggiungiTraccia(traccia);

        Playlist manuale =
                new Playlist("Le mie novit+แ");

        manuale.aggiungiTraccia(traccia);

        CatalogoPlaylist.getInstance()
                .aggiungiPlaylist(manuale);

        Traccia modificata =
                new Traccia(
                        traccia.getId(),
                        traccia.getTitolo(),
                        traccia.getAutore(),
                        traccia.getDurata(),
                        traccia.getGenere(),
                        traccia.getAnno()
                );

        catalogo.modificaTraccia(
                traccia,
                modificata
        );

        generatore
                .sincronizzaPlaylistAutomatichePerTag(
                        catalogo,
                        CatalogoPlaylist.getInstance()
                );

        assertTrue(
                CatalogoPlaylist.getInstance()
                        .contiene("Le mie novit+แ")
        );

        assertEquals(
                1,
                manuale.getNumeroTracce()
        );
    }

}
