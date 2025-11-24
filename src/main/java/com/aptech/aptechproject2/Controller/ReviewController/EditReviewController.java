package com.aptech.aptechproject2.Controller.ReviewController;

import com.aptech.aptechproject2.DAO.ReviewDAO;
import com.aptech.aptechproject2.Model.Review;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class EditReviewController {

    @FXML private TextField userField, bookField;
    @FXML private Slider ratingSlider;
    @FXML private TextArea commentArea;
    @FXML private Label ratingLabel, errorLabel;

    private Review reviewToEdit;
    private final ReviewDAO reviewDAO = new ReviewDAO();

    public void setReview(Review review) {
        this.reviewToEdit = review;
        userField.setText(review.getUserName());
        bookField.setText(review.getBookTitle());
        ratingSlider.setValue(review.getRating());
        commentArea.setText(review.getComment());
        ratingLabel.setText("★".repeat(review.getRating()));
    }

    @FXML
    private void initialize() {
        ratingSlider.valueProperty().addListener((obs, old, val) -> {
            int stars = val.intValue();
            ratingLabel.setText("★".repeat(stars) + "☆".repeat(5 - stars));
        });
    }

    @FXML
    private void onUpdate() {
        int rating = (int) ratingSlider.getValue();
        String comment = commentArea.getText().trim();

        if (rating < 1 || rating > 5) {
            errorLabel.setText("Đánh giá từ 1-5 sao!");
            return;
        }

        reviewToEdit.setRating(rating);
        reviewToEdit.setComment(comment);

        if (reviewDAO.update(reviewToEdit)) {
            success("Cập nhật đánh giá thành công!");
            close();
        } else {
            errorLabel.setText("Cập nhật thất bại!");
        }
    }

    @FXML
    private void onCancel() {
        close();
    }

    private void close() {
        Stage stage = (Stage) userField.getScene().getWindow();
        stage.close();
    }

    private void success(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }
}