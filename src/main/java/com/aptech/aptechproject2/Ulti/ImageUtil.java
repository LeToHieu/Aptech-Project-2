package com.aptech.aptechproject2.Ulti;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ImageUtil {

    private static final String IMAGE_DIR = "src/main/resources/images/"; // Adjust if needed for build
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    public static String selectAndSaveImage(Stage stage, ImageView previewView) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                // Create unique name
                String uniqueName = LocalDateTime.now().format(FORMATTER) + ".jpg";
                File dest = new File(IMAGE_DIR + uniqueName);
                Files.createDirectories(Paths.get(IMAGE_DIR)); // Ensure dir exists
                Files.copy(selectedFile.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Preview
                Image image = new Image(dest.toURI().toString());
                previewView.setImage(image);
                previewView.setFitWidth(150);
                previewView.setFitHeight(150);
                previewView.setPreserveRatio(true);

                return "/images/" + uniqueName; // Relative path for DB
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}