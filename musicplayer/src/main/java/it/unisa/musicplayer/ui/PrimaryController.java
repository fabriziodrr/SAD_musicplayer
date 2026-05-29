package it.unisa.musicplayer.ui;

import it.unisa.musicplayer.modello.*; 
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class PrimaryController {

    // Navigazione e Struttura della GUI
    @FXML private TabPane mainTabPane;
    @FXML private Button btnNavHome;
    @FXML private Button btnNavStats;
    @FXML private Button btnOpenMockDialog;
    
    // Ora è perfettamente mappato sull'id dell'FXML modificato
    @FXML private Button btnEditTrack; 
    @FXML private Button btnRemoveTrack;

    // Elementi di testo, etichette e liste
    @FXML private ListView<String> sidebarListView;
    @FXML private ListView<String> topTracksListView;
    @FXML private ListView<String> topPlaylistsListView;
    @FXML private Label currentTrackLabel;
    @FXML private Label catalogTitleLabel;

    // TableView mappata sulla classe reale "Traccia"
    @FXML private TableView<Traccia> songTableView;
    @FXML private TableColumn<Traccia, String> titleColumn;
    @FXML private TableColumn<Traccia, String> autoreColumn;
    @FXML private TableColumn<Traccia, String> genreColumn;
    @FXML private TableColumn<Traccia, Integer> yearColumn;
    @FXML private TableColumn<Traccia, String> durationColumn;

    private ObservableList<Traccia> singlePlaylistData;

    @FXML
    public void initialize() {
        
        // 1. COLLEGAMENTO DELLA TABLEVIEW AL CATALOGO SINGLETON REALE
        if (songTableView != null) {
            songTableView.setItems(Catalogo.getInstance().getTracce());
        }
        
        singlePlaylistData = FXCollections.observableArrayList();

        // 2. CONFIGURAZIONE DELLE COLONNE CON I GETTER DELLA CLASSE TRACCIA
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("titolo"));
        autoreColumn.setCellValueFactory(new PropertyValueFactory<>("autore"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genere"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("anno"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("durata"));

        // CellFactory per la colonna del Titolo + Tag orizzontali
        titleColumn.setCellFactory(column -> new TableCell<Traccia, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Traccia traccia = getTableView().getItems().get(getIndex());
                    Label lblTitolo = new Label(traccia.getTitolo());
                    lblTitolo.setStyle("-fx-text-fill: #FFFFFF; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 13px;");
                    
                    boolean esplicito = false;
                    boolean preferito = false;
                    boolean nuovaUscita = false;
                    try {
                        if (traccia.getTag() != null) {
                            for (Tag t : traccia.getTag()) {
                                String nomeTag = t.name().toUpperCase();
                                if (nomeTag.contains("EXPLICIT") || nomeTag.contains("ESPLICITO") || nomeTag.equals("E")) esplicito = true;
                                if (nomeTag.contains("FAVORITE") || nomeTag.contains("FAVOURITE") || nomeTag.contains("PREFERITO") || nomeTag.equals("STAR")) preferito = true;
                                if (nomeTag.contains("NEW") || nomeTag.contains("NUOVA") || nomeTag.contains("USCITA")) nuovaUscita = true;
                            }
                        }
                    } catch (Exception e) {}
                    
                    if (esplicito || preferito || nuovaUscita) {
                        javafx.scene.layout.HBox contenitoreOrizzontale = new javafx.scene.layout.HBox(6);
                        contenitoreOrizzontale.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                        contenitoreOrizzontale.getChildren().add(lblTitolo);
                        if (esplicito) {
                            Label lblBadge = new Label("[E]");
                            lblBadge.setStyle("-fx-text-fill: #E91E63; -fx-font-family: 'Segoe UI'; -fx-font-size: 11px; -fx-font-weight: bold;");
                            contenitoreOrizzontale.getChildren().add(lblBadge);
                        }
                        if (nuovaUscita) {
                            Label lblNew = new Label("[NEW]");
                            lblNew.setStyle("-fx-text-fill: #00D2FF; -fx-font-family: 'Segoe UI'; -fx-font-size: 11px; -fx-font-weight: bold;");
                            contenitoreOrizzontale.getChildren().add(lblNew);
                        }
                        if (preferito) {
                            Label lblStar = new Label("★");
                            lblStar.setStyle("-fx-text-fill: #1DB954; -fx-font-size: 13px;");
                            contenitoreOrizzontale.getChildren().add(lblStar);
                        }
                        setGraphic(contenitoreOrizzontale);
                    } else {
                        setGraphic(lblTitolo);
                    }
                    setText(null);
                }
            }
        });

        // 3. LOGICA DEL MUSIC PLAYER DI DESTRA (Aggiornamento con Doppio Click sulla riga)
        if (songTableView != null) {
            songTableView.setRowFactory(tv -> {
                TableRow<Traccia> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && (!row.isEmpty())) {
                        Traccia cliccata = row.getItem();
                        if (cliccata != null && currentTrackLabel != null) {
                            currentTrackLabel.setText(cliccata.getTitolo());
                            try {
                                javafx.scene.layout.VBox parentVBox = (javafx.scene.layout.VBox) currentTrackLabel.getParent();
                                if (parentVBox.getChildren().size() > 1 && parentVBox.getChildren().get(1) instanceof Label) {
                                    Label artistLabel = (Label) parentVBox.getChildren().get(1);
                                    artistLabel.setText(cliccata.getAutore()); 
                                }
                            } catch (Exception e) {}
                            cliccata.incrementaRiproduzioni(); 
                        }
                    }
                });
                return row;
            });
        }

        // 4. LOGICA DI SPOSTAMENTO SCHERMATE (ROUTING)
        if (btnNavHome != null && mainTabPane != null) {
            btnNavHome.setOnAction(e -> {
                mainTabPane.getSelectionModel().select(0);
                btnNavHome.setStyle("-fx-background-color: transparent; -fx-text-fill: #FFFFFF; -fx-font-weight: bold;");
                btnNavStats.setStyle("-fx-background-color: transparent; -fx-text-fill: #B3B3B3;");
                if (catalogTitleLabel != null) catalogTitleLabel.setText("Catalogo Globale");
                if (songTableView != null) songTableView.setItems(Catalogo.getInstance().getTracce());
            });
        }

        if (btnNavStats != null && mainTabPane != null) {
            btnNavStats.setOnAction(e -> {
                mainTabPane.getSelectionModel().select(1);
                btnNavStats.setStyle("-fx-background-color: transparent; -fx-text-fill: #FFFFFF; -fx-font-weight: bold;");
                btnNavHome.setStyle("-fx-background-color: transparent; -fx-text-fill: #B3B3B3;");
            });
        }

        // 5. COMPORTAMENTO CLIC SIDEBAR PLAYLIST
        if (sidebarListView != null) {
            sidebarListView.getItems().addAll("⚡ Rock Classics", "☕ Jazz Chillout", "🎵 Workout Hits", "🧠 Focus Coding");
            sidebarListView.getSelectionModel().selectedItemProperty().addListener((o, old, newVal) -> {
                if (newVal != null) {
                    mainTabPane.getSelectionModel().select(0);
                    btnNavHome.setStyle("-fx-background-color: transparent; -fx-text-fill: #FFFFFF; -fx-font-weight: bold;");
                    btnNavStats.setStyle("-fx-background-color: transparent; -fx-text-fill: #B3B3B3;");
                    if (catalogTitleLabel != null) catalogTitleLabel.setText("Contenuto: " + newVal);
                    if (songTableView != null) songTableView.setItems(singlePlaylistData);
                }
            });
        }

        // 6. POPOLAMENTO STATISTICHE
        if (topTracksListView != null) {
            topTracksListView.getItems().clear(); 
        }
        if (topPlaylistsListView != null) {
            topPlaylistsListView.getItems().addAll("1. Rock Anthems", "2. Chill Vibes", "3. Top 50 Italia");
        }

        // 7. INTERFACCIA DI AGGIUNTA AL CATALOGO
        if (btnOpenMockDialog != null) {
            btnOpenMockDialog.setOnAction(e -> {
                try {
                    javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("song_dialog.fxml"));
                    DialogPane dialogPane = loader.load();
                    SongDialogController dialogController = loader.getController();
                    
                    Dialog<ButtonType> dialog = new Dialog<>();
                    dialog.setDialogPane(dialogPane);
                    dialog.setTitle("Gestione Catalogo - Aggiungi");
                    dialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
                    
                    java.util.Optional<ButtonType> result = dialog.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        Traccia nuovaTraccia = dialogController.getSongFromForm();
                        if (nuovaTraccia != null) {
                            Catalogo.getInstance().aggiungiTraccia(nuovaTraccia);
                            
                            // Gestione playlist corrente visiva
                            if (catalogTitleLabel != null && catalogTitleLabel.getText().startsWith("Contenuto: ")) {
                                //String playlistSelezionata = catalogTitleLabel.getText().replace("Contenuto: ", "").trim();
                                //Catalogo.getInstance().aggiungiTracciaAPlaylist(playlistSelezionata, nuovaTraccia);
                                //singlePlaylistData.add(nuovaTraccia);
                            }
                        }
                    }
                } catch (IllegalArgumentException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Errore di inserimento: " + ex.getMessage());
                    alert.showAndWait();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }

        // 8. INTERFACCIA REALE DI MODIFICA BRANO (US-03)
        if (btnEditTrack != null) {
            btnEditTrack.setOnAction(e -> {
                // Recuperiamo la canzone attualmente selezionata con un singolo click nella tabella
                Traccia tracciaSelezionata = songTableView.getSelectionModel().getSelectedItem();
                
                if (tracciaSelezionata == null) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Seleziona prima un brano dalla tabella con un singolo click!");
                    alert.showAndWait();
                    return;
                }

                try {
                    javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("song_dialog.fxml"));
                    DialogPane dialogPane = loader.load();
                    SongDialogController dialogController = loader.getController();
                    
                    // PRE-COMPILAZIONE DEI CAMPI: Carichiamo i vecchi valori nel form
                    dialogController.caricaDatiTraccia(tracciaSelezionata);
                    
                    Dialog<ButtonType> dialog = new Dialog<>();
                    dialog.setDialogPane(dialogPane);
                    dialog.setTitle("Gestione Catalogo - Modifica Brano");
                    dialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
                    
                    java.util.Optional<ButtonType> result = dialog.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        // Estraiamo i testi aggiornati mantenendo l'ID originale immutato
                        Traccia tracciaModificata = dialogController.getSongFromForm();
                        
                        if (tracciaModificata != null) {
                            // Eseguiamo l'aggiornamento nel modello Singleton
                            Catalogo.getInstance().modificaTraccia(tracciaSelezionata, tracciaModificata);
                            
                            // L'uso della ObservableList aggiorna istantaneamente la tabella a schermo!
                        }
                    }
                } catch (IllegalArgumentException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Errore di validazione della modifica: " + ex.getMessage());
                    alert.showAndWait();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }
}
