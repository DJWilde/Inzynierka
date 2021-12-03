package com.djwilde.inzynierka.windows.newplotwindow;

import com.djwilde.inzynierka.config.GnuplotConstants;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.fxmisc.richtext.CodeArea;

import java.util.*;
import java.util.stream.Collectors;

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

    private Set<Map.Entry<String, List<String>>> entries;

    public void initialize() {
        entries = GnuplotConstants.GNUPLOT_COMMANDS_AND_ARGUMENTS.entrySet();
        Set<String> keywordSet = GnuplotConstants.GNUPLOT_COMMANDS_AND_ARGUMENTS.keySet();
        keywordListView.setItems(FXCollections.observableList(new ArrayList<>(keywordSet)));
        scriptTextArea.setDisable(true);

        addKeywordToScriptButton.setOnAction(actionEvent -> addKeywordToScript());
        editScriptButton.setOnAction(actionEvent -> editScript());
        executeButton.setOnAction(actionEvent -> executeScript());
        saveScriptButton.setOnAction(actionEvent -> saveScript());
        cancelButton.setOnAction(actionEvent -> cancel());
    }

    public void addKeywordToScript() {
//        String selectedKeyword = keywordListView.getSelectionModel().getSelectedItem();
//        scriptTextArea.appendText(selectedKeyword + "\n");
        String selectedKeyword = keywordListView.getSelectionModel().getSelectedItem();
        List<String> selectedParametersList = null;
        for (Map.Entry<String, List<String>> entry : entries) {
            if (entry.getKey().equals(selectedKeyword)) {
                if (entry.getValue() == null) {
                    selectedParametersList = new ArrayList<>();
                } else {
                    selectedParametersList = entry.getValue();
                }
                break;
            }
        }
        if (selectedParametersList.size() > 0) {
            generateNewCommandDialog(selectedKeyword, selectedParametersList);
        } else {
            scriptTextArea.appendText(selectedKeyword + "\n");
        }
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

    private void generateNewCommandDialog(String selectedKeyword, List<String> selectedParametersList) {
        Dialog<List<String>> generateNewCommandDialogBox = new Dialog<>();
        generateNewCommandDialogBox.setResizable(true);
        List<String> templateChoices = new ArrayList<>();
        templateChoices.add("Templatka 1");
        templateChoices.add("Templatka 2");
        templateChoices.add("Templatka 3");
        generateNewCommandDialogBox.setTitle("Dodawanie polecenia");
        generateNewCommandDialogBox.setHeaderText("Dodawanie polecenia");
        generateNewCommandDialogBox.setContentText("Użyj tego okna dialogowego w celu edycji polecenia i dodania go do skryptu.");

        VBox vBox = new VBox();

        GridPane selectTemplateChoicePane = new GridPane();
        selectTemplateChoicePane.setPrefWidth(500);
        selectTemplateChoicePane.setVgap(10);
        selectTemplateChoicePane.setHgap(10);

        ChoiceBox<String> templateChoiceBox = new ChoiceBox<>();
        templateChoiceBox.getItems().setAll(templateChoices);

        selectTemplateChoicePane.add(new Label("Wybierz szablon"), 0, 0);
        selectTemplateChoicePane.add(templateChoiceBox, 1, 0);

        selectTemplateChoicePane.setPadding(new Insets(10, 10, 10, 10));
        vBox.getChildren().add(selectTemplateChoicePane);

        GridPane parametersGridPane = new GridPane();
        parametersGridPane.setHgap(10);
        parametersGridPane.setVgap(10);

        List<TextField> parameterTextFields = new ArrayList<>();

        if (selectedParametersList != null) {
            for (int i = 0; i < selectedParametersList.size(); i++) {
                TextField parameterTextField = new TextField();
                parameterTextField.setPromptText(selectedParametersList.get(i));

                parametersGridPane.add(new Label(selectedParametersList.get(i)), 0, i);
                parametersGridPane.add(parameterTextField, 1, i);

                parameterTextFields.add(parameterTextField);
            }
        }

        vBox.getChildren().add(parametersGridPane);

        TextArea commandScriptArea = new TextArea();
        commandScriptArea.setEditable(false);
        commandScriptArea.setText(selectedKeyword);

        vBox.getChildren().add(commandScriptArea);

        ButtonType createTableButtonType = new ButtonType("Dodaj polecenie", ButtonBar.ButtonData.OK_DONE);
        generateNewCommandDialogBox.getDialogPane().getButtonTypes().addAll(createTableButtonType, ButtonType.CANCEL);

        generateNewCommandDialogBox.getDialogPane().setContent(vBox);

        generateNewCommandDialogBox.setResultConverter(buttonType ->
                List.of());

        Optional<List<String>> result = generateNewCommandDialogBox.showAndWait();
        result.ifPresent(strings -> {

            for (TextField parameters : parameterTextFields) {
                System.out.println(parameters.getText());
            }
        });
    }
}
