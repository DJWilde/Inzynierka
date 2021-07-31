package com.djwilde.inzynierka;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class ScriptEditorWindowController {
    @FXML
    private BorderPane scriptBorderPane;
    @FXML
    private Scene scriptEditorScene;

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
        KeyCombination newScriptShortcut = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
        Runnable newScriptShortcutRunner = this::showNewWindow;

        KeyCombination openScriptShortcut = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);
        Runnable openScriptShortcutRunner = this::showOpenFileDialog;

        KeyCombination saveScriptShortcut = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
        Runnable saveScriptShortcutRunner = this::showSaveFileDialog;

        KeyCombination cutShortcut = new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN);
        Runnable cutShortcutRunner = this::cutAction;

        KeyCombination copyShortcut = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
        Runnable copyShortcutRunner = this::copyAction;

        KeyCombination pasteShortcut = new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN);
        Runnable pasteShortcutRunner = this::pasteAction;

        KeyCombination undoShortcut = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN);
        Runnable undoShortcutRunner = this::undoAction;

        KeyCombination redoShortcut = new KeyCodeCombination(KeyCode.Z, KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN);
        Runnable redoShortcutRunner = this::redoAction;

        KeyCombination executeShortcut = new KeyCodeCombination(KeyCode.E, KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN);
        Runnable executeScriptRunner = this::executeScript;

        newScriptButton.setOnAction(actionEvent -> showNewWindow());
        newScriptMenuItem.setOnAction(actionEvent -> showNewWindow());
        newScriptMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));

        openScriptButton.setOnAction(actionEvent -> showOpenFileDialog());
        openScriptMenuItem.setOnAction(actionEvent -> showOpenFileDialog());
        openScriptMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));

        saveScriptButton.setOnAction(actionEvent -> showSaveFileDialog());
        saveMenuItem.setOnAction(actionEvent -> showSaveFileDialog());
        saveMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));

        cutButton.setOnAction(actionEvent -> cutAction());
        cutMenuItem.setOnAction(actionEvent -> cutAction());
        cutMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+X"));

        copyButton.setOnAction(actionEvent -> copyAction());
        copyMenuItem.setOnAction(actionEvent -> copyAction());
        copyMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+C"));

        pasteButton.setOnAction(actionEvent -> pasteAction());
        pasteMenuItem.setOnAction(actionEvent -> pasteAction());
        pasteMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+V"));

        undoButton.setOnAction(actionEvent -> undoAction());
        undoMenuItem.setOnAction(actionEvent -> undoAction());
        undoMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+Z"));

        redoButton.setOnAction(actionEvent -> redoAction());
        redoMenuItem.setOnAction(actionEvent -> redoAction());
        redoMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+Z"));

        executeButton.setOnAction(actionEvent -> executeScript());

        scriptEditorScene.getAccelerators().put(newScriptShortcut, newScriptShortcutRunner);
        scriptEditorScene.getAccelerators().put(openScriptShortcut, openScriptShortcutRunner);
        scriptEditorScene.getAccelerators().put(saveScriptShortcut, saveScriptShortcutRunner);
        scriptEditorScene.getAccelerators().put(cutShortcut, cutShortcutRunner);
        scriptEditorScene.getAccelerators().put(copyShortcut, copyShortcutRunner);
        scriptEditorScene.getAccelerators().put(pasteShortcut, pasteShortcutRunner);
        scriptEditorScene.getAccelerators().put(undoShortcut, undoShortcutRunner);
        scriptEditorScene.getAccelerators().put(redoShortcut, redoShortcutRunner);
        scriptEditorScene.getAccelerators().put(executeShortcut, executeScriptRunner);
    }

    public void showNewWindow() {

    }

    public void showOpenFileDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Otwórz plik");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Skrypty GNUPlot", ".gpi", ".plt", ".gp"),
                new FileChooser.ExtensionFilter("Pliki tekstowe", ".txt"),
                new FileChooser.ExtensionFilter("Wszystkie pliki", "*.*")
        );
        File scriptFile = fileChooser.showOpenDialog(scriptBorderPane.getScene().getWindow());
        if (scriptFile != null) {
            // Tu będzie wczytanie skryptu do codeArea
            openScript(scriptFile);
            System.out.println("Wczytano");
        }
    }

    public void showSaveFileDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapisz plik");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Skrypty GNUPlot", ".gpi", ".plt", ".gp"),
                new FileChooser.ExtensionFilter("Pliki tekstowe", ".txt"),
                new FileChooser.ExtensionFilter("Wszystkie pliki", "*.*")
        );
        File scriptFile = fileChooser.showSaveDialog(scriptBorderPane.getScene().getWindow());
        if (scriptFile != null) {
            saveScriptToFile(scriptFile);
            System.out.println("Zapisano");
        }
    }

    public void undoAction() {

    }

    public void redoAction() {

    }

    public void cutAction() {

    }

    public void copyAction() {

    }

    public void pasteAction() {

    }

    public void executeScript() {

    }

    private void openScript(File file) {
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String code = scanner.nextLine();
                scriptCodeArea.replaceText(0, 0, code);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveScriptToFile(File file) {
        try {
            PrintWriter writer = new PrintWriter(file);
            writer.println(scriptCodeArea.getText());
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
