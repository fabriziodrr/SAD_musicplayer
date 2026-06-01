package it.unisa.musicplayer.servizi;

import java.util.List;

import it.unisa.musicplayer.modello.Traccia;

public class Loop extends ModalitaRiproduzione {

    @Override
    public Traccia prossimaTraccia(List<Traccia> tracce, Traccia corrente) {
        // TODO: implementare in una sprint futura
        return corrente;
    }
}