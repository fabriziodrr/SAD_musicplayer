package it.unisa.musicplayer.servizi;

import it.unisa.musicplayer.modello.Catalogo;
import it.unisa.musicplayer.modello.CatalogoPlaylist;
import it.unisa.musicplayer.modello.Playlist;
import it.unisa.musicplayer.modello.Traccia;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

    class StatisticheTest {

        private Catalogo catalogo;
        private CatalogoPlaylist catalogoPlaylist;
        private Statistiche statistiche;

        @BeforeEach
        void setUp() {
            catalogo = Catalogo.getInstance();
            catalogoPlaylist = CatalogoPlaylist.getInstance();

            catalogo.svuota();
            catalogoPlaylist.svuota();

            statistiche = new Statistiche();
        }

        @AfterEach
        void tearDown() {
            catalogo.svuota();
            catalogoPlaylist.svuota();
        }

        @Test
        void testTopTracceOrdinatePerRiproduzioniDecrescenti() {
            Traccia pocoRiprodotta = creaTraccia("Poco");
            Traccia moltoRiprodotta = creaTraccia("Molto");
            Traccia mediaRiprodotta = creaTraccia("Media");

            incrementaRiproduzioni(pocoRiprodotta, 1);
            incrementaRiproduzioni(moltoRiprodotta, 5);
            incrementaRiproduzioni(mediaRiprodotta, 3);

            catalogo.aggiungiTraccia(pocoRiprodotta);
            catalogo.aggiungiTraccia(moltoRiprodotta);
            catalogo.aggiungiTraccia(mediaRiprodotta);

            List<Traccia> risultato =
                    statistiche.getTopTracce(catalogo, 3);

            assertEquals(3, risultato.size());
            assertSame(moltoRiprodotta, risultato.get(0));
            assertSame(mediaRiprodotta, risultato.get(1));
            assertSame(pocoRiprodotta, risultato.get(2));
        }

        @Test
        void testTopTracceRestituisceMassimoCinqueElementi() {
            for (int i = 1; i <= 6; i++) {
                Traccia traccia = creaTraccia("Traccia " + i);

                incrementaRiproduzioni(traccia, i);
                catalogo.aggiungiTraccia(traccia);
            }

            List<Traccia> risultato =
                    statistiche.getTopTracce(catalogo, 5);

            assertEquals(5, risultato.size());
            assertEquals(
                    6,
                    risultato.get(0).getContaRiproduzioni()
            );
            assertEquals(
                    2,
                    risultato.get(4).getContaRiproduzioni()
            );
        }

        @Test
        void testTopTracceConCatalogoVuoto() {
            List<Traccia> risultato =
                    statistiche.getTopTracce(catalogo, 5);

            assertNotNull(risultato);
            assertTrue(risultato.isEmpty());
        }

        @Test
        void testTopTracceConLimiteNonPositivo() {
            assertTrue(
                    statistiche.getTopTracce(catalogo, 0)
                            .isEmpty()
            );

            assertTrue(
                    statistiche.getTopTracce(catalogo, -1)
                            .isEmpty()
            );
        }

        @Test
        void testTopTracceNonModificaOrdineCatalogo() {
            Traccia prima = creaTraccia("Prima");
            Traccia seconda = creaTraccia("Seconda");
            Traccia terza = creaTraccia("Terza");

            incrementaRiproduzioni(prima, 1);
            incrementaRiproduzioni(seconda, 5);
            incrementaRiproduzioni(terza, 3);

            catalogo.aggiungiTraccia(prima);
            catalogo.aggiungiTraccia(seconda);
            catalogo.aggiungiTraccia(terza);

            List<Traccia> ordineOriginale =
                    new ArrayList<>(catalogo.getTracce());

            statistiche.getTopTracce(catalogo, 3);

            assertEquals(
                    ordineOriginale,
                    catalogo.getTracce()
            );
        }

        @Test
        void testTopPlaylistOrdinatePerRiproduzioniDecrescenti() {
            Playlist pocoRiprodotta = new Playlist("Poco");
            Playlist moltoRiprodotta = new Playlist("Molto");
            Playlist mediaRiprodotta = new Playlist("Media");

            incrementaRiproduzioni(pocoRiprodotta, 1);
            incrementaRiproduzioni(moltoRiprodotta, 5);
            incrementaRiproduzioni(mediaRiprodotta, 3);

            catalogoPlaylist.aggiungiPlaylist(pocoRiprodotta);
            catalogoPlaylist.aggiungiPlaylist(moltoRiprodotta);
            catalogoPlaylist.aggiungiPlaylist(mediaRiprodotta);

            List<Playlist> risultato =
                    statistiche.getTopPlaylist(
                            catalogoPlaylist,
                            3
                    );

            assertEquals(3, risultato.size());
            assertSame(moltoRiprodotta, risultato.get(0));
            assertSame(mediaRiprodotta, risultato.get(1));
            assertSame(pocoRiprodotta, risultato.get(2));
        }

        @Test
        void testTopPlaylistRestituisceMassimoCinqueElementi() {
            for (int i = 1; i <= 6; i++) {
                Playlist playlist =
                        new Playlist("Playlist " + i);

                incrementaRiproduzioni(playlist, i);
                catalogoPlaylist.aggiungiPlaylist(playlist);
            }

            List<Playlist> risultato =
                    statistiche.getTopPlaylist(
                            catalogoPlaylist,
                            5
                    );

            assertEquals(5, risultato.size());
            assertEquals(
                    6,
                    risultato.get(0).getContaRiproduzioni()
            );
            assertEquals(
                    2,
                    risultato.get(4).getContaRiproduzioni()
            );
        }

        @Test
        void testTopPlaylistConCatalogoVuoto() {
            List<Playlist> risultato =
                    statistiche.getTopPlaylist(
                            catalogoPlaylist,
                            5
                    );

            assertNotNull(risultato);
            assertTrue(risultato.isEmpty());
        }

        @Test
        void testContaTracceEPlaylist() {
            catalogo.aggiungiTraccia(creaTraccia("Uno"));
            catalogo.aggiungiTraccia(creaTraccia("Due"));

            catalogoPlaylist.aggiungiPlaylist(
                    new Playlist("Playlist 1")
            );

            assertEquals(
                    2,
                    statistiche.contaTracce(catalogo)
            );

            assertEquals(
                    1,
                    statistiche.contaPlaylist(catalogoPlaylist)
            );
        }

        @Test
        void testGetTracciaPiuRiprodotta() {
            Traccia prima = creaTraccia("Prima");
            Traccia seconda = creaTraccia("Seconda");

            incrementaRiproduzioni(prima, 2);
            incrementaRiproduzioni(seconda, 5);

            catalogo.aggiungiTraccia(prima);
            catalogo.aggiungiTraccia(seconda);

            assertSame(
                    seconda,
                    statistiche
                            .getTracciaPiuRiprodotta(catalogo)
                            .orElseThrow()
            );
        }

        @Test
        void testGetPlaylistPiuRiprodotta() {
            Playlist prima = new Playlist("Prima");
            Playlist seconda = new Playlist("Seconda");

            incrementaRiproduzioni(prima, 2);
            incrementaRiproduzioni(seconda, 5);

            catalogoPlaylist.aggiungiPlaylist(prima);
            catalogoPlaylist.aggiungiPlaylist(seconda);

            assertSame(
                    seconda,
                    statistiche
                            .getPlaylistPiuRiprodotta(
                                    catalogoPlaylist
                            )
                            .orElseThrow()
            );
        }

        @Test
        void testElementiPiuRiprodottiAssentiConCataloghiVuoti() {
            assertTrue(
                    statistiche
                            .getTracciaPiuRiprodotta(catalogo)
                            .isEmpty()
            );

            assertTrue(
                    statistiche
                            .getPlaylistPiuRiprodotta(
                                    catalogoPlaylist
                            )
                            .isEmpty()
            );
        }

        @Test
        void testCataloghiNullLancianoEccezione() {
            assertThrows(
                    NullPointerException.class,
                    () -> statistiche.getTopTracce(null, 5)
            );

            assertThrows(
                    NullPointerException.class,
                    () -> statistiche.getTopPlaylist(null, 5)
            );

            assertThrows(
                    NullPointerException.class,
                    () -> statistiche.contaTracce(null)
            );

            assertThrows(
                    NullPointerException.class,
                    () -> statistiche.contaPlaylist(null)
            );
        }

        private Traccia creaTraccia(String titolo) {
            return new Traccia(
                    UUID.randomUUID().toString(),
                    titolo,
                    "Artista " + titolo,
                    "3:00",
                    "Rock",
                    2024
            );
        }

        private void incrementaRiproduzioni(
                Traccia traccia,
                int numero
        ) {
            for (int i = 0; i < numero; i++) {
                traccia.incrementaRiproduzioni();
            }
        }

        private void incrementaRiproduzioni(
                Playlist playlist,
                int numero
        ) {
            for (int i = 0; i < numero; i++) {
                playlist.incrementaRiproduzioni();
            }
        }
    }
