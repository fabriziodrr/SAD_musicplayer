package it.unisa.musicplayer.servizi;

import java.util.Objects;

import it.unisa.musicplayer.modello.Catalogo;
import it.unisa.musicplayer.modello.Traccia;

public class AggiungiTracciaCatalogo extends Operazione {

    private final Catalogo catalogo;
    private final Traccia traccia;

    public AggiungiTracciaCatalogo(Catalogo catalogo, Traccia traccia) {
        this.catalogo = Objects.requireNonNull(catalogo, "Il catalogo non può essere null");
        this.traccia = Objects.requireNonNull(traccia, "La traccia non può essere null");
    }

    @Override
    public void esegui() {
        catalogo.aggiungiTraccia(traccia);
    }

    @Override
    public void annulla() {
        if (catalogo.getTracce().contains(traccia)) {
            catalogo.rimuoviTraccia(traccia);
        }
    }

    @Override
    public String getDescrizione() {
        return "Aggiunta traccia al catalogo: " + traccia.getTitolo();
    }
}
