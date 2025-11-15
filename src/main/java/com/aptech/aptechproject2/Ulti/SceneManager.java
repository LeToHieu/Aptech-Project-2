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

    public static void loadScene(String fxmlPath, Scene currentScene) {
        try {
            Parent root = FXMLLoader.load(SceneManager.class.getResource(fxmlPath));
            Stage stage = (Stage) currentScene.getWindow();

            // ✅ Giữ nguyên kích thước cũ
            double width = currentScene.getWidth();
            double height = currentScene.getHeight();
            boolean isMax = stage.isMaximized();

            Scene newScene = new Scene(root, width, height);

            stage.setScene(newScene);
            stage.setMaximized(isMax); // vẫn giữ trạng thái full màn hình nếu đang bật
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Lỗi tải FXML: " + fxmlPath).show();
        }
    }


}
