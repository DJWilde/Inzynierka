package com.djwilde.inzynierka.config;

import javafx.concurrent.Task;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SyntaxHighlightConfig {
    private static final SyntaxHighlightConfig instance = new SyntaxHighlightConfig(getInstance().codeArea);

    private static final String[] GNUPLOT_KEYWORDS = new String[] {
            "break", "cd", "call", "clear", "continue", "do", "evaluate", "exit", "fit", "help", "history", "if", "for",
            "import", "load", "pause", "plot", "print", "printerr", "pwd", "quit", "raise", "refresh", "replot", "reread",
            "reset", "save", "set", "shell", "splot", "stats", "system", "test", "toggle", "undefine", "unset", "update",
            "while"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", GNUPLOT_KEYWORDS) + ")\\b";
    private static final String PARENTHESIS_PATTERN = "\\(|\\)";
    private static final String CURLY_BRACES_PATTERN = "\\{|\\}";
    private static final String BRACKETS_PATTERN = "\\[|\\]";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<PARENTHESIS>" + PARENTHESIS_PATTERN + ")"
                    + "|(?<CURLYBRACE>" + CURLY_BRACES_PATTERN + ")"
                    + "|(?<BRACKETS>" + BRACKETS_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );

    private CodeArea codeArea;
    private ExecutorService executorService;

    private SyntaxHighlightConfig(CodeArea codeArea) {
        this.codeArea = codeArea;
    }

    public static SyntaxHighlightConfig getInstance() {
        return instance;
    }

    public void highlightSyntax() {
        executorService = Executors.newSingleThreadExecutor();
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        Subscription highlightSyntaxSubscription = codeArea.multiPlainChanges().successionEnds(Duration.ofMillis(500))
                .supplyTask(this::applySyntaxHighlightAsync).awaitLatest(codeArea.multiPlainChanges()).filterMap(t -> {
            if (t.isSuccess()) {
                return Optional.of(t.get());
            } else {
                t.getFailure().printStackTrace();
                return Optional.empty();
            }
        }).subscribe(this::applyHighlights);

        highlightSyntaxSubscription.unsubscribe();
    }

    private Task<StyleSpans<Collection<String>>> applySyntaxHighlightAsync() {
        String code = codeArea.getText();
        Task<StyleSpans<Collection<String>>> task = new Task<>() {
            @Override
            protected StyleSpans<Collection<String>> call() throws Exception {
                return computeHighlights(code);
            }
        };

        executorService.execute(task);
        return task;
    }

    private void applyHighlights(StyleSpans<Collection<String>> highlight) {
        codeArea.setStyleSpans(0, highlight);
    }

    private static StyleSpans<Collection<String>> computeHighlights(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass = matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("PARENTHESIS") != null ? "parenthesis" :
                            matcher.group("CURLYBRACE") != null ? "curlybrace" :
                            matcher.group("BRACKETS") != null ? "brackets" :
                            matcher.group("STRING") != null ? "string" :
                            matcher.group("COMMENT") != null ? "comment" : null;

            spansBuilder.add(Collections.emptyList(), matcher.start() - lastEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastEnd = matcher.end();
        }

        spansBuilder.add(Collections.emptyList(), text.length() - lastEnd);
        return spansBuilder.create();
    }
}
