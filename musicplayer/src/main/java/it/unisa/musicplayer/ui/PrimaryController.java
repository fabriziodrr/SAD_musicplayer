package it.unisa.musicplayer.ui;

import java.util.ArrayList;
import java.util.List;

import it.unisa.musicplayer.modello.Catalogo;
import it.unisa.musicplayer.modello.CatalogoPlaylist;
import it.unisa.musicplayer.modello.Playlist;
import it.unisa.musicplayer.modello.Tag;
import it.unisa.musicplayer.modello.Traccia;
import it.unisa.musicplayer.servizi.Lettore;
import it.unisa.musicplayer.servizi.Sequenziale;
import it.unisa.musicplayer.servizi.StatoLettore;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
public class PrimaryController {

    // Navigazione e Struttura della GUI
    @FXML private TabPane mainTabPane;
    @FXML private Button btnNavHome;
    @FXML private Button btnNavStats;
    @FXML private Button btnOpenMockDialog;
    
    // Ora è perfettamente mappato sull'id dell'FXML modificato
    @FXML private Button btnEditTrack; 
    @FXML private Button btnRemoveTrack;
    @FXML private Button createNewPlaylistButton;

    // Elementi di testo, etichette e liste
    @FXML private ListView<Playlist> sidebarListView;
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


    // Player Controls - US-09
    @FXML private Button btnPlay;
    @FXML private Button btnPausa;
    @FXML private Button btnSkip;
    @FXML private Button btnPrecedente;
    @FXML private ProgressBar barraProgresso;
    @FXML private Label labelAutoreTraccia;
    @FXML private Label labelTempoTrascorso;
    @FXML private Label labelDurataTotale;
    @FXML private ImageView copertina;

    private it.unisa.musicplayer.servizi.Lettore lettore;
    private javafx.animation.Timeline timer;
    
