package com.djwilde.inzynierka.windows.networkdownloadwindow;

import com.djwilde.inzynierka.helpers.GoogleDriveConnector;
import com.djwilde.inzynierka.helpers.InternetConnector;
import com.djwilde.inzynierka.windows.WindowController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class NetworkDownloadWindowController implements WindowController {
    @FXML
    private ListView<String> folderListView;
    @FXML
    private Button cancelButton;
    @FXML
    private Button downloadButton;

    private InternetConnector internetConnector;

    public static List<?> internetFolders;

    public void initialize() {
//        try {
//            List<File> files = (List<File>) internetConnector.getRootFolders();
//            System.out.println("AAAAA");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void openFile(File file) {

    }

    @Override
    public void saveFile(File file) {

    }

//    public void downloadFilesToLocalDrive(List<File> files) throws IOException {
//        String directoryName = networkDirectoriesTreeView.getSelectionModel().getSelectedItem().toString();
//        CompletableFuture<java.io.File> createNewDirectory = CompletableFuture.supplyAsync(() -> {
//            java.io.File directory = new java.io.File(directoryName);
//            boolean directoryCreated = directory.mkdir();
//            if (directoryCreated) {
//                return directory;
//            }
//            return null;
//        });
//        CompletableFuture<java.io.File> downloadFilesToHardDriveAsync = CompletableFuture.supplyAsync(() -> getFilesToLocalDrive(files));
//    }

//    private java.io.File getFilesToLocalDrive(List<File> files) {
//        OutputStream outputStream = new ByteArrayOutputStream();
//        files.forEach(file -> {
//            String fileId = file.getId();
//        });
//    }
}
