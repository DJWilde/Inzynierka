package com.djwilde.inzynierka.windows.tableeditorwindow;

import com.djwilde.inzynierka.helpers.FileDialogInputOutput;
import com.djwilde.inzynierka.threads.LaunchGnuplotThread;
import com.djwilde.inzynierka.threads.LoadPictureThread;
import javafx.application.Platform;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    private List<DataRecord> loadedData;

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
            for (int i = 0; i < Integer.parseInt(noOfColumns.getText()); i++) {
                TableColumn<ObservableList<String>, String> column = new TableColumn<>("Kolumna " + (i + 1));
                column.setCellFactory(TextFieldTableCell.forTableColumn());
                int index = i;
                column.setOnEditCommit(observableListStringCellEditEvent ->
                        observableListStringCellEditEvent.getTableView().getItems().get(observableListStringCellEditEvent.getTablePosition().getRow())
                                .set(index, observableListStringCellEditEvent.getNewValue()));
                dataTableView.getColumns().add(column);
            }
            for (int i = 0; i < Integer.parseInt(noOfRows.getText()); i++) {
                List<String> strings = new ArrayList<>(dataTableView.getColumns().size());
                ObservableList<String> observableStrings = FXCollections.observableArrayList(strings);
                dataTableView.getItems().add(observableStrings);
            }
        });
    }

    public void addColumn() {
        TableColumn<ObservableList<String>, String> newColumn = new TableColumn<>("Nowa kolumna");
        newColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        int noOfColumns = dataTableView.getColumns().size();
        System.out.println(noOfColumns);
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
            System.out.println("Otwarto plik");
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
            System.out.println("Zapisano do pliku");
        }
    }

    private void openFile(File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            BufferedReader br = new BufferedReader(new InputStreamReader(dataInputStream));
            String strLine;
            String[] dataLineTokens = null;
            while ((strLine = br.readLine()) != null) {
                if (strLine.startsWith("#")) {
                    continue;
                }
                dataLineTokens = strLine.split(" ");
                DataRecord dataRecord = new DataRecord(dataLineTokens);
                LoadedData.getInstance().getLoadedData().add(dataRecord);
            }
            assert dataLineTokens != null;
            loadedDataInfo.setNoOfColumns(dataLineTokens.length);
            dataInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveFile(File file) {
        List<DataRecord> dataRecords = LoadedData.getInstance().getLoadedData();
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(dataOutputStream));
            for (DataRecord record : dataRecords) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < record.getDataColumns().size(); i++) {
                    stringBuilder.append(record.getDataColumns().get(i));
                    if (i != record.getDataColumns().size() - 1) {
                        stringBuilder.append(" ");
                    }
                }
                stringBuilder.append("\n");
                bw.write(stringBuilder.toString());
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private void displayLoadedData() {
//        for (int i = 0; i < loadedDataInfo.getNoOfColumns(); i++) {
//            TableColumn<String, DataRecord> column = new TableColumn<>();
//            column.setCellValueFactory(new PropertyValueFactory<>("Kolumna " + (i + 1)));
//            dataTableView.getColumns().add(column);
//        }
//
//        List<DataRecord> records = LoadedData.getInstance().getLoadedData();
//        for (DataRecord record : records) {
//            dataTableView.getItems().add(record);
//        }
//    }

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
