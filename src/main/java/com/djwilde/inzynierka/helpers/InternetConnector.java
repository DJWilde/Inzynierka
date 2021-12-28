package com.djwilde.inzynierka.helpers;

import java.io.IOException;
import java.util.List;

public abstract class InternetConnector {
    protected static final String APPLICATION_NAME = "JPlotter";

    public abstract void initialize();
    public abstract List<?> getSubfoldersById(String parentFolderId) throws IOException;
    public abstract List<?> getRootFolders() throws IOException;
    public abstract List<?> getSubfolderByName(String parentFolderId, String subfolderName) throws IOException;
    public abstract List<?> getRootFolderByName(String folderName) throws IOException;
    public abstract List<?> getFilesByName(String filename) throws IOException;
}
