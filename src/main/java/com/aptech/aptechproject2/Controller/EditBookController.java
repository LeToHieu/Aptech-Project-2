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

public class EditBookController {

    @FXML private TextField titleField, imageField, urlField;
    @FXML private TextArea descriptionField;
    @FXML private Label errorLabel;

    private Book bookToEdit;
    private final BookDAO bookDAO = new BookDAO();

    public void setBook(Book book) {
        this.bookToEdit = book;
        titleField.setText(book.getTitle());
        descriptionField.setText(book.getDescription());
        imageField.setText(book.getImage());
        urlField.setText(book.getUrl());
    }

    @FXML
    private void onUpdateBook() {
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
        confirm.setContentText("Bạn có chắc chắn muốn sửa sách này?");
        if (confirm.showAndWait().get() != ButtonType.OK) {
            return;
        }

        bookToEdit.setTitle(title);
        bookToEdit.setDescription(description);
        bookToEdit.setImage(image);
        bookToEdit.setUrl(url);

        if (bookDAO.update(bookToEdit)) {
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Thành công");
            success.setContentText("Cập nhật sách thành công!");
            success.showAndWait();
            onCancel();
        } else {
            errorLabel.setText("Cập nhật thất bại!");
        }
    }

    @FXML
    private void onCancel() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }
}