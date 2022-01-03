package com.djwilde.inzynierka.windows.mainwindow;

import com.djwilde.inzynierka.helpers.*;
import com.djwilde.inzynierka.threads.LaunchGnuplotThread;
import com.djwilde.inzynierka.threads.LoadPictureThread;
import com.djwilde.inzynierka.windows.WindowController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.*;
import javafx.util.Pair;
import javafx.util.converter.DefaultStringConverter;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.ZoneId;
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
    private Button newCollectionButton;
    @FXML
    private Button loadCollectionButton;
    @FXML
    private Button connectToNetworkButton;

    @FXML
    private TextArea outputTextArea;

    @FXML
    private TabPane tabPane;
    @FXML
    private TreeView<String> collectionsTreeView;

    private final List<String> filePaths = new ArrayList<>();

    private final LogHelper logHelper = LogHelper.getInstance();

    private boolean isConnected = false;
    private InternetConnector internetConnector;

    public void initialize() {
        newCommandMenuItem.setOnAction(actionEvent -> showNewCommandWindow());
        toolbarNewCommandButton.setOnAction(actionEvent -> showNewCommandWindow());
        toolbarNewPlotButton.setOnAction(actionEvent -> openWindow("/NewPlotWindow.fxml", null));
        toolbarNewScriptButton.setOnAction(actionEvent -> openWindow("/ScriptEditorWindow.fxml", null));
        editDataButton.setOnAction(actionEvent -> openWindow("/TableEditorWindow.fxml", null));
        newCollectionButton.setOnAction(actionEvent -> newCollection());
        loadCollectionButton.setOnAction(actionEvent -> openCollection(mainWindowPane.getScene().getWindow()));
        connectToNetworkButton.setOnAction(actionEvent -> chooseOnlineDriveService());
        aboutMenu.setOnAction(actionEvent -> openWindow("/aboutDialog.fxml", null));

        outputTextArea.setEditable(false);

        closeAppMenu.setOnAction(actionEvent -> System.exit(0));

        File logFile = new File("log/" + Instant.now() + "-log.txt");
        logHelper.setLogFile(logFile);

        logHelper.log("Uruchamianie zasobów aplikacji...");
        logHelper.log("Wczytano pomyślnie. Witaj w JPlotter!");
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
                logHelper.appendOutputText(outputTextArea, "Uruchamianie gnuplota...");
                logHelper.appendOutputText(outputTextArea,"Generowanie wykresu...");
                gnuplotThread.start();
                try {
                    gnuplotThread.join();
                    logHelper.appendOutputText(outputTextArea,"Wykres wygenerowano pomyślnie.");
                } catch (InterruptedException e) {
                    logHelper.appendOutputText(outputTextArea,"Wystąpił błąd w trakcie generowania wykresu. Przerwano wykonanie zadania." + e.getMessage());
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
        logHelper.appendOutputText(outputTextArea,"Wczytywanie gotowego wykresu...");
        loadPictureThread.start();
        try {
            loadPictureThread.join();
            logHelper.appendOutputText(outputTextArea,"Wyświetlanie wykresu...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void openWindow(String filename, File file) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(filename));
        try {
            Stage stage = loader.load();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(mainWindowPane.getScene().getWindow());
            if (file != null) {
                WindowController windowController = loader.getController();
                System.out.println(windowController);
                windowController.openFile(file);
            }
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void newCollection() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Tworzenie nowej kolekcji");
        dialog.setHeaderText("Nazwa nowej kolekcji");
        dialog.setContentText("Podaj nazwę nowej kolekcji:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(s -> {
            logHelper.appendOutputText(outputTextArea,"Tworzenie nowej kolekcji...");
            File newDir = new File(s);
            boolean dirCreated = newDir.mkdir();
            if (dirCreated) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Powiadomienie");
                alert.setHeaderText("Sukces");
                alert.setContentText("Pomyślnie utworzono kolekcję.");
                alert.showAndWait();
                Tab tab = new Tab(s);
                tabPane.getTabs().add(tab);
                logHelper.appendOutputText(outputTextArea,"Pomyślnie utworzono nowy katalog na dysku...");
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                alert.setHeaderText("Błąd");
                alert.setContentText("Wystąpił błąd podczas tworzenia kolekcji.");
                alert.showAndWait();
                logHelper.appendOutputText(outputTextArea,"Wystąpił błąd podczas tworzenia kolekcji.");
            }
        });
    }

    public void openCollection(Window window) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDir = directoryChooser.showDialog(window);
        if (selectedDir != null) {
            if (tabPane.getTabs().get(0).getText().equals("Twoje kolekcje")) {
                tabPane.getTabs().remove(0);
            }
            logHelper.appendOutputText(outputTextArea,"Wczytywanie kolekcji...");
            Tab tab = new Tab(selectedDir.getName());
            TreeView<String> tabTreeView = new TreeView<>();
            tabPane.getTabs().add(tab);
            tabTreeView.setEditable(true);

            tabTreeView.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getClickCount() == 2 && mouseEvent.getButton() == MouseButton.PRIMARY) {
                    TreeItem<String> item = tabTreeView.getSelectionModel().getSelectedItem();
                    if (FilenameUtils.getExtension(item.getValue()).equals("png") || FilenameUtils.getExtension(item.getValue()).equals("jpg")) {
                        logHelper.appendOutputText(outputTextArea,"Wczytywanie pliku " + item.getValue() + " z kolekcji " + selectedDir.getName() + "...");
                        System.out.println(getAbsolutePathFromTreeView(item.getValue()));
                        Thread loadPictureThread = new Thread(new LoadPictureThread(getAbsolutePathFromTreeView(item.getValue()), plotImageView));
                        loadPictureThread.start();
                        try {
                            loadPictureThread.join();
                            logHelper.appendOutputText(outputTextArea,"Pomyślnie wczytano plik " + item.getValue() + ".");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else if (FilenameUtils.getExtension(item.getValue()).equals("plt")) {
                        logHelper.appendOutputText(outputTextArea,"Wczytywanie skryptu " + item.getValue() + " z kolekcji " + selectedDir.getName() + "...");
                        openWindow("/ScriptEditorWindow.fxml", new File(getAbsolutePathFromTreeView(item.getValue())));
                        logHelper.appendOutputText(outputTextArea,"Pomyślnie wczytano plik " + item.getValue());
                    } else if (FilenameUtils.getExtension(item.getValue()).equals("txt")) {
                        logHelper.appendOutputText(outputTextArea,"Wczytywanie danych z pliku " + item.getValue() + " z kolekcji " + selectedDir.getName() + "...");
                        openWindow("/TableEditorWindow.fxml", new File(getAbsolutePathFromTreeView(item.getValue())));
                        logHelper.appendOutputText(outputTextArea,"Pomyślnie wczytano plik " + item.getValue());
                    }
                } else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                    ContextMenu contextMenu = new ContextMenu();
                    MenuItem renameFileMenuItem = new MenuItem("Zmień nazwę");
                    MenuItem deleteFileMenuItem = new MenuItem("Usuń");

                    deleteFileMenuItem.setOnAction(actionEvent -> {
                        TreeItem<String> fileToBeDeleted = tabTreeView.getSelectionModel().getSelectedItem();
                        File file = new File(getAbsolutePathFromTreeView(fileToBeDeleted.getValue()));
                        logHelper.appendOutputText(outputTextArea,"Usuwanie pliku " + fileToBeDeleted.getValue() + " z kolekcji " + selectedDir.getName());
                        if (file.delete()) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Powiadomienie");
                            alert.setHeaderText("Sukces");
                            alert.setContentText("Pomyślnie usunięto plik.");
                            alert.showAndWait();

                            logHelper.appendOutputText(outputTextArea,"Usunięto plik " + fileToBeDeleted.getValue());
                            tabTreeView.getRoot().getChildren().remove(fileToBeDeleted);
                            filePaths.remove(file.getAbsolutePath());
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Błąd");
                            alert.setHeaderText("Błąd");
                            alert.setContentText("Wystąpił błąd podczas usuwania pliku.");
                            alert.showAndWait();
                            logHelper.appendOutputText(outputTextArea,"Wystąpił błąd podczas usuwania pliku.");
                        }
                    });

                    contextMenu.getItems().addAll(renameFileMenuItem, deleteFileMenuItem);
                    tabTreeView.setContextMenu(contextMenu);
                }
            });

            tab.setContent(tabTreeView);
            TreeItem<String> treeItem = new TreeItem<>(selectedDir.getName());
            tabTreeView.setRoot(treeItem);
            for (File file : selectedDir.listFiles()) {
                createTree(file, treeItem);
                filePaths.add(file.getAbsolutePath());
                logHelper.appendOutputText(outputTextArea,"Wczytano plik " + file.getName() + " do kolekcji " + selectedDir.getName());
                System.out.println(file.getAbsolutePath());
            }

            Runnable watchChangeDirectory = () -> {
                try {
                    Path path = Paths.get(selectedDir.getName());
                    WatchService watchService = path.getFileSystem().newWatchService();
                    path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY,
                            StandardWatchEventKinds.ENTRY_DELETE);

                    WatchKey watchKey = watchService.take();

                    List<WatchEvent<?>> events = watchKey.pollEvents();
                    for (WatchEvent<?> event : events) {
                        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                            String newFilename = event.context().toString();
                            File file = new File(newFilename);
                            TreeItem<String> newFile = new TreeItem<>(newFilename);
                            tabTreeView.getRoot().getChildren().add(newFile);
                            Path destination = Paths.get(selectedDir.getAbsolutePath() + "\\" + file.getName());
                            logHelper.appendOutputText(outputTextArea,"Dodano plik " + newFilename + " do kolekcji.");
                            filePaths.add(destination.toString());
                        }
                        if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                            System.out.println("Zmieniono plik");
                            String newFilename = event.context().toString();
                            System.out.println(newFilename);
                        }
                        if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                            System.out.println("Usunięto plik");
                        }
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            };

            new Thread(watchChangeDirectory).start();
            logHelper.appendOutputText(outputTextArea,"Pomyślnie wczytano kolekcję " + selectedDir.getName());
        }
    }

