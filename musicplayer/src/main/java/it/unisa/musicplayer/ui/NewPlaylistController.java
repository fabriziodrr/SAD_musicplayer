package it.unisa.musicplayer.ui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class NewPlaylistController {

    @FXML private TextField txtNomePlaylist;

    @FXML
    public void initialize() {
    }

    public String getNomePlaylist() {
        String nome = txtNomePlaylist.getText() != null ? txtNomePlaylist.getText().trim() : "";
        if (nome.isEmpty()) {
            throw new IllegalArgumentException("Il nome della playlist non può essere vuoto");
        }
        return nome;
    }
}
