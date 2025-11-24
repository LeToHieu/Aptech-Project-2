package com.aptech.aptechproject2.Controller.UserController;

import com.aptech.aptechproject2.DAO.UserDAO;
import com.aptech.aptechproject2.Model.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

public class AddUserController {

    @FXML private TextField usernameField, emailField, phoneField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private Label errorLabel;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void initialize() {
        roleCombo.setItems(FXCollections.observableArrayList("User", "Admin", "Librarian"));
    }

    @FXML
    private void onAddUser() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = passwordField.getText();
        String roleStr = roleCombo.getValue();

        if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || roleStr == null) {
            errorLabel.setText("Vui lòng điền đầy đủ thông tin!");
            return;
        }

        int role = switch (roleStr) {
            case "User" -> 0;
            case "Admin" -> 1;
            case "Librarian" -> 2;
            default -> -1;
        };

        // Kiểm tra trùng lặp
        if (userDAO.getByUsername(username) != null) {
            errorLabel.setText("Username đã tồn tại!");
            return;
        }
        if (userDAO.getByEmail(email) != null) {
            errorLabel.setText("Email đã tồn tại!");
            return;
        }
        if (userDAO.getByPhone(phone) != null) {
            errorLabel.setText("Số điện thoại đã tồn tại!");
            return;
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User newUser = new User(username, email, phone, hashedPassword, role);

        if (userDAO.create(newUser)) {
            onCancel(); // Đóng dialog sau thành công
        } else {
            errorLabel.setText("Thêm người dùng thất bại!");
        }
    }

    @FXML
    private void onCancel() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }
}