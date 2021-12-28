package com.djwilde.inzynierka.helpers;

import java.io.IOException;
import java.util.List;

public class DropboxConnector extends InternetConnector {

    @Override
    public void initialize() {

    }

    @Override
    public List<?> getSubfoldersById(String parentFolderId) throws IOException {
        return null;
    }

    @Override
    public List<?> getRootFolders() throws IOException {
        return null;
    }

    @Override
    public List<?> getSubfolderByName(String parentFolderId, String subfolderName) throws IOException {
        return null;
    }

    @Override
    public List<?> getRootFolderByName(String folderName) throws IOException {
        return null;
    }

    @Override
    public List<?> getFilesByName(String filename) throws IOException {
        return null;
    }
}
