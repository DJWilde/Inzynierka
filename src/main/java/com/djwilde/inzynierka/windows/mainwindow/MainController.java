package com.djwilde.inzynierka.windows.mainwindow;

import com.djwilde.inzynierka.threads.LaunchGnuplotThread;
import com.djwilde.inzynierka.threads.LoadPictureThread;
import com.djwilde.inzynierka.windows.WindowController;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.*;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.*;
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
    private TextArea gnuplotOutputTextArea;

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
        newCollectionButton.setOnAction(actionEvent -> newCollection());
        loadCollectionButton.setOnAction(actionEvent -> openCollection(mainWindowPane.getScene().getWindow()));
        aboutMenu.setOnAction(actionEvent -> openWindow("/aboutDialog.fxml", null));

        gnuplotOutputTextArea.setEditable(false);

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
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                alert.setHeaderText("Błąd");
                alert.setContentText("Wystąpił błąd podczas tworzenia kolekcji.");
                alert.showAndWait();
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
            Tab tab = new Tab(selectedDir.getName());
            TreeView<String> tabTreeView = new TreeView<>();
            tabPane.getTabs().add(tab);
            tabTreeView.setEditable(true);

            tabTreeView.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getClickCount() == 2 && mouseEvent.getButton() == MouseButton.PRIMARY) {
                    TreeItem<String> item = tabTreeView.getSelectionModel().getSelectedItem();
                    if (FilenameUtils.getExtension(item.getValue()).equals("png") || FilenameUtils.getExtension(item.getValue()).equals("jpg")) {
                        System.out.println(getAbsolutePathFromTreeView(item.getValue()));
                        Thread loadPictureThread = new Thread(new LoadPictureThread(getAbsolutePathFromTreeView(item.getValue()), plotImageView));
                        loadPictureThread.start();
                        try {
                            loadPictureThread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else if (FilenameUtils.getExtension(item.getValue()).equals("plt")) {
                        System.out.println(getAbsolutePathFromTreeView(item.getValue()));
                        openWindow("/ScriptEditorWindow.fxml", new File(getAbsolutePathFromTreeView(item.getValue())));
                        System.out.println("Wybrano plik .plt");
                    } else if (FilenameUtils.getExtension(item.getValue()).equals("txt")) {
                        System.out.println(getAbsolutePathFromTreeView(item.getValue()));
                        openWindow("/TableEditorWindow.fxml", new File(getAbsolutePathFromTreeView(item.getValue())));
                        System.out.println("Wybrano plik .txt");
                    }
                } else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                    ContextMenu contextMenu = new ContextMenu();
                    MenuItem renameFileMenuItem = new MenuItem("Zmień nazwę");
                    MenuItem deleteFileMenuItem = new MenuItem("Usuń");

                    deleteFileMenuItem.setOnAction(actionEvent -> {
                        TreeItem<String> fileToBeDeleted = tabTreeView.getSelectionModel().getSelectedItem();
                        File file = new File(getAbsolutePathFromTreeView(fileToBeDeleted.getValue()));
                        if (file.delete()) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Powiadomienie");
                            alert.setHeaderText("Sukces");
                            alert.setContentText("Pomyślnie usunięto plik.");
                            alert.showAndWait();

                            tabTreeView.getRoot().getChildren().remove(fileToBeDeleted);
                            filePaths.remove(file.getAbsolutePath());
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Błąd");
                            alert.setHeaderText("Błąd");
                            alert.setContentText("Wystąpił błąd podczas usuwania pliku.");
                            alert.showAndWait();
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
