package it.unisa.musicplayer.modello;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import it.unisa.musicplayer.modello.Traccia;
import it.unisa.musicplayer.modello.Tag;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Traccia — Test unitari")
class TracciaTest {

    // ── Fixture ───────────────────────────────────────────────────────────────

    private static final String ID     = "uuid-001";
    private static final String TITOLO = "Bohemian Rhapsody";
    private static final String AUTORE = "Queen";
    private static final String DURATA = "5:55";
    private static final String GENERE = "Rock";
    private static final int    ANNO   = 1975;

    private Traccia traccia;

    @BeforeEach
    void setUp() {
        traccia = new Traccia(ID, TITOLO, AUTORE, DURATA, GENERE, ANNO);
    }

    // ── Costruttore — percorso felice ─────────────────────────────────────────

    @Test
    @DisplayName("Costruttore valido — tutti i campi inizializzati correttamente")
    void costruttore_valido() {
        assertEquals(ID,     traccia.getId());
        assertEquals(TITOLO, traccia.getTitolo());
        assertEquals(AUTORE, traccia.getAutore());
        assertEquals(DURATA, traccia.getDurata());
        assertEquals(GENERE, traccia.getGenere());
        assertEquals(ANNO,   traccia.getAnno());
        assertEquals(0,      traccia.getContaRiproduzioni());
        assertTrue(traccia.getTag().isEmpty());
    }

    @Test
    @DisplayName("Costruttore — gli spazi iniziali/finali vengono rimossi")
    void costruttore_trimming() {
        Traccia t = new Traccia("  " + ID + "  ", "  " + TITOLO + "  ",
                                AUTORE, DURATA, GENERE, ANNO);
        assertEquals(ID,     t.getId());
        assertEquals(TITOLO, t.getTitolo());
    }

    // ── Costruttore — valori non validi ───────────────────────────────────────

    @Test
    @DisplayName("Costruttore — id null lancia IllegalArgumentException")
    void costruttore_idNull() {
        assertThrows(IllegalArgumentException.class,
            () -> new Traccia(null, TITOLO, AUTORE, DURATA, GENERE, ANNO));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    @DisplayName("Costruttore — titolo vuoto o blank lancia IllegalArgumentException")
    void costruttore_titoloVuoto(String titoloNonValido) {
        assertThrows(IllegalArgumentException.class,
            () -> new Traccia(ID, titoloNonValido, AUTORE, DURATA, GENERE, ANNO));
    }

    @ParameterizedTest
    @ValueSource(strings = {"3:60", "abc", "3-45", "3:4", ":30", "3:"})
    @DisplayName("Costruttore — formato durata non valido lancia IllegalArgumentException")
    void costruttore_durataFormato(String durataErrata) {
        assertThrows(IllegalArgumentException.class,
            () -> new Traccia(ID, TITOLO, AUTORE, durataErrata, GENERE, ANNO));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -100})
    @DisplayName("Costruttore — anno non positivo lancia IllegalArgumentException")
    void costruttore_annoNonPositivo(int annoErrato) {
        assertThrows(IllegalArgumentException.class,
            () -> new Traccia(ID, TITOLO, AUTORE, DURATA, GENERE, annoErrato));
    }

    // ── Setter ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("setTitolo — aggiorna il titolo correttamente")
    void setTitolo_valido() {
        traccia.setTitolo("We Will Rock You");
        assertEquals("We Will Rock You", traccia.getTitolo());
    }

    @Test
    @DisplayName("setTitolo — null lancia IllegalArgumentException")
    void setTitolo_null() {
        assertThrows(IllegalArgumentException.class, () -> traccia.setTitolo(null));
    }

    @Test
    @DisplayName("setDurata — formato valido aggiornato")
    void setDurata_valida() {
        traccia.setDurata("4:10");
        assertEquals("4:10", traccia.getDurata());
    }

    @Test
    @DisplayName("setDurata — formato non valido lancia IllegalArgumentException")
    void setDurata_formatoErrato() {
        assertThrows(IllegalArgumentException.class, () -> traccia.setDurata("4:60"));
    }

    @Test
    @DisplayName("setAnno — anno valido aggiornato")
    void setAnno_valido() {
        traccia.setAnno(1985);
        assertEquals(1985, traccia.getAnno());
    }

    @Test
    @DisplayName("setAnno — anno zero lancia IllegalArgumentException")
    void setAnno_zero() {
        assertThrows(IllegalArgumentException.class, () -> traccia.setAnno(0));
    }

    // ── Tag ───────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("aggiungiTag — tag aggiunto correttamente")
    void aggiungiTag_valido() {
        traccia.aggiungiTag(Tag.FAVOURITE);
        assertTrue(traccia.hasTag(Tag.FAVOURITE));
    }

