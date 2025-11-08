package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.DAO.UserDAO;
import com.aptech.aptechproject2.Model.User;
import com.aptech.aptechproject2.Ulti.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.mindrot.jbcrypt.BCrypt;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void onRegister(ActionEvent e) {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String pass = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (username.isEmpty() || email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            errorLabel.setText("❌ Please fill in all fields!");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errorLabel.setText("❌ Invalid email format!");
            return;
        }

        if (pass.length() < 6) {
            errorLabel.setText("❌ Password must be at least 6 characters!");
            return;
        }

        if (!pass.equals(confirm)) {
            errorLabel.setText("❌ Passwords do not match!");
            return;
        }

        if (userDAO.getByEmail(email) != null) {
            errorLabel.setText("❌ Email already exists!");
            return;
        }

        String hash = BCrypt.hashpw(pass, BCrypt.gensalt());
        User newUser = new User(0, username, email, hash);

        if (userDAO.create(newUser)) {
            showAlert(Alert.AlertType.INFORMATION, "Register successful!");
            SceneManager.loadScene("/com/aptech/aptechproject2/fxml/login.fxml", emailField.getScene());
        } else {
            errorLabel.setText("❌ Register failed, try again!");
        }
    }

    @FXML
    private void onLoginLink(ActionEvent e) {
        SceneManager.loadScene("/com/aptech/aptechproject2/fxml/login.fxml", emailField.getScene());
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert a = new Alert(type, msg);
        a.showAndWait();
    }
}
