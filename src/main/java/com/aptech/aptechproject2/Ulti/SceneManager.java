package com.aptech.aptechproject2.Ulti;

import com.aptech.aptechproject2.Model.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {
    private static User currentUser;

//    public static void loadScene(String fxmlPath, Scene currentScene) {
//        try {
//            Parent root = FXMLLoader.load(SceneManager.class.getResource(fxmlPath));
//            Stage stage = (Stage) currentScene.getWindow();
//
//            boolean isMax = stage.isMaximized(); // Giữ trạng thái full màn
//
//            Scene newScene = new Scene(root);
//            stage.setScene(newScene);
//            stage.setMaximized(isMax); // Áp lại full màn nếu đang bật
//            stage.centerOnScreen();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static void loadScene(String fxmlPath, Scene currentScene) {
        try {
            Parent root = FXMLLoader.load(SceneManager.class.getResource(fxmlPath));
            Stage stage = (Stage) currentScene.getWindow();
            Scene newScene = new Scene(root);
            stage.setScene(newScene);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Lỗi tải FXML: " + fxmlPath).show();
        }
    }

    public static User getCurrentUser() { return currentUser; }
    public static void setCurrentUser(User user) { currentUser = user; }
}
