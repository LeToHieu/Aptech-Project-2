package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.DAO.UserDAO;
import com.aptech.aptechproject2.Model.User;
import com.aptech.aptechproject2.Ulti.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.mindrot.jbcrypt.BCrypt;

public class RegisterController {

    @FXML private TextField usernameField, emailField, fullNameField;
    @FXML private PasswordField passwordField, confirmPasswordField;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void onRegister(ActionEvent e) {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String pass = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (username.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Vui lòng điền đầy đủ!");
            return;
        }

        if (!pass.equals(confirm)) {
            showAlert(Alert.AlertType.ERROR, "Mật khẩu không khớp!");
            return;
        }

        if (userDAO.getByEmail(email) != null) {
            showAlert(Alert.AlertType.ERROR, "Email đã tồn tại!");
            return;
        }

        String hash = BCrypt.hashpw(pass, BCrypt.gensalt());
        User user = new User(0, username, email, hash);
        user.setFullName(fullName);

        if (userDAO.create(user)) {
            showAlert(Alert.AlertType.INFORMATION, "Đăng ký thành công! Đang chuyển đến đăng nhập...");
            SceneManager.loadScene("/com/aptech/aptechproject2/fxml/login.fxml", usernameField.getScene());
        } else {
            showAlert(Alert.AlertType.ERROR, "Đăng ký thất bại!");
        }
    }

    @FXML
    private void onLoginLink(ActionEvent e) {
        SceneManager.loadScene("/com/aptech/aptechproject2/fxml/login.fxml", usernameField.getScene());
    }

    private void showAlert(Alert.AlertType type, String message) {
        new Alert(type, message).showAndWait();
    }
}