package it.unisa.musicplayer.servizi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;
import it.unisa.musicplayer.modello.DatiApplicazione;

/**
 * Componente per il salvataggio dei dati sul Desktop in formato JSON.
 */
public class GestoreFile {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        // Rende il codice JSON indentato e leggibile aprendolo con un editor di testo
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    // Rintraccia dinamicamente la cartella Desktop dell'utente corrente. 
    private static String getDesktopPath() {
        String userHome = System.getProperty("user.home");
        return userHome + File.separator + "Desktop" + File.separator + "catalogo_spotify.json";
    }

    /**
     * Serializza l'oggetto DatiApplicazione salvandolo sul Desktop.
     */
    public static void esporta(DatiApplicazione dati) {
        try {
            File fileDestinazione = new File(getDesktopPath());
            mapper.writeValue(fileDestinazione, dati);
            System.out.println("[SAVE OK] Catalogo e Playlist salvati sul Desktop: " + fileDestinazione.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("[ERRORE SAVE] Impossibile scrivere il file JSON sul Desktop: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
     // Carica i dati dal file JSON sul Desktop. Se il file non esiste o è corrotto, restituisce un DatiApplicazione vuoto.
    
    public static DatiApplicazione importa() {
        return importaDaPercorso(getDesktopPath());
    }

    static DatiApplicazione importaDaPercorso(String percorso) {
        try {
            File file = new File(percorso);
            if (!file.exists()) {
                System.out.println("[LOAD] Nessun file trovato in " + percorso + ", avvio con catalogo vuoto.");
                return new DatiApplicazione();
            }
            DatiApplicazione dati = mapper.readValue(file, DatiApplicazione.class);
            System.out.println("[LOAD OK] Catalogo caricato da: " + percorso);
            return dati;
        } catch (IOException e) {
            System.err.println("[ERRORE LOAD] Impossibile leggere il file JSON: " + e.getMessage());
            return new DatiApplicazione();
        }
    }
}
