package it.unisa.musicplayer.servizi;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unisa.musicplayer.modello.DatiApplicazione;
import it.unisa.musicplayer.modello.Traccia;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GestoreFileTest {

    @Test
    void testImportaRestituisceVuotoSeFileNonEsiste() {
        String percorsoInesistente = System.getProperty("java.io.tmpdir") + File.separator + "inesistente_" + UUID.randomUUID() + ".json";
        DatiApplicazione risultato = GestoreFile.importaDaPercorso(percorsoInesistente);
        assertNotNull(risultato);
        assertNotNull(risultato.getCanzoni());
        assertTrue(risultato.getCanzoni().isEmpty());
    }

    @Test
    void testEsportaImportaRoundTrip() throws Exception {
        File tempFile = File.createTempFile("test_catalogo_", ".json");
        tempFile.deleteOnExit();

        DatiApplicazione originale = new DatiApplicazione();
        Traccia t1 = new Traccia(UUID.randomUUID().toString(), "Test Song", "Test Artist", "3:45", "Rock", 2024);
        Traccia t2 = new Traccia(UUID.randomUUID().toString(), "Another", "Another Artist", "2:30", "Pop", 2023);
        originale.getCanzoni().add(t1);
        originale.getCanzoni().add(t2);

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(tempFile, originale);

        DatiApplicazione caricato = GestoreFile.importaDaPercorso(tempFile.getAbsolutePath());
        assertNotNull(caricato);
        assertEquals(2, caricato.getCanzoni().size());
        assertEquals("Test Song", caricato.getCanzoni().get(0).getTitolo());
        assertEquals("Test Artist", caricato.getCanzoni().get(0).getAutore());
        assertEquals("3:45", caricato.getCanzoni().get(0).getDurata());
        assertEquals("Rock", caricato.getCanzoni().get(0).getGenere());
        assertEquals(2024, caricato.getCanzoni().get(0).getAnno());
    }
}
