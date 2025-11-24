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

public class EditCategoryController {

    @FXML private TextField nameField;
    @FXML private TextArea descriptionField;
    @FXML private Label errorLabel;

    private Category categoryToEdit;
    private final CategoryDAO categoryDAO = new CategoryDAO();

    public void setCategory(Category category) {
        this.categoryToEdit = category;
        nameField.setText(category.getName());
        descriptionField.setText(category.getDescription());
    }

    @FXML
    private void onUpdateCategory() {
        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();

        if (name.isEmpty()) {
            errorLabel.setText("Vui lòng điền tên thể loại!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setContentText("Bạn có chắc chắn muốn sửa thể loại này?");
        if (confirm.showAndWait().get() != ButtonType.OK) {
            return;
        }

        categoryToEdit.setName(name);
        categoryToEdit.setDescription(description);

        if (categoryDAO.update(categoryToEdit)) {
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Thành công");
            success.setContentText("Cập nhật thể loại thành công!");
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