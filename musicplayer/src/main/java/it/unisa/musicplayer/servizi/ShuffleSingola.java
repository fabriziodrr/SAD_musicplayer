package it.unisa.musicplayer.servizi;

import java.util.List;
import java.util.Random;

import it.unisa.musicplayer.modello.Traccia;

public class ShuffleSingola extends ModalitaRiproduzione {

    private final Random random = new Random();

    @Override
    public Traccia prossimaTraccia(List<Traccia> tracce, Traccia corrente) {
        if (tracce == null || tracce.isEmpty()) return null;
        if (tracce.size() == 1) return tracce.get(0);
        
        Traccia prossima;
        do {
            prossima = tracce.get(random.nextInt(tracce.size()));
        } while (prossima.getId().equals(corrente.getId()));
        
        return prossima;
    }

    @Override
    public String getNome() { return "SHUFFLE_SINGOLA"; }
}