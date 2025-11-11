package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.DAO.AuthorDAO;
import com.aptech.aptechproject2.Model.Author;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditAuthorController {

    @FXML private TextField nameField, imageField;
    @FXML private TextArea descriptionField;
    @FXML private Label errorLabel;

    private Author authorToEdit;
    private final AuthorDAO authorDAO = new AuthorDAO();

    public void setAuthor(Author author) {
        this.authorToEdit = author;
        nameField.setText(author.getName());
        descriptionField.setText(author.getDescription());
        imageField.setText(author.getImage());
    }

    @FXML
    private void onUpdateAuthor() {
        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();
        String image = imageField.getText().trim();

        if (name.isEmpty()) {
            errorLabel.setText("Vui lòng điền tên tác giả!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setContentText("Bạn có chắc chắn muốn sửa tác giả này?");
        if (confirm.showAndWait().get() != ButtonType.OK) {
            return;
        }

        authorToEdit.setName(name);
        authorToEdit.setDescription(description);
        authorToEdit.setImage(image);

        if (authorDAO.update(authorToEdit)) {
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Thành công");
            success.setContentText("Cập nhật tác giả thành công!");
            success.showAndWait();
            onCancel();
        } else {
            errorLabel.setText("Cập nhật thất bại!");
        }
    }

    @FXML
    private void onCancel() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}