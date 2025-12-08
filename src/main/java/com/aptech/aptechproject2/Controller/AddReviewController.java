package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.DAO.ReviewDAO;
import com.aptech.aptechproject2.Model.Book;
import com.aptech.aptechproject2.Model.Review;
import com.aptech.aptechproject2.Ulti.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class AddReviewController {

    @FXML private Slider ratingSlider;
    @FXML private Label ratingLabel;
    @FXML private TextArea commentArea;
    @FXML private Label errorLabel;

    private Stage stage;
    private Book book;  // Book to review

    private final ReviewDAO reviewDAO = new ReviewDAO();

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    @FXML
    private void initialize() {
        ratingSlider.valueProperty().addListener((obs, old, newVal) -> {
            int rating = newVal.intValue();
            String stars = "★★★★★".substring(0, rating) + "☆☆☆☆☆".substring(rating);
            ratingLabel.setText(stars);
        });
    }

    @FXML
    private void onSubmit() {
        errorLabel.setText("");

        int rating = (int) ratingSlider.getValue();
        String comment = commentArea.getText().trim();

        if (comment.isEmpty()) {
            errorLabel.setText("Vui lòng nhập bình luận.");
            return;
        }

        Review review = new Review();
        review.setUserId(Session.getCurrentUser().getId());
        review.setBookId(book.getId());
        review.setRating(rating);
        review.setComment(comment);

        if (reviewDAO.create(review)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Đánh giá đã được gửi thành công!");
            alert.showAndWait();
            stage.close();
        } else {
            errorLabel.setText("Lỗi khi gửi đánh giá. Vui lòng thử lại.");
        }
    }

    @FXML
    private void onCancel() {
        stage.close();
    }
}