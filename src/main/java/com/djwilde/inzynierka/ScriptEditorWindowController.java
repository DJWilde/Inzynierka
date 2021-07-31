package com.djwilde.inzynierka;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import org.fxmisc.richtext.CodeArea;

public class ScriptEditorWindowController {
    @FXML
    private BorderPane scriptBorderPane;

    @FXML
    private MenuItem newScriptMenuItem;
    @FXML
    private MenuItem openScriptMenuItem;
    @FXML
    private MenuItem saveMenuItem;
    @FXML
    private MenuItem saveAllMenuItem;
    @FXML
    private MenuItem exitMenuItem;
    @FXML
    private MenuItem undoMenuItem;
    @FXML
    private MenuItem redoMenuItem;
    @FXML
    private MenuItem cutMenuItem;
    @FXML
    private MenuItem copyMenuItem;
    @FXML
    private MenuItem pasteMenuItem;
    @FXML
    private MenuItem deleteMenuItem;
    @FXML
    private MenuItem selectAllMenuItem;
    @FXML
    private MenuItem removeSelectMenuItem;
    @FXML
    private MenuItem documentationMenuItem;
    @FXML
    private MenuItem aboutMenuItem;

    @FXML
    private Button newScriptButton;
    @FXML
    private Button openScriptButton;
    @FXML
    private Button saveScriptButton;
    @FXML
    private Button cutButton;
    @FXML
    private Button copyButton;
    @FXML
    private Button pasteButton;
    @FXML
    private Button undoButton;
    @FXML
    private Button redoButton;
    @FXML
    private Button executeButton;

    @FXML
    private CodeArea scriptCodeArea;

    public void initialize() {

    }
}
