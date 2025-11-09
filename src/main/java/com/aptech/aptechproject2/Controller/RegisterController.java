package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.DAO.UserDAO;
import com.aptech.aptechproject2.Model.User;
import com.aptech.aptechproject2.Ulti.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.mindrot.jbcrypt.BCrypt;

public class RegisterController {

    @FXML private TextField usernameField, emailField, phoneField; // Thêm phoneField
    @FXML private PasswordField passwordField, confirmPasswordField;
    @FXML private Label errorLabel;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void onRegister(ActionEvent e) {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String pass = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            errorLabel.setText("❌ Vui lòng điền đầy đủ!");
            return;
        }

        // Kiểm tra trùng
        if (userDAO.getByUsername(username) != null) {
            errorLabel.setText("❌ Username đã tồn tại!");
            return;
        }
        if (userDAO.getByEmail(email) != null) {
            errorLabel.setText("❌ Email đã tồn tại!");
            return;
        }
        if (userDAO.getByPhone(phone) != null) {
            errorLabel.setText("❌ SĐT đã tồn tại!");
            return;
        }

        if (!pass.equals(confirm)) {
            errorLabel.setText("❌ Mật khẩu không khớp!");
            return;
        }

        String hash = BCrypt.hashpw(pass, BCrypt.gensalt());
        User newUser = new User(username, email, phone, hash, 2); // Role 2 = User

        if (userDAO.create(newUser)) {
            showAlert(Alert.AlertType.INFORMATION, "Đăng ký thành công!");
            SceneManager.loadScene("/com/aptech/aptechproject2/fxml/login.fxml", usernameField.getScene());
        } else {
            errorLabel.setText("❌ Đăng ký thất bại!");
        }
    }

    @FXML
    private void onLoginLink(ActionEvent e) {
        SceneManager.loadScene("/com/aptech/aptechproject2/fxml/login.fxml", usernameField.getScene());
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert a = new Alert(type, msg);
        a.showAndWait();
    }
}