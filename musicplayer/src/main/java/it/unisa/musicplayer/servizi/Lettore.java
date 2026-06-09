package it.unisa.musicplayer.servizi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.unisa.musicplayer.modello.Traccia;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Lettore {

    private StatoLettore stato;
    private List<Traccia> coda;
    private ModalitaRiproduzione modalita;
    private final ObjectProperty<StatoLettore> statoProperty = 
        new SimpleObjectProperty<>(StatoLettore.STOPPED);

    private final ObjectProperty<Traccia> tracciaCorrente = new SimpleObjectProperty<>();
    private final IntegerProperty tempoTrascorso = new SimpleIntegerProperty(0);

    public Lettore() {
        this.stato = StatoLettore.STOPPED;
        this.coda = new ArrayList<>();
        this.modalita = new Sequenziale();
    }

    public void play() {
        if (!coda.isEmpty() && tracciaCorrente.get() == null) {
            tracciaCorrente.set(coda.get(0));
        }
        stato = StatoLettore.PLAYING;
        statoProperty.set(StatoLettore.PLAYING);
    }
    
    public void pausa() {
        if (stato == StatoLettore.PLAYING) {
            stato = StatoLettore.PAUSED;
            statoProperty.set(StatoLettore.PAUSED);
        }
    }
    
    public void stop() {
        stato = StatoLettore.STOPPED;
        statoProperty.set(StatoLettore.STOPPED);
        tempoTrascorso.set(0);
        tracciaCorrente.set(null);
    }
    
    public void skip() {
        if (modalita != null && tracciaCorrente.get() != null) {
            Traccia prossima = modalita.prossimaTraccia(coda, tracciaCorrente.get());
            tracciaCorrente.set(prossima);
            tempoTrascorso.set(0);
            stato = prossima != null ? StatoLettore.PLAYING : StatoLettore.STOPPED;
            statoProperty.set(stato);
        } else {
            stato = StatoLettore.STOPPED;
            statoProperty.set(StatoLettore.STOPPED);
        }
    }

    public void avanzaTempo(int secondi) {
        if (stato == StatoLettore.PLAYING) {
            tempoTrascorso.set(tempoTrascorso.get() + secondi);
        }
    }

    public void aggiornaCodeTracce(List<Traccia> tracce) {
        this.coda = new ArrayList<>(tracce);
        tempoTrascorso.set(0);
        tracciaCorrente.set(coda.isEmpty() ? null : coda.get(0));
    }

    public void setModalita(ModalitaRiproduzione modalita) {
        this.modalita = Objects.requireNonNull(
                modalita,
                "La modalità di riproduzione non può essere null"
        );
    }

    public StatoLettore getStato() { return stato; }
    public ModalitaRiproduzione getModalita() { return modalita; }
    public ObjectProperty<Traccia> tracciaCorrenteProperty() { return tracciaCorrente; }
    public Traccia getTracciaCorrente() { return tracciaCorrente.get(); }
    public IntegerProperty tempoTrascorsoProperty() { return tempoTrascorso; }
    public int getTempoTrascorso() { return tempoTrascorso.get(); }
    public List<Traccia> getCoda() { return coda; }
    public ObjectProperty<StatoLettore> statoProperty() { return statoProperty; }
}