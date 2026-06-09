package it.unisa.musicplayer.servizi;

import java.util.List;

import it.unisa.musicplayer.modello.Traccia;

public class LoopSingola extends ModalitaRiproduzione {

    @Override
    public Traccia prossimaTraccia(List<Traccia> tracce, Traccia corrente) {
        if (corrente == null && !tracce.isEmpty()) return tracce.get(0);
        return corrente; // ripete sempre la stessa traccia
    }

    @Override
    public String getNome() { return "LOOP_SINGOLA"; }
}