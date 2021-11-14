package com.djwilde.inzynierka.windows;

import com.djwilde.inzynierka.windows.scripteditorwindow.ScriptEditorWindowController;
import com.djwilde.inzynierka.windows.tableeditorwindow.TableEditorWindowController;

import java.util.Map;
import java.util.function.Supplier;

public class WindowControllerSupplier {
    private static final Map<String, Supplier<WindowController>> WINDOW_CONTROLLER_MAP;

    static {
        WINDOW_CONTROLLER_MAP = Map.of("txt", TableEditorWindowController::new, "plt", ScriptEditorWindowController::new);
    }

    public WindowController supplyWindowController(String fileExtension) {
        Supplier<WindowController> supplier = WINDOW_CONTROLLER_MAP.get(fileExtension);
        if (supplier == null) {
            throw new IllegalArgumentException("Invalid window controller");
        }

        return supplier.get();
    }
}
