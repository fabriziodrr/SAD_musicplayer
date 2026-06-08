package it.unisa.musicplayer.servizi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import it.unisa.musicplayer.modello.Traccia;

public class Shuffle extends ModalitaRiproduzione {

    private final Set<Traccia> giaRiprodotte = new HashSet<>();
    private final Random random = new Random();

    @Override
    public Traccia prossimaTraccia(List<Traccia> tracce, Traccia corrente) {
        if (tracce == null || tracce.isEmpty()) {
            reset();
            return null;
        }

        /*
         * Con una sola traccia non esistono alternative:
         * viene restituita sempre quella traccia.
         */
        if (tracce.size() == 1) {
            Traccia unicaTraccia = tracce.get(0);
            giaRiprodotte.add(unicaTraccia);
            return unicaTraccia;
        }

        /*
         * Elimina dallo storico eventuali tracce
         * che non appartengono più alla playlist.
         */
        giaRiprodotte.retainAll(tracce);

        /*
         * La traccia corrente viene considerata già riprodotta,
         * quindi non può essere scelta immediatamente di nuovo.
         */
        if (corrente != null && tracce.contains(corrente)) {
            giaRiprodotte.add(corrente);
        }

        List<Traccia> disponibili = calcolaTracceDisponibili(tracce, corrente);

        /*
         * Se tutte le tracce sono state riprodotte,
         * comincia un nuovo ciclo, evitando comunque
         * la ripetizione consecutiva della traccia corrente.
         */
        if (disponibili.isEmpty()) {
            reset();

            if (corrente != null && tracce.contains(corrente)) {
                giaRiprodotte.add(corrente);
            }

            disponibili = calcolaTracceDisponibili(tracce, corrente);
        }

        Traccia prossima = disponibili.get(random.nextInt(disponibili.size()));
        giaRiprodotte.add(prossima);

        return prossima;
    }

    private List<Traccia> calcolaTracceDisponibili(
            List<Traccia> tracce,
            Traccia corrente
    ) {
        List<Traccia> disponibili = new ArrayList<>();

        for (Traccia traccia : tracce) {
            boolean nonRiprodotta = !giaRiprodotte.contains(traccia);
            boolean diversaDallaCorrente = !Objects.equals(traccia, corrente);

            if (nonRiprodotta && diversaDallaCorrente) {
                disponibili.add(traccia);
            }
        }

        return disponibili;
    }

    public void reset() {
        giaRiprodotte.clear();
    }
}