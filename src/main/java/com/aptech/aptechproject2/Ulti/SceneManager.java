package com.aptech.aptechproject2.Ulti;

import com.aptech.aptechproject2.Model.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class SceneManager {
    private static User currentUser;

    public static void loadScene(String fxmlPath, Scene currentScene) {
        try {
            Parent root = FXMLLoader.load(SceneManager.class.getResource(fxmlPath));
            Stage stage = (Stage) currentScene.getWindow();

            double width = currentScene.getWidth();
            double height = currentScene.getHeight();
            boolean isMax = stage.isMaximized();

            Scene newScene = new Scene(root, width, height);
            stage.setScene(newScene);
            stage.setMaximized(isMax);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            alert("Lỗi tải giao diện: " + fxmlPath);
        }
    }

    public static Object loadContent(String fxmlPath, Parent container) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();

            if (container instanceof Pane) {
                ((Pane) container).getChildren().setAll(root);
            } else if (container instanceof BorderPane) {
                ((BorderPane) container).setCenter(root);
            }

            return loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            alert("Lỗi tải nội dung: " + fxmlPath);
            return null;
        }
    }

    public static void loadInto(Pane container, String fxmlPath) {
        try {
            Node node = FXMLLoader.load(SceneManager.class.getResource(fxmlPath));
            container.getChildren().clear();
            container.getChildren().add(node);
            if (node instanceof Region region) {
                region.prefWidthProperty().bind(container.widthProperty());
                region.prefHeightProperty().bind(container.heightProperty());
            }
        } catch (IOException e) {
            e.printStackTrace();
            alert("Lỗi tải nội dung: " + e.getMessage());
        }
    }

    public static void openModal(String fxmlPath, Object data) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Thông tin");
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            alert("Không thể mở cửa sổ!");
        }
    }

    // THÊM 3 HÀM SAU ĐÂY - BẮT BUỘC PHẢI CÓ
    public static void alert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void success(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thành công");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static boolean confirm(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận");
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}