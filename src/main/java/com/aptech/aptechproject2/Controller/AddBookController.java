package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.DAO.BookDAO;
import com.aptech.aptechproject2.Model.Book;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddBookController {

    @FXML private TextField titleField, imageField, urlField;
    @FXML private TextArea descriptionField;
    @FXML private Label errorLabel;

    private final BookDAO bookDAO = new BookDAO();

    @FXML
    private void onAddBook() {
        String title = titleField.getText().trim();
        String description = descriptionField.getText().trim();
        String image = imageField.getText().trim();
        String url = urlField.getText().trim();

        if (title.isEmpty()) {
            errorLabel.setText("Vui lòng điền ít nhất tựa đề!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setContentText("Bạn có chắc chắn muốn thêm sách này?");
        if (confirm.showAndWait().get() != ButtonType.OK) {
            return;
        }

        Book newBook = new Book();
        newBook.setTitle(title);
        newBook.setDescription(description);
        newBook.setImage(image);
        newBook.setUrl(url);

        if (bookDAO.create(newBook)) {
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Thành công");
            success.setContentText("Thêm sách thành công!");
            success.showAndWait();
            onCancel();
        } else {
            errorLabel.setText("Thêm sách thất bại!");
        }
    }

    @FXML
    private void onCancel() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }
}