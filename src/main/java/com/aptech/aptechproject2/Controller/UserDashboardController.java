package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.Ulti.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class UserDashboardController {
    @FXML private Label welcomeLabel;

    @FXML
    private void initialize() {
        welcomeLabel.setText("Xin chào, " + SceneManager.getCurrentUser().getUsername() + "!");
    }

    @FXML private void showBooks() { /* Load danh sách sách */ }
    @FXML private void onLogout() {
        SceneManager.setCurrentUser(null);
        SceneManager.loadScene("/com/aptech/aptechproject2/fxml/login.fxml", welcomeLabel.getScene());
    }
}