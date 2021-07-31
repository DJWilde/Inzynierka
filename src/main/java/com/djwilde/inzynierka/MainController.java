package com.djwilde.inzynierka;

import com.djwilde.inzynierka.threads.LaunchGnuplotThread;
import com.djwilde.inzynierka.threads.LoadPictureThread;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class MainController {
    @FXML
    private BorderPane mainWindowPane;
    @FXML
    private MenuItem closeAppMenu;
    @FXML
    private MenuItem aboutMenu;
    @FXML
    private MenuItem newCommandMenuItem;
    @FXML
    private ImageView plotImageView;
    // Przyciski w toolbarze
    @FXML
    private Button toolbarNewPlotButton;
    @FXML
    private Button toolbarNewCommandButton;
    @FXML
    private Button toolbarNewScriptButton;

    public void initialize() {
        newCommandMenuItem.setOnAction(actionEvent -> showNewCommandWindow());
        toolbarNewCommandButton.setOnAction(actionEvent -> showNewCommandWindow());
        toolbarNewPlotButton.setOnAction(actionEvent -> openWindow("/NewPlotWindow.fxml"));
        toolbarNewScriptButton.setOnAction(actionEvent -> openWindow("/ScriptEditorWindow.fxml"));
        aboutMenu.setOnAction(actionEvent -> openWindow("/aboutDialog.fxml"));

        closeAppMenu.setOnAction(actionEvent -> System.exit(0));
    }

    public void showNewCommandWindow() {
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("Wpisz nazwę funkcji");
        inputDialog.setHeaderText("Wpisz nazwę funkcji");
        inputDialog.setContentText("Wpisz nazwę funkcji (dwie lub więcej funkcji rozdziel średnikiem): ");

        inputDialog.getDialogPane().setMinWidth(600);

        ((Button) inputDialog.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("Anuluj");
        ((Button) inputDialog.getDialogPane().lookupButton(ButtonType.OK)).setText("Wykonaj");
        Optional<String> result = inputDialog.showAndWait();
        result.ifPresent(text -> {
            if (!text.equals("")) {
                Thread gnuplotThread = new Thread(new LaunchGnuplotThread(text));
                gnuplotThread.start();
                try {
                    gnuplotThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.initOwner(inputDialog.getOwner());
                alert.setTitle("Błąd");
                alert.setHeaderText("Brak polecenia");
                alert.setContentText("Pole polecenia nie może być puste. Wpisz poprawne polecenie programu gnuplot.");
                alert.showAndWait();
            }
        });
        Thread loadPictureThread = new Thread(new LoadPictureThread("test.png", plotImageView));
        loadPictureThread.start();
        try {
            loadPictureThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void openWindow(String filename) {
        FXMLLoader loader = new FXMLLoader();
        try {
            Pane pane = loader.load(getClass().getResource(filename));
            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(mainWindowPane.getScene().getWindow());
            stage.setScene(new Scene(pane));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
