package com.djwilde.inzynierka.windows.scripteditorwindow;

import com.djwilde.inzynierka.helpers.FileDialogInputOutput;
import com.djwilde.inzynierka.helpers.LogHelper;
import com.djwilde.inzynierka.helpers.ScriptHelper;
import com.djwilde.inzynierka.helpers.Timer;
import com.djwilde.inzynierka.windows.WindowController;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import org.fxmisc.richtext.CodeArea;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class ScriptEditorWindowController implements FileDialogInputOutput, WindowController {
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

    private Clipboard systemClipboard;

    private boolean isScriptOpen = false;

    private LogHelper logHelper = LogHelper.getInstance();

    public void initialize() {
        KeyCombination newScriptShortcut = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
        Runnable newScriptShortcutRunner = this::newScript;

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

        newScriptButton.setOnAction(actionEvent -> newScript());
        newScriptMenuItem.setOnAction(actionEvent -> newScript());
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

        systemClipboard = Clipboard.getSystemClipboard();

//        scriptCodeArea.setOnKeyPressed(keyEvent -> SyntaxHighlightConfig.getInstance().highlightSyntax());
    }

    public void newScript() {
        if (isScriptOpen) {
            clearScriptDialog();
            isScriptOpen = false;
        }
        scriptCodeArea.clear();
        isScriptOpen = true;
    }

    public void showOpenFileDialog() {
        if (isScriptOpen) {
            clearScriptDialog();
            isScriptOpen = false;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Otwórz plik");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Skrypty GNUPlot", ".gpi", ".plt", ".gp"),
                new FileChooser.ExtensionFilter("Pliki tekstowe", ".txt"),
                new FileChooser.ExtensionFilter("Wszystkie pliki", "*.*")
        );
        File scriptFile = fileChooser.showOpenDialog(scriptBorderPane.getScene().getWindow());
        if (scriptFile != null) {
            openFile(scriptFile);
        }
        isScriptOpen = true;
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
            saveFile(scriptFile);
        }
    }

    private void clearScriptDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Zapisać?");
        alert.setHeaderText("Zapisać skrypt?");
        alert.setContentText("Czy chcesz zapisać skrypt? Niezapisane dane mogą zostać utracone.");
        ButtonType yesButton = new ButtonType("Tak", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("Nie", ButtonBar.ButtonData.NO);
        ButtonType cancelButton = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(yesButton, noButton, cancelButton);
        Optional<ButtonType> result = alert.showAndWait();
        result.ifPresent(buttonType -> {
            if (buttonType == yesButton) {
                showSaveFileDialog();
                scriptCodeArea.clear();
            } else if (buttonType == noButton) {
                scriptCodeArea.clear();
            } else {
                alert.close();
            }
        });
    }

    public void undoAction() {

    }

    public void redoAction() {

    }

    public void cutAction() {
        String text = getSelectedText();

        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(text);
        systemClipboard.setContent(clipboardContent);

        scriptCodeArea.deleteText(scriptCodeArea.getSelection());
    }

    public void copyAction() {
        String text = getSelectedText();

        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(text);
        systemClipboard.setContent(clipboardContent);
    }

    public void pasteAction() {

    }

    @Timer
    public void executeScript() {
        logHelper.log("Generowanie wykresu...");
        CompletableFuture<Void> runScriptFuture = CompletableFuture.runAsync(() -> saveFile(new File("test.plt"))).thenRunAsync(() -> {
            System.out.println("Zapisano skrypt");
            ScriptHelper.executeScriptFromAnotherProcess("test.plt");
        }).thenRunAsync(() -> System.out.println("Wykonano skrypt"));

        CompletableFuture<Void> completableFuture = CompletableFuture.allOf(runScriptFuture);

        completableFuture.join();
        logHelper.log("Wygenerowano wykres.");
    }

    private String getSelectedText() {
        return (scriptCodeArea.getSelectedText() != null) ? scriptCodeArea.getSelectedText() : null;
    }

    @Timer
    public void openFile(File file) {
        logHelper.log("Otwieranie pliku " + file.getName());
        StringBuilder code = new StringBuilder();
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                code.append(line).append("\n");
            }
            scriptCodeArea.replaceText(code.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        logHelper.log("Otwarto plik " + file.getName());
    }

    @Timer
    public void saveFile(File file) {
        logHelper.log("Zapisywanie do pliku " + file.getName());
        ScriptHelper.saveScript(file, scriptCodeArea.getText());
        logHelper.log("Zapisano do pliku " + file.getName());
    }
}
