package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.DAO.ShelfDAO;
import com.aptech.aptechproject2.Model.Shelf;
import com.aptech.aptechproject2.Ulti.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditShelfController {

    @FXML private TextField nameField;
    @FXML private TextArea descArea;

    private Shelf shelfToEdit;
    private final ShelfDAO shelfDAO = new ShelfDAO();

    public void setShelf(Shelf shelf) {
        this.shelfToEdit = shelf;
        nameField.setText(shelf.getName());
        descArea.setText(shelf.getDescription() != null ? shelf.getDescription() : "");
    }

    @FXML
    private void onUpdate() {
        String name = nameField.getText().trim();
        String desc = descArea.getText().trim();

        if (name.isEmpty()) {
            alert("Tên kệ không được để trống!");
            return;
        }

        shelfToEdit.setName(name);
        shelfToEdit.setDescription(desc.isEmpty() ? null : desc);

        if (shelfDAO.update(shelfToEdit)) {
            success("Cập nhật kệ thành công!");
            close();
        } else {
            alert("Cập nhật thất bại!");
        }
    }

    @FXML
    private void onCancel() {
        close();
    }

    private void close() {
        ((Stage) nameField.getScene().getWindow()).close();
    }

    private void alert(String msg) {
        new Alert(Alert.AlertType.WARNING, msg).showAndWait();
    }

    private void success(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }
}