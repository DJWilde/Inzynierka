package com.djwilde.inzynierka.helpers;

import javafx.scene.control.TextArea;

import java.time.Instant;
import java.time.ZoneId;

public class LogHelper {
    private static final LogHelper instance = new LogHelper();
    private StringBuilder outputText = new StringBuilder();

    private LogHelper() {

    }

    public static LogHelper getInstance() {
        return instance;
    }

    public void appendOutputText(TextArea outputTextArea, String output) {
        outputText.append("[ ").append(Instant.now().atZone(ZoneId.systemDefault())).append(" ] ").append(output).append("\n");
        outputTextArea.setText(outputText.toString());
        scrollMessagesToBottom(outputTextArea);
    }

    private void scrollMessagesToBottom(TextArea outputTextArea) {
        int caretPos = outputTextArea.caretPositionProperty().get();
        outputTextArea.positionCaret(caretPos);
        outputTextArea.setScrollTop(Double.MAX_VALUE);
    }
}