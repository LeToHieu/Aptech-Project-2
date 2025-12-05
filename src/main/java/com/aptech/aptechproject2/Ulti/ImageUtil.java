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

///com/aptech/aptechproject2
    public static final String IMAGE_DIR = "src/main/resources/com/aptech/aptechproject2/images/"; // Adjust if needed for build
//    private static final String IMAGE_DIR = "src/main/resources/images/"; // Adjust if needed for build
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

                return uniqueName; // Relative path for DB
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void loadImageToView(String imagePath, ImageView imageView) {
        String fallbackPath = IMAGE_DIR + "no_image.jpg";

        if (imagePath != null && !imagePath.isEmpty()) {
            // Extract filename if path contains slashes (for backward compatibility)
            String filename = imagePath.substring(imagePath.lastIndexOf("/") + 1);
            String fullFilePath = IMAGE_DIR + filename;

            File imageFile = new File(fullFilePath);
            if (imageFile.exists()) {
                // Load from file system
                Image image = new Image("file:" + imageFile.getAbsolutePath());
                imageView.setImage(image);
                imageView.setFitWidth(150);
                imageView.setFitHeight(150);
                imageView.setPreserveRatio(true);
                imageView.setImage(image);

                return;
            }
        }

        // Fallback if no path or file not exists
        Image fallback = new Image("file:" + fallbackPath);
        imageView.setImage(fallback);
    }

    public static void deleteImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            // Extract filename if path contains slashes
            String filename = imagePath.substring(imagePath.lastIndexOf("/") + 1);
            String fullFilePath = IMAGE_DIR + filename;

            try {
                Files.deleteIfExists(Paths.get(fullFilePath));
            } catch (IOException e) {
                // Bỏ qua lỗi nếu file không tồn tại hoặc không xóa được
                e.printStackTrace(); // Optional: Log lỗi
            }
        }
    }
}

