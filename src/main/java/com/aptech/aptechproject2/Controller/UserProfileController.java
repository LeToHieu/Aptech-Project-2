package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.DAO.UserDAO;
import com.aptech.aptechproject2.Model.User;
import com.aptech.aptechproject2.Ulti.DBUtil;
import com.aptech.aptechproject2.Ulti.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.net.URL;
import java.util.ResourceBundle;

public class UserProfileController implements Initializable {

    @FXML private TextField txtUsername;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPhone;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnSave;

    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        User u = Session.getCurrentUser();
        if (u != null) {
            loadUser(u);
        }
    }

    public void loadUser(User user) {
        if (user == null) return;
        this.currentUser = user;
        txtUsername.setText(user.getUsername());
        txtEmail.setText(user.getEmail());
        txtPhone.setText(user.getPhoneNumber());
        txtPassword.setText("");
    }

    @FXML
    private void onSave(ActionEvent event) {
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "No user loaded");
            return;
        }

        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String newPassword = txtPassword.getText();

        if (username.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Username is required");
            return;
        }

        currentUser.setUsername(username);
        currentUser.setEmail(email);
        currentUser.setPhoneNumber(phone);
        if (newPassword != null && !newPassword.isEmpty()) {
            currentUser.setPassword(newPassword);
        }

        try {
            UserDAO userDAO = new UserDAO();
            boolean ok = userDAO.update(currentUser);
            if (ok) {
                Session.setCurrentUser(currentUser);
                showAlert(Alert.AlertType.INFORMATION, "Profile updated successfully");
                txtPassword.setText("");
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to update profile");
            }
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Error while saving profile: " + ex.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert a = new Alert(type);
        a.setTitle("Profile");
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }
}
