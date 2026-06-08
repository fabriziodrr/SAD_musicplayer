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

    // ── 1. TEST STRUTTURA E SINGLETON ─────────────────────────────────────────

    @Test
    void testGetInstanceRestituisceStessaIstanza() {
        Catalogo altra = Catalogo.getInstance();
        assertSame(catalogo, altra);
    }

    @Test
    void testGetTracceAllAvvioRestituisceListaVuota() {
        ObservableList<Traccia> tracce = catalogo.getTracce();
        assertNotNull(tracce);
        assertTrue(tracce.isEmpty());
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

    // ── 2. TEST AGGIUNTA TRACCIA ───────────────────────────────────────

    @Test
    void testGetTracceRestituisceListaNonVuotaDopoAggiunta() {
        Traccia t = new Traccia(UUID.randomUUID().toString(), "Bohemian Rhapsody", "Queen", "5:55", "Rock", 1975);
        catalogo.aggiungiTraccia(t);

        // Verifica inserimento corretto
        ObservableList<Traccia> tracce = catalogo.getTracce();
        assertNotNull(tracce);
        assertEquals(1, tracce.size());
        assertEquals("Bohemian Rhapsody", tracce.get(0).getTitolo());
        assertEquals("Queen", tracce.get(0).getAutore());
    }

    @Test
    void testAggiungiTracciaLanciaEccezioneSeNull() {
        assertThrows(IllegalArgumentException.class, () -> catalogo.aggiungiTraccia(null));
    }

    @Test
    void testAggiungiTracciaLanciaEccezionePerDuplicato() {
        Traccia t1 = new Traccia(UUID.randomUUID().toString(), "Song", "Artist", "3:00", "Pop", 2020);
        catalogo.aggiungiTraccia(t1);

        Traccia t2 = new Traccia(UUID.randomUUID().toString(), "Song", "Artist", "4:00", "Rock", 2021);
        assertThrows(IllegalArgumentException.class, () -> catalogo.aggiungiTraccia(t2));
    }

    @Test
    void testAggiungiTracciaDuplicataCaseInsensitive() {
        Traccia t1 = new Traccia(UUID.randomUUID().toString(), "Spaccacuore", "Samuele Bersani", "4:20", "Pop", 1994);
        catalogo.aggiungiTraccia(t1);

        // Stesso titolo e autore ma con maiuscole/minuscole diverse
        Traccia t2 = new Traccia(UUID.randomUUID().toString(), "SPACCACUORE", "samuele bersani", "4:20", "Pop", 1994);
        assertThrows(IllegalArgumentException.class, () -> catalogo.aggiungiTraccia(t2));
    }

    @Test
    void testAggiungiTracciaConStessoTitoloMaAutoreDiverso() {
        // Il vincolo di unicità si applica alla coppia (Titolo + Autore), canzoni omonime sono permesse
        Traccia t1 = new Traccia(UUID.randomUUID().toString(), "Hello", "Adele", "4:55", "Pop", 2015);
        Traccia t2 = new Traccia(UUID.randomUUID().toString(), "Hello", "Lionel Richie", "4:08", "Pop", 1983);

        assertDoesNotThrow(() -> catalogo.aggiungiTraccia(t1));
        assertDoesNotThrow(() -> catalogo.aggiungiTraccia(t2));
        assertEquals(2, catalogo.getSize());
    }

    // ── 3. TEST MODIFICA TRACCIA ───────────────────────────────────────

    @Test
    void testModificaTracciaDatiSecondariSuccesso() {
        Traccia vecchia = new Traccia(UUID.randomUUID().toString(), "Anima Fragile", "Vasco Rossi", "4:00", "Rock", 1980);
        catalogo.aggiungiTraccia(vecchia);

        // Modifica parziale senza toccare la coppia identificativa (titolo/autore)
        Traccia nuova = new Traccia(vecchia.getId(), "Anima Fragile", "Vasco Rossi", "4:30", "Rock Italiano", 1980);
        
        assertDoesNotThrow(() -> catalogo.modificaTraccia(vecchia, nuova));
        
        Traccia aggiornata = catalogo.getTracce().get(0);
        assertEquals("4:30", aggiornata.getDurata());
        assertEquals("Rock Italiano", aggiornata.getGenere());
    }

    @Test
    void testModificaTracciaCambioTitoloEAutoreSuccesso() {
        Traccia vecchia = new Traccia(UUID.randomUUID().toString(), "Vento d'estate", "Max Gazzè", "3:40", "Pop", 1998);
        catalogo.aggiungiTraccia(vecchia);

        // Modifica completa dei dati (nuovo identikit non occupato)
        Traccia nuova = new Traccia(vecchia.getId(), "L'uomo più furbo", "Max Gazzè", "4:00", "Pop", 2000);

        assertDoesNotThrow(() -> catalogo.modificaTraccia(vecchia, nuova));
        assertFalse(catalogo.contiene("Vento d'estate", "Max Gazzè"));
        assertTrue(catalogo.contiene("L'uomo più furbo", "Max Gazzè"));
    }

    @Test
    void testModificaTracciaLanciaEccezioneSeNuovoIdentikitEUnDuplicato() {
        Traccia t1 = new Traccia(UUID.randomUUID().toString(), "Albachiara", "Vasco Rossi", "4:03", "Rock", 1979);
        Traccia t2 = new Traccia(UUID.randomUUID().toString(), "Rewind", "Vasco Rossi", "3:50", "Rock", 1999);
        catalogo.aggiungiTraccia(t1);
        catalogo.aggiungiTraccia(t2);

        // Tento di modificare t2 dandogli lo stesso titolo e autore di t1
        Traccia t2ModificataInDuplicato = new Traccia(t2.getId(), "Albachiara", "Vasco Rossi", "3:50", "Rock", 1999);

        assertThrows(IllegalArgumentException.class, () -> catalogo.modificaTraccia(t2, t2ModificataInDuplicato));
    }

    @Test
    void testModificaTracciaLanciaEccezioneSeVecchiaNonEsiste() {
        Traccia vecchiaInesistente = new Traccia(UUID.randomUUID().toString(), "Mai Inserita", "Nessuno", "3:00", "Pop", 2020);
        Traccia nuova = new Traccia(UUID.randomUUID().toString(), "Nuova", "Qualcuno", "3:00", "Pop", 2020);

        assertThrows(IllegalArgumentException.class, () -> catalogo.modificaTraccia(vecchiaInesistente, nuova));
    }

    @Test
    void testModificaTracciaLanciaEccezioneConParametriNull() {
        Traccia valida = new Traccia(UUID.randomUUID().toString(), "Titolo", "Autore", "3:00", "Pop", 2020);
        catalogo.aggiungiTraccia(valida);

        assertThrows(IllegalArgumentException.class, () -> catalogo.modificaTraccia(null, valida));
        assertThrows(IllegalArgumentException.class, () -> catalogo.modificaTraccia(valida, null));
    }

    // ── 4. TEST DI RICERCA E PERSISTENZA ──────────────────────────────────────

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

    // ── 5. TEST AGGIORNAMENTO AUTOMATICO PLAYLIST AUTOMATICHE ─────────────────

    @Test
    void testAggiungiTracciaAggiornaPLaylistAutoPerAnnoCorrispondente() {
        Playlist autoAnno = new Playlist("Auto - 2000");
        CatalogoPlaylist.getInstance().aggiungiPlaylist(autoAnno);

        Traccia t = new Traccia(UUID.randomUUID().toString(), "Test Song", "Test Artist", "3:00", "Pop", 2000);
        catalogo.aggiungiTraccia(t);

        assertTrue(autoAnno.getTracce().contains(t));
        assertTrue(autoAnno.getIdTracce().contains(t.getId()));
    }

    @Test
    void testAggiungiTracciaAggiornaPLaylistAutoPerGenereCorrispondente() {
        Playlist autoGenere = new Playlist("Auto - Rock");
        CatalogoPlaylist.getInstance().aggiungiPlaylist(autoGenere);

        Traccia t = new Traccia(UUID.randomUUID().toString(), "Rock Song", "Rock Artist", "4:00", "Rock", 1990);
        catalogo.aggiungiTraccia(t);

        assertTrue(autoGenere.getTracce().contains(t));
        assertTrue(autoGenere.getIdTracce().contains(t.getId()));
    }

    @Test
    void testAggiungiTracciaAnnoNonCorrisponenteNonAggiornaPLaylistAuto() {
        Playlist autoAnno = new Playlist("Auto - 1999");
        CatalogoPlaylist.getInstance().aggiungiPlaylist(autoAnno);

        Traccia t = new Traccia(UUID.randomUUID().toString(), "Another Song", "Another Artist", "3:30", "Pop", 2000);
        catalogo.aggiungiTraccia(t);

        assertFalse(autoAnno.getTracce().contains(t));
        assertEquals(0, autoAnno.getNumeroTracce());
    }

    @Test
    void testAggiungiTracciaGenereNonCorrisponenteNonAggiornaPLaylistAuto() {
        Playlist autoGenere = new Playlist("Auto - Jazz");
        CatalogoPlaylist.getInstance().aggiungiPlaylist(autoGenere);

        Traccia t = new Traccia(UUID.randomUUID().toString(), "Pop Song", "Pop Artist", "3:00", "Pop", 2005);
        catalogo.aggiungiTraccia(t);

        assertFalse(autoGenere.getTracce().contains(t));
        assertEquals(0, autoGenere.getNumeroTracce());
    }

    @Test
    void testAggiungiTracciaConNessunaPlaylistAutoNonGeneraEccezioni() {
        Traccia t = new Traccia(UUID.randomUUID().toString(), "Solo Song", "Solo Artist", "3:00", "Pop", 2010);
        assertDoesNotThrow(() -> catalogo.aggiungiTraccia(t));
        assertEquals(1, catalogo.getSize());
    }

    @Test
    void testAggiungiTracciaNoAggiornamentoPlaylistNonAuto() {
        Playlist manuale = new Playlist("La mia playlist");
        CatalogoPlaylist.getInstance().aggiungiPlaylist(manuale);

        Traccia t = new Traccia(UUID.randomUUID().toString(), "Manual Song", "Manual Artist", "3:00", "Rock", 2000);
        catalogo.aggiungiTraccia(t);

        assertFalse(manuale.getTracce().contains(t));
        assertEquals(0, manuale.getNumeroTracce());
    }

    @Test
    void testAggiungiTracciaGenereMatchCaseInsensitive() {
        Playlist autoGenere = new Playlist("Auto - rock");
        CatalogoPlaylist.getInstance().aggiungiPlaylist(autoGenere);

        Traccia t = new Traccia(UUID.randomUUID().toString(), "Hard Rock Song", "Hard Artist", "5:00", "Rock", 1985);
        catalogo.aggiungiTraccia(t);

        assertTrue(autoGenere.getTracce().contains(t));
    }

    @Test
    void testAggiungiTracciaAggiornaPiuPlaylistAutoSimultaneamente() {
        Playlist autoAnno = new Playlist("Auto - 2000");
        Playlist autoGenere = new Playlist("Auto - Pop");
        CatalogoPlaylist.getInstance().aggiungiPlaylist(autoAnno);
        CatalogoPlaylist.getInstance().aggiungiPlaylist(autoGenere);

        Traccia t = new Traccia(UUID.randomUUID().toString(), "Pop 2000 Song", "Pop Artist", "3:00", "Pop", 2000);
        catalogo.aggiungiTraccia(t);

        assertTrue(autoAnno.getTracce().contains(t));
        assertTrue(autoGenere.getTracce().contains(t));
    }

    @Test
    void testAggiungiTracciaAutoNonAggiungeAllePlaylistConNomeSimileNonPrefissato() {
        Playlist nonAuto = new Playlist("Automatica - 2000");
        CatalogoPlaylist.getInstance().aggiungiPlaylist(nonAuto);

        Traccia t = new Traccia(UUID.randomUUID().toString(), "Song 2000", "Artist", "3:00", "Pop", 2000);
        catalogo.aggiungiTraccia(t);

        assertFalse(nonAuto.getTracce().contains(t));
        assertEquals(0, nonAuto.getNumeroTracce());
    }
}
