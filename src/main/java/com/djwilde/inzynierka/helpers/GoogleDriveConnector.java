package com.djwilde.inzynierka.helpers;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GoogleDriveConnector extends InternetConnector {
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private final File CREDENTIALS_FOLDER = new File("config/credentials");
    private final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private final String CLIENT_SECRET_FILENAME = "client_secret.json";

    private DataStoreFactory dataStoreFactory;
    private HttpTransport httpTransport;

    private Drive driveService;

    @Override
    public void initialize() {
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            dataStoreFactory = new FileDataStoreFactory(CREDENTIALS_FOLDER);
            driveService = getDriveService();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<com.google.api.services.drive.model.File> getSubfoldersById(String parentFolderId) throws IOException {
        String nextPageToken = null;

        List<com.google.api.services.drive.model.File> googleDriveFiles = new ArrayList<>();
        String query;

        if (parentFolderId == null) {
            query = " mimeType = 'application/vnd.google-apps.folder' and 'root' in parents";
        } else {
            query = " mimeType = 'application/vnd.google-apps.folder' and '" + parentFolderId + "' in parents";
        }

        do {
            FileList resultList = driveService.files().list().setQ(query).setSpaces("drive").setFields("nextPageToken, files(id, name, createdTime)")
                    .setPageToken(nextPageToken).execute();
            googleDriveFiles.addAll(resultList.getFiles());
            nextPageToken = resultList.getNextPageToken();
        } while (nextPageToken != null);

        return googleDriveFiles;
    }

    @Override
    public List<com.google.api.services.drive.model.File> getRootFolders() throws IOException {
        return getSubfoldersById(null);
    }

    @Override
    public List<com.google.api.services.drive.model.File> getSubfolderByName(String parentFolderId, String subfolderName) throws IOException {
        String nextPageToken = null;

        List<com.google.api.services.drive.model.File> googleDriveSubfolderFiles = new ArrayList<>();
        String query;

        if (parentFolderId == null) {
            query = " name = '" + subfolderName + "' and mimeType = 'application/vnd.google-apps.folder' and 'root' in parents";
        } else {
            query = " name = '" + subfolderName + "' and mimeType = 'application/vnd.google-apps.folder' and '" + parentFolderId + "' in parents";
        }

        do {
            FileList resultList = driveService.files().list().setQ(query).setSpaces("drive").setFields("nextPageToken, files(id, name, createdTime)")
                    .setPageToken(nextPageToken).execute();
            googleDriveSubfolderFiles.addAll(resultList.getFiles());
            nextPageToken = resultList.getNextPageToken();
        } while (nextPageToken != null);

        return googleDriveSubfolderFiles;
    }

    @Override
    public List<com.google.api.services.drive.model.File> getRootFolderByName(String folderName) throws IOException {
        return getSubfolderByName(null, folderName);
    }

    @Override
    public List<com.google.api.services.drive.model.File> getFilesByName(String filename) throws IOException {
        String nextPageToken = null;

        List<com.google.api.services.drive.model.File> googleDriveFoundFiles = new ArrayList<>();
        String query = " name contains '" + filename + "' and mimetype != 'application/vnd.google-apps.folder'";

        do {
            FileList resultList = driveService.files().list().setQ(query).setSpaces("drive").setFields("nextPageToken, files(id, name, createdTime, mimeType)")
                    .setPageToken(nextPageToken).execute();
            googleDriveFoundFiles.addAll(resultList.getFiles());
            nextPageToken = resultList.getNextPageToken();
        } while (nextPageToken != null);

        return googleDriveFoundFiles;
    }

    public com.google.api.services.drive.model.File createNewFolder(String parentFolderId, String newFolderName) throws IOException {
        com.google.api.services.drive.model.File newFile = new com.google.api.services.drive.model.File();

        newFile.setName(newFolderName);
        newFile.setMimeType("application/vnd.google-apps.folder");

        if (parentFolderId != null) {
            List<String> parentFolders = List.of(parentFolderId);
            newFile.setParents(parentFolders);
        }

        return driveService.files().create(newFile).setFields("id, name").execute();
    }

    public com.google.api.services.drive.model.File createNewFile(String parentFolderId, String contentType, String filename, File file) throws IOException {
        com.google.api.services.drive.model.File newFile = new com.google.api.services.drive.model.File();
        newFile.setName(filename);

        List<String> parentFolders = List.of(parentFolderId);
        newFile.setParents(parentFolders);

        AbstractInputStreamContent uploadedInputStreamContent = new FileContent(contentType, file);

        return driveService.files().create(newFile, uploadedInputStreamContent).setFields("id, webContentLink, webViewLink, parents")
                .execute();
    }

    public Permission shareFolder(String fileId, String email, String permissionType, String permissionRole) throws IOException {
        Permission permission = new Permission();
        permission.setType(permissionType);
        permission.setRole(permissionRole);
        permission.setEmailAddress(email);

        return driveService.permissions().create(fileId, permission).execute();
    }

    private Credential getCredentials() throws IOException {
        File clientSecretFile = new File(CREDENTIALS_FOLDER, CLIENT_SECRET_FILENAME);
        if (!clientSecretFile.exists()) {
            throw new FileNotFoundException("Skopiuj plik " + CLIENT_SECRET_FILENAME + " do folderu " +
                    CREDENTIALS_FOLDER.getAbsolutePath() + " i spróbuj jeszcze raz.");

        }

        InputStream inputStream = new FileInputStream(clientSecretFile);
        GoogleClientSecrets googleClientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(inputStream));
        GoogleAuthorizationCodeFlow authorizationCodeFlow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, googleClientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(CREDENTIALS_FOLDER)).setAccessType("offline").build();

        return new AuthorizationCodeInstalledApp(authorizationCodeFlow, new LocalServerReceiver()).authorize("user");
    }

    private Drive getDriveService() throws IOException {
        if (driveService != null) {
            return driveService;
        }

        Credential credential = getCredentials();
        driveService = new Drive.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();

        return driveService;
    }
}
