package com.djwilde.inzynierka.helpers;

import com.dropbox.core.DbxApiException;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import java.io.IOException;
import java.util.List;

public class DropboxConnector extends InternetConnector {
    private DbxRequestConfig dbxRequestConfig;
    private DbxClientV2 dbxClient;

    private static final String ACCESS_TOKEN = "sl.A_cil-VjCXcsC5-_rRi1xcJKEPJd4NlHBt_hkQww47XkPKZXjOClg7IwZvmUZpgh4Oh_RLeRPRqhJdHGY8hfEGJ8o5GDd3yFoIZcmHTuVrVsKDU2NhnqTp2B2HP_5G3LfWk5akLNaJI";

    @Override
    public void initialize() {
        dbxRequestConfig = DbxRequestConfig.newBuilder("JPlotter").build();
        dbxClient = new DbxClientV2(dbxRequestConfig, ACCESS_TOKEN);
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