    @FXML
    public void initialize() {
        
        // 1. COLLEGAMENTO DELLA TABLEVIEW AL CATALOGO SINGLETON REALE
        if (songTableView != null) {
            songTableView.setItems(Catalogo.getInstance().getTracce());
            songTableView.setPlaceholder(new Label("Nessuna traccia presente"));
        }

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

   
        

        // 3.  LETTORE US-09
        lettore = new Lettore();
        lettore.setModalita(new Sequenziale());

// Binding label nome traccia
if (currentTrackLabel != null) {
    currentTrackLabel.textProperty().bind(
        javafx.beans.binding.Bindings.createStringBinding(
            () -> {
                Traccia t = lettore.getTracciaCorrente();
                return t != null ? t.getTitolo() : "Seleziona un brano";
            },
            lettore.tracciaCorrenteProperty()
        )
    );
}

// Binding label autore
if (labelAutoreTraccia != null) {
    labelAutoreTraccia.textProperty().bind(
        javafx.beans.binding.Bindings.createStringBinding(
            () -> {
                Traccia t = lettore.getTracciaCorrente();
                return t != null ? t.getAutore() : "";
            },
            lettore.tracciaCorrenteProperty()
        )
    );
}

// Binding barra progresso
if (barraProgresso != null) {
    barraProgresso.progressProperty().bind(
        javafx.beans.binding.Bindings.createDoubleBinding(
            () -> {
                Traccia t = lettore.getTracciaCorrente();
                if (t == null) return 0.0;
                String[] parti = t.getDurata().split(":");
                int totale = Integer.parseInt(parti[0]) * 60 + Integer.parseInt(parti[1]);
                return totale > 0 ? (double) lettore.getTempoTrascorso() / totale : 0.0;
            },
            lettore.tempoTrascorsoProperty(),
            lettore.tracciaCorrenteProperty()
        )
    );
}

// Binding tempo trascorso
if (labelTempoTrascorso != null) {
    labelTempoTrascorso.textProperty().bind(
        javafx.beans.binding.Bindings.createStringBinding(
            () -> {
                int sec = lettore.getTempoTrascorso();
                return String.format("%d:%02d", sec / 60, sec % 60);
            },
            lettore.tempoTrascorsoProperty()
        )
    );
}

// Binding durata totale
if (labelDurataTotale != null) {
    labelDurataTotale.textProperty().bind(
        javafx.beans.binding.Bindings.createStringBinding(
            () -> {
                Traccia t = lettore.getTracciaCorrente();
                return t != null ? t.getDurata() : "0:00";
            },
            lettore.tracciaCorrenteProperty()
        )
    );
}

// Bottone Play/Pausa toggle
if (btnPlay != null) {
    btnPlay.setOnAction(e -> {
        if (lettore.getStato() == StatoLettore.PLAYING) {
            lettore.pausa();
            btnPlay.setText("▶");
            timer.pause();
        } else {
            if (lettore.getTracciaCorrente() == null) return;
            lettore.play();
            btnPlay.setText("||");
            timer.play();
        }
    });
}

// Bottone Skip
if (btnSkip != null) {
    btnSkip.setOnAction(e -> {
        if (lettore.getTracciaCorrente() == null) return;
        lettore.skip();
        if (lettore.getTracciaCorrente() == null) {
            // Riparte dall'inizio
            List<Traccia> coda = new ArrayList<>(Catalogo.getInstance().getTracce());
            lettore.aggiornaCodeTracce(coda);
            lettore.play();
        }
        timer.play();
        btnPlay.setText("||");
    });
}

// Bottone Precedente
if (btnPrecedente != null) {
    btnPrecedente.setOnAction(e -> {
        if (lettore.getTracciaCorrente() == null) return;
        List<Traccia> tracce = new ArrayList<>(Catalogo.getInstance().getTracce());
        int indice = tracce.indexOf(lettore.getTracciaCorrente());
        if (indice > 0) {
            List<Traccia> nuovaCoda = tracce.subList(indice - 1, tracce.size());
            lettore.aggiornaCodeTracce(new ArrayList<>(nuovaCoda));
            lettore.play();
        }
        timer.play();
        btnPlay.setText("||");
    });
}

// Timer
timer = new javafx.animation.Timeline(
    new javafx.animation.KeyFrame(
        javafx.util.Duration.seconds(1),
        e -> {
            Traccia t = lettore.getTracciaCorrente();
            if (t != null) {
                String[] parti = t.getDurata().split(":");
                int durataTotale = Integer.parseInt(parti[0]) * 60 + Integer.parseInt(parti[1]);
                if (lettore.getTempoTrascorso() >= durataTotale) {
                    lettore.skip();
                    if (lettore.getTracciaCorrente() == null) {
                        timer.pause();
                        btnPlay.setText("▶");
                    }
                } else {
                    lettore.avanzaTempo(1);
                }
            }
        }
    )
);
timer.setCycleCount(javafx.animation.Animation.INDEFINITE);

// Evidenzia la traccia corrente nella tabella
lettore.tracciaCorrenteProperty().addListener((obs, old, nuova) -> {
    if (nuova != null && songTableView != null) {
        songTableView.getSelectionModel().select(nuova);
        songTableView.scrollTo(nuova);
    }
});


// Mostra copertina solo quando c'è una traccia in riproduzione
if (copertina != null) {
    lettore.tracciaCorrenteProperty().addListener((obs, old, nuova) -> {
        copertina.setVisible(nuova != null);
    });
}
// Doppio click sulla tabella
songTableView.setRowFactory(tv -> {
    TableRow<Traccia> row = new TableRow<>();
    row.setOnMouseClicked(event -> {
        if (event.getClickCount() == 2 && !row.isEmpty()) {
            Traccia cliccata = row.getItem();
            if (cliccata != null) {
                List<Traccia> tutteLeTracce = new ArrayList<>(Catalogo.getInstance().getTracce());
                lettore.aggiornaCodeTracce(tutteLeTracce);
                lettore.tracciaCorrenteProperty().set(cliccata);
                lettore.play();
                timer.play();
                btnPlay.setText("||");
                cliccata.incrementaRiproduzioni();
            }
        }
    });
    return row;
});

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

        // 5. COMPORTAMENTO CLIC SIDEBAR PLAYLIST (BINDING CON CATALOGOPLAYLIST)
        if (sidebarListView != null) {
            sidebarListView.setItems(CatalogoPlaylist.getInstance().getPlaylists());
            sidebarListView.setPlaceholder(new Label("Nessuna playlist presente"));
            sidebarListView.getSelectionModel().selectedItemProperty().addListener((o, old, newVal) -> {
                if (newVal != null) {
                    mainTabPane.getSelectionModel().select(0);
                    btnNavHome.setStyle("-fx-background-color: transparent; -fx-text-fill: #FFFFFF; -fx-font-weight: bold;");
                    btnNavStats.setStyle("-fx-background-color: transparent; -fx-text-fill: #B3B3B3;");
                    if (catalogTitleLabel != null) catalogTitleLabel.setText("Playlist: " + newVal.getNome());
                    if (songTableView != null) songTableView.setItems(newVal.getTracce());
                }
            });
        }

        // 5B. PULSANTE CREAZIONE NUOVA PLAYLIST
        if (createNewPlaylistButton != null) {
            createNewPlaylistButton.setOnAction(e -> {
                try {
                    javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/new_playlist_dialog.fxml"));
                    DialogPane dialogPane = loader.load();
                    NewPlaylistController dialogController = loader.getController();

                    Dialog<ButtonType> dialog = new Dialog<>();
                    dialog.setDialogPane(dialogPane);
                    dialog.setTitle("Nuova Playlist");
                    dialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

                    java.util.Optional<ButtonType> result = dialog.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        try {
                            String nome = dialogController.getNomePlaylist();
                            Playlist nuova = new Playlist(nome);
                            CatalogoPlaylist.getInstance().aggiungiPlaylist(nuova);
                        } catch (IllegalArgumentException ex) {
                            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
                            alert.showAndWait();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
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
                    javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/song_dialog.fxml"));
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
                    javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/song_dialog.fxml"));
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
