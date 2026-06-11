package it.unisa.musicplayer.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import it.unisa.musicplayer.modello.Catalogo;
import it.unisa.musicplayer.modello.CatalogoPlaylist;
import it.unisa.musicplayer.modello.Playlist;
import it.unisa.musicplayer.modello.Tag;
import it.unisa.musicplayer.modello.Traccia;
import it.unisa.musicplayer.servizi.AggiungiTracciaCatalogo;
import it.unisa.musicplayer.servizi.AggiungiTracciaPlaylist;
import it.unisa.musicplayer.servizi.GeneratorePlaylistAutomatica;
import it.unisa.musicplayer.servizi.GestoreOperazioni;
import it.unisa.musicplayer.servizi.Lettore;
import it.unisa.musicplayer.servizi.Loop;
import it.unisa.musicplayer.servizi.LoopSingola;
import it.unisa.musicplayer.servizi.ModalitaRiproduzione;
import it.unisa.musicplayer.servizi.Operazione;
import it.unisa.musicplayer.servizi.RimuoviTracciaCatalogo;
import it.unisa.musicplayer.servizi.RimuoviTracciaPlaylist;
import it.unisa.musicplayer.servizi.Sequenziale;
import it.unisa.musicplayer.servizi.SequenzialeSingola;
import it.unisa.musicplayer.servizi.Shuffle;
import it.unisa.musicplayer.servizi.ShuffleSingola;
import it.unisa.musicplayer.servizi.StatoLettore;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
public class PrimaryController {

    // Navigazione e Struttura della GUI
    @FXML
    private TabPane mainTabPane;
    @FXML
    private Button btnNavHome;
    @FXML
    private Button btnNavStats;
    @FXML
    private Button btnOpenMockDialog;
    @FXML
    private Button mainPlaylistPlayButton;

    // Ora è perfettamente mappato sull'id dell'FXML modificato
    @FXML
    private Button btnEditTrack;
    @FXML
    private Button btnRemoveTrack;
    @FXML
    private Button btnAddToPlaylist;
    @FXML
    private Button undoButton;
    @FXML
    private Button createNewPlaylistButton;
    @FXML private Button btnGeneraPlaylistAuto;
    @FXML private Button trackShuffleButton;
    @FXML private Button trackLoopButton;

    // Elementi di testo, etichette e liste
    @FXML
    private ListView<Playlist> sidebarListView;
    @FXML
    private ListView<String> topTracksListView;
    @FXML
    private ListView<String> topPlaylistsListView;
    @FXML
    private Label currentTrackLabel;
    @FXML
    private Label catalogTitleLabel;
    // Controlli modalità della playlist - US-10
    @FXML
    private HBox playlistModeControls;
    @FXML
    private ToggleButton btnPlaylistSequenziale;
    @FXML
    private ToggleButton btnPlaylistLoop;
    @FXML
    private ToggleButton btnPlaylistShuffle;


    // TableView mappata sulla classe reale "Traccia"
    @FXML
    private TableView<Traccia> songTableView;
    @FXML
    private TableColumn<Traccia, String> titleColumn;
    @FXML
    private TableColumn<Traccia, String> autoreColumn;
    @FXML
    private TableColumn<Traccia, String> genreColumn;
    @FXML
    private TableColumn<Traccia, Integer> yearColumn;
    @FXML
    private TableColumn<Traccia, String> durationColumn;


    // Player Controls - US-09
    @FXML
    private Button btnPlay;
    @FXML
    private Button btnPausa;
    @FXML
    private Button btnSkip;
    @FXML
    private Button btnPrecedente;
    @FXML
    private ProgressBar barraProgresso;
    @FXML
    private Label labelAutoreTraccia;
    @FXML
    private Label labelTempoTrascorso;
    @FXML
    private Label labelDurataTotale;
    @FXML
    private ImageView copertina;

    private it.unisa.musicplayer.servizi.Lettore lettore;
    private javafx.animation.Timeline timer;
    private Playlist playlistCorrenteUi;
    private Playlist playlistInRiproduzione;
    private final GestoreOperazioni gestoreOperazioni = new GestoreOperazioni();
    private final ToggleGroup gruppoModalitaPlaylist = new ToggleGroup();

    private ModalitaRiproduzione modalitaPlaylistSelezionata =
            new Sequenziale();

