package it.unisa.musicplayer.servizi;

import java.util.List;

import it.unisa.musicplayer.modello.Traccia;

public class Sequenziale extends ModalitaRiproduzione {

    @Override
    public Traccia prossimaTraccia(List<Traccia> tracce, Traccia corrente) {
        int indice = -1;
        for (int i = 0; i < tracce.size(); i++) {
            if (tracce.get(i).getId().equals(corrente.getId())) {
                indice = i;
                break;
            }
        }
        if (indice == -1 || indice >= tracce.size() - 1) {
            return tracce.get(0);
        }
        return tracce.get(indice + 1);
    }
}
