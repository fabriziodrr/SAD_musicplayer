package it.unisa.musicplayer.servizi;

import java.util.List;

import it.unisa.musicplayer.modello.Traccia;

public abstract class ModalitaRiproduzione {
    public abstract Traccia prossimaTraccia(List<Traccia> tracce, Traccia corrente);
    public abstract String getNome();
}