    @FXML
    public void initialize() {

        // 1. COLLEGAMENTO DELLA TABLEVIEW AL CATALOGO SINGLETON REALE
        if (songTableView != null) {
            songTableView.setItems(Catalogo.getInstance().getTracce());
            songTableView.setPlaceholder(new Label("Nessuna traccia presente"));
            songTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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
                                if (nomeTag.contains("EXPLICIT") || nomeTag.contains("ESPLICITO") || nomeTag.equals("E"))
                                    esplicito = true;
                                if (nomeTag.contains("FAVORITE") || nomeTag.contains("FAVOURITE") || nomeTag.contains("PREFERITO") || nomeTag.equals("STAR"))
                                    preferito = true;
                                if (nomeTag.contains("NEW") || nomeTag.contains("NUOVA") || nomeTag.contains("USCITA"))
                                    nuovaUscita = true;
                            }
                        }
                    } catch (Exception e) {
                    }

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
        lettore.setModalita(new SequenzialeSingola());

        configuraControlliModalitaPlaylist();
        mostraControlliModalitaPlaylist(true);
        configuraPulsanteUndo();

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
                    if (lettore.getTracciaCorrente() == null) {
                        Traccia tracciaDaAvviare = songTableView.getSelectionModel().getSelectedItem();
                        if (tracciaDaAvviare == null && !songTableView.getItems().isEmpty()) {
                            tracciaDaAvviare = songTableView.getItems().get(0);
                        }
                        if (tracciaDaAvviare == null) return;
                        avviaRiproduzioneDaTraccia(tracciaDaAvviare);
                    } else {
                        lettore.play();
                        btnPlay.setText("||");
                        timer.play();
                    }
                }
            });
        }

        if (mainPlaylistPlayButton != null) {
            mainPlaylistPlayButton.setOnAction(e -> {
                Traccia tracciaDaAvviare = songTableView.getSelectionModel().getSelectedItem();
                if (tracciaDaAvviare == null && !songTableView.getItems().isEmpty()) {
                    tracciaDaAvviare = songTableView.getItems().get(0);
                }
                if (tracciaDaAvviare != null) {
                    avviaRiproduzioneDaTraccia(tracciaDaAvviare);
                }
            });
        }

// Bottone Skip
      /*  if (btnSkip != null) {
            btnSkip.setOnAction(e -> {
                if (lettore.getTracciaCorrente() == null) {
                    return;
                }

                lettore.skip();

                if (lettore.getTracciaCorrente() == null) {
                    timer.pause();

                    if (btnPlay != null) {
                        btnPlay.setText("▶");
                    }
                } else {
                    timer.play();

                    if (btnPlay != null) {
                        btnPlay.setText("||");
                    }
                }
            });
        }*/

        if (btnSkip != null) {
            btnSkip.setOnAction(e -> {
                if (lettore.getTracciaCorrente() == null) return;
        
                lettore.skip();
        
                if (lettore.getTracciaCorrente() == null) {
                    List<Traccia> coda = creaCodaRiproduzioneCorrente();
                    lettore.aggiornaCodeTracce(coda);
                    lettore.setModalita(new SequenzialeSingola());
                    lettore.play();
                    timer.play();
                    if (btnPlay != null) btnPlay.setText("||");
                } else {
                    timer.play();
                    if (btnPlay != null) btnPlay.setText("||");
                }
            });
        }

// Bottone Precedente
        if (btnPrecedente != null) {
            btnPrecedente.setOnAction(e -> {
                if (lettore.getTracciaCorrente() == null) return;
                List<Traccia> tracce = creaCodaRiproduzioneCorrente();
                int indice = tracce.indexOf(lettore.getTracciaCorrente());
                if (indice > 0) {
                    List<Traccia> nuovaCoda = tracce.subList(indice - 1, tracce.size());
                    lettore.aggiornaCodeTracce(new ArrayList<>(nuovaCoda));
                    lettore.setModalita(new SequenzialeSingola());
                    lettore.play();
                }
                timer.play();
                btnPlay.setText("||");
            });
        }

// Bottone Shuffle
if (trackShuffleButton != null) {
    trackShuffleButton.setOnAction(e -> {
        if ("SHUFFLE_SINGOLA".equals(lettore.getModalita().getNome())) {
            lettore.setModalita(new Sequenziale());
            trackShuffleButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #B3B3B3; -fx-font-size: 15px; -fx-cursor: hand;");
        } else {
            lettore.setModalita(new ShuffleSingola());
            trackShuffleButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #1DB954; -fx-font-size: 15px; -fx-cursor: hand;");
            trackLoopButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #B3B3B3; -fx-font-size: 15px; -fx-cursor: hand;");
        }
    });
}

// Bottone Loop
if (trackLoopButton != null) {
    trackLoopButton.setOnAction(e -> {
        if ("LOOP_SINGOLA".equals(lettore.getModalita().getNome())) {
            lettore.setModalita(new Sequenziale());
            trackLoopButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #B3B3B3; -fx-font-size: 15px; -fx-cursor: hand;");
        } else {
            lettore.setModalita(new LoopSingola());
            trackLoopButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #1DB954; -fx-font-size: 15px; -fx-cursor: hand;");
            trackShuffleButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #B3B3B3; -fx-font-size: 15px; -fx-cursor: hand;");
        }
    });
}