    @Test
    @DisplayName("aggiungiTag — idempotente: aggiungere lo stesso tag due volte non duplica")
    void aggiungiTag_idempotente() {
        traccia.aggiungiTag(Tag.FAVOURITE);
        traccia.aggiungiTag(Tag.FAVOURITE);
        assertEquals(1, traccia.getTag().size());
    }

    @Test
    @DisplayName("aggiungiTag — più tag contemporaneamente")
    void aggiungiTag_multipli() {
        traccia.aggiungiTag(Tag.FAVOURITE);
        traccia.aggiungiTag(Tag.EXPLICIT);
        Set<Tag> tags = traccia.getTag();
        assertTrue(tags.contains(Tag.FAVOURITE));
        assertTrue(tags.contains(Tag.EXPLICIT));
        assertEquals(2, tags.size());
    }

    @Test
    @DisplayName("aggiungiTag — null lancia NullPointerException")
    void aggiungiTag_null() {
        assertThrows(NullPointerException.class, () -> traccia.aggiungiTag(null));
    }

    @Test
    @DisplayName("rimuoviTag — tag rimosso correttamente")
    void rimuoviTag_valido() {
        traccia.aggiungiTag(Tag.FAVOURITE);
        traccia.rimuoviTag(Tag.FAVOURITE);
        assertFalse(traccia.hasTag(Tag.FAVOURITE));
    }

    @Test
    @DisplayName("rimuoviTag — idempotente: rimuovere tag assente non lancia eccezione")
    void rimuoviTag_assente() {
        assertDoesNotThrow(() -> traccia.rimuoviTag(Tag.EXPLICIT));
    }

    @Test
    @DisplayName("getTags — restituisce copia difensiva: modifiche esterne non impattano la traccia")
    void getTags_copiaDefensiva() {
        traccia.aggiungiTag(Tag.FAVOURITE);
        Set<Tag> copia = traccia.getTag();
        copia.clear(); // modifica la copia
        assertTrue(traccia.hasTag(Tag.FAVOURITE)); // traccia invariata
    }

    // ── Riproduzione ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("incrementaRiproduzioni — contatore incrementato correttamente")
    void incrementaRiproduzioni() {
        traccia.incrementaRiproduzioni();
        traccia.incrementaRiproduzioni();
        assertEquals(2, traccia.getContaRiproduzioni());
    }

    // ── equals e hashCode ─────────────────────────────────────────────────────

    @Test
    @DisplayName("equals — stessa istanza restituisce true")
    void equals_stessaIstanza() {
        assertEquals(traccia, traccia);
    }

    @Test
    @DisplayName("equals — stesso id, campi diversi: uguali")
    void equals_stessoId() {
        Traccia altra = new Traccia(ID, "Altro Titolo", "Altro Autore", "1:00", "Pop", 2000);
        assertEquals(traccia, altra);
    }

    @Test
    @DisplayName("equals — id diverso: non uguali")
    void equals_idDiverso() {
        Traccia altra = new Traccia("uuid-002", TITOLO, AUTORE, DURATA, GENERE, ANNO);
        assertNotEquals(traccia, altra);
    }

    @Test
    @DisplayName("equals — null: non uguale")
    void equals_null() {
        assertNotEquals(traccia, null);
    }

    @Test
    @DisplayName("hashCode — consistente con equals: stesso id → stesso hashCode")
    void hashCode_consistente() {
        Traccia altra = new Traccia(ID, "Altro Titolo", "Altro Autore", "1:00", "Pop", 2000);
        assertEquals(traccia.hashCode(), altra.hashCode());
    }

    // ── compareTo ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("compareTo — titoli diversi: ordine alfabetico")
    void compareTo_titoliDiversi() {
        Traccia altra = new Traccia("uuid-002", "We Will Rock You", AUTORE, "2:01", GENERE, ANNO);
        assertTrue(traccia.compareTo(altra) < 0); // "Bohemian" < "We Will"
    }

    @Test
    @DisplayName("compareTo — stesso titolo: ordinato per autore")
    void compareTo_stessoTitolo() {
        Traccia altra = new Traccia("uuid-002", TITOLO, "Zeppelin", DURATA, GENERE, ANNO);
        assertTrue(traccia.compareTo(altra) < 0); // "Queen" < "Zeppelin"
    }

    @Test
    @DisplayName("compareTo — case-insensitive: 'bohemian' == 'BOHEMIAN'")
    void compareTo_caseInsensitive() {
        Traccia altra = new Traccia("uuid-002", TITOLO.toLowerCase(), AUTORE, DURATA, GENERE, ANNO);
        assertEquals(0, traccia.compareTo(altra));
    }

    // ── toString ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("toString — contiene id e titolo")
    void toString_contenuto() {
        String s = traccia.toString();
        assertTrue(s.contains(ID));
        assertTrue(s.contains(TITOLO));
    }
}
