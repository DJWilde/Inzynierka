package com.djwilde.inzynierka.windows.tableeditorwindow;

import java.util.List;

public class DataRecord {
    private List<String> dataColumns;

    public DataRecord(String... dataColumns) {
        this.dataColumns = List.of(dataColumns);
    }

    public List<String> getDataColumns() {
        return dataColumns;
    }
}
