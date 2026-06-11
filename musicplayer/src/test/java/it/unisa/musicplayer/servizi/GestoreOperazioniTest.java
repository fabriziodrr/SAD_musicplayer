package it.unisa.musicplayer.servizi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unisa.musicplayer.modello.Catalogo;
import it.unisa.musicplayer.modello.CatalogoPlaylist;
import it.unisa.musicplayer.modello.Playlist;
import it.unisa.musicplayer.modello.Traccia;

class GestoreOperazioniTest {

    private Catalogo catalogo;
    private CatalogoPlaylist catalogoPlaylist;
    private GestoreOperazioni gestoreOperazioni;

    @BeforeEach
    void setUp() {
        catalogo = Catalogo.getInstance();
        catalogoPlaylist = CatalogoPlaylist.getInstance();
        catalogo.svuota();
        catalogoPlaylist.svuota();
        gestoreOperazioni = new GestoreOperazioni();
    }

    @AfterEach
    void tearDown() {
        catalogo.svuota();
        catalogoPlaylist.svuota();
    }

    @Test
    void undoAggiuntaCatalogoRimuoveTraccia() {
        Traccia traccia = creaTraccia("Traccia A");

        gestoreOperazioni.eseguiOperazione(new AggiungiTracciaCatalogo(catalogo, traccia));
        gestoreOperazioni.annullaUltimaOperazione();

        assertFalse(catalogo.getTracce().contains(traccia));
        assertFalse(gestoreOperazioni.puoAnnullare());
    }

    @Test
    void undoRimozioneCatalogoRipristinaPlaylistEPosizioniOriginali() {
        Traccia traccia1 = creaTraccia("Traccia 1");
        Traccia traccia2 = creaTraccia("Traccia 2");
        Traccia traccia3 = creaTraccia("Traccia 3");
        Playlist playlist = new Playlist("Playlist Test");

        catalogo.aggiungiTraccia(traccia1);
        catalogo.aggiungiTraccia(traccia2);
        catalogo.aggiungiTraccia(traccia3);
        playlist.aggiungiTraccia(traccia1);
        playlist.aggiungiTraccia(traccia2);
        playlist.aggiungiTraccia(traccia3);
        catalogoPlaylist.aggiungiPlaylist(playlist);

        gestoreOperazioni.eseguiOperazione(new RimuoviTracciaCatalogo(catalogo, catalogoPlaylist, traccia2));
        gestoreOperazioni.annullaUltimaOperazione();

        assertEquals(traccia2, catalogo.getTracce().get(1));
        assertEquals(traccia2, playlist.getTracce().get(1));
    }

    @Test
    void undoAggiuntaPlaylistRimuoveTracciaDallaPlaylist() {
        Playlist playlist = new Playlist("Playlist Test");
        Traccia traccia = creaTraccia("Traccia Playlist");

        gestoreOperazioni.eseguiOperazione(new AggiungiTracciaPlaylist(playlist, traccia));
        gestoreOperazioni.annullaUltimaOperazione();

        assertFalse(playlist.getTracce().contains(traccia));
    }

    @Test
    void undoRimozionePlaylistRipristinaIndiceOriginale() {
        Playlist playlist = new Playlist("Playlist Test");
        Traccia traccia1 = creaTraccia("Traccia 1");
        Traccia traccia2 = creaTraccia("Traccia 2");
        Traccia traccia3 = creaTraccia("Traccia 3");
        playlist.aggiungiTraccia(traccia1);
        playlist.aggiungiTraccia(traccia2);
        playlist.aggiungiTraccia(traccia3);

        gestoreOperazioni.eseguiOperazione(new RimuoviTracciaPlaylist(playlist, traccia2));
        gestoreOperazioni.annullaUltimaOperazione();

        assertEquals(traccia2, playlist.getTracce().get(1));
    }

    @Test
    void undoMultipliConsecutiviSeguonoOrdineLifo() {
        Traccia traccia1 = creaTraccia("Traccia 1");
        Traccia traccia2 = creaTraccia("Traccia 2");

        gestoreOperazioni.eseguiOperazione(new AggiungiTracciaCatalogo(catalogo, traccia1));
        gestoreOperazioni.eseguiOperazione(new AggiungiTracciaCatalogo(catalogo, traccia2));

        gestoreOperazioni.annullaUltimaOperazione();
        assertFalse(catalogo.getTracce().contains(traccia2));
        assertTrue(catalogo.getTracce().contains(traccia1));

        gestoreOperazioni.annullaUltimaOperazione();
        assertFalse(catalogo.getTracce().contains(traccia1));
    }

    @Test
    void limiteMassimoMantieneSoloUltimeDieciOperazioni() {
        Traccia primaTraccia = null;

        // Undici operazioni fanno uscire dallo stack la prima operazione eseguita.
        for (int i = 0; i < 11; i++) {
            Traccia traccia = creaTraccia("Traccia " + i);
            if (i == 0) {
                primaTraccia = traccia;
            }
            gestoreOperazioni.eseguiOperazione(new AggiungiTracciaCatalogo(catalogo, traccia));
        }

        for (int i = 0; i < 10; i++) {
            gestoreOperazioni.annullaUltimaOperazione();
        }

        assertTrue(catalogo.getTracce().contains(primaTraccia));
        assertFalse(gestoreOperazioni.puoAnnullare());
    }

    private Traccia creaTraccia(String titolo) {
        return new Traccia(UUID.randomUUID().toString(), titolo, "Autore", "3:00", "Pop", 2024);
    }
}
