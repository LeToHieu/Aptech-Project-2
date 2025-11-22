package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.DAO.UserDAO;
import com.aptech.aptechproject2.Model.User;
import com.aptech.aptechproject2.Ulti.SceneManager;
import com.aptech.aptechproject2.Ulti.Session; // Thêm import này để sử dụng Session
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.mindrot.jbcrypt.BCrypt;

public class LoginController {

    @FXML private TextField usernameField; // Đổi từ emailField → usernameField
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void onLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Vui lòng nhập đầy đủ!");
            return;
        }

        User user = userDAO.getByUsername(username);
        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
            Session.setCurrentUser(user); // Sửa từ SceneManager → Session (đồng bộ với Dashboard)
            // Phân trang theo role (sửa logic cho đúng với getRoleName: 1=Admin → admin_dashboard, 0=User hoặc 2=Librarian → user_dashboard)
            int role = user.getRole();
            if (role == 2|| role == 1) {
                SceneManager.loadScene("/com/aptech/aptechproject2/fxml/AdminPage/admin_dashboard.fxml", usernameField.getScene());
            } else if (role == 0) {
                    SceneManager.loadScene("/com/aptech/aptechproject2/fxml/user_dashboard.fxml", usernameField.getScene());
            } else {
                showAlert(Alert.AlertType.ERROR, "Vai trò không hợp lệ!");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Username hoặc mật khẩu sai!");
        }
    }

    @FXML
    private void onRegisterLink(ActionEvent e) {
        SceneManager.loadScene("/com/aptech/aptechproject2/fxml/register.fxml", usernameField.getScene());
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.showAndWait();
    }
}