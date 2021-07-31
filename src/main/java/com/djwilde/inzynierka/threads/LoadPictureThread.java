package com.djwilde.inzynierka.threads;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.net.MalformedURLException;

public class LoadPictureThread implements Runnable {
    private String filename;
    private ImageView imageView;

    public LoadPictureThread(String filename, ImageView imageView) {
        this.filename = filename;
        this.imageView = imageView;
    }

    @Override
    public void run() {
        File file = new File(filename);
        try {
            String localFile = file.toURI().toURL().toString();
            Image image = new Image(localFile);
            imageView.setImage(image);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
