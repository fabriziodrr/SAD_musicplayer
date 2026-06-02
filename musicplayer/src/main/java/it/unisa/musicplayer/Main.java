package it.unisa.musicplayer;

import it.unisa.musicplayer.modello.Catalogo;
import it.unisa.musicplayer.modello.CatalogoPlaylist;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Catalogo.getInstance().caricaDaFile();
        CatalogoPlaylist.getInstance().caricaDaFile();

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/primary.fxml"));
        Scene scene = new Scene(loader.load(), 1120, 700);
        scene.getStylesheets().add(Main.class.getResource("/css/app.css").toExternalForm());

        stage.setTitle("Music Playlist Manager");
        stage.setMinWidth(820);
        stage.setMinHeight(540);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
