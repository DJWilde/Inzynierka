package com.djwilde.inzynierka.windows.tableeditorwindow;

import com.djwilde.inzynierka.helpers.FileDialogInputOutput;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.List;

public class TableEditorWindowController implements FileDialogInputOutput {
    @FXML
    private BorderPane tableEditorBorderPane;
    @FXML
    private Button loadDataButton;
    @FXML
    private Button saveDataButton;
    @FXML
    private TableView dataTableView;

    private final LoadedDataInfo loadedDataInfo = new LoadedDataInfo();

    private List<DataRecord> loadedData;

    public void initialize() {
        if (LoadedData.getInstance().getLoadedData().size() == 0) {
            dataTableView.setPlaceholder(new Label("Brak danych"));
        } else {
            displayLoadedData();
        }

        loadDataButton.setOnAction(actionEvent -> showOpenFileDialog());
        saveDataButton.setOnAction(actionEvent -> showSaveFileDialog());
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

    private void displayLoadedData() {
        for (int i = 0; i < loadedDataInfo.getNoOfColumns(); i++) {
            TableColumn<String, DataRecord> column = new TableColumn<>();
            column.setCellValueFactory(new PropertyValueFactory<>("Kolumna " + (i + 1)));
            dataTableView.getColumns().add(column);
        }

        List<DataRecord> records = LoadedData.getInstance().getLoadedData();
        for (DataRecord record : records) {
            dataTableView.getItems().add(record);
        }
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
