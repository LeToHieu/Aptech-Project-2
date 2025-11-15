package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.DAO.ReviewDAO;
import com.aptech.aptechproject2.DAO.BookDAO;
import com.aptech.aptechproject2.DAO.UserDAO;
import com.aptech.aptechproject2.Model.Review;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddReviewController {

    @FXML private ComboBox<String> userCombo, bookCombo;
    @FXML private Slider ratingSlider;
    @FXML private TextArea commentArea;
    @FXML private Label ratingLabel, errorLabel;

    private final UserDAO userDAO = new UserDAO();
    private final BookDAO bookDAO = new BookDAO();
    private final ReviewDAO reviewDAO = new ReviewDAO();

    @FXML private void initialize() {
        userCombo.setItems(FXCollections.observableArrayList(userDAO.getAllUsers().stream().map(u -> u.getUsername()).toList()));
        bookCombo.setItems(FXCollections.observableArrayList(bookDAO.getAllBooks().stream().map(b -> b.getTitle()).toList()));
        ratingSlider.valueProperty().addListener((obs, old, val) -> ratingLabel.setText("★".repeat(val.intValue())));
    }

    @FXML private void onAdd() {
        String user = userCombo.getValue();
        String book = bookCombo.getValue();
        int rating = (int) ratingSlider.getValue();
        String comment = commentArea.getText().trim();

        if (user == null || book == null || rating < 1) {
            errorLabel.setText("Chọn đầy đủ!");
            return;
        }

        long userId = userDAO.getAllUsers().stream().filter(u -> u.getUsername().equals(user)).findFirst().get().getId();
        long bookId = bookDAO.getAllBooks().stream().filter(b -> b.getTitle().equals(book)).findFirst().get().getId();

        if (reviewDAO.create(new Review(0, userId, bookId, rating, comment, null))) {
            close();
        } else {
            errorLabel.setText("Thêm thất bại!");
        }
    }

    @FXML private void onCancel() { close(); }
    private void close() { ((Stage) userCombo.getScene().getWindow()).close(); }
}