package it.unisa.musicplayer.modello;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Playlist {

    private String nome;

    @JsonIgnore
    private final ObservableList<Traccia> tracce;

    private List<String> idTracce;

    /*
     * Contatore valido soltanto durante la sessione corrente.
     * Non viene salvato nel file JSON.
     */
    @JsonIgnore
    private int contaRiproduzioni;

    @JsonCreator
    public Playlist(@JsonProperty("nome") String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Il nome della playlist non può essere vuoto"
            );
        }

        this.nome = nome.trim();
        this.tracce = FXCollections.observableArrayList();
        this.idTracce = new ArrayList<>();
        this.contaRiproduzioni = 0;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Il nome della playlist non può essere vuoto"
            );
        }

        this.nome = nome.trim();
    }

    @JsonIgnore
    public ObservableList<Traccia> getTracce() {
        return tracce;
    }

    public List<String> getIdTracce() {
        return idTracce;
    }

    public void setIdTracce(List<String> idTracce) {
        this.idTracce = idTracce != null
                ? idTracce
                : new ArrayList<>();
    }

    /**
     * Restituisce il numero di volte in cui la playlist
     * è stata avviata durante la sessione corrente.
     *
     * @return numero di riproduzioni della playlist
     */
    @JsonIgnore
    public int getContaRiproduzioni() {
        return contaRiproduzioni;
    }

    /**
     * Incrementa il numero di riproduzioni della playlist.
     */
    public void incrementaRiproduzioni() {
        contaRiproduzioni++;
    }

    public void aggiungiTraccia(Traccia t) {
        Objects.requireNonNull(
                t,
                "La traccia non può essere null"
        );

        if (!idTracce.contains(t.getId())) {
            idTracce.add(t.getId());
            tracce.add(t);
        }
    }

    public void inserisciTraccia(Traccia t, int indice) {
        Objects.requireNonNull(
                t,
                "La traccia non può essere null"
        );

        if (idTracce.contains(t.getId())) {
            return;
        }

        /*
         * L'indice viene normalizzato per ripristinare
         * la posizione più vicina possibile.
         */
        int indiceSicuro = Math.max(
                0,
                Math.min(indice, tracce.size())
        );

        idTracce.add(indiceSicuro, t.getId());
        tracce.add(indiceSicuro, t);
    }

    public void rimuoviTraccia(Traccia t) {
        Objects.requireNonNull(
                t,
                "La traccia non può essere null"
        );

        idTracce.remove(t.getId());
        tracce.remove(t);
    }

    public void risolviRiferimenti(Catalogo catalogo) {
        tracce.clear();

        for (String id : idTracce) {
            catalogo.cercaPerId(id)
                    .ifPresent(tracce::add);
        }
    }

    public void spostaSu(Traccia t) {
        int indice = tracce.indexOf(t);
        if (indice > 0) {
            tracce.remove(indice);
            tracce.add(indice - 1, t);
        }
    }
    
    public void spostaGiu(Traccia t) {
        int indice = tracce.indexOf(t);
        if (indice >= 0 && indice < tracce.size() - 1) {
            tracce.remove(indice);
            tracce.add(indice + 1, t);
        }
    }

    @JsonIgnore
    public int getNumeroTracce() {
        return tracce.size();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Playlist)) {
            return false;
        }

        Playlist altra = (Playlist) obj;
        return nome.equalsIgnoreCase(altra.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome.toLowerCase());
    }

    @Override
    public String toString() {
        return nome + " (" + tracce.size() + ")";
    }
}