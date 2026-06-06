package it.unisa.musicplayer.servizi;

import java.util.List;

import it.unisa.musicplayer.modello.Traccia;

public class Loop extends ModalitaRiproduzione {

    @Override
    public Traccia prossimaTraccia(List<Traccia> tracce, Traccia corrente) {
        if (tracce == null || tracce.isEmpty()) {
            return null;
        }

        /*
         * Se la riproduzione non è ancora iniziata,
         * si parte dalla prima traccia.
         */
        if (corrente == null) {
            return tracce.get(0);
        }

        int indiceCorrente = tracce.indexOf(corrente);

        /*
         * Se la traccia corrente non appartiene alla lista,
         * non è possibile determinare la successiva.
         */
        if (indiceCorrente == -1) {
            return null;
        }

        /*
         * Se siamo arrivati all'ultima traccia,
         * il loop riparte dalla prima.
         */
        if (indiceCorrente == tracce.size() - 1) {
            return tracce.get(0);
        }

        return tracce.get(indiceCorrente + 1);
    }
}