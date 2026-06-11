package it.unisa.musicplayer.servizi;

import java.util.List;

import it.unisa.musicplayer.modello.Traccia;

public class SequenzialeSingola extends Sequenziale {

    @Override
    public Traccia prossimaTraccia(List<Traccia> tracce, Traccia corrente) {
        if (tracce == null || tracce.isEmpty()) return null;
        if (corrente == null) return tracce.get(0);

        int indice = -1;
        for (int i = 0; i < tracce.size(); i++) {
            if (tracce.get(i).getId().equals(corrente.getId())) {
                indice = i;
                break;
            }
        }

        if (indice == -1) return null;
        
        // Se ultima traccia → riparte dalla prima
        if (indice == tracce.size() - 1) {
            return tracce.get(0);
        }

        return tracce.get(indice + 1);
    }

    @Override
    public String getNome() { return "SEQUENZIALE_SINGOLA"; }
}