// Timer
       /* timer = new javafx.animation.Timeline(
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
                                          // Se modalità sequenziale → si ferma
                                     if ("SEQUENZIALE".equals(lettore.getModalita().getNome())) {
                                     timer.pause();
                                     btnPlay.setText("▶");
                                     lettore.avanzaTempo(1); 
                                    } else {
                                        // Usa la playlist corrente se disponibile, altrimenti il catalogo
                                        List<Traccia> coda = playlistCorrenteUi != null 
                                            ? new ArrayList<>(playlistCorrenteUi.getTracce())
                                            : new ArrayList<>(Catalogo.getInstance().getTracce());
                                        lettore.aggiornaCodeTracce(coda);
                                        lettore.play();
                                        timer.play();
                                        btnPlay.setText("||");
                                    }                                   
                                } else {
                                    lettore.avanzaTempo(1); 
                                }
                            }
                            }
                        }
                )
        );
        timer.setCycleCount(javafx.animation.Animation.INDEFINITE);*/

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
                                // Se modalità sequenziale → si ferma
                                if ("SEQUENZIALE".equals(lettore.getModalita().getNome())) {
                                    timer.pause();
                                    btnPlay.setText("▶");
                                } else {
                                    // Altrimenti riparte dall'inizio
                                    List<Traccia> coda = creaCodaRiproduzioneCorrente();
                                    lettore.aggiornaCodeTracce(coda);
                                    lettore.play();
                                    timer.play();
                                    btnPlay.setText("||");
                                }
                            } else {
                                btnPlay.setText("||");
                            }
                        } else {
                            lettore.avanzaTempo(1);
                        }
                    }
                }
            )
        );
        timer.setCycleCount(javafx.animation.Animation.INDEFINITE);

        // Aggiorna la coda automaticamente quando il catalogo cambia
Catalogo.getInstance().getTracce().addListener(
    (javafx.collections.ListChangeListener<Traccia>) change -> {
        if (lettore.getStato() == StatoLettore.PLAYING || 
            lettore.getStato() == StatoLettore.PAUSED) {
            Traccia corrente = lettore.getTracciaCorrente();
            java.util.List<Traccia> sorgenteCoda = creaCodaRiproduzioneCorrente();
            // Aggiorna la coda senza resettare la traccia corrente
            lettore.getCoda().clear();
            lettore.getCoda().addAll(sorgenteCoda);
            // Mantieni la traccia corrente
            lettore.tracciaCorrenteProperty().set(corrente);
        }
    }
);

// Evidenzia la traccia corrente nella tabella
        lettore.tracciaCorrenteProperty().addListener((obs, old, nuova) -> {
            if (nuova != null && songTableView != null) {
                songTableView.scrollTo(nuova);
                songTableView.getSelectionModel().clearSelection();
            } else {
                if (songTableView != null) songTableView.getSelectionModel().clearSelection();
            }
        });


// Mostra copertina solo quando c'è una traccia in riproduzione
        if (copertina != null) {
            lettore.tracciaCorrenteProperty().addListener((obs, old, nuova) -> {
                copertina.setVisible(nuova != null);
            });
        }

        songTableView.setRowFactory(tv -> new TableRow<Traccia>() {
            {
                lettore.tracciaCorrenteProperty().addListener((obs, old, nuova) -> 
                    javafx.application.Platform.runLater(() -> updateStyle()));
                lettore.statoProperty().addListener((obs, old, nuova) -> 
                    javafx.application.Platform.runLater(() -> updateStyle()));
                
                setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && !isEmpty()) {
                        Traccia cliccata = getItem();
                        if (cliccata != null) {
                            avviaRiproduzioneDaTraccia(cliccata);
                            javafx.application.Platform.runLater(() -> {
                                songTableView.getSelectionModel().clearSelection();
                                updateStyle();
                            });
                        }
                    }
                });
            }
            
            private void updateStyle() {
                Traccia traccia = getItem();
                Traccia corrente = lettore.getTracciaCorrente();
                if (traccia != null && corrente != null && 
                    traccia.getId().equals(corrente.getId()) &&
                    (lettore.getStato() == StatoLettore.PLAYING || 
                     lettore.getStato() == StatoLettore.PAUSED)) {
                    setStyle("-fx-background-color: #1DB954; -fx-opacity: 0.3;");
                } else {
                    setStyle("");
                }
            }
            
            @Override
            protected void updateItem(Traccia item, boolean empty) {
                super.updateItem(item, empty);
                updateStyle();
            }
        });

       

        // 4. LOGICA DI SPOSTAMENTO SCHERMATE (ROUTING)
