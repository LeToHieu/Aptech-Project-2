package com.aptech.aptechproject2.Controller.UserController;

import com.aptech.aptechproject2.DAO.UserDAO;
import com.aptech.aptechproject2.Model.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class EditUserController {

    @FXML private TextField usernameField, emailField, phoneField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private Label errorLabel;

    private User userToEdit;
    private final UserDAO userDAO = new UserDAO();

    public void setUser(User user) {
        this.userToEdit = user;
        usernameField.setText(user.getUsername());
        emailField.setText(user.getEmail());
        phoneField.setText(user.getPhoneNumber());
        roleCombo.setValue(user.getRoleName());
    }

    @FXML
    private void initialize() {
        roleCombo.setItems(FXCollections.observableArrayList("User", "Admin", "Librarian"));
    }

    @FXML
    private void onUpdateUser() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String roleStr = roleCombo.getValue();

        if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || roleStr == null) {
            errorLabel.setText("Vui lòng điền đầy đủ thông tin!");
            return;
        }

        int role = switch (roleStr) {
            case "User" -> 0;
            case "Admin" -> 1;
            case "Librarian" -> 2;
            default -> -1;
        };

        // Kiểm tra trùng lặp với user khác
        User existingUsername = userDAO.getByUsername(username);
        if (existingUsername != null && existingUsername.getId() != userToEdit.getId()) {
            errorLabel.setText("Username đã tồn tại!");
            return;
        }
        User existingEmail = userDAO.getByEmail(email);
        if (existingEmail != null && existingEmail.getId() != userToEdit.getId()) {
            errorLabel.setText("Email đã tồn tại!");
            return;
        }
        User existingPhone = userDAO.getByPhone(phone);
        if (existingPhone != null && existingPhone.getId() != userToEdit.getId()) {
            errorLabel.setText("Số điện thoại đã tồn tại!");
            return;
        }

        userToEdit.setUsername(username);
        userToEdit.setEmail(email);
        userToEdit.setPhoneNumber(phone);
        userToEdit.setRole(role);

        if (userDAO.update(userToEdit)) {
            onCancel(); // Đóng dialog sau thành công
        } else {
            errorLabel.setText("Cập nhật thất bại!");
        }
    }

    @FXML
    private void onCancel() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }
}