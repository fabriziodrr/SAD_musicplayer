package it.unisa.musicplayer.servizi;

public abstract class Operazione {

    public abstract void esegui();

    public abstract void annulla();

    public abstract String getDescrizione();
}
