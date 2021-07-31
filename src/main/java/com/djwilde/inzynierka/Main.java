package com.djwilde.inzynierka;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainWindow.fxml"));
        primaryStage = loader.load();
        primaryStage.setTitle("JPlotter");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
