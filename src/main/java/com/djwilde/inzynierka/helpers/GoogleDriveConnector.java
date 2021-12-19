package com.djwilde.inzynierka.helpers;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class GoogleDriveConnector {
    private static final String APPLICATION_NAME = "JPlotter";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static final File CREDENTIALS_FOLDER = new File("config/credentials");
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private static final String CLIENT_SECRET_FILENAME = "client_secret.json";

    private static Credential getCredentials(NetHttpTransport httpTransport) throws IOException {
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

    public static void connect() throws GeneralSecurityException, IOException {
        if (!CREDENTIALS_FOLDER.exists()) {
            boolean isFolderCreated = CREDENTIALS_FOLDER.mkdirs();
            if (isFolderCreated) {
                System.out.println("Utworzono folder o nazwie " + CREDENTIALS_FOLDER.getAbsolutePath());
                System.out.println("Przenieś plik " + CLIENT_SECRET_FILENAME + " do tego folderu i spróbuj jeszcze raz");
                return;
            }
        }

        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Credential credentials = getCredentials(httpTransport);

        Drive drive = new Drive.Builder(httpTransport, JSON_FACTORY, credentials).setApplicationName(APPLICATION_NAME).build();

        FileList result = drive.files().list().setPageSize(20).setFields("nextPageToken, files(id, name)").execute();
        List<com.google.api.services.drive.model.File> fileList = result.getFiles();
        if (fileList != null) {
            System.out.println("Pliki: ");
            for (com.google.api.services.drive.model.File file : fileList) {
                System.out.println("Nazwa pliku: " + file.getName() + " (" + file.getId() + ")");
            }
        } else {
            System.out.println("Nie znaleziono plików");
        }
    }
}
