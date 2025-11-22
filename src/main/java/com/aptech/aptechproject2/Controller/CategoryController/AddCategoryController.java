package com.aptech.aptechproject2.Controller.CategoryController;

import com.aptech.aptechproject2.DAO.CategoryDAO;
import com.aptech.aptechproject2.Model.Category;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddCategoryController {

    @FXML private TextField nameField;
    @FXML private TextArea descriptionField;
    @FXML private Label errorLabel;

    private final CategoryDAO categoryDAO = new CategoryDAO();

    @FXML
    private void onAddCategory() {
        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();

        if (name.isEmpty()) {
            errorLabel.setText("Vui lòng điền tên thể loại!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setContentText("Bạn có chắc chắn muốn thêm thể loại này?");
        if (confirm.showAndWait().get() != ButtonType.OK) {
            return;
        }

        Category newCategory = new Category(0, name, description);

        if (categoryDAO.create(newCategory)) {
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Thành công");
            success.setContentText("Thêm thể loại thành công!");
            success.showAndWait();
            onCancel();
        } else {
            errorLabel.setText("Thêm thể loại thất bại!");
        }
    }

    @FXML
    private void onCancel() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}