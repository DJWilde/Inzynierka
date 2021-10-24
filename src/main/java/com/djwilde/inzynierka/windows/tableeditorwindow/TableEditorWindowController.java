package com.djwilde.inzynierka.windows.tableeditorwindow;

import com.djwilde.inzynierka.helpers.FileDialogInputOutput;
import com.djwilde.inzynierka.threads.LaunchGnuplotThread;
import com.djwilde.inzynierka.threads.LoadPictureThread;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableListValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.Pair;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;

public class TableEditorWindowController implements FileDialogInputOutput {
    @FXML
    private BorderPane tableEditorBorderPane;
    @FXML
    private Button createTableButton;
    @FXML
    private Button addNewColumnButton;
    @FXML
    private Button loadDataButton;
    @FXML
    private Button saveDataButton;
    @FXML
    private TableView<ObservableList<String>> dataTableView;

    private final LoadedDataInfo loadedDataInfo = new LoadedDataInfo();

    private final ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

    public void initialize() {
        if (LoadedData.getInstance().getLoadedData().size() == 0) {
            dataTableView.setPlaceholder(new Label("Brak danych"));
        } else {
//            displayLoadedData();
        }

        dataTableView.setEditable(true);

        createTableButton.setOnAction(actionEvent -> createTable());
        addNewColumnButton.setOnAction(actionEvent -> addColumn());
        loadDataButton.setOnAction(actionEvent -> showOpenFileDialog());
        saveDataButton.setOnAction(actionEvent -> showSaveFileDialog());
    }

    public void createTable() {
        Dialog<Pair<String, String>> createTableDialog = new Dialog<>();
        createTableDialog.setTitle("Tworzenie tabeli");
        createTableDialog.setHeaderText("Tworzenie tabeli");

        ButtonType createTableButtonType = new ButtonType("Utwórz", ButtonBar.ButtonData.OK_DONE);
        createTableDialog.getDialogPane().getButtonTypes().addAll(createTableButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        TextField noOfColumns = new TextField();
        noOfColumns.setPromptText("Liczba kolumn");
        TextField noOfRows = new TextField();
        noOfRows.setPromptText("Liczba wierszy");

        gridPane.add(new Label("Liczba kolumn"), 0, 0);
        gridPane.add(noOfColumns, 1, 0);
        gridPane.add(new Label("Liczba wierszy"), 0, 1);
        gridPane.add(noOfRows, 1, 1);

        Node createTableButton = createTableDialog.getDialogPane().lookupButton(createTableButtonType);
        createTableButton.setDisable(true);

        noOfColumns.textProperty().addListener((observableValue, oldValue, newValue) ->
                createTableButton.setDisable(newValue.trim().isEmpty()));

        createTableDialog.getDialogPane().setContent(gridPane);

        createTableDialog.setResultConverter(buttonType ->
                new Pair<>(noOfColumns.getText(), noOfRows.getText()));

        Optional<Pair<String, String>> result = createTableDialog.showAndWait();
        result.ifPresent(pair -> {
            for (int i = 0; i < Integer.parseInt(noOfRows.getText()); i++) {
                ObservableList<String> tableRow = FXCollections.observableArrayList();
                for (int j = 0; j < Integer.parseInt(noOfColumns.getText()); j++) {
                    tableRow.add("");
                }
                data.add(tableRow);
            }
            for (int i = 0; i < Integer.parseInt(noOfColumns.getText()); i++) {
                TableColumn<ObservableList<String>, String> column = new TableColumn<>("Kolumna " + (i + 1));
                column.setCellFactory(TextFieldTableCell.forTableColumn());
                int index = i;
                column.setOnEditCommit(observableListStringCellEditEvent -> {
                    ObservableList<String> tableRow = observableListStringCellEditEvent.getRowValue();
                    String newValue = observableListStringCellEditEvent.getNewValue();
                    tableRow.set(index, newValue);
                    System.out.println(data);
                });
                dataTableView.getColumns().add(column);
            }
            for (ObservableList<String> list : data) {
                dataTableView.getItems().add(list);
            }
        });
    }

    public void addColumn() {
        TableColumn<ObservableList<String>, String> newColumn = new TableColumn<>("Nowa kolumna");
        newColumn.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    public void showOpenFileDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Otwórz plik");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Pliki tekstowe", ".txt"),
                new FileChooser.ExtensionFilter("Wszystkie pliki", "*.*")
        );
        File dataFile = fileChooser.showOpenDialog(tableEditorBorderPane.getScene().getWindow());
        if (dataFile != null) {
            openFile(dataFile);
        }
    }

    public void showSaveFileDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapisz plik");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Pliki tekstowe", ".txt"),
                new FileChooser.ExtensionFilter("Wszystkie pliki", "*.*")
        );
        File dataFile = fileChooser.showSaveDialog(tableEditorBorderPane.getScene().getWindow());
        if (dataFile != null) {
            saveFile(dataFile);
        }
    }

    private void openFile(File file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }
                String[] tokens = line.split("\\s");
                ObservableList<String> row = FXCollections.observableArrayList();
                row.addAll(Arrays.asList(tokens));
                data.add(row);
            }

            br.close();
            displayLoadedData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveFile(File file) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            ObservableList<TableColumn<ObservableList<String>, ?>> tableNames = dataTableView.getColumns();
            StringBuilder stringBuilder = new StringBuilder();

            for (TableColumn<ObservableList<String>, ?> table : tableNames) {
                stringBuilder.append(table).append(" ");
            }
            bufferedWriter.write("# " + stringBuilder + "\n");
            for (ObservableList<String> row : data) {
                for (String col : row) {
                    bufferedWriter.write(col + " ");
                }
                bufferedWriter.write("\n");
            }

            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayLoadedData() {
        for (int i = 0; i < data.get(0).size(); i++) {
            TableColumn<ObservableList<String>, String> column = new TableColumn<>("Kolumna " + (i + 1));
            column.setCellFactory(TextFieldTableCell.forTableColumn());
            column.setCellValueFactory(new PropertyValueFactory<>(data.get(i).get(i)));
            int index = i;
            column.setOnEditCommit(observableListStringCellEditEvent -> {
                ObservableList<String> tableRow = observableListStringCellEditEvent.getRowValue();
                String newValue = observableListStringCellEditEvent.getNewValue();
                tableRow.set(index, newValue);
                System.out.println(data);
            });
            dataTableView.getColumns().add(column);
        }
        for (ObservableList<String> list : data) {
            dataTableView.getItems().add(list);
        }

        dataTableView.refresh();
        System.out.println(dataTableView.getItems());
    }

    private static class LoadedDataInfo {
        private int noOfColumns;

        public LoadedDataInfo() {
            noOfColumns = 0;
        }

        public int getNoOfColumns() {
            return noOfColumns;
        }

        public void setNoOfColumns(int noOfColumns) {
            this.noOfColumns = noOfColumns;
        }
    }

}