if (btnNavHome != null && mainTabPane != null) {
    btnNavHome.setOnAction(e -> {
        mainTabPane.getSelectionModel().select(0);
        aggiornaNavigazioneAttiva(btnNavHome);
        if (catalogTitleLabel != null) catalogTitleLabel.setText("Catalogo Globale");
        if (songTableView != null) songTableView.setItems(Catalogo.getInstance().getTracce());
        playlistCorrenteUi = null;
        mostraControlliModalitaPlaylist(true);
        if (sidebarListView != null) sidebarListView.getSelectionModel().clearSelection();
        // Mostra bottone modifica quando sei nel catalogo
        if (btnEditTrack != null) {
            btnEditTrack.setVisible(true);
            btnEditTrack.setManaged(true);
        }
    });
}

if (btnNavStats != null && mainTabPane != null) {
    btnNavStats.setOnAction(e -> {
        mainTabPane.getSelectionModel().select(1);
        aggiornaNavigazioneAttiva(btnNavStats);
        if (sidebarListView != null) sidebarListView.getSelectionModel().clearSelection();
    });
}

// 5. COMPORTAMENTO CLIC SIDEBAR PLAYLIST
if (sidebarListView != null) {
    sidebarListView.setItems(CatalogoPlaylist.getInstance().getPlaylists());
    sidebarListView.setPlaceholder(new Label("Nessuna playlist presente"));
    sidebarListView.getSelectionModel().selectedItemProperty().addListener((o, old, newVal) -> {
        if (newVal != null) {
            mainTabPane.getSelectionModel().select(0);
            aggiornaNavigazioneAttiva(btnNavHome);
            if (catalogTitleLabel != null) catalogTitleLabel.setText("Playlist: " + newVal.getNome());
            if (songTableView != null) songTableView.setItems(newVal.getTracce());
            playlistCorrenteUi = newVal;
            lettore.setModalita(modalitaPlaylistSelezionata);
            // Nascondi bottone modifica quando sei in una playlist
            if (btnEditTrack != null) {
                btnEditTrack.setVisible(false);
                btnEditTrack.setManaged(false);
            }
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

                    java.util.Optional<ButtonType> result = mostraDialog(dialog);
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        try {
                            String nome = dialogController.getNomePlaylist();
                            Playlist nuova = new Playlist(nome);
                            CatalogoPlaylist.getInstance().aggiungiPlaylist(nuova);
                        } catch (IllegalArgumentException ex) {
                            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
                            mostraAlert(alert);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }

        // 5C. GENERAZIONE AUTOMATICA PLAYLIST (US)
        if (btnGeneraPlaylistAuto != null) {
            btnGeneraPlaylistAuto.setOnAction(e -> onGeneraPlaylistAutomatica());
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

                    java.util.Optional<ButtonType> result = mostraDialog(dialog);

                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        Traccia nuovaTraccia = dialogController.getSongFromForm();
                        if (nuovaTraccia != null) {
                            eseguiOperazione(new AggiungiTracciaCatalogo(Catalogo.getInstance(), nuovaTraccia));
                            // Se siamo in una playlist, aggiungi anche lì
                            if (playlistCorrenteUi != null && 
                                catalogTitleLabel.getText().startsWith("Playlist:")) {
                                eseguiOperazione(new AggiungiTracciaPlaylist(playlistCorrenteUi, nuovaTraccia));
                                if (playlistCorrenteUi.equals(playlistInRiproduzione)) {
                                    lettore.aggiungiTracciaInCoda(nuovaTraccia);
                                }
                                CatalogoPlaylist.getInstance().eseguiSalvataggioAutomatico();
                            }
                        }
                        songTableView.getSelectionModel().clearSelection();
                    }
                   /* if (result.isPresent() && result.get() == ButtonType.OK) {
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
                        songTableView.getSelectionModel().clearSelection();
                    }*/
                } catch (IllegalArgumentException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Errore di inserimento: " + ex.getMessage());
                    mostraAlert(alert);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }

        // 8. INTERFACCIA REALE DI MODIFICA BRANO (US-03)
        if (btnEditTrack != null) {
            btnEditTrack.setOnAction(e -> {
                // Disabilita modifica se siamo in una playlist
                if (catalogTitleLabel != null && catalogTitleLabel.getText().startsWith("Playlist:")) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, 
                        "Non è possibile modificare una traccia dall'interno di una playlist. Vai al catalogo generale.");
                    alert.setTitle("Operazione non consentita");
                    alert.setHeaderText(null);
                    mostraAlert(alert);
                    return;
                }
                // Recuperiamo la canzone attualmente selezionata con un singolo click nella tabella
                Traccia tracciaSelezionata = songTableView.getSelectionModel().getSelectedItem();

                if (tracciaSelezionata == null) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Seleziona prima un brano dalla tabella con un singolo click!");
                    mostraAlert(alert);
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

                    java.util.Optional<ButtonType> result = mostraDialog(dialog);
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        // Estraiamo i testi aggiornati mantenendo l'ID originale immutato
                        Traccia tracciaModificata = dialogController.getSongFromForm();

                        if (tracciaModificata != null) {
                            // Eseguiamo l'aggiornamento nel modello Singleton
                            Catalogo.getInstance().modificaTraccia(tracciaSelezionata, tracciaModificata);
                            songTableView.getSelectionModel().clearSelection();
                            // L'uso della ObservableList aggiorna istantaneamente la tabella a schermo!
                        }
                    }
                    else {
                        songTableView.getSelectionModel().clearSelection(); // ← aggiungi
                    }
                } catch (IllegalArgumentException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Errore di validazione della modifica: " + ex.getMessage());
                    mostraAlert(alert);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }

        // 8B. INTERFACCIA DI AGGIUNTA BRANI A PLAYLIST (US-07)
        if (btnAddToPlaylist != null) {
            btnAddToPlaylist.setOnAction(e -> onAggiungiTracciaPlaylist());
        }

    

        // 9. INTERFACCIA DI RIMOZIONE BRANO DAL CATALOGO (US-05)
if (btnRemoveTrack != null) {
    btnRemoveTrack.setOnAction(e -> {
        Traccia tracciaSelezionata = songTableView.getSelectionModel().getSelectedItem();

        if (tracciaSelezionata == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Seleziona prima una traccia dalla tabella.");
            alert.setTitle("Nessuna traccia selezionata");
            alert.setHeaderText(null);
            mostraAlert(alert);
            return;
        }

        // Controlla se siamo in una playlist o nel catalogo
        Playlist playlistSelezionata = sidebarListView.getSelectionModel().getSelectedItem();
        boolean siamoInPlaylist = playlistSelezionata != null && 
                                  catalogTitleLabel.getText().startsWith("Playlist:");

        if (siamoInPlaylist) {
            // Rimuovi solo dalla playlist
            Alert conferma = new Alert(Alert.AlertType.CONFIRMATION);
            conferma.setTitle("Conferma rimozione");
            conferma.setHeaderText("Rimuovere dalla playlist?");
            conferma.setContentText("La traccia \"" + tracciaSelezionata.getTitolo() + 
                "\" verrà rimossa dalla playlist \"" + playlistSelezionata.getNome() + 
                "\" ma rimarrà nel catalogo generale.");

            java.util.Optional<ButtonType> scelta = mostraDialog(conferma);
            if (scelta.isPresent() && scelta.get() == ButtonType.OK) {
                if (playlistSelezionata.equals(playlistInRiproduzione)) {
                    lettore.rimuoviTracciaDallaCoda(tracciaSelezionata);
                }
                eseguiOperazione(new RimuoviTracciaPlaylist(playlistSelezionata, tracciaSelezionata));
                CatalogoPlaylist.getInstance().eseguiSalvataggioAutomatico();
                songTableView.refresh();
            } else {
                songTableView.getSelectionModel().clearSelection();
            }

        } else {
            // Rimuovi dal catalogo generale
            Alert conferma = new Alert(Alert.AlertType.CONFIRMATION);
            conferma.setTitle("Conferma eliminazione");
            conferma.setHeaderText("Eliminare definitivamente la traccia?");
            conferma.setContentText("La traccia \"" + tracciaSelezionata.getTitolo() + 
                "\" verrà rimossa dal catalogo e da tutte le playlist in cui è presente.");

            java.util.Optional<ButtonType> scelta = mostraDialog(conferma);
            if (scelta.isPresent() && scelta.get() == ButtonType.OK) {
                try {
                    lettore.rimuoviTracciaDallaCoda(tracciaSelezionata);
                    eseguiOperazione(new RimuoviTracciaCatalogo(
                            Catalogo.getInstance(),
                            CatalogoPlaylist.getInstance(),
                            tracciaSelezionata
                    ));
                    songTableView.getSelectionModel().clearSelection();

                    if (currentTrackLabel != null &&
                            currentTrackLabel.getText().equals(tracciaSelezionata.getTitolo())) {
                        currentTrackLabel.setText("Seleziona un brano");
                    }
                } catch (IllegalArgumentException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
                    alert.setTitle("Errore eliminazione");
                    alert.setHeaderText(null);
                    mostraAlert(alert);
                }
            } else {
                songTableView.getSelectionModel().clearSelection();
            }
        }
    });
}
    }

    private void configuraControlliModalitaPlaylist() {
        if (btnPlaylistSequenziale == null
                || btnPlaylistLoop == null
                || btnPlaylistShuffle == null) {
            return;
        }

        btnPlaylistSequenziale.setToggleGroup(
                gruppoModalitaPlaylist
        );

        btnPlaylistLoop.setToggleGroup(
                gruppoModalitaPlaylist
        );

        btnPlaylistShuffle.setToggleGroup(
                gruppoModalitaPlaylist
        );

        gruppoModalitaPlaylist
                .selectedToggleProperty()
                .addListener((observable, precedente, nuova) -> {

                    /*
                     * Non permettiamo di lasciare tutte
                     * le modalità deselezionate.
                     */
                    if (nuova == null) {
                        if (precedente != null) {
                            precedente.setSelected(true);
                        }
                        return;
                    }

                    onCambiaModalita();
                });

        // Modalità iniziale
        btnPlaylistSequenziale.setSelected(true);
        onCambiaModalita();
    }


    private void onCambiaModalita() {
        if (btnPlaylistLoop != null && btnPlaylistLoop.isSelected()) {
            modalitaPlaylistSelezionata = new Loop();
        } else if (btnPlaylistShuffle != null && btnPlaylistShuffle.isSelected()) {
            modalitaPlaylistSelezionata = new Shuffle();
        } else {
            modalitaPlaylistSelezionata = new Sequenziale();
            if (btnPlaylistSequenziale != null && !btnPlaylistSequenziale.isSelected()) {
                btnPlaylistSequenziale.setSelected(true);
            }
        }

    
        if (lettore != null) {
            lettore.setModalita(modalitaPlaylistSelezionata);
    
           
    if (playlistCorrenteUi != null && !playlistCorrenteUi.getTracce().isEmpty()) {
        if ("SEQUENZIALE".equals(modalitaPlaylistSelezionata.getNome()) ||
            "LOOP".equals(modalitaPlaylistSelezionata.getNome())) {
            // Non fermare la riproduzione corrente
            // Solo prepara la coda per il prossimo play
            if (btnPlay != null) btnPlay.setText(
                lettore.getStato() == StatoLettore.PLAYING ? "||" : "▶"
            );
        }
    }

    // Resetta i bottoni singoli quando si cambia modalità playlist
if (trackShuffleButton != null) {
    trackShuffleButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #B3B3B3; -fx-font-size: 15px; -fx-cursor: hand;");
}
if (trackLoopButton != null) {
    trackLoopButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #B3B3B3; -fx-font-size: 15px; -fx-cursor: hand;");
}
}
    }
    private void mostraControlliModalitaPlaylist(boolean visibili) {
        if (playlistModeControls == null) {
            return;
        }

        playlistModeControls.setVisible(visibili);
        playlistModeControls.setManaged(visibili);
    }

    private void onAggiungiTracciaPlaylist() {
        java.util.List<Traccia> tracceSelezionate =
                new java.util.ArrayList<>(songTableView.getSelectionModel().getSelectedItems());

        if (tracceSelezionate.isEmpty()) {
            Alert alert = new Alert(
                    Alert.AlertType.WARNING,
                    "Seleziona almeno una traccia dal catalogo prima di aggiungerla a una playlist."
            );
            alert.setTitle("Nessuna traccia selezionata");
            alert.setHeaderText(null);
            mostraAlert(alert);
            return;
        }

        if (CatalogoPlaylist.getInstance().getPlaylists().isEmpty()) {
            Alert alert = new Alert(
                    Alert.AlertType.WARNING,
                    "Non esiste ancora nessuna playlist. Crea prima una playlist."
            );
            alert.setTitle("Nessuna playlist disponibile");
            alert.setHeaderText(null);
            mostraAlert(alert);
            return;
        }

        ChoiceDialog<Playlist> dialog = new ChoiceDialog<>(
                CatalogoPlaylist.getInstance().getPlaylists().get(0),
                CatalogoPlaylist.getInstance().getPlaylists()
        );

        dialog.setTitle("Aggiungi a playlist");
        dialog.setHeaderText("Scegli la playlist di destinazione");
        dialog.setContentText("Playlist:");

        java.util.Optional<Playlist> risultato = mostraDialog(dialog);

        if (risultato.isEmpty()) {
            return;
        }

        Playlist playlistScelta = risultato.get();
        int numeroTraccePrima = playlistScelta.getNumeroTracce();

        for (Traccia traccia : tracceSelezionate) {
            boolean tracciaGiaPresente = playlistScelta.getTracce().contains(traccia);
            if (!tracciaGiaPresente) {
                eseguiOperazione(new AggiungiTracciaPlaylist(playlistScelta, traccia));
            }
            if (playlistScelta.equals(playlistInRiproduzione)) {
                lettore.aggiungiTracciaInCoda(traccia);
            }
        }

        CatalogoPlaylist.getInstance().eseguiSalvataggioAutomatico();

        if (sidebarListView != null) {
            sidebarListView.refresh();
        }

        if (songTableView != null) {
            Playlist playlistSelezionata = sidebarListView != null
                    ? sidebarListView.getSelectionModel().getSelectedItem()
                    : null;

            if (playlistScelta.equals(playlistSelezionata)) {
                songTableView.setItems(playlistScelta.getTracce());
                if (catalogTitleLabel != null) {
                    catalogTitleLabel.setText("Playlist: " + playlistScelta.getNome());
                }
            }

            songTableView.refresh();
            songTableView.getSelectionModel().clearSelection();
        }

        int numeroTracceAggiunte = playlistScelta.getNumeroTracce() - numeroTraccePrima;

        Alert conferma = new Alert(Alert.AlertType.INFORMATION);
        conferma.setTitle("Aggiunta completata");
        conferma.setHeaderText(null);

        if (numeroTracceAggiunte == 0) {
            conferma.setContentText(
                    "Le tracce selezionate erano già presenti nella playlist \"" +
                            playlistScelta.getNome() + "\"."
            );
        } else if (numeroTracceAggiunte == 1) {
            conferma.setContentText(
                    "1 traccia aggiunta alla playlist \"" + playlistScelta.getNome() + "\"."
            );
        } else {
            conferma.setContentText(
                    numeroTracceAggiunte + " tracce aggiunte alla playlist \"" +
                            playlistScelta.getNome() + "\"."
            );
        }

        mostraAlert(conferma);
        songTableView.getSelectionModel().clearSelection();
    }

    private void onGeneraPlaylistAutomatica() {
        ChoiceDialog<String> criterioDialog = new ChoiceDialog<>("Per Genere", Arrays.asList("Per Genere", "Per Anno"));
        criterioDialog.setTitle("Genera Playlist Automatica");
        criterioDialog.setHeaderText("Scegli il criterio di generazione");
        criterioDialog.setContentText("Filtra per:");

        Optional<String> criterio = mostraDialog(criterioDialog);
        if (!criterio.isPresent()) return;

        Catalogo catalogo = Catalogo.getInstance();
        GeneratorePlaylistAutomatica generatore = new GeneratorePlaylistAutomatica();

        if ("Per Genere".equals(criterio.get())) {
            List<String> generi = catalogo.getTracce().stream()
                    .map(Traccia::getGenere).distinct().sorted().collect(Collectors.toList());
            if (generi.isEmpty()) {
                mostraAlert(new Alert(Alert.AlertType.WARNING, "Nessuna traccia nel catalogo."));
                return;
            }
            ChoiceDialog<String> genereDialog = new ChoiceDialog<>(generi.get(0), generi);
            genereDialog.setTitle("Genera per Genere");
            genereDialog.setHeaderText("Seleziona il genere");
            genereDialog.setContentText("Genere:");
            Optional<String> genere = mostraDialog(genereDialog);
            if (!genere.isPresent()) return;
            aggiungiPlaylistGenerata(generatore.generaPerGenere(catalogo, genere.get()));

        } else {
            List<Integer> anni = catalogo.getTracce().stream()
                    .map(Traccia::getAnno).distinct().sorted().collect(Collectors.toList());
            if (anni.isEmpty()) {
                mostraAlert(new Alert(Alert.AlertType.WARNING, "Nessuna traccia nel catalogo."));
                return;
            }
            ChoiceDialog<Integer> annoDialog = new ChoiceDialog<>(anni.get(0), anni);
            annoDialog.setTitle("Genera per Anno");
            annoDialog.setHeaderText("Seleziona l'anno");
            annoDialog.setContentText("Anno:");
            Optional<Integer> anno = mostraDialog(annoDialog);
            if (!anno.isPresent()) return;
            aggiungiPlaylistGenerata(generatore.generaPerAnno(catalogo, anno.get()));
        }
    }

    private void aggiungiPlaylistGenerata(Playlist playlist) {
        try {
            CatalogoPlaylist.getInstance().aggiungiPlaylist(playlist);
            Alert info = new Alert(Alert.AlertType.INFORMATION,
                    "Playlist \"" + playlist.getNome() + "\" generata con " + playlist.getNumeroTracce() + " brani.");
            info.setHeaderText(null);
            mostraAlert(info);
        } catch (IllegalArgumentException ex) {
            Alert conferma = new Alert(Alert.AlertType.CONFIRMATION);
            conferma.setTitle("Playlist già esistente");
            conferma.setHeaderText("Esiste già \"" + playlist.getNome() + "\"");
            conferma.setContentText("Vuoi sovrascriverla con i dati aggiornati?");
            Optional<ButtonType> r = mostraDialog(conferma);
            if (r.isPresent() && r.get() == ButtonType.OK) {
                CatalogoPlaylist.getInstance().rimuoviPlaylist(playlist.getNome());
                CatalogoPlaylist.getInstance().aggiungiPlaylist(playlist);
            }
        }
    }

    private void avviaRiproduzioneDaTraccia(Traccia traccia) {
        if (traccia == null
                || songTableView == null
                || songTableView.getItems().isEmpty()) {
            return;
        }

        if (playlistCorrenteUi != null) {
            lettore.aggiornaCodeTracce(new ArrayList<>(playlistCorrenteUi.getTracce()));
            playlistInRiproduzione = playlistCorrenteUi;
        } else {
            lettore.aggiornaCodeTracce(new ArrayList<>(Catalogo.getInstance().getTracce()));
            playlistInRiproduzione = null;
        }
        lettore.setModalita(modalitaPlaylistSelezionata);

        lettore.tracciaCorrenteProperty().set(traccia);
        lettore.play();
        timer.play();

        if (btnPlay != null) {
            btnPlay.setText("||");
        }

        traccia.incrementaRiproduzioni();
    }

    private List<Traccia> creaCodaRiproduzioneCorrente() {
        if (playlistInRiproduzione != null) {
            return new ArrayList<>(playlistInRiproduzione.getTracce());
        }
        return new ArrayList<>(Catalogo.getInstance().getTracce());
    }

    private void configuraPulsanteUndo() {
        if (undoButton == null) {
            return;
        }

        undoButton.setOnAction(e -> {
            gestoreOperazioni.annullaUltimaOperazione();
            sincronizzaCodaDopoUndo();
            CatalogoPlaylist.getInstance().eseguiSalvataggioAutomatico();
            if (songTableView != null) {
                songTableView.refresh();
                songTableView.getSelectionModel().clearSelection();
            }
            if (sidebarListView != null) {
                sidebarListView.refresh();
            }
            aggiornaPulsanteUndo();
        });

        aggiornaPulsanteUndo();
    }

    private void eseguiOperazione(Operazione operazione) {
        gestoreOperazioni.eseguiOperazione(operazione);
        aggiornaPulsanteUndo();
    }

    private void aggiornaPulsanteUndo() {
        if (undoButton == null) {
            return;
        }

        boolean puoAnnullare = gestoreOperazioni.puoAnnullare();
        undoButton.setDisable(!puoAnnullare);
        undoButton.setText(puoAnnullare
                ? "↶ Annulla: " + gestoreOperazioni.getDescrizioneUltimaOperazione()
                : "↶ Annulla");
    }

    private void sincronizzaCodaDopoUndo() {
        if (lettore == null
                || lettore.getStato() == StatoLettore.STOPPED
                || lettore.getTracciaCorrente() == null) {
            return;
        }

        Traccia corrente = lettore.getTracciaCorrente();
        List<Traccia> codaAggiornata = creaCodaRiproduzioneCorrente();

        // Dopo un undo la coda deve riflettere la sorgente in riproduzione.
        lettore.getCoda().clear();
        lettore.getCoda().addAll(codaAggiornata);

        if (codaAggiornata.contains(corrente)) {
            lettore.tracciaCorrenteProperty().set(corrente);
        } else {
            lettore.aggiornaCodeTracce(codaAggiornata);
        }
    }

    private void aggiornaNavigazioneAttiva(Button bottoneAttivo) {
        aggiornaStatoNavButton(btnNavHome, bottoneAttivo == btnNavHome);
        aggiornaStatoNavButton(btnNavStats, bottoneAttivo == btnNavStats);
    }

    private void aggiornaStatoNavButton(Button bottone, boolean attivo) {
        if (bottone == null) {
            return;
        }

        bottone.setStyle("");
        bottone.getStyleClass().remove("nav-button-active");
        if (attivo) {
            bottone.getStyleClass().add("nav-button-active");
        }
    }

    private <T> Optional<T> mostraDialog(Dialog<T> dialog) {
        applicaTemaDialog(dialog);
        return dialog.showAndWait();
    }

    private void mostraAlert(Alert alert) {
        applicaTemaDialog(alert);
        alert.showAndWait();
    }

    private void applicaTemaDialog(Dialog<?> dialog) {
        DialogPane dialogPane = dialog.getDialogPane();
        String css = getClass().getResource("/css/app.css").toExternalForm();

        // Gli Alert JavaFX hanno una scena separata, quindi il tema va applicato al DialogPane.
        if (!dialogPane.getStylesheets().contains(css)) {
            dialogPane.getStylesheets().add(css);
        }
        if (!dialogPane.getStyleClass().contains("dialog-pane")) {
            dialogPane.getStyleClass().add("dialog-pane");
        }
    }
}
