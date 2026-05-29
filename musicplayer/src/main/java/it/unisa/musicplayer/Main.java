package it.unisa.musicplayer;

import it.unisa.musicplayer.modello.Catalogo;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Catalogo.getInstance().caricaDaFile();

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/primary.fxml"));
        Scene scene = new Scene(loader.load(), 900, 600);

        stage.setTitle("Music Playlist Manager");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
