package it.unisa.musicplayer.ui;

import it.unisa.musicplayer.modello.Traccia;
import it.unisa.musicplayer.modello.Tag;
import java.util.UUID;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class SongDialogController {

    @FXML private TextField txtTitle;
    @FXML private TextField txtArtist;
    @FXML private TextField txtDuration;
    @FXML private TextField txtGenre;
    @FXML private TextField txtYear;
    
    @FXML private CheckBox chkExplicit;
    @FXML private CheckBox chkFavorite;
    @FXML private CheckBox chkNewRelease;

    // Teniamo traccia se stiamo modificando una traccia esistente per conservarne l'ID originale
    private String idTracciaInModifica = null;

    @FXML
    public void initialize() {
        // Avvio vuoto
    }

    /**
     * Pre-compila i campi del modulo con i dati della traccia selezionata (US-03).
     */
    public void caricaDatiTraccia(Traccia t) {
        if (t == null) return;
        
        this.idTracciaInModifica = t.getId(); // Conserviamo l'ID originale!
        
        txtTitle.setText(t.getTitolo());
        txtArtist.setText(t.getAutore());
        txtDuration.setText(t.getDurata());
        txtGenre.setText(t.getGenere());
        txtYear.setText(String.valueOf(t.getAnno()));
        
        // Pre-compila i CheckBox dei Tag
        chkExplicit.setSelected(false);
        chkFavorite.setSelected(false);
        chkNewRelease.setSelected(false);
        
        try {
            if (t.getTag() != null) {
                for (Tag tag : t.getTag()) {
                    String nomeTag = tag.name().toUpperCase();
                    if (nomeTag.contains("EXPLICIT") || nomeTag.contains("ESPLICITO") || nomeTag.equals("E")) chkExplicit.setSelected(true);
                    if (nomeTag.contains("FAVORITE") || nomeTag.contains("FAVOURITE") || nomeTag.contains("PREFERITO") || nomeTag.equals("STAR")) chkFavorite.setSelected(true);
                    if (nomeTag.contains("NEW") || nomeTag.contains("NUOVA") || nomeTag.contains("USCITA")) chkNewRelease.setSelected(true);
                }
            }
        } catch (Exception e) {
            // Failsafe
        }
    }

    /**
     * Raccoglie i dati e restituisce l'oggetto Traccia. 
     * Se eravamo in modalità modifica, mantiene l'ID originale della traccia.
     */
    public Traccia getSongFromForm() {
        String titolo = txtTitle.getText() != null ? txtTitle.getText().trim() : "";
        String autore = txtArtist.getText() != null ? txtArtist.getText().trim() : "";
        String durata = txtDuration.getText() != null ? txtDuration.getText().trim() : "";
        String genere = txtGenre.getText() != null ? txtGenre.getText().trim() : "";
        String annoStr = txtYear.getText() != null ? txtYear.getText().trim() : "";

        if (titolo.isEmpty() || autore.isEmpty() || durata.isEmpty() || genere.isEmpty() || annoStr.isEmpty()) {
            throw new IllegalArgumentException("Tutti i campi del modulo sono obbligatori! Compilare ogni riquadro.");
        }

        if (!durata.matches("\\d+:[0-5]\\d")) {
            throw new IllegalArgumentException("Formato della Durata non valido. Usa la sintassi mm:ss (es. 03:45).");
        }

        int anno;
        try {
            anno = Integer.parseInt(annoStr);
            if (anno <= 0 || anno >2026) throw new IllegalArgumentException("L'anno deve essere un valore valido.");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Il campo Anno deve contenere unicamente un numero intero.");
        }

        // SE ERAVAMO IN MODIFICA TIENE L'ID DI PRIMA, ALTRIMENTI GENERA UN NUOVO UUID
        String idFinale = (idTracciaInModifica != null) ? idTracciaInModifica : UUID.randomUUID().toString();
        
        Traccia nuovaTraccia = new Traccia(idFinale, titolo, autore, durata, genere, anno);
        
        // Associazione Tag
        try {
            for (Tag t : Tag.values()) {
                String nomeTag = t.name().toUpperCase();
                if (chkExplicit.isSelected() && (nomeTag.contains("EXPLICIT") || nomeTag.contains("ESPLICITO") || nomeTag.equals("E"))) nuovaTraccia.aggiungiTag(t);
                if (chkFavorite.isSelected() && (nomeTag.contains("FAVORITE") || nomeTag.contains("FAVOURITE") || nomeTag.contains("PREFERITO") || nomeTag.equals("STAR"))) nuovaTraccia.aggiungiTag(t);
                if (chkNewRelease.isSelected() && (nomeTag.contains("NEW") || nomeTag.contains("NUOVA") || nomeTag.contains("USCITA"))) nuovaTraccia.aggiungiTag(t);
            }
        } catch (Exception e) {}

        return nuovaTraccia;
    }
}
