package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.Ulti.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class AdminDashboardController {

    @FXML private StackPane contentArea;
    @FXML private Button usersBtn, booksBtn, authorsBtn;

    @FXML
    private void initialize() {
//        showBooks();
        showUsers();
        setActiveButton(usersBtn);
    }

    @FXML private void showUsers()   { loadContent("/com/aptech/aptechproject2/fxml/user_list.fxml");   setActiveButton(usersBtn); }
    @FXML private void showBooks()   { loadContent("/com/aptech/aptechproject2/fxml/book_list.fxml");   setActiveButton(booksBtn); }
    @FXML private void showAuthors() { loadContent("/com/aptech/aptechproject2/fxml/author_list.fxml"); setActiveButton(authorsBtn); }

    @FXML
    private void onLogout() {
        SceneManager.setCurrentUser(null);
        SceneManager.loadScene("/com/aptech/aptechproject2/fxml/login.fxml", contentArea.getScene());
    }

    private void loadContent(String fxmlPath) {
        try {
            Parent content = FXMLLoader.load(AdminDashboardController.class.getResource(fxmlPath));
            contentArea.getChildren().setAll(content);
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Không tìm thấy file: " + fxmlPath).show();
        }
    }

    private void setActiveButton(Button activeBtn) {
        usersBtn.getStyleClass().remove("active");
        booksBtn.getStyleClass().remove("active");
        authorsBtn.getStyleClass().remove("active");
        activeBtn.getStyleClass().add("active");
    }
}