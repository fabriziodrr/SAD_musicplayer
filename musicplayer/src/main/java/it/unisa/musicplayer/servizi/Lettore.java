package it.unisa.musicplayer.servizi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.unisa.musicplayer.modello.Playlist;
import it.unisa.musicplayer.modello.Traccia;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;

public class Lettore {

    private StatoLettore stato;
    private List<Traccia> coda;
    private ModalitaRiproduzione modalita;
    private Playlist playlistCorrente;
    private ListChangeListener<Traccia> listenerPlaylistCorrente;

    // JavaFX Properties per binding con UI
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
    }

    public void pausa() {
        if (stato == StatoLettore.PLAYING) {
            stato = StatoLettore.PAUSED;
        }
    }

    public void stop() {
        stato = StatoLettore.STOPPED;
        tempoTrascorso.set(0);
        tracciaCorrente.set(null);
    }

    public void skip() {
        if (modalita != null && tracciaCorrente.get() != null) {
            Traccia prossima = modalita.prossimaTraccia(coda, tracciaCorrente.get());
            tracciaCorrente.set(prossima);
            tempoTrascorso.set(0);
            stato = prossima != null ? StatoLettore.PLAYING : StatoLettore.STOPPED;
        } else {
            stato = StatoLettore.STOPPED;
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

    public void sincronizzaConPlaylist(Playlist playlist) {
        if (playlist == null) {
            throw new IllegalArgumentException("La playlist da sincronizzare non può essere null");
        }

        if (playlistCorrente != null && listenerPlaylistCorrente != null) {
            playlistCorrente.getTracce().removeListener(listenerPlaylistCorrente);
        }

        playlistCorrente = playlist;
        coda = new ArrayList<>(playlist.getTracce());
        if (tracciaCorrente.get() == null && !coda.isEmpty()) {
            tracciaCorrente.set(coda.get(0));
        }

        listenerPlaylistCorrente = change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    coda.addAll(change.getAddedSubList());
                }
                if (change.wasRemoved()) {
                    List<? extends Traccia> rimosse = change.getRemoved();
                    boolean correnteRimossa = false;
                    for (Traccia rimossa : rimosse) {
                        if (rimossa.equals(tracciaCorrente.get())) {
                            correnteRimossa = true;
                        }
                    }
                    coda.removeAll(rimosse);
                    if (correnteRimossa) {
                        tempoTrascorso.set(0);
                        if (coda.isEmpty()) {
                            tracciaCorrente.set(null);
                            stato = StatoLettore.STOPPED;
                        } else if (modalita != null) {
                            Traccia prossima = modalita.prossimaTraccia(coda, tracciaCorrente.get());
                            tracciaCorrente.set(prossima);
                            stato = StatoLettore.PLAYING;
                        }
                    }
                }
            }
        };

        playlistCorrente.getTracce().addListener(listenerPlaylistCorrente);
    }

    public void setModalita(ModalitaRiproduzione modalita) {
        this.modalita = Objects.requireNonNull(
                modalita,
                "La modalità di riproduzione non può essere null"
        );
    }

    public StatoLettore getStato() { return stato; }

    public ObjectProperty<Traccia> tracciaCorrenteProperty() { return tracciaCorrente; }
    public Traccia getTracciaCorrente() { return tracciaCorrente.get(); }

    public IntegerProperty tempoTrascorsoProperty() { return tempoTrascorso; }
    public int getTempoTrascorso() { return tempoTrascorso.get(); }

    public List<Traccia> getCoda() { return coda; }
}
