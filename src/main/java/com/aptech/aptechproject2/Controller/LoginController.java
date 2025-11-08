package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.DAO.UserDAO;
import com.aptech.aptechproject2.Model.User;
import com.aptech.aptechproject2.Ulti.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.mindrot.jbcrypt.BCrypt;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void onLogin(ActionEvent e) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Vui lòng nhập đầy đủ!");
            return;
        }

        User user = userDAO.getByEmail(email);
        if (user != null && BCrypt.checkpw(password, user.getPasswordHash())) {
            SceneManager.setCurrentUser(user);
            SceneManager.loadScene("/com/aptech/aptechproject2/fxml/admin_dashboard.fxml", emailField.getScene());
        } else {
            showAlert(Alert.AlertType.ERROR, "Email hoặc mật khẩu sai!");
        }
    }

    @FXML
    private void onRegisterLink(ActionEvent e) {
        SceneManager.loadScene("/com/aptech/aptechproject2/fxml/register.fxml", emailField.getScene());
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.showAndWait();
    }
}