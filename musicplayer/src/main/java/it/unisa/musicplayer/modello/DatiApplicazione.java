package it.unisa.musicplayer.modello;

import java.util.ArrayList;
import java.util.List;

/**
 * Oggetto contenitore (Wrapper) per la serializzazione su file JSON.
 * Configurato esclusivamente per il catalogo delle canzoni.
 */
public class DatiApplicazione {
    
    private List<Traccia> canzoni;

    /** Costruttore vuoto necessario per la deserializzazione Jackson */
    public DatiApplicazione() {
        this.canzoni = new ArrayList<>();
    }

    /** Costruttore utilizzato dal flusso di salvataggio automatico */
    public DatiApplicazione(List<Traccia> canzoni) {
        this.canzoni = canzoni;
    }

    // Getter e Setter fondamentali per Jackson
    public List<Traccia> getCanzoni() { 
        return canzoni; 
    }
    
    public void setCanzoni(List<Traccia> canzoni) { 
        this.canzoni = canzoni; 
    }
}
