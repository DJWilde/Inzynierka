package com.djwilde.inzynierka.windows.mainwindow;

import com.djwilde.inzynierka.threads.LaunchGnuplotThread;
import com.djwilde.inzynierka.threads.LoadPictureThread;
import com.djwilde.inzynierka.windows.WindowController;
import com.djwilde.inzynierka.windows.WindowControllerSupplier;
import com.djwilde.inzynierka.windows.scripteditorwindow.ScriptEditorWindowController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.*;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
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
    @FXML
    private Button editDataButton;
    @FXML
    private Button loadCollectionButton;

    @FXML
    private TabPane tabPane;
    @FXML
    private TreeView<String> collectionsTreeView;

    private final List<String> filePaths = new ArrayList<>();

    public void initialize() {
        newCommandMenuItem.setOnAction(actionEvent -> showNewCommandWindow());
        toolbarNewCommandButton.setOnAction(actionEvent -> showNewCommandWindow());
        toolbarNewPlotButton.setOnAction(actionEvent -> openWindow("/NewPlotWindow.fxml", null));
        toolbarNewScriptButton.setOnAction(actionEvent -> openWindow("/ScriptEditorWindow.fxml", null));
        editDataButton.setOnAction(actionEvent -> openWindow("/TableEditorWindow.fxml", null));
        loadCollectionButton.setOnAction(actionEvent -> openCollection(mainWindowPane.getScene().getWindow()));
        aboutMenu.setOnAction(actionEvent -> openWindow("/aboutDialog.fxml", null));

        TreeItem<String> rootItem = new TreeItem<>("Nowa kolekcja");
        rootItem.setExpanded(true);
        // Testowo!
        for (int i = 0; i < 5; i++) {
            TreeItem<String> childItem = new TreeItem<>("Wykres " + (i + 1));
            rootItem.getChildren().add(childItem);
        }
        collectionsTreeView.setRoot(rootItem);
        collectionsTreeView.setEditable(true);

        collectionsTreeView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (FilenameUtils.getExtension(newValue.getValue()).equals("png") || FilenameUtils.getExtension(newValue.getValue()).equals("jpg")) {
                System.out.println(getAbsolutePathFromTreeView(newValue.getValue()));
                Thread loadPictureThread = new Thread(new LoadPictureThread(getAbsolutePathFromTreeView(newValue.getValue()), plotImageView));
                loadPictureThread.start();
                try {
                    loadPictureThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (FilenameUtils.getExtension(newValue.getValue()).equals("plt")) {
                System.out.println(getAbsolutePathFromTreeView(newValue.getValue()));
                openWindow("/ScriptEditorWindow.fxml", new File(getAbsolutePathFromTreeView(newValue.getValue())));
                System.out.println("Wybrano plik .plt");
            } else if (FilenameUtils.getExtension(newValue.getValue()).equals("txt")) {
                System.out.println(getAbsolutePathFromTreeView(newValue.getValue()));
                openWindow("/TableEditorWindow.fxml", new File(getAbsolutePathFromTreeView(newValue.getValue())));
                System.out.println("Wybrano plik .txt");
            }
        });

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

    public void openWindow(String filename, File file) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(filename));
        if (file != null) {
            WindowControllerSupplier controllerSupplier = new WindowControllerSupplier();
            WindowController windowController = controllerSupplier.supplyWindowController(FilenameUtils.getExtension(file.getName()));
            System.out.println(windowController);
            windowController.openFile(file);
        }
        try {
            Stage stage = loader.load();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(mainWindowPane.getScene().getWindow());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openCollection(Window window) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDir = directoryChooser.showDialog(window);
        if (selectedDir != null) {
            TreeItem<String> treeItem = new TreeItem<>(selectedDir.getName());
            collectionsTreeView.setRoot(treeItem);
            for (File file : selectedDir.listFiles()) {
                createTree(file, treeItem);
                filePaths.add(file.getAbsolutePath());
                System.out.println(file.getAbsolutePath());
            }
        }
    }

    private String getAbsolutePathFromTreeView(String filename) {
        for (String path : filePaths) {
            if (path.substring(path.lastIndexOf("\\") + 1).equals(filename)) {
                System.out.println(path);
                return path;
            }
        }

        return null;
    }

    private void createTree(File file, TreeItem<String> rootItem) {
        if (file.isDirectory()) {
            TreeItem<String> newRootItem = new TreeItem<>(file.getName());
            collectionsTreeView.setRoot(newRootItem);
            for (File f : file.listFiles()) {
                createTree(f, newRootItem);
            }
        } else if ("plt".equals(FilenameUtils.getExtension(file.getName())) || "txt".equals(FilenameUtils.getExtension(file.getName())) ||
                    "png".equals(FilenameUtils.getExtension(file.getName())) || "jpg".equals(FilenameUtils.getExtension(file.getName()))) {
            TreeItem<String> newTreeItem = new TreeItem<>(file.getName());
            // todo
            // Jeżeli plik jest png lub jpg to wyświetl wykres
            // Jeżeli plik jest plt to wczytaj go do edytora tekstu
            // Jeżeli plik jest txt to wczytaj go do edytora tabel
            rootItem.getChildren().add(newTreeItem);
        }
    }

}
