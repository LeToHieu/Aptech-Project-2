package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.DAO.BookDAO;
import com.aptech.aptechproject2.DAO.BorrowDAO;
import com.aptech.aptechproject2.DAO.ReviewDAO;
import com.aptech.aptechproject2.Model.Book;
import com.aptech.aptechproject2.Model.Borrow;
import com.aptech.aptechproject2.Model.Review;
import com.aptech.aptechproject2.Ulti.ImageUtil;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BorrowedBookDetailController {

    @FXML private Label titleLabel, descriptionLabel, authorsLabel, categoriesLabel;
    @FXML private Label availabilityLabel, statusIcon;
    @FXML private HBox ratingBox;
    @FXML private ImageView bookImageView;
    @FXML private VBox reviewsContainer;
    @FXML private Button borrowButton;
    @FXML private Label borrowDayLabel, expireDayLabel, daysLeftLabel;

    private Borrow borrow;
    private Book book;
    private final BorrowDAO borrowDAO = new BorrowDAO();
    private final BookDAO bookDAO = new BookDAO();
    private final ReviewDAO reviewDAO = new ReviewDAO();

    public void setBorrow(Borrow borrow) {
        this.borrow = borrow;
        this.book = bookDAO.getBookById((int) borrow.getBookId());
        loadAllData();
    }

    private void loadAllData() {
        loadBookInfo();
        loadBorrowInfo();
        loadReviews();
    }

    private void loadBookInfo() {
        titleLabel.setText(book.getTitle());
        descriptionLabel.setText(book.getDescription() != null && !book.getDescription().isEmpty() ? book.getDescription() : "Chưa có mô tả.");

        authorsLabel.setText(book.getAuthors().isEmpty() ? "Không rõ" :
                book.getAuthors().stream().map(a -> a.getName()).collect(java.util.stream.Collectors.joining(", ")));

        categoriesLabel.setText(book.getCategories().isEmpty() ? "Không rõ" :
                book.getCategories().stream().map(c -> c.getName()).collect(java.util.stream.Collectors.joining(", ")));

        int available = book.getTotalBook() - book.getBorrowBook();
        availabilityLabel.setText(available + " cuốn còn lại");
        if (available > 0) {
            statusIcon.setText("Còn hàng");
            statusIcon.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 5 12; -fx-background-radius: 20;");
        } else {
            statusIcon.setText("Hết sách");
            statusIcon.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 5 12; -fx-background-radius: 20;");
        }

        // Rating
        ratingBox.getChildren().clear();
        double rating = book.getAverageRating();
        int fullStars = (int) Math.round(rating);
        for (int i = 0; i < 5; i++) {
            Text star = new Text(i < fullStars ? "★" : "☆");
            star.setStyle(i < fullStars ? "-fx-fill: #f39c12; -fx-font-size: 28px;" : "-fx-fill: #bdc3c7; -fx-font-size: 28px;");
            ratingBox.getChildren().add(star);
        }
        if (rating > 0) {
            Label score = new Label(String.format(" (%.1f)", rating));
            score.setStyle("-fx-font-size: 18px; -fx-text-fill: #2c3e50;");
            ratingBox.getChildren().add(score);
        }

        ImageUtil.loadImageToView(book.getImage(), bookImageView);
    }

    private void loadBorrowInfo() {
        borrowDayLabel.setText("Ngày mượn: " + borrow.getBorrowDayFormatted());
        expireDayLabel.setText("Hạn trả: " + borrow.getExpireDayFormatted());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expire = borrow.getExpireDay().toLocalDateTime();
        Duration duration = Duration.between(now, expire);
        long daysLeft = duration.toDays();

        if (daysLeft > 0) {
            daysLeftLabel.setText("Còn " + daysLeft + " ngày");
            daysLeftLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        } else if (daysLeft == 0) {
            daysLeftLabel.setText("Hôm nay là hạn cuối");
            daysLeftLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
        } else {
            daysLeftLabel.setText("Quá hạn " + Math.abs(daysLeft) + " ngày");
            daysLeftLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        }

        borrowButton.setText("Trả sách");
        borrowButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 12 12; -fx-background-radius: 50; -fx-font-weight: bold;");
    }

    private void loadReviews() {
        reviewsContainer.getChildren().clear();
        List<Review> reviews = reviewDAO.getReviewsByBookId(book.getId());
        if (reviews.isEmpty()) {
            Label noReview = new Label("Chưa có đánh giá nào.");
            noReview.setStyle("-fx-font-size: 16px; -fx-text-fill: #95a5a6;");
            reviewsContainer.getChildren().add(noReview);
            return;
        }

        for (Review r : reviews) {
            VBox reviewBox = new VBox(8);
            reviewBox.setStyle("-fx-background-color: #fff; -fx-padding: 15; -fx-background-radius: 10; -fx-border-color: #ddd; -fx-border-radius: 10;");

            HBox header = new HBox(10);
            header.setAlignment(Pos.CENTER_LEFT);
            Label user = new Label(r.getUserName());
            user.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
            Label date = new Label(r.getCreatedAt().toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            date.setStyle("-fx-font-size: 13px; -fx-text-fill: #7f8c8d;");
            Region spacer = new Region();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
            header.getChildren().addAll(user, spacer, date);

            HBox rating = new HBox(3);
            int stars = r.getRating();
            for (int i = 0; i < 5; i++) {
                Text star = new Text(i < stars ? "★" : "☆");
                star.setStyle(i < stars ? "-fx-fill: #f39c12; -fx-font-size: 18;" : "-fx-fill: #bdc3c7; -fx-font-size: 18;");
                rating.getChildren().add(star);
            }

            Label comment = new Label(r.getComment());
            comment.setWrapText(true);
            comment.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");

            reviewBox.getChildren().addAll(header, rating, comment);
            reviewsContainer.getChildren().add(reviewBox);
        }
    }

    @FXML
    private void onPreviewPdf() {
        if (book.getUrl() == null || book.getUrl().trim().isEmpty()) {
            showAlert("Không có link xem trước cho sách này!");
            return;
        }
        try {
            Desktop.getDesktop().browse(new URI(book.getUrl()));
        } catch (Exception e) {
            showAlert("Không thể mở link: " + e.getMessage());
        }
    }

    @FXML
    private void onReturnBook() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận trả sách");
        confirm.setHeaderText("Trả: " + book.getTitle());
        confirm.setContentText("Bạn có chắc muốn trả sách này?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;

        borrowButton.setText("Đang xử lý...");
        borrowButton.setDisable(true);

        Task<Boolean> task = new Task<>() {
            @Override protected Boolean call() {
                return borrowDAO.returnBorrow(borrow.getId());
            }
        };

        task.setOnSucceeded(e -> {
            if (task.getValue()) {
                showAlert("Trả sách thành công!");
                showReviewModal();  // Show review modal after success
                ((Stage) borrowButton.getScene().getWindow()).close();  // Close detail window
            } else {
                showAlert("Không thể trả sách. Vui lòng thử lại.");
                borrowButton.setDisable(false);
            }
        });

        new Thread(task).start();
    }

    private void showReviewModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/aptech/aptechproject2/fxml/add_review_modal.fxml"));
            Parent root = loader.load();
            AddReviewController controller = loader.getController();
            controller.setBook(book);

            Stage stage = new Stage();
            stage.setTitle("Đánh giá sách");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(borrowButton.getScene().getWindow());
            controller.setStage(stage);
            stage.showAndWait();
        } catch (Exception ex) {
            showAlert("Lỗi mở modal đánh giá: " + ex.getMessage());
        }
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.showAndWait();
    }
}