package com.djwilde.inzynierka.windows.newplotwindow;

import com.djwilde.inzynierka.config.GnuplotConstants;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import org.fxmisc.richtext.CodeArea;

import java.util.List;

public class NewPlotWindowController {
    @FXML
    private BorderPane newPlotBorderPane;

    @FXML
    private TextField plotTitleTextField;
    @FXML
    private CheckBox xCheckBox;
    @FXML
    private CheckBox yCheckBox;
    @FXML
    private CheckBox zCheckBox;
    @FXML
    private CheckBox legendCheckBox;
    @FXML
    private ListView<String> keywordListView;
    @FXML
    private Button addKeywordToScriptButton;
    @FXML
    private Button editScriptButton;
    @FXML
    private CodeArea scriptTextArea;
    @FXML
    private Button executeButton;
    @FXML
    private Button saveScriptButton;
    @FXML
    private Button cancelButton;

    public void initialize() {
        keywordListView.setItems(FXCollections.observableList(List.of(GnuplotConstants.GNUPLOT_KEYWORDS)));
        scriptTextArea.setDisable(true);

        addKeywordToScriptButton.setOnAction(actionEvent -> addKeywordToScript());
        editScriptButton.setOnAction(actionEvent -> editScript());
        executeButton.setOnAction(actionEvent -> executeScript());
        saveScriptButton.setOnAction(actionEvent -> saveScript());
        cancelButton.setOnAction(actionEvent -> cancel());
    }

    public void addKeywordToScript() {
        String selectedKeyword = keywordListView.getSelectionModel().getSelectedItem();
        scriptTextArea.appendText(selectedKeyword + "\n");
    }

    public void editScript() {
        scriptTextArea.setDisable(!scriptTextArea.isDisabled());
    }

    public void executeScript() {

    }

    public void saveScript() {

    }

    public void cancel() {

    }
}
