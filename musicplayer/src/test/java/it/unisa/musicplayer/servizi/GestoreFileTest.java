package it.unisa.musicplayer.servizi;

import it.unisa.musicplayer.modello.DatiApplicazione;
import it.unisa.musicplayer.modello.Playlist;
import it.unisa.musicplayer.modello.Traccia;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GestoreFileTest {

    @TempDir
    Path tempDir;

    private Traccia creaTraccia(String titolo, String autore) {
        return new Traccia(UUID.randomUUID().toString(), titolo, autore, "3:00", "Rock", 2024);
    }

    // ── AC6: Ripristino - file assente ─────────────────────────────────────────

    @Test
    void testImportaRestituisceVuotoSeFileNonEsiste() {
        String percorsoInesistente = tempDir.resolve("inesistente_" + UUID.randomUUID() + ".json").toString();
        DatiApplicazione risultato = GestoreFile.importaDaPercorso(percorsoInesistente);
        assertNotNull(risultato);
        assertNotNull(risultato.getCanzoni());
        assertTrue(risultato.getCanzoni().isEmpty());
    }

    // ── AC1: Salvataggio del catalogo tracce ──────────────────────────────────

    @Test
    void testEsportaSalvaTracceNelFile() throws IOException {
        DatiApplicazione dati = new DatiApplicazione();
        dati.getCanzoni().add(creaTraccia("Canzone A", "Artista A"));
        dati.getCanzoni().add(creaTraccia("Canzone B", "Artista B"));

        String percorso = tempDir.resolve("catalogo.json").toString();
        GestoreFile.esportaSuPercorso(dati, percorso);

        DatiApplicazione caricato = GestoreFile.importaDaPercorso(percorso);
        assertEquals(2, caricato.getCanzoni().size());
    }

    // ── AC2: Salvataggio degli attributi delle tracce ─────────────────────────

    @Test
    void testEsportaSalvaTuttiGliAttributiTraccia() throws IOException {
        String id = UUID.randomUUID().toString();
        Traccia t = new Traccia(id, "Bohemian Rhapsody", "Queen", "5:55", "Rock", 1975);
        DatiApplicazione dati = new DatiApplicazione();
        dati.getCanzoni().add(t);

        String percorso = tempDir.resolve("traccia.json").toString();
        GestoreFile.esportaSuPercorso(dati, percorso);

        DatiApplicazione caricato = GestoreFile.importaDaPercorso(percorso);
        Traccia letta = caricato.getCanzoni().get(0);

        assertEquals(id,                   letta.getId());
        assertEquals("Bohemian Rhapsody",  letta.getTitolo());
        assertEquals("Queen",              letta.getAutore());
        assertEquals("5:55",               letta.getDurata());
        assertEquals("Rock",               letta.getGenere());
        assertEquals(1975,                 letta.getAnno());
    }

    // ── AC3: Salvataggio delle playlist ──────────────────────────────────────

    @Test
    void testEsportaSalvaPlaylists() throws IOException {
        DatiApplicazione dati = new DatiApplicazione();
        dati.getPlaylists().add(new Playlist("Preferiti"));
        dati.getPlaylists().add(new Playlist("Workout"));

        String percorso = tempDir.resolve("playlists.json").toString();
        GestoreFile.esportaSuPercorso(dati, percorso);

        DatiApplicazione caricato = GestoreFile.importaDaPercorso(percorso);
        assertEquals(2, caricato.getPlaylists().size());
        assertEquals("Preferiti", caricato.getPlaylists().get(0).getNome());
        assertEquals("Workout",   caricato.getPlaylists().get(1).getNome());
    }

    // ── AC4: Salvataggio delle tracce nelle playlist ──────────────────────────

    @Test
    void testEsportaSalvaTracceNellePlaylist() throws IOException {
        Traccia t1 = creaTraccia("Track One", "Artist");
        Traccia t2 = creaTraccia("Track Two", "Artist");

        Playlist playlist = new Playlist("Mix");
        playlist.aggiungiTraccia(t1);
        playlist.aggiungiTraccia(t2);

        DatiApplicazione dati = new DatiApplicazione();
        dati.getCanzoni().add(t1);
        dati.getCanzoni().add(t2);
        dati.getPlaylists().add(playlist);

        String percorso = tempDir.resolve("playlist_tracce.json").toString();
        GestoreFile.esportaSuPercorso(dati, percorso);

        DatiApplicazione caricato = GestoreFile.importaDaPercorso(percorso);
        Playlist letta = caricato.getPlaylists().get(0);

        assertEquals("Mix", letta.getNome());
        assertEquals(2, letta.getIdTracce().size());
        assertTrue(letta.getIdTracce().contains(t1.getId()));
        assertTrue(letta.getIdTracce().contains(t2.getId()));
    }

    // ── AC5: Mantenimento dell'ordine delle tracce ────────────────────────────

    @Test
    void testEsportaMantieneOrdineTracceInPlaylist() throws IOException {
        Traccia t1 = creaTraccia("Prima",   "A");
        Traccia t2 = creaTraccia("Seconda", "B");
        Traccia t3 = creaTraccia("Terza",   "C");

        Playlist playlist = new Playlist("Ordinata");
        playlist.aggiungiTraccia(t1);
        playlist.aggiungiTraccia(t2);
        playlist.aggiungiTraccia(t3);

        DatiApplicazione dati = new DatiApplicazione();
        dati.getPlaylists().add(playlist);

        String percorso = tempDir.resolve("ordine.json").toString();
        GestoreFile.esportaSuPercorso(dati, percorso);

        DatiApplicazione caricato = GestoreFile.importaDaPercorso(percorso);
        List<String> idCaricati = caricato.getPlaylists().get(0).getIdTracce();

        assertEquals(t1.getId(), idCaricati.get(0));
        assertEquals(t2.getId(), idCaricati.get(1));
        assertEquals(t3.getId(), idCaricati.get(2));
    }

    // ── AC6: Ripristino dei dati salvati (round-trip completo) ────────────────

    @Test
    void testEsportaImportaRoundTrip() throws IOException {
        Traccia t1 = new Traccia(UUID.randomUUID().toString(), "Test Song", "Test Artist",    "3:45", "Rock", 2024);
        Traccia t2 = new Traccia(UUID.randomUUID().toString(), "Another",   "Another Artist", "2:30", "Pop",  2023);

        DatiApplicazione originale = new DatiApplicazione();
        originale.getCanzoni().add(t1);
        originale.getCanzoni().add(t2);

        String percorso = tempDir.resolve("roundtrip.json").toString();
        GestoreFile.esportaSuPercorso(originale, percorso);

        DatiApplicazione caricato = GestoreFile.importaDaPercorso(percorso);
        assertNotNull(caricato);
        assertEquals(2, caricato.getCanzoni().size());

        Traccia letta = caricato.getCanzoni().get(0);
        assertEquals("Test Song",   letta.getTitolo());
        assertEquals("Test Artist", letta.getAutore());
        assertEquals("3:45",        letta.getDurata());
        assertEquals("Rock",        letta.getGenere());
        assertEquals(2024,          letta.getAnno());
    }

    // ── AC7: Salvataggio con cataloghi vuoti ──────────────────────────────────

    @Test
    void testEsportaCatalogoVuotoNonGeneraErrori() throws IOException {
        DatiApplicazione vuoto = new DatiApplicazione();
        String percorso = tempDir.resolve("vuoto.json").toString();

        assertDoesNotThrow(() -> GestoreFile.esportaSuPercorso(vuoto, percorso));

        DatiApplicazione caricato = GestoreFile.importaDaPercorso(percorso);
        assertNotNull(caricato);
        assertTrue(caricato.getCanzoni().isEmpty());
        assertTrue(caricato.getPlaylists().isEmpty());
    }

    // ── AC8: Errore durante il salvataggio ────────────────────────────────────

    @Test
    void testEsportaLanciaIOExceptionSePercorsoNonValido() {
        DatiApplicazione dati = new DatiApplicazione();
        dati.getCanzoni().add(creaTraccia("X", "Y"));

        // Puntare a una directory esistente provoca IOException (non si può scrivere su una dir)
        String percorsoDirectory = tempDir.toString();

        assertThrows(IOException.class,
            () -> GestoreFile.esportaSuPercorso(dati, percorsoDirectory));
    }

    // ── AC6 extra: importa restituisce vuoto se il JSON è corrotto ────────────

    @Test
    void testImportaRestituisceVuotoSeFileCorreotto() throws IOException {
        File fileCorreotto = tempDir.resolve("corrotto.json").toFile();
        java.nio.file.Files.writeString(fileCorreotto.toPath(), "{ QUESTO NON E JSON VALIDO }}}");

        DatiApplicazione risultato = GestoreFile.importaDaPercorso(fileCorreotto.getAbsolutePath());
        assertNotNull(risultato);
        assertNotNull(risultato.getCanzoni());
        assertTrue(risultato.getCanzoni().isEmpty());
    }
}
