package com.djwilde.inzynierka.windows.tableeditorwindow;

import com.djwilde.inzynierka.helpers.FileDialogInputOutput;
import com.djwilde.inzynierka.windows.WindowController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.util.Pair;

import java.io.*;
import java.util.*;

public class TableEditorWindowController implements FileDialogInputOutput, WindowController {
    @FXML
    private BorderPane tableEditorBorderPane;
    @FXML
    private Button createTableButton;
    @FXML
    private Button addNewColumnButton;
    @FXML
    private Button addNewRowButton;
    @FXML
    private Button loadDataButton;
    @FXML
    private Button saveDataButton;
    @FXML
    private TableView<ObservableList<String>> dataTableView;

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
        addNewRowButton.setOnAction(actionEvent -> addRow());
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
        if (data.size() == 0) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Tworzenie tabeli");
            dialog.setHeaderText("Tworzenie tabeli");
            dialog.setContentText("Wygląda na to, że tabela jest pusta. Podaj ilość wierszy:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(res -> {
                for (int i = 0; i < Integer.parseInt(res); i++) {
                    ObservableList<String> tableRow = FXCollections.observableArrayList();
                    tableRow.add("");
                    data.add(tableRow);
                }

                TableColumn<ObservableList<String>, String> column = new TableColumn<>("Nowa kolumna");
                column.setCellFactory(TextFieldTableCell.forTableColumn());
                column.setOnEditCommit(observableListStringCellEditEvent -> {
                    ObservableList<String> row = observableListStringCellEditEvent.getRowValue();
                    String newValue = observableListStringCellEditEvent.getNewValue();
                    System.out.println(dataTableView.getColumns().size());
                    row.set(0, newValue);
                    System.out.println(data);
                });
                dataTableView.getColumns().add(column);
                for (ObservableList<String> list : data) {
                    dataTableView.getItems().add(list);
                }
                System.out.println(data);
            });
        } else {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Tworzenie nowej kolumny");
            dialog.setHeaderText("Nazwa nowej kolumny");
            dialog.setContentText("Podaj nazwę nowej kolumny:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(s -> {
                for (ObservableList<String> dataRow : data) {
                    dataRow.add("");
                }

                TableColumn<ObservableList<String>, String> newColumn = new TableColumn<>(s);
                newColumn.setCellFactory(TextFieldTableCell.forTableColumn());
                newColumn.setOnEditCommit(observableListStringCellEditEvent -> {
                    ObservableList<String> row = observableListStringCellEditEvent.getRowValue();
                    String newValue = observableListStringCellEditEvent.getNewValue();
                    System.out.println(dataTableView.getColumns().size());
                    row.set(dataTableView.getColumns().size() - 1, newValue);
                    System.out.println(data);
                });
                dataTableView.getColumns().add(newColumn);
            });
        }
    }

    public void addRow() {
        if (data.size() == 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Brak kolumn");
            alert.setHeaderText("Błąd");
            alert.setContentText("Brakuje kolumn w tabeli. Dodaj najpierw kolumnę zanim dodasz nowy wiersz.");
            alert.showAndWait();
            return;
        }
        ObservableList<String> newRow = FXCollections.observableArrayList();
        for (var i = 0; i < data.get(0).size(); i++) {
            newRow.add("");
        }
        data.add(newRow);
        dataTableView.getItems().add(newRow);
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

    public void openFile(File file) {
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

            System.out.println(data);
            br.close();
            displayLoadedData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveFile(File file) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            ObservableList<TableColumn<ObservableList<String>, ?>> tableNames = dataTableView.getColumns();
            StringBuilder stringBuilder = new StringBuilder();

            for (TableColumn<ObservableList<String>, ?> table : tableNames) {
                stringBuilder.append(table.getText()).append(" ");
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
            int index = i;
            column.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(index)));

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

        System.out.println(dataTableView.getItems());
    }
}
