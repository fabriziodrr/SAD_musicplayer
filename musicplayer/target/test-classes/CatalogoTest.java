import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import it.unisa.musicplayer.modello.Traccia;
import it.unisa.musicplayer.modello.Catalogo;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Catalogo — Test unitari")
class CatalogoTest {

    private Catalogo catalogo;
    private Traccia  t1;
    private Traccia  t2;

    @BeforeEach
    void setUp() {
        catalogo = Catalogo.getInstance();
        catalogo.svuota();

        t1 = new Traccia("id-001", "Bohemian Rhapsody",  "Queen",        "5:55", "Rock", 1975);
        t2 = new Traccia("id-002", "Stairway to Heaven", "Led Zeppelin", "8:02", "Rock", 1971);
    }

    // ── Singleton ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getInstance — restituisce sempre la stessa istanza")
    void singleton_stessaIstanza() {
        Catalogo a = Catalogo.getInstance();
        Catalogo b = Catalogo.getInstance();
        assertSame(a, b);
    }

    // ── aggiungiTraccia ───────────────────────────────────────────────────────

    @Test
    @DisplayName("aggiungiTraccia — traccia aggiunta correttamente")
    void aggiungiTraccia_valida() {
        catalogo.aggiungiTraccia(t1);
        assertEquals(1, catalogo.getSize());
        assertTrue(catalogo.getTracce().contains(t1));
    }

    @Test
    @DisplayName("aggiungiTraccia — null lancia IllegalArgumentException")
    void aggiungiTraccia_null() {
        assertThrows(IllegalArgumentException.class,
            () -> catalogo.aggiungiTraccia(null));
    }

    @Test
    @DisplayName("aggiungiTraccia — duplicato logico (stesso titolo+autore) lancia IllegalArgumentException")
    void aggiungiTraccia_duplicatoLogico() {
        catalogo.aggiungiTraccia(t1);
        // Stesso titolo e autore ma id diverso (come avviene con UUID nella GUI)
        Traccia copia = new Traccia(UUID.randomUUID().toString(),
                                    "Bohemian Rhapsody", "Queen", "5:55", "Rock", 1975);
        assertThrows(IllegalArgumentException.class,
            () -> catalogo.aggiungiTraccia(copia));
    }

    @Test
    @DisplayName("aggiungiTraccia — stesso titolo ma autore diverso è permesso")
    void aggiungiTraccia_stessoTitoloAutoreDiverso() {
        catalogo.aggiungiTraccia(t1);
        Traccia coverVersion = new Traccia("id-003", "Bohemian Rhapsody",
                                           "Weezer", "5:48", "Rock", 1992);
        assertDoesNotThrow(() -> catalogo.aggiungiTraccia(coverVersion));
        assertEquals(2, catalogo.getSize());
    }

    // ── rimuoviTraccia ────────────────────────────────────────────────────────

    @Test
    @DisplayName("rimuoviTraccia — traccia rimossa correttamente")
    void rimuoviTraccia_valida() {

    }

    @Test
    @DisplayName("rimuoviTraccia — traccia non presente lancia IllegalArgumentException")
    void rimuoviTraccia_nonPresente() {

    }

    @Test
    @DisplayName("rimuoviTraccia — null lancia IllegalArgumentException")
    void rimuoviTraccia_null() {

    }

    // ── modificaTraccia ───────────────────────────────────────────────────────

    @Test
    @DisplayName("modificaTraccia — traccia sostituita mantenendo la posizione")
    void modificaTraccia_valida() {
        catalogo.aggiungiTraccia(t1);
        catalogo.aggiungiTraccia(t2);

        Traccia aggiornata = new Traccia("id-001", "Bohemian Rhapsody (Remaster)",
                                         "Queen", "5:55", "Rock", 1975);
        catalogo.modificaTraccia(t1, aggiornata);

        assertEquals(2, catalogo.getSize());
        assertEquals("Bohemian Rhapsody (Remaster)",
                     catalogo.getTracce().get(0).getTitolo());
    }

    @Test
    @DisplayName("modificaTraccia — traccia non presente lancia IllegalArgumentException")
    void modificaTraccia_nonPresente() {
        assertThrows(IllegalArgumentException.class,
            () -> catalogo.modificaTraccia(t1, t2));
    }

    @Test
    @DisplayName("modificaTraccia — parametro null lancia IllegalArgumentException")
    void modificaTraccia_null() {
        assertThrows(IllegalArgumentException.class,
            () -> catalogo.modificaTraccia(null, t2));
    }

    // ── esisteDuplicato ───────────────────────────────────────────────────────

    @Test
    @DisplayName("esisteDuplicato — true se titolo e autore coincidono")
    void contiene_presente() {
        catalogo.aggiungiTraccia(t1);
        assertTrue(catalogo.contiene("Bohemian Rhapsody", "Queen"));
    }

    @Test
    @DisplayName("esisteDuplicato — case-insensitive su titolo e autore")
    void contiene_caseInsensitive() {
        catalogo.aggiungiTraccia(t1);
        assertTrue(catalogo.contiene("bohemian rhapsody", "queen"));
        assertTrue(catalogo.contiene("BOHEMIAN RHAPSODY", "QUEEN"));
    }

    @Test
    @DisplayName("esisteDuplicato — false se titolo presente ma autore diverso")
    void contiene_stessoTitoloAutoreDiverso() {
        catalogo.aggiungiTraccia(t1);
        assertFalse(catalogo.contiene("Bohemian Rhapsody", "Weezer"));
    }

    @Test
    @DisplayName("esisteDuplicato — false se catalogo vuoto")
    void contiene_catalogoVuoto() {
        assertFalse(catalogo.contiene("Bohemian Rhapsody", "Queen"));
    }

    // ── cercaPerId ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("cercaPerId — traccia trovata restituisce Optional con valore")
    void cercaPerId_trovata() {
        catalogo.aggiungiTraccia(t1);
        Optional<Traccia> risultato = catalogo.cercaPerId("id-001");
        assertTrue(risultato.isPresent());
        assertEquals(t1, risultato.get());
    }

    @Test
    @DisplayName("cercaPerId — traccia non trovata restituisce Optional.empty")
    void cercaPerId_nonTrovata() {
        Optional<Traccia> risultato = catalogo.cercaPerId("id-999");
        assertFalse(risultato.isPresent());
    }

    @Test
    @DisplayName("cercaPerId — id null restituisce Optional.empty")
    void cercaPerId_null() {
        assertFalse(catalogo.cercaPerId(null).isPresent());
    }

    // ── svuota ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("svuota — catalogo vuoto dopo la chiamata")
    void svuota_funziona() {
        catalogo.aggiungiTraccia(t1);
        catalogo.aggiungiTraccia(t2);
        catalogo.svuota();
        assertEquals(0, catalogo.getSize());
    }

    // ── getTracce (ObservableList) ────────────────────────────────────────────

    @Test
    @DisplayName("getTracce — la lista si aggiorna dopo aggiungiTraccia")
    void getTracce_aggiornatoDopoAggiunta() {
        catalogo.aggiungiTraccia(t1);
        assertTrue(catalogo.getTracce().contains(t1));
    }

    @Test
    @DisplayName("getTracce — la lista si aggiorna dopo rimuoviTraccia")
    void getTracce_aggiornatoDopoRimozione() {

    }
}