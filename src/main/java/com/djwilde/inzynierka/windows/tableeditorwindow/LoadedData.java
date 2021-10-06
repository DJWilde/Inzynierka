package com.djwilde.inzynierka.windows.tableeditorwindow;

import java.util.ArrayList;
import java.util.List;

public class LoadedData {
    private final List<DataRecord> loadedData = new ArrayList<>();
    private static final LoadedData instance = getInstance();

    private LoadedData() {}

    public static LoadedData getInstance() {
        if (instance == null) {
            return new LoadedData();
        }
        return instance;
    }

    public List<DataRecord> getLoadedData() {
        return loadedData;
    }
}
