package com.aptech.aptechproject2.Controller.ShelfController;

import com.aptech.aptechproject2.DAO.ShelfDAO;
import com.aptech.aptechproject2.Model.Shelf;
import com.aptech.aptechproject2.Ulti.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddShelfController {

    @FXML private TextField nameField;
    @FXML private TextArea descArea;

    private final ShelfDAO shelfDAO = new ShelfDAO();

    @FXML private void onAdd() {
        String name = nameField.getText().trim();
        String desc = descArea.getText().trim();
        if (name.isEmpty()) {
            alert("Nhập tên kệ!"); return;
        }
        Shelf shelf = new Shelf(0, name, Session.getCurrentUser().getId(), desc, null);
        if (shelfDAO.create(shelf)) {
            success("Tạo kệ thành công!");
            close();
        } else {
            alert("Tạo thất bại!");
        }
    }

    @FXML private void onCancel() { close(); }
    private void close() { ((Stage) nameField.getScene().getWindow()).close(); }
    private void alert(String msg) { new Alert(Alert.AlertType.WARNING, msg).show(); }
    private void success(String msg) { new Alert(Alert.AlertType.INFORMATION, msg).show(); }
}