//    public void connectToNetworkDrive() throws GeneralSecurityException, IOException {
//        GoogleDriveConnector googleDriveConnector = new GoogleDriveConnector();
//        googleDriveConnector.initialize();
//        List<com.google.api.services.drive.model.File> driveFiles = googleDriveConnector.getRootFolderByName("Documents");
//        for (com.google.api.services.drive.model.File file : driveFiles) {
//            logHelper.appendOutputText(outputTextArea, "Folder ID: " + file.getId() + "; folder name: " + file.getName());
//        }
//    }

    public void chooseOnlineDriveService() {
        List<String> choices = new ArrayList<>();
        choices.add("Google Drive");
        choices.add("Dropbox");

        ChoiceDialog<String> choiceDialog = new ChoiceDialog<>("Google Drive", choices);
        choiceDialog.setTitle("Połącz się z dyskiem");
        choiceDialog.setHeaderText("Połącz się z dyskiem sieciowym");
        choiceDialog.setContentText("Wybierz usługę z którą chcesz się połączyć: ");

        Optional<String> result = choiceDialog.showAndWait();
        result.ifPresent(service -> {
            InternetConnectorCommand connectorCommand = new InternetConnectorCommand();
            internetConnector = connectorCommand.createConnector(service);
            internetConnector.initialize();
            isConnected = true;
            List<com.google.api.services.drive.model.File> files = null;
            try {
                files = (List<com.google.api.services.drive.model.File>) internetConnector.getRootFolders();
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (com.google.api.services.drive.model.File file : files) {
                logHelper.appendOutputText(outputTextArea, "Folder ID: " + file.getId() + "; folder name: " + file.getName());
            }
        });
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
            rootItem.getChildren().add(newTreeItem);
        }
    }

    private static final class RenameTreeNodeCell extends TextFieldTreeCell<String> {
        private ContextMenu contextMenu = new ContextMenu();

        public RenameTreeNodeCell() {
            super(new DefaultStringConverter());

            MenuItem renameMenuItem = new MenuItem("Zmień nazwę");
            renameMenuItem.setOnAction(actionEvent -> startEdit());
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (!isEditing()) {
                setContextMenu(contextMenu);
            }
        }
    }

}
